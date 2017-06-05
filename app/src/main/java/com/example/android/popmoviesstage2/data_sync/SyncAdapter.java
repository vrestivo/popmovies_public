package com.example.android.popmoviesstage2.data_sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.android.popmoviesstage2.FetchData;
import com.example.android.popmoviesstage2.R;
import com.example.android.popmoviesstage2.Utility;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * sync adapter implementation
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    //log tag
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Context mContext;
    private String mFirstRunKey;
    private ContentResolver mContentResolver;

    public static final String EXTRA_STATUS_CODE = "EXTRA_STATUS_CODE";

    //status code constants
    public static final int STATUS_OK = 0;
    public static final int STATUS_RESOURCE_UNAVAILABLE = -1;
    public static final int STATUS_IO_ERROR = -2;
    public static final int STATUS_NETWORK_CONNECTION_ERROR = -3;
    public static final int STATUS_INVALID_URL = -4;
    public static final int STATUS_TOO_MANY_REQUESTS = -5;
    public static final int STATUS_UNKNOWN_ERROR = -6;


    public static Account createSyncAccount(Context context) {
        Account newAccount = new Account("MySyncAccount",
                context.getString(R.string.account_type));

        Log.v("_created Account: ", newAccount.toString());

        AccountManager am = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (am == null) {
            Log.v("_Account Manager: ", "is null");
        } else {
            if (am.addAccountExplicitly(newAccount, null, null)) {
                Log.v("_Adding Account: ", "success!");
            } else {
                Log.v("_Adding Account: ", "acount exists, or something else happened!");
                Log.v("_Adding Account: ", "attempting retrieve existing account");
                Account[] accounts = am.getAccountsByTypeForPackage(context.getString(R.string.account_type),
                        context.getString(R.string.content_authority));
                if (accounts.length == 1) {
                    newAccount = accounts[0];
                    Log.v("_Adding Account: ", "account retrieved");

                } else {
                    Log.v("_Adding Account: ", " accounts[] != 1: " + accounts.length);
                }
            }
        }

        return newAccount;
    }

    //required constructor
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mContext = context;
        mFirstRunKey = mContext.getString(R.string.pref_firstrun_key);
    }

    //required constructor
    public SyncAdapter(Context context,
                       boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mContext = context;
        mFirstRunKey = mContext.getString(R.string.pref_firstrun_key);

    }

    @Override
    public void onPerformSync(Account account,
                              Bundle bundle,
                              String s,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {

        Context context = getContext();

        boolean firstrun = getContext().getSharedPreferences(mFirstRunKey, mContext.MODE_PRIVATE)
                .getBoolean(mFirstRunKey, true);

        //TODO handle network errors

        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.app_name), MODE_PRIVATE);

        //download data
        int statusCode = initialDataPull(context, firstrun);

        //notify broadcast receiver that the data sync is complete
        sendBroadcast(statusCode);

    }

    private void sendBroadcast(int statusCode) {
        //TODO replace strings
        Log.v(LOG_TAG, "_sending broadcast");
        Intent intent = new Intent("sync_complete").putExtra(EXTRA_STATUS_CODE, statusCode);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


    /**
     * convenience method for perfroming an initial data
     * request from the TMDB
     *
     * @param context
     * @param firstrun
     */
    //TODO refactor to return a result code
    private int initialDataPull(Context context, boolean firstrun) {
        int statusCode = STATUS_UNKNOWN_ERROR;
        int tempStatus = STATUS_UNKNOWN_ERROR;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo().isConnected()) {
            tempStatus = Utility.pullMoviesAndBulkInsert(context, firstrun);
            if (tempStatus < 0) {
                statusCode = tempStatus;
                if (statusCode == STATUS_NETWORK_CONNECTION_ERROR) {
                    return statusCode;
                }
            } else {
                statusCode = STATUS_OK;
            }

        } else {
            return STATUS_NETWORK_CONNECTION_ERROR;
        }


        if (connectivityManager.getActiveNetworkInfo().isConnected()) {
            //TODO get status code
            tempStatus = Utility.pullDetailsDataAndBulkInsert(context);
        } else {
            return STATUS_NETWORK_CONNECTION_ERROR;
        }

        //pull list of poster URLs from the movies table
        ArrayList<String> posterDownloadList =
                Utility.getPosterUrlsFromDb(context);

        //pull a list of movie trailers URLs from the database
        ArrayList<String> thumbnailDownloadList =
                Utility.getThumbnailUrlsFromDb(context);


        if (connectivityManager.getActiveNetworkInfo().isConnected()) {
            //download movie posters
            if (!posterDownloadList.isEmpty()) {
                FetchData.downloadAndSaveMoviePosters(posterDownloadList,
                        context);
            }

        } else {
            statusCode = STATUS_NETWORK_CONNECTION_ERROR;

        }


        if (connectivityManager.getActiveNetworkInfo().isConnected()) {
            //download movie trailers thumbnails
            if (!thumbnailDownloadList.isEmpty()) {
                FetchData.downloadAndSaveTrailerThumbnails(Utility.getThumbnailUrlsFromDb(context),
                        context);
            }
        } else {
            statusCode = STATUS_NETWORK_CONNECTION_ERROR;

        }

        return statusCode;
    }

}
