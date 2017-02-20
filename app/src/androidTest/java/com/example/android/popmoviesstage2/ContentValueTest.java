package com.example.android.popmoviesstage2;

import android.content.ContentValues;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by devbox on 12/3/16.
 */

@RunWith(JUnit4.class)
public class ContentValueTest {



    @Test
    public void contentValuesTest(){
        final String LOG_TAG = "_CV_test";
        ContentValues cv = new ContentValues();

        cv.put("key", 1);
        cv.put("key", 2);
        cv.put("key", 3);

        Log.v(LOG_TAG, String.valueOf(cv.size()));
        Log.v(LOG_TAG, String.valueOf(cv.get("key")));

    }

}
