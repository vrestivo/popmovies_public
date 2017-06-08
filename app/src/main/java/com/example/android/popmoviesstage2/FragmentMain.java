package com.example.android.popmoviesstage2;

import android.accounts.Account;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data_sync.SyncAdapter;

/**
 * Created by devbox on 11/7/16.
 */

public class FragmentMain extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MovieGridAdapter.GridItemClickListener {
    public static final String ARG_MOVIE_ID = "ARG_MOVIE_ID";
    private final String KEY_GRID_COLLAPSED = "KEY_GRID_COLLAPSED";
    private final String KEY_SCROLL_POS = "KEY_SCROLL_POS";
    public static final int LOADER_ID = 123;
    private final String SYNC_DONE = "sync_complete";

    private BroadcastReceiver mBroadcastReceiver;
    private boolean firstRun;
    private ConnectivityManager mConnectivityManager;
    private String LOG_TAG = "FragmentMain";

    //stores cursor's rowId of a clicked item
    private long mRowId;
    private SharedPreferences mPreferences;

    private String SETTING_VOTE;
    private String SETTING_POPULARITY;
    private String SETTING_FAVORITES;
    private String PREF_KEY_SORT_BY;
    private String PREF_KEY_FIRSTRUN;

    private boolean mTwoPane;
    private boolean mIsClicked = false;


    //progress dialog
    private final String DIALOG_TAG = "DIALOG_TAG";

    private MovieGridAdapter mGridAdapter;
    private int mGridSpanNum;
    private int mCollapsedSpanNum;
    private GridLayoutManager mGridLayoutManager;
    private boolean mGridCollapsed = false;
    private int mPosition = RecyclerView.NO_POSITION;
    private Context mContext;
    private TextView mNoDataMessage;
    private RecyclerView mRecyclerView;

    //callback interface
    public interface FragmentMainCallback {
        public void setFragment(Uri uri, @Nullable View view);
    }

    @Override
    public void onGridItemClick(long movieId, View posterImageView, int position) {
        Uri clickedItemUri = DataContract.Movies.buildMovieWithIdUri(movieId);
        if(!mTwoPane){
            mPosition = position;
            mIsClicked = true;
        }
        Log.v(LOG_TAG, "_uri: " + clickedItemUri.toString());
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setFragment(clickedItemUri, posterImageView);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);


        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mPreferences = getActivity().getPreferences(mContext.MODE_PRIVATE);

        SETTING_VOTE = getString(R.string.pref_setting_vote);
        SETTING_POPULARITY = getString(R.string.pref_setting_popularity);
        PREF_KEY_SORT_BY = getString(R.string.pref_sort_key);
        SETTING_FAVORITES = getString(R.string.pref_setting_favorites);
        PREF_KEY_FIRSTRUN = getString(R.string.pref_firstrun_key);

        firstRun = mPreferences.getBoolean(PREF_KEY_FIRSTRUN, true);

        if (firstRun) {
            Toast.makeText(mContext,
                    getString(R.string.msg_initial_run),
                    Toast.LENGTH_LONG).show();
        }


        /**
         * this broadcast receiver is used to refresh the IU
         * upon completion of the data sync
         */
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(LOG_TAG, "_restaring cursorloader");
                MainActivity mainActivity = (MainActivity) getActivity();

                int statusCode = intent.getIntExtra(SyncAdapter.EXTRA_STATUS_CODE, SyncAdapter.STATUS_UNKNOWN_ERROR);

                if (mainActivity != null) {
                    mainActivity.restartFragmentMainLoader();
                    String toastMessage = null;

                    switch (statusCode){
                        case SyncAdapter.STATUS_OK: {
                            toastMessage = getContext().getString(R.string.message_update_complete);
                            break;
                        }
                        case SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR: {
                            toastMessage = getContext().getString(R.string.error_partial_update);
                            break;
                        }
                        case SyncAdapter.STATUS_TOO_MANY_REQUESTS:{
                            toastMessage = getContext().getString(R.string.error_too_many_requests);
                            break;
                        }
                        default: {
                            toastMessage = getContext().getString(R.string.error_update_error);
                        }
                    }

                    Toast.makeText(mainActivity, toastMessage, Toast.LENGTH_SHORT).show();
                    ProgressFragment pf = (ProgressFragment) mainActivity.getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
                    if (pf != null) {
                        pf.dismiss();
                    }
                }
            }
        };
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        mGridAdapter = new MovieGridAdapter(mContext, this);

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(KEY_GRID_COLLAPSED)) {
                mGridCollapsed = savedInstanceState.getBoolean(KEY_GRID_COLLAPSED);
            }
            if(savedInstanceState.containsKey(KEY_SCROLL_POS)){
                mPosition = savedInstanceState.getInt(KEY_SCROLL_POS);
                Log.v(LOG_TAG, "_saved positinon: " + mPosition);

            }
        }
        else {
            Log.v(LOG_TAG, "_saved instance state is null");
            Log.v(LOG_TAG, "_sa unsaved mPosition: " + mPosition);

        }

        final MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            mTwoPane = mainActivity.isTwoPane();
            Log.v(LOG_TAG, "_two pane: " + String.valueOf(mTwoPane));
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mNoDataMessage = (TextView) rootView.findViewById(R.id.msg_no_content_main_fragment);


        mGridSpanNum = getResources().getInteger(R.integer.grid_span);

        if(mTwoPane){
            mCollapsedSpanNum = getResources().getInteger(R.integer.collapsed_span);
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_grid);
        if(mGridCollapsed) {
            mGridLayoutManager = new GridLayoutManager(mContext, mCollapsedSpanNum);
        }
        else {
            mGridLayoutManager = new GridLayoutManager(mContext, mGridSpanNum);
        }

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mGridAdapter);


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreferences.edit().putBoolean(PREF_KEY_FIRSTRUN, false).apply();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        mIsClicked = false;
        //register broadcast receiver to listen for callbacks
        //used to signal the end of data sync
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(SYNC_DONE));

        //restarting loader to reflect sort setting changes
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.v(LOG_TAG, "_sa in onSaveInstanceState");

        outState.putBoolean(KEY_GRID_COLLAPSED, mGridCollapsed);

         int curPos;
        if(mTwoPane) {
            curPos = mGridLayoutManager.findFirstVisibleItemPosition();
        }else {
            if(mIsClicked){
            curPos = mPosition;
            }
            else {
                curPos = mGridLayoutManager.findFirstVisibleItemPosition();
            }
        }
        Log.v(LOG_TAG, "_sa in onSaveInstanceState() mpos/curpost: " + mPosition + "/"+ curPos);
        outState.putInt(KEY_SCROLL_POS, curPos);
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //LoaderManager.LoaderCallbacks implemetation

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = getActivity().getApplicationContext();
        String selection = null;

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (firstRun) {
                //run initial sync
                syncNow(mContext, firstRun);
            }
            firstRun = false;

        } else {
            Toast.makeText(mContext,
                    getString(R.string.error_data_unavailable),
                    Toast.LENGTH_SHORT).show();
        }

        String sortOrder = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_KEY_SORT_BY, "");

        //set sorting by popular vote if matches
        if (sortOrder.contentEquals(SETTING_VOTE)) {
            sortOrder = DataContract.Movies.COL_VOTE_AVG + " DESC";
        }
        //set soring by favorites if matches
        else if (sortOrder.contentEquals(SETTING_FAVORITES)) {
            //sort by favorites
            selection = SETTING_FAVORITES + ">0";
        }
        //set sorting to default (popularity)
        else {
            sortOrder = DataContract.Movies.COL_POPULARITY + " DESC";
        }

        return new CursorLoader(
                getActivity(),
                DataContract.Movies.buildMoviesUri(), //Uri
                DataContract.Movies.defaultProjection, //projection
                selection,         //selection
                null,               //selection arguments
                sortOrder           //sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = getContext();

        if(data!=null && data.moveToFirst()) {
            if(mNoDataMessage.getVisibility() == View.VISIBLE){
                mNoDataMessage.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            mGridAdapter.swapCursor(data);
            if(mPosition != RecyclerView.NO_POSITION && mPosition < data.getCount()){
                Log.v(LOG_TAG, "_in onLoadFinished() pos: " + mPosition);
                //mRecyclerView.getLayoutManager().scrollToPosition(mPosition);
                mGridLayoutManager.scrollToPositionWithOffset(mPosition,0);
            }
        }
        else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String sortOrder = preferences.getString(PREF_KEY_SORT_BY, "");
            if (sortOrder.equals(SETTING_FAVORITES)){
                mNoDataMessage.setText(getString(R.string.msg_no_favorites));
            }
            else {
                mNoDataMessage.setText(getString(R.string.msg_no_data_main));
            }
            mRecyclerView.setVisibility(View.GONE);
            mNoDataMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGridAdapter.swapCursor(null);
    }


    public void setmGridSpanNum(boolean collapsed) {
        mGridCollapsed=collapsed;
        if (mGridCollapsed) {
            mGridLayoutManager.setSpanCount(mCollapsedSpanNum);
        }
        else {
            mGridLayoutManager.setSpanCount(mGridSpanNum);
        }
    }


    /**
     * perform manual sync immediately
     * @param context
     */
    public void syncNow(Context context, boolean AppFirstRun) {
        Account account = SyncAdapter.createSyncAccount(context);
        Bundle syncSettings = new Bundle();

        syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        syncSettings.putBoolean(PREF_KEY_FIRSTRUN, AppFirstRun);

        //start dialog
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ProgressFragment progressFragment = new ProgressFragment();
        progressFragment.setCancelable(false);
        progressFragment.show(fragmentManager, DIALOG_TAG);

        ContentResolver.requestSync(account, getString(R.string.content_authority), syncSettings);
    }

    public static class ProgressFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog progressDialog = ProgressDialog.show(
                    getActivity(),
                    "",
                    getString(R.string.message_refresh_wait)
            );
            return progressDialog;
        }
    }


}



