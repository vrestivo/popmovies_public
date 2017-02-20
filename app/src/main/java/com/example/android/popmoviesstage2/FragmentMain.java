package com.example.android.popmoviesstage2;

import android.accounts.Account;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data_sync.SyncAdapter;

/**
 * Created by devbox on 11/7/16.
 */

public class FragmentMain extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private BroadcastReceiver mBroadcastReceiver;

    private final String SYNC_DONE = "sync_complete";
    private boolean firstRun;

    private ConnectivityManager mConnectivityManager;

    private String LOG_TAG = "FragmentMain";

    //CursorLoader ID
    public static final int LOADER_ID = 123;

    //Extra tag for position
    private static final String POS_EXTRA = "POSITION";

    //intent tag for passed Uri
    private static final String POS_URI = "POSITION";


    //stores cursor's rowId of a clicked item
    private long mRowId;

    private SharedPreferences mPreferences;

    private String SETTING_VOTE;
    private String SETTING_POPULARITY;
    private String SETTING_FAVORITES;
    private String PREF_KEY_SORT_BY;
    private String PREF_KEY_FIRSTRUN;

    private boolean mTwoPane;


    //CursorAdapter for the grid view
    //since no cursor is available yet, null passed to the constructor
    DataAdapter mDataAdapter;

    //List Position variable use for scrolling to the last viewed item
    //in case of a configuration change
    private int mPosition = GridView.INVALID_POSITION;

    private Context mContext;

    //callback interface
    public interface FragmentMainCallback {
        public void setFragment(Uri uri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

                if (mainActivity != null) {

                    mainActivity.restartFragmentMainLoader();
                }
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        mDataAdapter = new DataAdapter(getActivity(), null, 0);


        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            mTwoPane = mainActivity.isTwoPane();
            Log.v(LOG_TAG, "_two pane: " + String.valueOf(mTwoPane));
        }

        View rootView;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView = inflater.inflate(R.layout.fragment_main_landscape, container, false);

        } else {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

        }

        GridView gridView = (GridView) rootView.findViewById(R.id.id_grid_view);


        //initializing grid view and populating with images
        gridView.setAdapter(mDataAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(

        ) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mPosition = position;
                mRowId = id;

                //build uri for clicked movie item to be passed to detail view
                Uri clickedItemUri = DataContract.Movies.buildMovieWithIdUri(mRowId);
                Log.v(LOG_TAG, "_uri: " + clickedItemUri.toString());

                ((MainActivity) getActivity()).setFragment(clickedItemUri);

            }
        });


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

        //resiter broadcast receiver to listen for callbacks
        //used to signal the end of sync and refresh the view
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver,
                new IntentFilter(SYNC_DONE));

        //restarting loader to refelect sort setting changes
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //"this" parameter referes to the class implemeting loader callbacks,
        //which is the current class
        super.onActivityCreated(savedInstanceState);
        //getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        String seletction = null;


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
            seletction = SETTING_FAVORITES + ">0";

        }
        //set sorting to default (popularity)
        else {
            sortOrder = DataContract.Movies.COL_POPULARITY + " DESC";
        }

        return new CursorLoader(getActivity(),
                DataContract.Movies.buildMoviesUri(), //Uri
                DataContract.Movies.defaultProjection, //projection
                seletction,   //selection
                null,   //selection arguments
                sortOrder //sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDataAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDataAdapter.swapCursor(null);

    }

    /**
     * perform manual sync immediately
     *
     * @param context
     */
    private void syncNow(Context context, boolean AppFirstRun) {
        Account account = SyncAdapter.createSyncAccount(context);

        Bundle syncSettings = new Bundle();

        syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        syncSettings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        syncSettings.putBoolean(PREF_KEY_FIRSTRUN, AppFirstRun);

        ContentResolver.requestSync(account, getString(R.string.content_authority), syncSettings);

    }
}



