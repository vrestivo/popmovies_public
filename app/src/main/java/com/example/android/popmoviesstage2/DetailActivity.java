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

        setContentView(R.layout.activity_detail);

        DetailFragment df = new DetailFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.movie_detail_container, df).commit();

    }

}