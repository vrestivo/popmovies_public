package com.example.android.popmoviesstage2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;

import com.example.android.popmoviesstage2.Utility;

import java.util.ArrayList;

/**
 * Content Provider for the app
 */

public class MovieProvider extends ContentProvider {

    private Context mContext;
    //private MovieDbHelper mMovieDBHelper;
    //private SQLiteDatabase mDatabase;


    //URI matching constants
    //NOTE:  matching constants must be declared as final
    //otherwize case lause in match(uri) will cause an error

    //this match will pull all movies entries
    public final static int MOVIES = 100;

    //this match will pull a specific movie record
    public final static int MOVIE_RECORD = 101;

    //this will pull movie ids for data fetching
    public final static int MOVIE_IDS = 102;

    //multiple runtime values in movies table
    public final static int MOVIE_RUNTIMES = 103;

    //sinle runtime values in movies table
    public final static int MOVIE_RUNTIME = 104;

    //this match will pull all favorites
    public final static int MOVIES_FAVORITES = 200;

    //this match will pull specific farorite record
    public final static int MOVIE_FAV_RECORD = 201;

    //this match will pull specific farorite record
    public final static int MOVIE_TOGGLE_FAV = 202;

    //this will delete all but favorites
    public final static int DELETE_ALL_BUT_FAV = 205;

    //this match will pull all trailers entries
    public final static int TRAILERS = 300;

    //this match will pull all trailers entries
    //for a specific movie it
    public final static int TRAILERS_BY_MOVIE_ID = 301;

    //this match will pull all review entries
    public final static int REVIEWS = 400;

    //this match will pull all review entries
    //for a specific movie it
    public final static int REVIEWS_BY_MOVIE_ID = 401;

    //thiw match will delete everything and pull fresh data
    //from the database
    public final static int THE_CLEAN_SLATE_PROCOLOL = 500;

    private static final UriMatcher mMatcher = buildUriMatcher();


    static UriMatcher buildUriMatcher() {
        final String authority = DataContract.CONTENT_AUTHORITY;
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(authority, DataContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, DataContract.PATH_MOVIE_IDS, MOVIE_IDS);
        matcher.addURI(authority, DataContract.PATH_MOVIES + "/#", MOVIE_RECORD);
        matcher.addURI(authority, DataContract.PATH_FAVORITES, MOVIES_FAVORITES);
        matcher.addURI(authority, DataContract.PATH_FAVORITES + "/#", MOVIE_FAV_RECORD);
        matcher.addURI(authority, DataContract.PATH_NOT_FAVORITES, DELETE_ALL_BUT_FAV);
        matcher.addURI(authority, DataContract.PATH_TOGGLE_FAVORITES + "/#", MOVIE_TOGGLE_FAV);
        matcher.addURI(authority, DataContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, DataContract.PATH_TRAILERS + "/#", TRAILERS_BY_MOVIE_ID);
        matcher.addURI(authority, DataContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, DataContract.PATH_REVIEWS + "/#", REVIEWS_BY_MOVIE_ID);
        matcher.addURI(authority, DataContract.PATH_MOVIE_RUNTIME, MOVIE_RUNTIMES);
        matcher.addURI(authority, DataContract.PATH_MOVIE_RUNTIME + "/#", MOVIE_RUNTIME);
        matcher.addURI(authority, DataContract.PATH_CLEAN_SLATE_PROTOCOL, THE_CLEAN_SLATE_PROCOLOL);

        return matcher;
    }

    @Override
    public boolean onCreate() {

        mContext = getContext();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);

        Cursor retValues = null;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //initializa databse if has not been done already
        if (db != null) {


            int match = mMatcher.match(uri);

            switch (match) {
                case MOVIES: {
                    retValues = db.query(DataContract.TABLE_MOVIES,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder
                    );
                    break;
                }
                case MOVIE_RECORD: {
                    long rowId = DataContract.Movies.getMovieIdFromUri(uri);
                    retValues = db.query(DataContract.TABLE_MOVIES,
                            DataContract.Movies.defaultProjection,
                            DataContract.Movies._ID + "=?",
                            new String[]{String.valueOf(rowId)},
                            null,
                            null,
                            sortOrder
                    );
                    break;
                }
                //return Cuursor with valid Movie IDs
                case MOVIE_IDS: {
                    retValues = db.query(DataContract.TABLE_MOVIES,
                            new String[]{DataContract.Movies._ID},
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                    break;
                }
                case MOVIE_TOGGLE_FAV: {
                    String movieID = uri.getLastPathSegment();
                    ContentValues cvWithFavFlag = Utility.getCvFromFavoriteToggleUri(uri);
                    int updates = db.update(DataContract.TABLE_MOVIES,
                            cvWithFavFlag,
                            DataContract.Movies._ID + "=?",
                            new String[]{movieID}
                    );

                    retValues = db.query(DataContract.TABLE_MOVIES,
                            DataContract.Movies.defaultProjection,
                            DataContract.Movies._ID + "=?",
                            new String[]{movieID},
                            null,
                            null,
                            sortOrder
                    );

                    break;
                }
                case TRAILERS_BY_MOVIE_ID: {
                    String movieId = uri.getLastPathSegment();
                    retValues = db.query(DataContract.TABLE_TRAILERS,
                            DataContract.Trailers.defaultTrailerProjection,
                            DataContract.Trailers.COL_MOVIE_ID +"=?",
                            new String[]{movieId},
                            null,
                            null,
                            null
                    );

                    break;
                }

                case REVIEWS_BY_MOVIE_ID: {
                    String movieId = uri.getLastPathSegment();
                    retValues = db.query(DataContract.TABLE_REVIEWS,
                            DataContract.Reviews.defaultReviewsProjection,
                            DataContract.Trailers.COL_MOVIE_ID +"=?",
                            new String[]{movieId},
                            null,
                            null,
                            null
                    );

                    break;
                }

                case MOVIES_FAVORITES: {
                    retValues = db.query(DataContract.TABLE_MOVIES,
                            DataContract.Movies.defaultProjection,
                            DataContract.Movies.COL_FAVORITE +">0",
                            null,
                            null,
                            null,
                            null
                    );

                    break;
                }

                default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);

            } // end of switch statement

        } // end of if(db ! = null)

        return retValues;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {

        int match = mMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return DataContract.CONTENT_TYPE_DIR;
            case MOVIE_IDS:
                return DataContract.CONTENT_TYPE_DIR;
            case MOVIES_FAVORITES:
                return DataContract.CONTENT_TYPE_DIR;
            case MOVIE_RECORD:
                return DataContract.CONTENT_DYPE_ITEM;
            case MOVIE_FAV_RECORD:
                return DataContract.CONTENT_DYPE_ITEM;
            case TRAILERS:
                return DataContract.CONTENT_TYPE_DIR;
            case TRAILERS_BY_MOVIE_ID:
                return DataContract.CONTENT_TYPE_DIR;
            case REVIEWS:
                return DataContract.CONTENT_TYPE_DIR;
            case REVIEWS_BY_MOVIE_ID:
                return DataContract.CONTENT_TYPE_DIR;
            case MOVIE_RUNTIMES:
                return DataContract.CONTENT_TYPE_DIR;
            case MOVIE_RUNTIME:
                return DataContract.CONTENT_DYPE_ITEM;
            case DELETE_ALL_BUT_FAV:
                return DataContract.CONTENT_TYPE_DIR;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = null;
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //initialize database if has not been done so already
        if (db != null) {

            int match = mMatcher.match(uri);

            switch (match) {
                case MOVIES: {
                    long returnId = db.insert(DataContract.TABLE_MOVIES,
                            null, values);
                    if (returnId > 0) {
                        returnUri = DataContract.Movies.buildMovieWithIdUri(returnId);
                    } else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);


            }

            getContext().getContentResolver().notifyChange(uri, null, false);

        } //end of if(db != null)

        db.close();
        return returnUri;
    }


    /**
     * This method performs a bulk insert into SqliteDatabase,
     * or returns 0 if nothing was inserted
     *
     * @param uri    uri to matlch database operation
     * @param values ContentValues[] to be inserted
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int returnCount = 0;


        if (db != null) {

            final int match = mMatcher.match(uri);


            switch (match) {
                case MOVIES: {
                    returnCount = insertInBulk(db, values, DataContract.TABLE_MOVIES);
                    break;
                }

                case TRAILERS_BY_MOVIE_ID: {
                    returnCount = insertInBulkOnConflictReplace(db, values, DataContract.TABLE_TRAILERS);

                    break;
                }
                case REVIEWS_BY_MOVIE_ID: {
                    returnCount = insertInBulkOnConflictReplace(db, values, DataContract.TABLE_REVIEWS);
                    break;
                }
                case MOVIE_RUNTIME: {
                    returnCount = insertInBulkOnConflictReplace(db, values, DataContract.TABLE_MOVIES);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null, false);
        } //end of if(db != null)

        db.close();

        return returnCount;

    }


    /**
     * this method performs delete operations on the
     * database
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final String LOG_TAG = "_MovieProvider";
        Context context = getContext();
        int returnCount = 0;

        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        if (db != null) {

            int match = mMatcher.match(uri);

            Log.v(LOG_TAG + " match: ", "constant: " + String.valueOf(DELETE_ALL_BUT_FAV));


            switch (match) {
                //this will delete all rows in all tables
                case THE_CLEAN_SLATE_PROCOLOL: {
                    returnCount = db.delete(DataContract.TABLE_REVIEWS, null, null);
                    returnCount += db.delete(DataContract.TABLE_TRAILERS, null, null);
                    returnCount += db.delete(DataContract.TABLE_MOVIES, null, null);

                    break;

                }

                //delete all entries not marked as favorites in the movies table
                case DELETE_ALL_BUT_FAV: {
                    ArrayList<String> hitList = getANonFavoriteHitList(db,
                            DataContract.TABLE_MOVIES,
                            context);

                    if (hitList.size() > 0) {

                        //delete entries from the reviews table
                        returnCount += deleteInBulkbyMovieId(db, DataContract.TABLE_REVIEWS,
                                DataContract.Reviews.COL_MOVIE_ID,
                                hitList,
                                context);

                        //deete entries from the trailers table
                        returnCount += deleteInBulkbyMovieId(db, DataContract.TABLE_TRAILERS,
                                DataContract.Trailers.COL_MOVIE_ID,
                                hitList,
                                context);

                        //due to specified database contstrains entries
                        //in movies table have to be deleted last
                        returnCount += deleteInBulkbyMovieId(db, DataContract.TABLE_MOVIES,
                                DataContract.Reviews._ID,
                                hitList,
                                context);
                    }

                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);

            } //end of switch(match)

        } // end of if(db!=null)

        db.close();
        return returnCount;
    }



    /**
     * this method updates runtime and favorite column values
     * in the movies table
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnCount = 0;

        if (db != null) {

            final int match = mMatcher.match(uri);

            switch (match) {
                case MOVIE_RUNTIME: {
                    String movieId = String.valueOf(DataContract.Movies.getMovieIdFromUri(uri));
                    returnCount = db.update(DataContract.TABLE_MOVIES,
                            values,
                            selection,
                            selectionArgs
                    );

                    break;
                }
                case MOVIE_TOGGLE_FAV: {
                    returnCount = db.update(
                            DataContract.TABLE_MOVIES,
                            values,
                            selection,
                            selectionArgs
                    );

                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null, false);

        }

        db.close();
        return returnCount;

    }



    /**
     * bulk insert implementation
     *
     * @param db        database to call insert on
     * @param values    ContentValues[] to insert
     * @param tableName table name as a string
     * @return number of values inserted
     */
    private int insertInBulk(SQLiteDatabase db, ContentValues[] values, String tableName) {
        int returnCount = 0;

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(tableName,
                        null,
                        value
                );
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
     * bulk insert implementation with "ON CONFLICT REPLACE"
     * resolution strategy
     *
     * @param db        database to call insert on
     * @param values    ContentValues[] to insert
     * @param tableName table name as a string
     * @return number of values inserted
     */
    private int insertInBulkOnConflictReplace(SQLiteDatabase db, ContentValues[] values, String tableName) {
        int returnCount = 0;

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insertWithOnConflict(tableName,
                        null,
                        value,
                        SQLiteDatabase.CONFLICT_REPLACE);
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
     * This method takes database, table name, target column name,
     * a ArrayList<String> of movie Ids, and context.
     * For every item in the ArrayList it deletes row, where
     * a target column matches the ArrayList item.
     *
     * @param db         target database
     * @param table      target table
     * @param columnName target column for matching
     * @param hitList    target list of movie IDs
     * @param context    context
     * @return
     */
    private int deleteInBulkbyMovieId(SQLiteDatabase db,
                                      String table,
                                      String columnName,
                                      ArrayList<String> hitList,
                                      Context context
    ) {
        int returnCount = 0;

        if (db != null && !hitList.isEmpty()
                && !table.isEmpty() && !columnName.isEmpty()) {

            db.beginTransaction();
            try {

                for (String item : hitList) {
                    returnCount += db.delete(
                            table,
                            columnName + "=" + item,
                            null
                    );
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        return returnCount;
    }

    /**
     * This method searches for database entries which
     * are not marked as favorites and returns them
     * as an ArrayList<String>
     *
     * @param db      database to search
     * @param table   table name to search
     * @param context
     * @return ArrayList<String> of movie Ids of movies
     * not marked as favorites
     */
    private ArrayList<String> getANonFavoriteHitList(SQLiteDatabase db, String table, Context context) {

        if (db == null) {
            return null;
        }

        ArrayList<String> hitList = new ArrayList<String>();

        Cursor cursor = db.query(table,
                new String[]{DataContract.Movies._ID},
                DataContract.Movies.COL_FAVORITE + "<1",
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                hitList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return hitList;

    }

}
