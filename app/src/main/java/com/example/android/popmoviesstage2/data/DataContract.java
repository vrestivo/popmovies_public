package com.example.android.popmoviesstage2.data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.popmoviesstage2.R;


/**
 * defines com.example.android.popmoviesstage2.data constants binding and helper methods
 */


public class DataContract {

    private static final String LOG_TAG = "DataContract: ";


    //database table name definitions
    public static final String TABLE_MOVIES = "movies";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String TABLE_TRAILERS = "trailers";

    //URI path strings
    public static final String CONTENT_AUTHORITY = "com.example.android.popmoviesstage2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_TRAILERS_FAV = "trailers_fav";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TOGGLE_FAVORITES = "toggle_favorites";
    public static final String PATH_NOT_FAVORITES = "not_favorites";
    public static final String PATH_MOVIE_IDS = "movieIDs";
    public static final String PATH_MOVIE_RUNTIME = "runtime";
    public static final String PATH_CLEAN_SLATE_PROTOCOL = "clean_slate";

    //key for url query encoding for toggling favorites
    //see buildToggleFavoritesUri()
    public static final String KEY_FLAG = "flag";

    //key for movie Id argument passed in a bundle
    //to the TrailerFragment and ReviewsFragment
    public static final String KEY_MOVIE_ID = "movieId";


    //content type definitions
    public static final String CONTENT_TYPE_DIR =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                    + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
    public static final String CONTENT_DYPE_ITEM =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                    + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


    //Raw Inner join query for favorite trailers
    //"select key from movies JOIN trailers on movies._id=trailers.movie_id where favorite=1;";
    public static final String RAW_FAVORITE_TRAILERS_INNER_JOIN =
    "SELECT " + Trailers.COL_KEY + " FROM " + TABLE_MOVIES +
            " JOIN " + TABLE_TRAILERS + " ON " +
            TABLE_MOVIES+"."+Movies._ID + "=" +TABLE_TRAILERS+"."+Trailers.COL_MOVIE_ID +
            " WHERE " + Movies.COL_FAVORITE +"=1;";


    public static final class Trailers implements BaseColumns {

        private static final String LOG_TAG = "Trailers: ";



        //column name definitions for the movie table
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_KEY = "key";

        public static final int COL_MOVIE_ID_INDEX = 1;
        public static final int COL_KEY_INDEX = 2;

        public static final String[] defaultTrailerProjection = {
                _ID,
                COL_MOVIE_ID,
                COL_KEY
        };


        /**
         * build URI for requesting a list of trailers
         *
         * @return resulting uri
         */
        public static Uri buildTrailersUri() {
            Uri traierUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_TRAILERS).build();
            return traierUri;
        }

        /**
         * build content URI for requesting a list of trailers
         * by movie ID
         *
         * @return resulting uri
         */
        public static Uri buildTrailersByMovieIdUri(long movieId) {
            Uri trailersUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_TRAILERS)
                    .appendPath(String.valueOf(movieId))
                    .build();
            return trailersUri;
        }


        /**
         * build content URI for requesting a list of trailers
         * for favorite movies
         * @return
         */
        public static Uri buildFavoriteTrailersUri(){
            Uri favoriteTrailersUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_TRAILERS_FAV)
                    .build();

            return favoriteTrailersUri;
        }


        /**
         *
         * @param context
         * @param youtubeId Youtube video ID in string form
         * @param pictureFileName file name to append to the end of the Uri
         *                        Ex: 0.jpg is a defaulf value for an higher resolution thumbnail
         * @return URL to the thumbnail
         */

        public static Uri buildYoutubeThumbnailUrl(Context context, String youtubeId, String pictureFileName){

            if(youtubeId!=null && pictureFileName!=null) {
                Uri.Builder builder = new Uri.Builder()
                        .encodedPath(context.getString(R.string.youtube_thumbnail_base_url))
                        .appendPath(youtubeId)
                        .appendPath(pictureFileName);

                return builder.build();
            }

            return null;

        }



    }

    public static final class Reviews implements BaseColumns {
        private static final String LOG_TAG = "Reviews: ";


        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_CONTENT = "reviews";

        public static final int COL_ID_INDEX = 0;
        public static final int COL_MOVIE_ID_INDEX = 1;
        public static final int COL_CONTENT_INDEX = 2;




        public static final String[] defaultReviewsProjection = {
            _ID,
            COL_MOVIE_ID,
            COL_CONTENT
        };


        /**
         * build URI for requesting a list of reviews
         *
         * @return resulting uri
         */
        public static Uri buildReviewsUri() {
            Uri reviewsUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_REVIEWS).build();
            return reviewsUri;
        }

        /**
         * build URI for requesting a list of reviews
         * by movie ID
         *
         * @return resulting uri
         */
        public static Uri buildReviewsByMovieIdUri(long movieId) {
            Uri reviewsUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_REVIEWS)
                    .appendPath(String.valueOf(movieId))
                    .build();
            return reviewsUri;
        }

    }


    public static final class Movies implements BaseColumns {

        //column name definitions for the movie table
        public static final String COL_TITLE = "title";
        public static final String COL_REL_DATE = "date";
        public static final String COL_POPULARITY = "popularity";
        public static final String COL_VOTE_AVG = "vote_avg";
        public static final String COL_POSTER_PATH = "poster_path";
        public static final String COL_OVERVIEW = "overview";
        public static final String COL_FAVORITE = "favorite";
        public static final String COL_TIMESTAMP = "timestamp";
        public static final String COL_RUNTIME = "runtime";


        //Columns indes constants for the movies table
        public static final int COL_ID_INDEX = 0;
        public static final int COL_TITLE_INDEX = 1;
        public static final int COL_REL_DATE_INDEX = 2;
        public static final int COL_POPULARITY_INDEX = 3;
        public static final int COL_VOTE_AVG_INDEX = 4;
        public static final int COL_POSTER_PATH_INDEX = 5;
        public static final int COL_OVERVIEW_INDEX = 6;
        public static final int COL_FAVORITES_INDEX = 7;
        public static final int COL_TIMESTAMP_INDEX = 8;
        public static final int COL_RUNTIME_INDEX = 9;


        //default projection for the movies table
        public static final String[] defaultProjection = {
                _ID,
                COL_TITLE,
                COL_REL_DATE,
                COL_POPULARITY,
                COL_VOTE_AVG,
                COL_POSTER_PATH,
                COL_OVERVIEW,
                COL_FAVORITE,
                COL_TIMESTAMP,
                COL_RUNTIME

        };


        /**
         * build URI for requesting a list of movies
         *
         * @return resulting uri
         */
        public static Uri buildMoviesUri() {
            Uri movieUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIES).build();
            return movieUri;
        }

        /**
         * builds URI for requesting a list of movie IDs
         *
         * @return returns resulting uri
         */
        public static Uri buildMovieIDsUri() {
            Uri movieIDsUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIE_IDS).build();
            return movieIDsUri;
        }

        /**
         * builds URI for requesting a specific movie by ID
         *
         * @return returns resulting uri
         */
        public static Uri buildMovieWithIdUri(long id) {
            Uri movieUri = buildMoviesUri().buildUpon()
                    .appendPath(String.valueOf(id)).build();
            return movieUri;
        }

        /**
         * builds URI for requesting a list of favorite movies
         *
         * @return returns resulting uri
         */
        public static Uri buildFavoritesUri() {
            Uri favorites = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_FAVORITES).build();
            return favorites;
        }

        /**
         * builds URI for requesting a single favorite movie
         *
         * @return returns resulting uri
         */
        public static Uri buildFavoritesWithIdUri(long id) {
            Uri favoritesWithID = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_FAVORITES)
                    .appendPath(String.valueOf(id)).build();
            return favoritesWithID;
        }

        /**
         * builds URI for inserting movies runtime into movies table
         *
         * @return returns resulting uri
         */
        public static Uri buildRuntimeUriByMovieId(long MovieId) {
            Uri favoritesWithID = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIE_RUNTIME)
                    .appendPath(String.valueOf(MovieId))
                    .build();
            return favoritesWithID;
        }


        /**
         * extracts a movie ID from a uri
         *
         * @return returns movie Id as a long
         */
        public static long getMovieIdFromUri(Uri uri) {
            if (uri == null) {
                throw new UnsupportedOperationException("Null Uri");
            }
            long returnValue = Long.parseLong((uri.getPathSegments().get(1)));

            return returnValue;
        }

        /**
         * builds URI for requesting a list of not favorite movies
         *
         * @return returns resulting uri
         */
        public static Uri buildToggleFavoritesUri(String MovieId, Boolean flag) {
            Uri toggleFavoriteUri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_TOGGLE_FAVORITES)
                    .appendQueryParameter(KEY_FLAG, String.valueOf(flag))
                    .appendPath(MovieId)
                    .build();
            return toggleFavoriteUri;
        }

    }

    /**
     * builds URI for requesting a list of not favorite movies
     *
     * @return returns resulting uri
     */
    public static Uri buildNotFavoritesUri() {
        Uri notFavoriteUri = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOT_FAVORITES)
                .build();
        return notFavoriteUri;
    }

    /**
     * builds URI for deleting all rows in all tables
     *
     * @return returns resulting uri
     */
    public static Uri buildCleanSlateProtocolUri() {
        Uri cleanSlateProtocolUri = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CLEAN_SLATE_PROTOCOL)
                .build();
        return cleanSlateProtocolUri;
    }



}
