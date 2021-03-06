package com.example.android.popmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data.MovieDbHelper;
import com.example.android.popmoviesstage2.data.MovieProvider;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.example.android.popmoviesstage2.Utility.getThumbnailUrlsFromDb;

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


/*

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
*/


/**
     * pull general movies data from TMDB and inserts it into empty movies table
     * NOTE: it will fail if the data already is in the table,
     * and zero insertions performed
     */

/*

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

*/


    /**
     * Test pulling favorite trailers keys from the SQLite database
     * through content provider.  Content provider uses raw query to
     * perform an INNER JOIN on movies and trailers tables
     */
    @Test
    public void pullFavoriteTrailersViaConentProviderTest(){
        Context context =  getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        ArrayList<String> trailerKeys = new ArrayList<>();


        Cursor cursor = contentResolver.query(DataContract.Trailers.buildFavoriteTrailersUri(),
                null,
                null,
                null,
                null
                );

        if(cursor!=null && cursor.moveToFirst()){
            do{
                trailerKeys.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Assert.assertTrue(trailerKeys.size()>0);

        for(String key : trailerKeys){
            System.out.println("_TKEY: " + key);
        }

    }

    @Test
    public void pullTrailerThumbnails(){
        ArrayList<String> links = Utility.getThumbnailUrlsFromDb(getTargetContext());

        Assert.assertNotNull(links);

        for(String link : links){
            System.out.println(link);
        }

    }


}
