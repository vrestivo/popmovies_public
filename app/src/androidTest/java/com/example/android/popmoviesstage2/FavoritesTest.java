package com.example.android.popmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data.MovieDbHelper;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Created by devbox on 12/7/16.
 */

@RunWith(JUnit4.class)
public class FavoritesTest {




    /**
     * Covenience method sets favorite column value to 1 in the movies
     * table for every record with the movie ID greater than the
     * given movie ID
     * @param valueToSet va
     * @param baseMovieId baseline movie ID. anything greater will be set
     *                    as favorite.
     * @param context context
     */
    public static int toggleFavoriteWithMovieIdAboveGiven(int valueToSet, String baseMovieId, Context context){

        ContentValues favoriteCv = new ContentValues();
        favoriteCv.put(DataContract.Movies.COL_FAVORITE, valueToSet);

        int valuesUpdated = 0;

        final String favoriteSelection = DataContract.Movies._ID + ">?";

        ContentResolver contentResolver = context.getContentResolver();

        valuesUpdated = contentResolver.update(
                DataContract.Movies.buildToggleFavoritesUri(baseMovieId, true),
                favoriteCv,
                favoriteSelection,
                new String[]{baseMovieId}
        );

        return valuesUpdated;
    }




    /**
     * sets favorite values in the movies table to 1
     * for records with _ID greater than movieIdLimit
     */
/*

    @Test
    public void makeFavorites(){
        final String LOG_TAG = "makeFavoriteTest";

        final String movieIdLimit = "270000";

        Context context = InstrumentationRegistry.getTargetContext();

        int valuesUpdated = 0;

        valuesUpdated = toggleFavoriteWithMovieIdAboveGiven(1, movieIdLimit, context);
        Log.v(LOG_TAG, String.valueOf(valuesUpdated));

    }

*/


//    @Test
//    public void DeleteNonFavoritesTest(){
//        final String LOG_TAG = "DeleteNonFavoriteTest";
//
//        final String movieIdLimit = "270000";
//
//        Context context = InstrumentationRegistry.getTargetContext();
//
//        toggleFavoriteWithMovieIdAboveGiven(1, movieIdLimit, context);
//
//        SQLiteOpenHelper dbHelper = new MovieDbHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        Uri notFaves = DataContract.buildNotFavoritesUri();
//        Log.v(LOG_TAG, notFaves.toString());
//
//        ContentResolver contentResolver = context.getContentResolver();
//
//        int deathToll = contentResolver.delete(notFaves,
//                null,
//                null
//                );
//
//        Assert.assertTrue("Rookies can't shoot straight. Death toll: " + String.valueOf(deathToll), deathToll>0);
//        db.close();
//        Log.v(LOG_TAG, "Good shootin', Rookie, you've got " + String.valueOf(deathToll) + " kills!");
//
//    }



    //TODO validate the results
    @Test
    public void deleteNonFavoriteJPGs(){

        Utility.deleteNonFavoriteJPGsAndMovieRecords(getTargetContext());



    }


}
