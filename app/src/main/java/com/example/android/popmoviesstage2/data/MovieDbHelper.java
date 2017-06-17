package com.example.android.popmoviesstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.ContactsContract;
import android.util.Log;

/**
 *
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    //database version
    private static final int DB_VERSION = 1;

    //database name
    public static final String DB_NAME = "movies.db";

    private static final String LOG_TAG = "_MovieDbHelper: ";


    /**
     * Creates an instance of SqliteOpenHelper
     * @param context
     * calls superclass constructor, passig context
     * database name, and database version, defined in database contract.
     */
    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //SQLite create table statement
        final String CREATE_MOVIES_TABLE =
                "CREATE TABLE " + DataContract.TABLE_MOVIES + " ("
                        + DataContract.Movies._ID + " INTEGER PRIMARY KEY NOT NULL,"
                        + DataContract.Movies.COL_TITLE + " TEXT NOT NULL, "
                        + DataContract.Movies.COL_REL_DATE + " TEXT NOT NULL, "
                        + DataContract.Movies.COL_POPULARITY + " REAL NOT NULL, "
                        + DataContract.Movies.COL_VOTE_AVG + " REAL NOT NULL, "
                        + DataContract.Movies.COL_POSTER_PATH + " TEXT NOT NULL, "
                        + DataContract.Movies.COL_OVERVIEW + " TEXT NOT NULL, "
                        + DataContract.Movies.COL_FAVORITE + " BOOLEAN DEFAULT 0, "
                        + DataContract.Movies.COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + DataContract.Movies.COL_RUNTIME + " INTEGER, "
                        + " UNIQUE (" + DataContract.Movies._ID + ") "
                        + "ON CONFLICT REPLACE"
                        + ");";




        final String CREATE_TRAILERS_TABLE =
                "CREATE TABLE "+ DataContract.TABLE_TRAILERS + " ("
                        + DataContract.Trailers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + DataContract.Trailers.COL_MOVIE_ID + " INTEGER NOT NULL, "
                        + DataContract.Trailers.COL_KEY + " TEXT NOT NULL, "
                        + " FOREIGN KEY (" + DataContract.Trailers.COL_MOVIE_ID + ") REFERENCES "
                        + DataContract.TABLE_MOVIES + "(" + DataContract.Movies._ID + ")"
                        + " UNIQUE (" + DataContract.Trailers.COL_KEY + ") "
                        + "ON CONFLICT REPLACE"
                        + ");";


        final String CREATE_REVIEWS_TABLE =
                "CREATE TABLE "+ DataContract.TABLE_REVIEWS + " ("
                        + DataContract.Reviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + DataContract.Reviews.COL_MOVIE_ID + " INTEGER NOT NULL, "
                        + DataContract.Reviews.COL_CONTENT + " TEXT NOT NULL, "
                        + " FOREIGN KEY (" + DataContract.Reviews.COL_MOVIE_ID + ") REFERENCES "
                        + DataContract.TABLE_MOVIES + "(" + DataContract.Movies._ID + ")"
                        + " UNIQUE (" + DataContract.Reviews.COL_CONTENT + ") "
                        + "ON CONFLICT REPLACE"
                        + ");";



        db.execSQL(CREATE_MOVIES_TABLE);
        db.execSQL(CREATE_TRAILERS_TABLE);
        db.execSQL(CREATE_REVIEWS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop old version of the database if exists
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.TABLE_TRAILERS);
        onCreate(db);
    }


}
