package com.example.android.popmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data.MovieDbHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * contatins utility methods that make life easier
 */


public class Utility {


    // *********************                                    *********************
    // *********************  Data retrieval-related operations  *********************
    // *********************                                    *********************


    /**
     * This method requests movie data from TMDB
     * and inserts it into movies table.
     * It performs TMDB API calls,
     * one for post popular movies,
     * the second is for top rated.
     *
     * The purpose for 2 sequential api calls is to facilitate
     * data pre-fetching
     *
     * @param c Context
     * @return number of entries inserted
     */
    public static int pullMoviesAndBulkInsert(Context c, boolean firstrun) {
        final String LOG_TAG = "pullMoviesBulkInsert";

        int returnCount = 0;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);

        Uri moviesUri = DataContract.Movies.buildMoviesUri();

        ContentValues[] results = null;

        String rawJSON = null;

        //change firstrun setting to false on the first fun
        if(firstrun) {
            sharedPreferences.edit().putBoolean("firstrun", false).commit();
        }

        //pull movies by vote_average
        rawJSON = FetchData.getJsonData(FetchData.generateUrlByGivenPreference(c,
                c.getString(R.string.pref_setting_vote)));

        //convert movie data to ContentValue Array
        if (rawJSON != null) {
            results = FetchData.rawMoviesJsonDataToCvArray(rawJSON, c);
        }

        //insert movie data into database
        if (results != null && results.length > 0) {
            ContentResolver contentResolver = c.getContentResolver();
            returnCount = contentResolver.bulkInsert(moviesUri, results);
        }

        //pull movies by popularity
        rawJSON = FetchData.getJsonData(FetchData.generateUrlByGivenPreference(c,
                c.getString(R.string.pref_setting_popularity)));

        //convert movie data to ContentValue Array
        if (rawJSON != null) {
            results = FetchData.rawMoviesJsonDataToCvArray(rawJSON, c);
        }

        //insert movie data into database
        if (results != null && results.length > 0) {
            ContentResolver contentResolver = c.getContentResolver();
            returnCount = contentResolver.bulkInsert(moviesUri, results);
        }

        c.getContentResolver().notifyChange(DataContract.Movies.buildMoviesUri(), null, false);

        return returnCount;

    }


    /**
     * This method pulls details data which includes
     * movie runtime, trailers, and reviews;
     * then performs the following:
     * 1) inserts reviews in a review table
     * 2) inserts trailers in a trailer table
     * 3) updates runtime column of the movies table
     *
     * @param context
     */
    public static void pullDetailsDataAndBulkInsert(Context context) {

        int retValues = 0;

        String rawJSONDetails = null;

        ContentResolver cr = context.getContentResolver();

        HashMap<String, HashMap<String, ContentValues[]>> details;

        int reviewsInserted = 0;
        int trailersInserted = 0;
        int runtimesInserted = 0;


        ContentValues[] reviews, trailers, runtimes;

        ArrayList<String> movieIds = Utility.getMovieIdsFromDB(context);

        for (String movieId : movieIds) {
            rawJSONDetails = FetchData.fetchDetailsByMovieId(movieId, context);
            if (rawJSONDetails != null) {
                details = FetchData.parseRawDetails(movieId, rawJSONDetails, context);
                if (!details.isEmpty()) {
                    reviews = Utility.getReviewsCv(movieId, details, context);
                    trailers = Utility.getTrailersCV(movieId, details, context);
                    runtimes = Utility.getRuntimeCv(movieId, details, context);
                    if (reviews != null && reviews.length > 0) {
                        reviewsInserted = cr.bulkInsert(DataContract.Reviews.buildReviewsByMovieIdUri(Long.parseLong(movieId)),
                                reviews);

                    }
                    if (trailers != null && trailers.length > 0) {
                        trailersInserted = cr.bulkInsert(DataContract.
                                        Trailers.buildTrailersByMovieIdUri(Long.parseLong(movieId)),
                                trailers);

                    }
                    //Note ContentValues[] stores only 1 runtime key-value pair
                    if (runtimes != null && runtimes.length > 0) {

                        runtimesInserted = cr.update(DataContract.Movies.buildRuntimeUriByMovieId(Long.parseLong(movieId)),
                                runtimes[0],
                                DataContract.Movies._ID + "=?",
                                new String[]{movieId}
                        );

                    }
                }
            }
        }


    }


    /**
     * this method takes a uri generated by
     * Database.Movies.buildToggleFavoritesUri()
     * extracts toggle flag
     * and returns ContentValues with toggle flag set
     * and ready to be used by update query
     *
     * @param uri
     * @return
     */
    public static ContentValues getCvFromFavoriteToggleUri(Uri uri) {
        String movieId = uri.getLastPathSegment();
        boolean boolFlag = uri.getBooleanQueryParameter(DataContract.KEY_FLAG, false);
        int intFlag = 0;
        if (boolFlag) {
            intFlag = 1;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.Movies.COL_FAVORITE, intFlag);

        return contentValues;
    }


    /**
     * parses the url in String form and returns movieId from it
     *
     * @param url in string form
     * @return movie ID as a String, or null on error
     */
    public static String getMovieIdFromDetailUrl(String url) {
        final String LOG_TAG = "getMovieIdFromUrl: ";

        if (url == null) {
            Log.d(LOG_TAG, "null url passed to method");
            return null;
        }

        String movieID = null;

        String[] segments = splitUrl(url);

        int i = 0;

        for (String segment : segments) {
            System.out.println(String.valueOf(i) + ": segment: " + segment);
            i++;
        }

        if (segments[5] != null) {
            String[] query = segments[5].split("\\?");

            //verify movie id by checking if
            //it contains all numbers
            if (query[0].matches("[0-9]+")) {
                movieID = query[0];
            }

        }

        return movieID;
    }


    /**
     * Convenience method get movie Ids from the
     * movies table and returns them in an ArrayList<String>
     *
     * @param c Context
     * @return ArrayList<String> of movieIds retrieved
     * from the movies table
     */
    public static ArrayList<String> getMovieIdsFromDB(Context c) {
        Cursor cursor;
        ArrayList<String> movieIds = new ArrayList<String>();

        MovieDbHelper dbHelper = new MovieDbHelper(c);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (db.isOpen()) {
            cursor = db.query(DataContract.TABLE_MOVIES,
                    new String[]{DataContract.Movies._ID},
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor.moveToFirst()) {
                do {
                    movieIds.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return movieIds;

    }


    /**
     * This method queries the movies table for all movie
     * poster URLs anr returns them in ArrayList<String> format
     *
     * @param context
     * @return list of poster URLs in ArrayList<String> format
     */
    public static ArrayList<String> getPosterUrlsFromDb(Context context) {
        final String LOG_TAG = "_getImageUrlFromDb: ";

        ArrayList<String> urlList = new ArrayList<String>();

        String imageUrl = null;

        SQLiteOpenHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db != null) {
            Cursor cursor = db.query(
                    DataContract.TABLE_MOVIES,
                    new String[]{DataContract.Movies.COL_POSTER_PATH},
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                do {
                    imageUrl = cursor.getString(0);
                    urlList.add(imageUrl);
                    Log.v(LOG_TAG, "_path: " + imageUrl);
                } while (cursor.moveToNext());
                cursor.close();

            } else {
                Log.v(LOG_TAG, "returned cursor is null");
            }
        } else {
            Log.v(LOG_TAG, "returned database is null");
        }

        db.close();

        return urlList;
    }


    public static ArrayList<String> getThumbnailUrlsFromDb(Context context){
        final String LOG_TAG = "_getThumbnailUrlFromDb: ";

        ArrayList<String> urlList = new ArrayList<String>();
        String youtubeVideoId = null;

        String fileSuffix = context.getString(R.string.youtube_thumbnail_default_file_suffix);

        Uri.Builder utiBuilder = new Uri.Builder();

        SQLiteOpenHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        if (db != null) {
            Cursor cursor = db.query(
                    DataContract.TABLE_TRAILERS,
                    new String[]{DataContract.Trailers.COL_KEY},
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                do {
                    youtubeVideoId = cursor.getString(0);
                    urlList.add(DataContract.Trailers.buildYoutubeThumbnailUrl(context, youtubeVideoId, fileSuffix).toString());
                    //Log.v(LOG_TAG, "_path: " + thumbnailUrl);
                } while (cursor.moveToNext());
                cursor.close();

            } else {
                Log.v(LOG_TAG, "returned cursor is null");
            }
        } else {
            Log.v(LOG_TAG, "returned database is null");
        }

        db.close();


        return urlList;

    }


    // *********************                             *********************
    // *********************  Data conversion operations  *********************
    // *********************                             *********************


    //Truncates date string, returned by the API call
    //to 4 characters (ex. 2000);
    public static String truncateDate(String date) {
        if (date.length() > 0 || !(date.length() < 4)) {
            String retString = date.substring(0, 4);
            return retString;
        } else {
            return null;
        }
    }


    /**
     * splits url into segments using "/" delimeter
     *
     * @param url
     * @return
     */
    public static String[] splitUrl(String url) {
        final String LOG_TAG = "splitUrl: ";

        if (url == null) {
            Log.d(LOG_TAG, "null url passed to method");
            return null;
        }

        String decodedUrl = null;

        try {
            decodedUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException uee) {
            Log.d(LOG_TAG, uee.toString());
            uee.printStackTrace();
        }

        String[] segments = decodedUrl.split("/");

        return segments;

    }

    public static String getThumbnailSaveName(String url){
        if(url != null){
            String[] segments = splitUrl(url);

            if(segments.length>2) {
                String filename = segments[segments.length - 2];
                if (filename != null){
                    return filename+ "_" + segments[segments.length-1];
                }
            }
        }
        return null;
    }



    /**
     * this methods extracts ConventValues array with trailer information
     * out of parsed JSON results
     *
     * @param movieId Movied ID in String form for which to get ContentValues[]
     * @param map     JSON Parsed results returned by FetchData.parseRawDetails()
     * @param c       Context
     * @return ContentValues[] with trailer information ready to be
     * inserted into the database
     */
    // HashMap<String, HashMap<String, ContentValues[]>>
    public static ContentValues[] getTrailersCV(String movieId,
                                                HashMap<String, HashMap<String, ContentValues[]>> map,
                                                Context c) {

        final String LOG_TAG = "_getTrailersCV";

        ContentValues[] trailerCvArray = null;

        //ContentValues[] trailerCvArray = null;
        String trailerKey = c.getString(R.string.json_details_trailers_videos);

        //validate movie ID
        if (movieId.matches("[0-9]+")) {
            if (map != null && !map.isEmpty()) {


                HashMap<String, ContentValues[]> values = map.get(movieId);


                //make sure that the hash map is not empty
                if (!values.isEmpty()) {

                    //get the ContentVallue[] containing trailers
                    if (values.containsKey(trailerKey)) {
                        trailerCvArray = values.get(trailerKey);
                        if (trailerCvArray == null || trailerCvArray.length < 1) {
                            Log.d(LOG_TAG, "Trailer ContentValues[] is null, or length < 1");
                            return null;
                        }
                    } else {
                        Log.v(LOG_TAG, "map does not contain a trailer key");
                        Log.v(LOG_TAG, String.valueOf(values.size()));
                        Log.v(LOG_TAG, values.keySet().toString());
                        Log.v(LOG_TAG, values.values().toString());


                    }
                }

            } else {
                Log.d(LOG_TAG, "HashMap<String, HashMap<String, ContentValues[]>> map is null or empty");
            }

        } else {
            Log.d(LOG_TAG, "invalid Movie Id");
        }

        return trailerCvArray;

    }


    /**
     * this methods extracts ConventValues array with reviews information
     * out of parsed JSON results
     *
     * @param movieId Movied ID in String form for which to get ContentValues[]
     * @param map     JSON Parsed results returned by FetchData.parseRawDetails()
     * @param c
     * @return
     */
    public static ContentValues[] getReviewsCv(String movieId, HashMap<String, HashMap<String, ContentValues[]>> map, Context c) {

        final String reviewsKey = c.getString(R.string.json_details_reviews_reviews);

        ContentValues[] reviewsCvArray = null;

        //validate movie ID
        if (movieId.matches("[0-9]+")) {
            if (map != null && !map.isEmpty()) {

                HashMap<String, ContentValues[]> values = map.get(movieId);

                //make sure that the hash map is not empty
                if (!values.isEmpty()) {

                    //get the ContentVallue[] containing trailers
                    if (values.containsKey(reviewsKey)) {
                        reviewsCvArray = values.get(reviewsKey);
                        if (reviewsCvArray == null || reviewsCvArray.length < 1) {
                            return null;
                        }
                    }
                }

            }

        }

        return reviewsCvArray;

    }


    /**
     * this method extracts runtime information for a given movie ID
     *
     * @param movieId Movied ID in String form for which to get ContentValues[]
     * @param map     JSON Parsed results returned by FetchData.parseRawDetails()
     * @param c
     * @return
     */
    public static ContentValues[] getRuntimeCv(String movieId, HashMap<String, HashMap<String, ContentValues[]>> map, Context c) {

        final String runtimesKey = c.getString(R.string.json_details_key_runtime);

        ContentValues[] runtimesCvArray = null;

        //validate movie ID
        if (movieId.matches("[0-9]+")) {
            if (map != null && !map.isEmpty()) {

                HashMap<String, ContentValues[]> values = map.get(movieId);

                //make sure that the hash map is not empty
                if (!values.isEmpty()) {

                    //get the ContentVallue[] containing trailers
                    if (values.containsKey(runtimesKey)) {
                        runtimesCvArray = values.get(runtimesKey);
                        if (runtimesCvArray == null || runtimesCvArray.length < 1) {
                            return null;
                        }
                    }
                }

            }

        }

        return runtimesCvArray;

    }


    /**
     * Takes a TMDB movie trailer key in String form and generates
     * a YouTube URL pointing to the trailer
     *
     * @param relativeUrl TMDB key tag in String form
     * @return YouTube url pointing to the trailer
     */
    public static String getTrailerYoutubeLink(String relativeUrl, Context c) {
        Context context = c;
        if (relativeUrl.contains("null")) {
            return null;
        } else if (relativeUrl == null) {
            return null;
        } else {
            Uri.Builder builder = new Uri.Builder();
            builder.path(context.getString(R.string.youtube_base_url));
            builder.appendQueryParameter("v", relativeUrl);
            String trailer = Uri.decode(builder.build().toString());
            return trailer;

        }
    }


    // *********************                                         *********************
    // *********************  Data image file management operations  *********************
    // *********************                                         *********************


    /**
     * this method gets a list of all JPG in files
     * directory of the app and returns JPG file names
     * in the ArrayList<String> format
     *
     * @param context
     * @return ArrayList<String> of JPG file names
     */
    public static ArrayList<String> listAllJpgs(Context context) {
        final String LOG_TAG = "listAllJpgs";

        File file = context.getFilesDir();

        ArrayList<String> returnList = new ArrayList<String>();

        String[] fileList = file.list();


        if (fileList.length > 0) {
            for (String filename : fileList) {
                if (filename.matches(".*\\.jpg")) {
                    returnList.add(filename);
                }
            }

        }

        return returnList;

    }


    /**
     * this method deletes jpg poster files for all movies
     * NOT marked as favorites
     *
     * @param context
     * @return return number of items deleted
     */
    public static int deleteNonFavoriteJPGsAndMovieRecords(Context context) {
        final String LOG_TAG = "deleteNonFavJPGs: ";

        String thumbnailSuffix = "_" + context.getString(R.string.youtube_thumbnail_default_file_suffix);

        File filesDir = context.getFilesDir();

        int itemsDeleted = 0;

        ArrayList<String> allJpgs = listAllJpgs(context);
        ArrayList<String> favoriteMoviePostersJpgs = new ArrayList<String>();
        ArrayList<String> favoriteTrailerJpgs = new ArrayList<String>();

        //TODO get favorite poster Jpgs

        Uri favoriteMoviesUri = DataContract.Movies.buildFavoritesUri();
        Uri hitListUri = DataContract.buildNotFavoritesUri();

        if (!allJpgs.isEmpty()) {

            Cursor cursor = context.getContentResolver().query(
                    favoriteMoviesUri,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor!=null && cursor.moveToFirst()) {
                do {
                    favoriteMoviePostersJpgs.add((Uri.parse(cursor.getString(DataContract.Movies.COL_POSTER_PATH_INDEX)).getLastPathSegment()));
                } while (cursor.moveToNext());

            }  //end of if(cursor.moveToFirst());

            if(cursor!=null) {
                cursor.close();
            }
                //TODO add query for favorite trailers

                Cursor trailerCursor = context.getContentResolver().query(
                        DataContract.Trailers.buildFavoriteTrailersUri(),
                        null,
                        null,
                        null,
                        null);


            if(trailerCursor!=null && trailerCursor.moveToFirst()){
                do {
                    favoriteTrailerJpgs.add(trailerCursor.getString(0)+thumbnailSuffix);
                }
                while (trailerCursor.moveToNext());
            }

            if(trailerCursor!=null){
                trailerCursor.close();
            }


                if (!favoriteMoviePostersJpgs.isEmpty()) {
                    allJpgs.removeAll(favoriteMoviePostersJpgs);
                    allJpgs.removeAll(favoriteTrailerJpgs);
                }




            else {
                Log.v(LOG_TAG, "_cursor is null");
            }

            if (!allJpgs.isEmpty()) {
                for (String hitName : allJpgs) {
                    context.deleteFile(hitName);
                    itemsDeleted++;
                }

            }

            int dbHitCount = context.getContentResolver().delete(
                    hitListUri,
                    null,
                    null
            );


        }  // end of if(!allJpgs.isEmpty())


        return itemsDeleted;


    }


}