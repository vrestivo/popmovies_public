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
        initialDataPull(context, firstrun);

        //notify broadcast receiver that the data sync is complete
        sendBroadcast();

    }

    private void sendBroadcast() {
        //TODO replace strings
        Log.v(LOG_TAG, "_sending broadcast");
        Intent intent = new Intent("sync_complete");
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
    private void initialDataPull(Context context, boolean firstrun) {
        Utility.pullMoviesAndBulkInsert(context, firstrun);
        Utility.pullDetailsDataAndBulkInsert(context);

        //pull list of poster URLs from the movies table
        ArrayList<String> posterDownloadList =
                Utility.getPosterUrlsFromDb(context);

        //pull a list of movie trailers URLs from the database
        ArrayList<String> thumbnailDownloadList =
                Utility.getThumbnailUrlsFromDb(context);


        //download movie posters
        if (!posterDownloadList.isEmpty()) {
            FetchData.downloadAndSaveMoviePosters(posterDownloadList,
                    context);
        }

        //download movie trailers thumbnails
        if(!thumbnailDownloadList.isEmpty()){
            FetchData.downloadAndSaveTrailerThumbnails(Utility.getThumbnailUrlsFromDb(context),
                    context);
        }
    }

}
