package com.example.android.popmoviesstage2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;


/**
 * Dialog Fragment to select sort options
 * Purpose: streamline UI
 */

public class PrefDialogFragment  extends DialogFragment {

    private String[] mEnries;
    private String[] mValues;
    private int mClickedIndex;
    private String mValue;
    private String mDefaultValue;
    SharedPreferences mSharedPrefs;
    private String mPrefKey;
    private DialogInterface.OnClickListener mSelectedItemListener;


    public PrefDialogFragment() {
        super();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        mPrefKey = getString(R.string.pref_sort_key);

         mSelectedItemListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which != getItemIndex()){
                    mValue = mValues[which];
                    //calling apply to save preferences asynchronously
                    mSharedPrefs.edit().putString(mPrefKey, mValue).apply();
                }

                dialog.dismiss();

                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.restartFragmentMainLoader();
            }
        };


        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEnries = getResources().getStringArray(R.array.pref_sort_by_entries);
        mValues = getResources().getStringArray(R.array.pref_sort_by_values);
        mDefaultValue = getString(R.string.pref_sort_by_default);
        mValue = mSharedPrefs.getString(mPrefKey, mDefaultValue);

    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(mEnries, getItemIndex(), mSelectedItemListener);
        //TODO optionally pretty up the appearance
        builder.setTitle(R.string.title_sort_by);
        return builder.create();
    }

    /**
     * utility method to find the value index in the
     * value string array
     * @return indes of the selected value
     */
    private int getItemIndex(){
        int index = -1;
        if(mValue!=null && mValue.length()>0){
            for(int i=0; i < mValues.length; i++){
                if(mValue.equals(mValues[i])){
                    return i;
                }
            }
        }
        return index;
    }

}
