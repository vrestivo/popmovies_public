package com.example.android.popmoviesstage2;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

        setContentView(R.layout.view_pager_fragment);

        //TODO delete when done
        Log.v(LOG_TAG, "_in onCreate()");


        //DetailFragment df = new DetailFragment();
        ViewPagetTabFragment fptf = new ViewPagetTabFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.movie_detail_container, fptf).commit();

    }

}