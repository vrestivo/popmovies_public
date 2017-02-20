package com.example.android.popmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.popmoviesstage2.data.DataContract;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Class containing tests for database operations
 */

@RunWith(JUnit4.class)
public class DatabaseTest {

    //movie id for "Interstellar"
    private final String movieId = "157336";


    /**
     * tests retrieval of movies Id from the movies table
     */
/*    @Test
    public void getMovieIDsTest() {
        final String LOG_TAG = "_getMovieIds: ";

        Context context = getTargetContext();

        Cursor data = Utility.getMovieIdsFromDB(context);

        Assert.assertTrue("EMPTY CURSOR", data.moveToFirst());

        do {
            String movieId = data.getString(0);
            Log.v(LOG_TAG, movieId);
        } while (data.moveToNext());

        data.close();
    }
    */


    public static int bulkInsert(SQLiteDatabase db, String tableName,  ContentValues[] values, Context c) {

        int returnCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(tableName,
                        null,
                        value);
                if (id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return returnCount;

    }



    /**
     * This method deletes all entries in trailers, reviews,
     * and movies tables
     * @param context
     */
    public static void initiateCleanSlateProtocol(Context context){
        ContentResolver contentResolver = context.getContentResolver();
        Uri cleanSlateUri = DataContract.buildCleanSlateProtocolUri();

        contentResolver.delete(cleanSlateUri, null, null);

    }



    public static int pullMoviesAndBulkInsert(Context c) {
        int returnCount = 0;

        Uri moviesUri = DataContract.Movies.buildMoviesUri();

        ContentValues[] results = null;

        String rawJSON = FetchData.getJsonData(FetchData.generateUrlByGivenPreference(c,
                "popularity"));

        if (rawJSON != null) {
            results = FetchData.rawMoviesJsonDataToCvArray(rawJSON, c);
        }

        if (results != null && results.length > 0) {
            ContentResolver contentResolver = c.getContentResolver();
            returnCount = contentResolver.bulkInsert(moviesUri, results);
        }

        return returnCount;

    }


    /**
     * pull general movies data from TMDB and inserts it into empty movies table
     * NOTE: it will fail if the data already is in the table,
     * and zero insertions performed
     */
    @Test
    public void pullMoviesDataAndBulkInsertToMoviesTable() {
        Context context = getTargetContext();

        initiateCleanSlateProtocol(context);

        int retValue = 0;

        retValue = pullMoviesAndBulkInsert(context);

        //insure movies are inserted
        Assert.assertTrue("_nothing was inserted", retValue > 0);

        Utility.pullDetailsDataAndBulkInsert(context);


    }






/*    @Test
    public void trailerDbTest() {
        final String LOG_TAG = "_getTrailersDbTest: ";

        Context context = getTargetContext();

        SQLiteOpenHelper dbHelper = new MovieDbHelper(context);

        HashMap<String, HashMap<String, ContentValues[]>> parsedResults;


        String rawDetailData = FetchData.fetchDetailsByMovieId(movieId, context);
        Assert.assertNotNull("Null JSON results", rawDetailData);

        parsedResults = FetchData.parseRawDetails(movieId, rawDetailData, context);
        Assert.assertNotNull("Null parsed JSON", rawDetailData);


        ContentValues[] trailersCV = Utility.getTrailersCV(movieId, parsedResults, context);

        Assert.assertNotNull("Null ContentValues[]", trailersCV);
        Assert.assertTrue("ContentValues[] is zero or less", (trailersCV.length > 0));

        ContentResolver contentProvider = context.getContentResolver();

        Uri uri = DataContract.Trailers.buildTrailersByMovieIdUri(Long.parseLong(movieId));
        Log.v(LOG_TAG, uri.toString());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count = bulkInsert(db, trailersCV, context);

        db.close();

        Assert.assertTrue("_bulk insert failed", count > 0);

    }*/

}
