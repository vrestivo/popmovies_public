package com.example.android.popmoviesstage2;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Displays detailed movie information
 */
public class DetailActivity extends AppCompatActivity {


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
        //DetailFragment df = new DetailFragment();
        ViewPagetTabFragment fptf = new ViewPagetTabFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.movie_detail_container, fptf).commit();

    }

}