package com.example.android.popmoviesstage2;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.popmoviesstage2.data_sync.SyncAdapter;



/**
 * the face of the app
 */
public class MainActivity extends AppCompatActivity implements FragmentMain.FragmentMainCallback {

    private String LOG_TAG = "MainActivity: ";
    private SharedPreferences mSharedPreferences;

    public static final String CONTENT_AUTHORITY = "com.example.android.popmoviesstage2";
    public static final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT";
    public static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT";
    public static final String DETAIL_URI_TAG = "DETAIL_URI";

    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 360L;

    //sync every 6 hours
    public static final long SYNC_INTERVAL =
            SECONDS_PER_MINUTE * SYNC_INTERVAL_IN_MINUTES;

    private Toolbar mToolbar;

    //detail container visibility for 2 pane layout
    private boolean mContainerVisibility;
    private final String VISIBILITY = "VISIBILITY";


    //tracks if device is a tabled and two pane layout
    //should be used
    private static boolean mTwoPane;

    private View mDetailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //superclass constructor
        super.onCreate(savedInstanceState);

        mSharedPreferences = this.getPreferences(MODE_PRIVATE);

        //set up automatic data syncs on first run
        if (mSharedPreferences.getBoolean(getString(R.string.pref_firstrun_key), true)) {
            AccountManager accountManager = AccountManager.get(this);
            Account account = SyncAdapter.createSyncAccount(this);
            ContentResolver contentResolver = getContentResolver();

            //check if account was created
            if (account != null) {
                contentResolver.setSyncAutomatically(
                        account,
                        CONTENT_AUTHORITY,
                        true
                );

                //this method and account creation tends to trigger a sync
                //however, it does not seem to be a reliable way to
                // initiate the app with data on the first run
                contentResolver.addPeriodicSync(
                        account,
                        CONTENT_AUTHORITY,
                        Bundle.EMPTY,
                        SYNC_INTERVAL
                );

            }

        }

        //set default preferences for the activity
        //if false is set, the system will only intialize values to
        //defaults if it has never been done before, or user has not changed
        //preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        //get connectivity manager to receive network status
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext()
                        .getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        //receive network status
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        FragmentMain fragmentMain = new FragmentMain();

        setContentView(R.layout.activity_main);

        //setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);


        //inflate layout based on orientation and screen size
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment_main, fragmentMain, MAIN_FRAGMENT_TAG).commit();

        } else {
            mTwoPane = false;
        }

        if (mTwoPane) {
            mDetailContainer = findViewById(R.id.movie_detail_container);
            if(savedInstanceState!=null){
                mContainerVisibility = savedInstanceState.getBoolean(VISIBILITY);
            }
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mTwoPane) {
            outState.putBoolean(VISIBILITY, mContainerVisibility);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTwoPane) {
            viewTwoPaneDetailContainer(mContainerVisibility);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                viewTwoPaneDetailContainer(false);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort: {
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingIntent);
                return true;
            }
            case R.id.button_refresh: {
                FragmentMain fragmentMain;
                if (isTwoPane()) {
                    fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
                } else {
                    fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.main_container);
                }
                if (fragmentMain != null) {
                    //passing false to will preserve the favorites is any are saved
                    fragmentMain.syncNow(this.getApplicationContext(), false);
                }
            }
            default:
                //super.onOptionsItemSelected return false by default
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    public static boolean isTwoPane() {
        return mTwoPane;
    }


    @Override
    public void setFragment(Uri uri, @Nullable View view) {
        if (mTwoPane && uri != null) {

            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString(DETAIL_URI_TAG, uri.toString());

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(fragmentArgs);

            //viewTwoPaneDetailContainer(true);


            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.beginTransaction().replace(R.id.movie_detail_container,
                        detailFragment, DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {
                fm.beginTransaction()
                        //.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                        .addToBackStack(DETAIL_FRAGMENT_TAG)
                        .replace(R.id.movie_detail_container,
                        detailFragment, DETAIL_FRAGMENT_TAG)
                        .commit();
                viewTwoPaneDetailContainer(true);

            }


        } else if (!mTwoPane && uri != null) {
            Intent intent = new Intent(getApplicationContext(),
                    DetailActivity.class);

            //pass uri to intent
            intent.setData(uri);
            if(view!=null) {
                //facilitate activity transition
                Bundle bundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(
                                this,
                                view,
                                view.getTransitionName())
                        .toBundle();
                //start detail activity
                startActivity(intent, bundle);
            }
            else {
                //start detail activity
                startActivity(intent);
            }
        }

    }

    public void restartFragmentMainLoader() {
        FragmentMain fragmentMain;

        if (isTwoPane()) {
            fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        } else {
            fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.main_container);
        }

        getSupportLoaderManager().restartLoader(FragmentMain.LOADER_ID, null, fragmentMain);

    }

    private void viewTwoPaneDetailContainer(boolean show) {
        if (mDetailContainer != null) {
            if (show) {
                mDetailContainer.setVisibility(View.VISIBLE);
            } else {
                mDetailContainer.setVisibility(View.GONE);

            }
        }
        mContainerVisibility = show;

    }

}