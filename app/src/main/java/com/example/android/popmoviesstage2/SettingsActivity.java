package com.example.android.popmoviesstage2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

//Activit

public class SettingsActivity extends PreferenceActivity {

    private final String LOG_TAG = "SettingsActivity: ";


    SharedPreferences.OnSharedPreferenceChangeListener listener;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //starts PreferenceFragment which controls
        //how settings appear on the screen
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,
                        new SettingsFragment()).commit();


    }

}
