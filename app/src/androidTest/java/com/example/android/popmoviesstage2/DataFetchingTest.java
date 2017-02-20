package com.example.android.popmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Class containing test for data fetching from TMBD
 */
@RunWith(JUnit4.class)
public class DataFetchingTest {


    //private final String movieId = "157336";
    private final String movieId = "284052";

    @After
    public void cleanup() {
    }

    @AfterClass
    public static void classCleanup() {
    }






    public static String fetchDetailsByMovieId(String movieId, Context c) {
        final String LOG_TAG = "fetchDetailsByMovieId: ";
        Context context = c;

        if (movieId == null) {
            Log.d(LOG_TAG, "null movie Id passed");
            return null;
        }

        String rawJsonDetails = null;

        rawJsonDetails = FetchData.getJsonData(
                FetchData.generateDetailUrl(Integer.parseInt(movieId),
                        context));

        return rawJsonDetails;

    }




    @Test
    public void pullExtraDataTest() {
        final String LOG_TAG = "pullExtraData: ";

        Context context = getTargetContext();

        ArrayList<String> ids = new ArrayList<String>();

        ids = Utility.getMovieIdsFromDB(context);

        int id = Integer.parseInt(ids.get(0));

        String results = fetchDetailsByMovieId(String.valueOf(id), context);

        Assert.assertNotNull("returned data is null", results);

        Log.v(LOG_TAG, results);


    }




//    @Test
//    public void getTrailerCvArrayTest(){
//        String LOG_TAG = "_getTrailerCvTest: ";
//
//        Context c = getTargetContext();
//
//        String rawDetails = FetchData.fetchDetailsByMovieId(movieId, c);
//
//        Assert.assertNotNull("Returned raw movies details are null", rawDetails);
//
//        HashMap<String, HashMap<String, ContentValues[]>> map =
//                FetchData.parseRawDetails(movieId, rawDetails, c);
//
//        Assert.assertTrue("map does not have a movei Id", map.containsKey(movieId));
//
//        Assert.assertFalse("HashMap is emtpy", map.isEmpty());
//
//        ContentValues[] trailers = Utility.getTrailersCV(movieId, map, c);
//
//        Assert.assertNotNull("trailer contentValues are null", trailers);
//
//        for(ContentValues value : trailers){
//            System.out.println("_trailerValue: " + value.toString());
//        }
//
//
//
//        Log.v(LOG_TAG, trailers.toString());
//        Log.v("_cv size:, ", String.valueOf(trailers.length));
//
//    }





//    @Test
//    public void getReviewsCvArrayTest(){
//        String LOG_TAG = "_getReviewsCvTest: ";
//
//        Context c = getTargetContext();
//
//        String rawDetails = FetchData.fetchDetailsByMovieId(movieId, c);
//
//        Assert.assertNotNull("Returned raw movies details are null", rawDetails);
//
//        HashMap<String, HashMap<String, ContentValues[]>> map =
//                FetchData.parseRawDetails(movieId, rawDetails, c);
//
//        Assert.assertTrue("map does not have a movei Id", map.containsKey(movieId));
//
//        Assert.assertFalse("HashMap is emtpy", map.isEmpty());
//
//        ContentValues[] reviews = Utility.getReviewsCv(movieId, map, c);
//
//        Assert.assertNotNull("trailer contentValues are null", reviews);
//
//        for(ContentValues value : reviews){
//            System.out.println("_reviewValue: " + value.toString());
//        }
//
//
//
//        Log.v(LOG_TAG, reviews.toString());
//        Log.v("_cv size:, ", String.valueOf(reviews.length));
//
//
//    }


}
