package com.example.android.popmoviesstage2;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.app.FragmentManager;

//provides UI for sorting preference selection

public class SettingsFragment extends PreferenceFragment {

    private final String LOG_TAG = "PreferenceFragement: ";

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        //sets default values
        getPreferenceManager().setDefaultValues(getActivity().getApplicationContext(),
                R.xml.preferences, false);

        //Load preference from XML
        addPreferencesFromResource(R.xml.preferences);


    }

}
