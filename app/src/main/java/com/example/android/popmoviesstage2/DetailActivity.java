package com.example.android.popmoviesstage2;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.popmoviesstage2.data.DataContract;

/**
 * Displays detailed movie information
 */
public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();

    /**
     * Receives movie information and sets the view to display it
     *
     * @param savedInstanceState receives information from activity that called it
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        //TODO delete when done
        Log.v(LOG_TAG, "_in onCreate()");

        if(savedInstanceState  == null) {
            DetailFragment fragment = new DetailFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.view_pager_container, fragment, MainActivity.DETAIL_FRAGMENT_TAG).commit();
        }

    }

}