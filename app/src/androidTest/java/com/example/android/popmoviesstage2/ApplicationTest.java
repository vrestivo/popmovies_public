package com.example.android.popmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data.MovieDbHelper;

import junit.framework.Assert;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(JUnit4.class)
public class ApplicationTest {

    private static final String LOG_TAG = "_testing: ";
    Context targetContext;
    SQLiteDatabase db;
    Cursor cursor;
    ContentValues testValues;


    public SQLiteDatabase createDb(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);

        return dbHelper.getWritableDatabase();
    }


    @After
    public void unitCleanup() {
        //getTargetContext().deleteDatabase(MovieDbHelper.DB_NAME);
    }

    @AfterClass
    public static void classCleamup() {
        //getTargetContext().deleteDatabase(MovieDbHelper.DB_NAME);
    }

    /**
     * Generates set of test valued to be inserted during the database
     *
     * @return generated ContentValues with test com.example.android.popmoviesstage2.data
     */
    public static ContentValues createTestData() {
        ContentValues testData = new ContentValues();
        testData.put(DataContract.Movies._ID, 284052);
        testData.put(DataContract.Movies.COL_TITLE, "Doctor Strange");
        testData.put(DataContract.Movies.COL_REL_DATE, "2016-10-25");
        testData.put(DataContract.Movies.COL_POPULARITY, 59.903455);
        testData.put(DataContract.Movies.COL_VOTE_AVG, 6.89);
        testData.put(DataContract.Movies.COL_POSTER_PATH, "/xfWac8MTYDxujaxgPVcRD9yZaul.jpg");
        testData.put(DataContract.Movies.COL_OVERVIEW,
                "After his career is destroyed, a brilliant but arrogant surgeon gets a new lease on life when a sorcerer takes him under his wing and trains him to defend the world against evil.");

        return testData;
    }

  /**
     * Takes error message, original ContentValues passed to the database
     * and a cursor, returned by the database query.  Checks column names
     * and values in the cursor against the originals supplied to ContentValues.
     *
     * @param resultCursor  cursor returned by database query
     * @param contentValues original ContentValues written to database
     */

    public void validateDataEntry(Cursor resultCursor, ContentValues contentValues) {
        //Test for valid cursor
        boolean validCursor = resultCursor.moveToFirst();
        Log.v(LOG_TAG, String.valueOf(validCursor));
        Assert.assertTrue("Empty cursor!", validCursor);

        //extract values from the original ContentValues
        //to a Set of String, Object key-values
        Set<Map.Entry<String, Object>> valueSet = contentValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int columnIndex = resultCursor.getColumnIndex(columnName);
            //Check if the column names match
            Assert.assertEquals("Column " + columnName + " doesn't match!",
                    columnName, resultCursor.getColumnName(columnIndex));

            //Check if values match
            Log.v(LOG_TAG, entry.getValue().toString() + " : " + resultCursor.getString(columnIndex));
        }
    }

    public long insertValues(String tableName, ContentValues values, SQLiteDatabase db) {
        return db.insert(tableName, null, values);
    }


/*

    @Test
    public void downloadAndSaveToDbTest() {
        String LOG_TAG = "_download_test: ";


        targetContext = getTargetContext();
        //Context appContext = getContext();

;

        FetchData fd = new FetchData();

        String url = FetchData.generateUrlByGivenPreference(targetContext,
                targetContext.getString(R.string.pref_setting_vote));

        Log.v(LOG_TAG, url);



        ContentValues[] returnedData = FetchData.rawMoviesJsonDataToCvArray(FetchData.getJsonData(url), targetContext);


        SQLiteOpenHelper dbHelper = new MovieDbHelper(targetContext);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (returnedData.length > 0) {
            db.beginTransaction();
            try {
                for (ContentValues values : returnedData) {
                    db.insert(DataContract.TABLE_MOVIES, null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
                db.close();
            }
        }

    }



    @Test
    public void sharedPrefsTest(){

        String LOG_TAG = "Shared_Prefs test:";
        Context context = getTargetContext();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(R.string.pref_sort_key);


        String sortPref = preferences.getString(prefKey, "_no_default");
        Log.v(LOG_TAG, sortPref);



    }
*/


    @Test
    public void shnazzyDbTest() {

        //get test context
        targetContext = getTargetContext();

        //get a database helper
        SQLiteOpenHelper dbHelper = new MovieDbHelper(targetContext);

        //get a writable database
        db = dbHelper.getWritableDatabase();

        //create test com.example.android.popmoviesstage2.data
        testValues = createTestData();

        //insert test values into the database
        long rowId = db.insert(DataContract.TABLE_MOVIES, null, testValues);
        Log.v(LOG_TAG, String.valueOf(rowId));

        cursor = db.query(
                DataContract.TABLE_MOVIES,
                null, //select all columns
                null, //no selection (No WHERE clause)
                null, //no selection arguments, since no selection used
                null, //no grouping by
                null, //no having
                null //no sorting/ordering by..
        );

        validateDataEntry(cursor, testValues);

        //free the resources
        cursor.close();
        db.close();

    }


}