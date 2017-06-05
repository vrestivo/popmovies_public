package com.example.android.popmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data.DataContract.Movies;
import com.example.android.popmoviesstage2.data_sync.SyncAdapter;
import com.example.android.popmoviesstage2.data_sync.TmdbResults;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetPermission;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import static com.example.android.popmoviesstage2.data.DataContract.Movies.COL_OVERVIEW;
import static com.example.android.popmoviesstage2.data.DataContract.Movies.COL_POPULARITY;
import static com.example.android.popmoviesstage2.data.DataContract.Movies.COL_POSTER_PATH;
import static com.example.android.popmoviesstage2.data.DataContract.Movies.COL_REL_DATE;
import static com.example.android.popmoviesstage2.data.DataContract.Movies.COL_TITLE;
import static com.example.android.popmoviesstage2.data.DataContract.Movies.COL_VOTE_AVG;


/**
 * Handles user preference-based TMDB API url request generateion
 * pulls DATA from the, all methods are static for convenience
 */
public class FetchData {

    private String LOG_TAG = "FetchData: ";
    private Context context;
    private String apiKeyParam;
    private String apiKey;
    private String baseUrl;
    private String videosAndReviews;
    public static int CONNECTION_TIMEOUT_MS = 3000;


    /**
     * This method generates a TMDB url request, based on user sort
     * preference, supplied as a String parameter
     * and returns a url request in String form
     *
     * @param context
     * @param sortSetting sort setting as a String
     * @return String url api request
     */
    public static String generateUrlByGivenPreference(Context context, String sortSetting) {
        String LOG_TAG = "FetchData generateMoviesRequest:";

        Uri.Builder builder;
        String urlString = "";
        String baseUrl = context.getString(R.string.tmdb_api_base_url);
        String apiKeyParam = context.getString(R.string.tmdb_api_key_param);

        //optionally can specify TMDB api key in the strings.xml
        //String apiKey = context.getString(R.string.tmdb_api_key_sequence);

        String apiKey = BuildConfig.TMDB_API_KEY;


        builder = new Uri.Builder();

        //set the base path
        builder.path(baseUrl);

        //generate normal api call if details are not needed

        //check and apply sorting preference
        //if user wants results sorted by popularity,
        //set api request accordingly
        if (sortSetting.equals(context.getString(R.string.pref_setting_popularity))) {
            builder.appendPath(context.getString(R.string.tmdb_api_sort_popular));
        }
        //if user wants results sorted by top rated
        // , set api request accordingly
        else if (sortSetting.equals(context.getString(R.string.pref_setting_vote))) {
            builder.appendPath(context.getString(R.string.tmdb_api_sort_top_rated));
        } else {
            builder.appendPath(context.getString(R.string.tmdb_api_sort_popular));
        }

        //set api key parameter and value
        builder.appendQueryParameter(apiKeyParam,
                apiKey);

        //decode the url so all characters appear nicely,
        //ex: a semicolon appears as ":", and not as "%3A"
        //url encoded characters cause issues during requests
        urlString = Uri.decode(builder.toString());


        return urlString;
    }

    /**
     * Takes relative poster location and returns a full poster url
     *
     * @param relativeUrl relativ poster url
     * @return poster complete poster url
     */
    public static String getPosterUrl(String relativeUrl, Context c) {
        Context context = c;
        if (relativeUrl.contains("null")) {
            return null;
        } else if (relativeUrl == null) {
            return null;
        } else {
            //poster url generation fix:
            //deletes a slash at the beginning of relative url
            relativeUrl = relativeUrl.substring(1, relativeUrl.length());
            Uri.Builder builder = new Uri.Builder();
            builder.path(context.getString(R.string.tmdb_poster_base_url));
            builder.appendPath(relativeUrl);
            String poster = Uri.decode(builder.build().toString());
            return poster;

        }
    }

    /**
     * This method takes a movie id, used by TMDB API, and a Context, then
     * generates an API Request URI, calling results
     * containing trailers and reviews
     *
     * @param movieId TMDB movie id, as a positive int
     * @param c       Context
     * @return TMDB API URL request in String form
     */
    public static String generateDetailUrl(int movieId, Context c) {
        Context context = c;
        String baseUrl = context.getString(R.string.tmdb_api_base_url);

        String apiKeyParam = context.getString(R.string.tmdb_api_key_param);
        //String apiKey = context.getString(R.string.tmdb_api_key_sequence);
        String apiKey = BuildConfig.TMDB_API_KEY;

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(baseUrl);

        String urlString;

        //movieId validation
        if (movieId > 0) {
            builder.appendPath(String.valueOf(movieId));
            builder.appendQueryParameter(apiKeyParam, apiKey);
            builder.appendQueryParameter(context.getString(R.string.tmdb_append_to_response_key),
                    context.getString(R.string.tmdb_append_to_response_values));
            urlString = builder.toString();

            return urlString;
        } else {
            //return null on invalid movieId
            return null;
        }

    }


    /**
     * This method establishes a connection with DMDB,
     * sends the api request for movie com.example.android.popmoviesstage2.data,
     * receives json data and returns it as a String
     *
     * @param urlString URL to pull data from in String form
     * @return returns raw String with JSON data
     * or null on error
     */

    public static TmdbResults getJsonData(String urlString) {
        String LOG_TAG = "_getJsonData";
        URL url = null;

        int statusCode = SyncAdapter.STATUS_UNKNOWN_ERROR;

        //variable to store unformatted JSON com.example.android.popmoviesstage2.data
        String ufJSONData = null;

        //Variable to store final results to return to the UI thread
        Vector<ContentValues> finalResults = null;

        //creating variables outside of try catch clause
        //to get around variable scope issues in the clause
        HttpURLConnection httpConnection = null;
        //URL url = null;
        BufferedReader reader = null;
        if (urlString != null) {
            //using try catch to handle exceptions
            try {
                String line = "";
                StringBuffer buffer = new StringBuffer();


                //create a url object
                url = new URL(urlString);

                //Check for network connection as per
                // https://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html

                //create a HTTPURLConnection
                httpConnection = (HttpURLConnection) url.openConnection();
                //set request method to GET
                httpConnection.setRequestMethod("GET");
                //connect to the web site
                httpConnection.connect();

                //create an input stream to read incoming com.example.android.popmoviesstage2.data
                InputStream inputStream =
                        new BufferedInputStream(httpConnection.getInputStream());

                //extra paranoid error checking measure
                //return null if no response received
                if (inputStream == null) {
                    //return null;
                    ufJSONData = null;
                    statusCode = SyncAdapter.STATUS_IO_ERROR;
                }
                //else load the buffer break input by lines and store it in
                //ufJSONData variable
                else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    if (buffer.length() == 0) {
                        //return null;
                        ufJSONData = null;
                    } else {
                        ufJSONData = buffer.toString();
                        statusCode = SyncAdapter.STATUS_OK;
                    }
                }

            }//end of try clause

            catch(FileNotFoundException fnfe){
                ufJSONData = null;
                statusCode = SyncAdapter.STATUS_TOO_MANY_REQUESTS;
            }
            catch (MalformedURLException mue) {
                mue.printStackTrace();
                Log.e(LOG_TAG, mue.toString());
                statusCode = SyncAdapter.STATUS_INVALID_URL;
                ufJSONData = null;
            } catch (SocketTimeoutException ste) {
                ste.printStackTrace();
                statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
                ufJSONData = null;
            } catch (SocketException se) {
                //TODO in final block return status and close resources
                statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
                ufJSONData = null;
            }
            //TODO check filenotfound ecxeption
            catch (UnknownHostException uhe) {
                uhe.printStackTrace();
                statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
                ufJSONData = null;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Log.v(LOG_TAG, " in_catch IOException" + ioe.toString());
                ufJSONData = null;
                statusCode = SyncAdapter.STATUS_IO_ERROR;
            } finally {
                //ensure connection and buffered reader are closed
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException readerCantClose) {
                        Log.e(LOG_TAG, "reader closing", readerCantClose);
                        readerCantClose.printStackTrace();
                        statusCode = SyncAdapter.STATUS_IO_ERROR;
                    }
                }
                //TODO return status code on network error

            }//end of finally clause

        }//end of if urlString != null block
        TmdbResults results = new TmdbResults();

        results.setStatusCode(statusCode);
        results.setJsonString(ufJSONData);

        return results;
    }


    /**
     * Parses JSON data from TMDB and
     * organizes it into a Vector of ContentValues objects,
     * to be converted into a ContentValues[] and inserted,
     * into the movies tabe
     *
     * @param unfJSONData raw JSON data in String form
     * @return Vector<ContentValues>
     */

    public static ContentValues[] rawMoviesJsonDataToCvArray(String unfJSONData, Context c) {
        final String LOG_TAG = "_getFinalResults: ";
        Context context = c;

        ContentValues[] cvArray = null;

        Vector<ContentValues> movies = new Vector<ContentValues>();


        String paramResults = context.getString(R.string.json_key_results);
        String paramPage = context.getString(R.string.json_key_page);
        String paramOverview = context.getString(R.string.json_key_overview);
        String paramDate = context.getString(R.string.json_key_release_date);
        String paramTitle = context.getString(R.string.json_key_original_title);
        String paramPath = context.getString(R.string.json_key_poster_path);
        String paramVote = context.getString(R.string.json_key_vote_average);
        String paramId = context.getString(R.string.json_key_movie_id);
        String paramPopularity = context.getString(R.string.json_key_popularity);
        //if no com.example.android.popmoviesstage2.data was passed, return null, else do the work
        if (unfJSONData == null) {
            movies = null;
        } else {

            try {
                JSONObject jsonBigObject = new JSONObject(unfJSONData);
                JSONArray resultsArray = jsonBigObject.getJSONArray(paramResults);

                //if resultArray has objects extract com.example.android.popmoviesstage2.data from them
                if (resultsArray.length() > 0) {
                    for (int i = 0; i < resultsArray.length(); i++) {

                        ContentValues movieCv = new ContentValues();
                        JSONObject jsonMovieItem = resultsArray.getJSONObject(i);

                        movieCv.put(Movies._ID, jsonMovieItem.getInt(paramId));
                        movieCv.put(COL_TITLE, jsonMovieItem.getString(paramTitle));
                        movieCv.put(COL_REL_DATE, jsonMovieItem.getString(paramDate));
                        movieCv.put(COL_POPULARITY, jsonMovieItem.getDouble(paramPopularity));
                        movieCv.put(COL_VOTE_AVG, jsonMovieItem.getDouble(paramVote));
                        movieCv.put(COL_OVERVIEW, jsonMovieItem.getString(paramOverview));


                        //check if JSON com.example.android.popmoviesstage2.data has null for poster value
                        String poster = jsonMovieItem.getString(paramPath);
                        if (poster != null) {
                            movieCv.put(COL_POSTER_PATH, (getPosterUrl(poster, context)));
                        } else {
                            movieCv.put(COL_POSTER_PATH, "");
                        }
                        movies.add(movieCv);
                    }
                } else {
                    movies = null;
                }

            } catch (JSONException je) {
                Log.e(LOG_TAG, "creating a json object", je);
                return null;
            }

            if (movies.size() > 0) {
                cvArray = new ContentValues[movies.size()];
                movies.toArray(cvArray);
            }
        }


        return cvArray;
    }


    public static HashMap<String, HashMap<String, ContentValues[]>> parseRawDetails(String movieId, String rawJSONDetails,
                                                                                    Context c) {
        final String LOG_TAG = "parseRawDetails: ";
        if (rawJSONDetails == null && !(movieId.matches("[0-9+]"))) {
            return null;
        }

        //outer HashMap, that will be returned
        HashMap<String, HashMap<String, ContentValues[]>> finalResults = new HashMap<String, HashMap<String, ContentValues[]>>();

        //inner HashMap
        HashMap<String, ContentValues[]> results = new HashMap<String, ContentValues[]>();

        Context context = c;


        //Constants for JSON details parameters
        final String paramRuntime = context.getString(R.string.json_details_key_runtime);
        final String paramResults = context.getString(R.string.json_key_results);

        //Constatns for JSON trailers parameters
        final String paramTVideos = context.getString(R.string.json_details_trailers_videos);
        final String paramTKey = context.getString(R.string.json_details_trailers_key);
        final String paramTSite = context.getString(R.string.json_details_trailers_site);
        final String paramTType = context.getString(R.string.json_details_trailers_type);

        //Constant for JSON reviews parameters
        final String paramRReviews = context.getString(R.string.json_details_reviews_reviews);
        final String paramRAuthor = context.getString(R.string.json_details_reviews_author);
        final String paramRContent = context.getString(R.string.json_details_reviews_content);


        //ContentValues containers that will hold values
        //ready to be inserted into the database;
        ContentValues[] detailsCvArray = new ContentValues[1];
        Vector<ContentValues> trailersCvVector = new Vector<ContentValues>();
        Vector<ContentValues> reviewsCvVector = new Vector<ContentValues>();


        //comparison value to verify this is a YouTube Trailer
        final String youtube = context
                .getString(R.string.json_details_trailers_youtube);
        //comparison value to verify the video type is "Trailer"
        final String trailer = context
                .getString(R.string.json_details_trailers_type_trailer);

        try {

            //jsonObject containing all returned data
            JSONObject allData = new JSONObject(rawJSONDetails);

            //get the runtime
            if (allData.has(paramRuntime)) {
                ContentValues detailsCv = new ContentValues();
                detailsCv.put(Movies.COL_RUNTIME, allData.getInt(paramRuntime));
                detailsCvArray[0] = detailsCv;
                results.put(paramRuntime, detailsCvArray);

            }

            //JSON array containing trailers
            JSONArray trailersArray = allData.getJSONObject(paramTVideos)
                    .getJSONArray(paramResults);

            //JSON array containing reviews
            JSONArray reviewsArray = allData.getJSONObject(paramRReviews)
                    .getJSONArray(paramResults);


            JSONObject object;

            //if there are trailers pull data from them
            if (trailersArray.length() > 0) {
                for (int i = 0; i < trailersArray.length(); i++) {
                    object = trailersArray.getJSONObject(i);
                    if (object.getString(paramTType).equals(trailer) &&
                            object.getString(paramTSite).equals(youtube)) {
                        ContentValues trailersCv = new ContentValues();
                        trailersCv.put(paramTKey, object.getString(paramTKey));

                        //add movieId to content values since it is a foreigh key in
                        //the trailers table
                        trailersCv.put(DataContract.Trailers.COL_MOVIE_ID, movieId);
                        trailersCvVector.add(trailersCv);
                    }
                }

                ContentValues[] trailersCvArray = new ContentValues[trailersCvVector.size()];
                trailersCvVector.toArray(trailersCvArray);

                results.put(paramTVideos, trailersCvArray);

            } else {
                Log.v(LOG_TAG, "JSON array with trailer results is null");
            }


            //if there are reviews pull data from them
            if (reviewsArray.length() > 0) {
                for (int i = 0; i < reviewsArray.length(); i++) {
                    object = reviewsArray.getJSONObject(i);
                    ContentValues reviewsCv = new ContentValues();
                    reviewsCv.put(paramRReviews, object.getString(paramRContent));
                    reviewsCv.put(DataContract.Reviews.COL_MOVIE_ID, movieId);
                    reviewsCvVector.add(reviewsCv);
                }

                ContentValues[] reviewsCvArray = new ContentValues[reviewsCvVector.size()];
                reviewsCvVector.toArray(reviewsCvArray);

                results.put(paramRReviews, reviewsCvArray);
            }


        } catch (JSONException jsonException) {
            Log.v(LOG_TAG, jsonException.toString());
            jsonException.printStackTrace();
        }


        if (results.isEmpty()) {
            Log.d(LOG_TAG, "HashMap<String, ContentValues[]> results is null");
            return null;
        } else {
            finalResults.put(movieId, results);
        }

        return finalResults;
    }

    /**
     * this method requests details data form TMDB for a given movie ID
     *
     * @param movieId
     * @param c
     * @return
     */
    public static TmdbResults fetchDetailsByMovieId(String movieId, Context c) {
        final String LOG_TAG = "_fetchDetailsByMovieId: ";
        Context context = c;
        TmdbResults results = new TmdbResults();

        if (movieId == null) {
            Log.d(LOG_TAG, "null movie Id passed");
            return null;
        }

        String rawJsonDetails = null;

        results = FetchData.getJsonData(
                FetchData.generateDetailUrl(Integer.parseInt(movieId),
                        context));

        return results;

    }


    //TODO add return status code and stop on network error
    public static int downloadAndSaveMoviePosters(ArrayList<String> passedUrlList, Context context) {

        final String LOG_TAG = "dMultImages: ";

        int statusCode = SyncAdapter.STATUS_UNKNOWN_ERROR;

        InputStream is = null;
        HttpURLConnection connection = null;
        FileOutputStream outputStream = null;

        for (String passedUrl : passedUrlList) {
            String imageName = Uri.parse(passedUrl).getLastPathSegment();

            try {
                URL url = new URL(passedUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(CONNECTION_TIMEOUT_MS);
                connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                Log.v(LOG_TAG, "response is: " + response);
                is = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(is);

                outputStream = context.openFileOutput(imageName, context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            }
            catch (MalformedURLException mue) {
                mue.printStackTrace();
                Log.e(LOG_TAG, mue.toString());
                statusCode = SyncAdapter.STATUS_INVALID_URL;
            } catch (SocketTimeoutException ste) {
                ste.printStackTrace();
                statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
            } catch (SocketException se) {
                //TODO in final block return status and close resources
                statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
            }
            catch (UnknownHostException uhe){
                uhe.printStackTrace();
                statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                        connection.disconnect();
                        outputStream.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                if (statusCode == SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR) {
                    return statusCode;
                }
            }
        }

        return statusCode;
    }


    /**
     * this method downloads youtube video thumbnails and stores in the app's files directory
     *
     * @param passedUrlList arrays list of trailer thumbnails to download
     * @param context       context
     * @throws IOException
     */
    public static int downloadAndSaveTrailerThumbnails(ArrayList<String> passedUrlList, Context context) {

        final String LOG_TAG = "downloadAndSaveTrailerThumbnails";

        int statusCode = SyncAdapter.STATUS_UNKNOWN_ERROR;


        if (passedUrlList != null) {
            InputStream is = null;
            HttpURLConnection connection = null;
            FileOutputStream outputStream = null;

            for (String thumbnailUrl : passedUrlList) {

                if (thumbnailUrl != null) {

                    String thumbnailSaveName = Utility.getThumbnailSaveName(thumbnailUrl);
                    System.out.println("_thub_save_name: " + thumbnailSaveName);

                    if (thumbnailSaveName != null) {

                        try {
                            URL url = new URL(thumbnailUrl);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setReadTimeout(CONNECTION_TIMEOUT_MS);
                            connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
                            connection.setRequestMethod("GET");
                            connection.setDoInput(true);
                            connection.connect();
                            int response = connection.getResponseCode();
                            Log.v(LOG_TAG, "response is: " + response);
                            is = connection.getInputStream();

                            Bitmap bitmap = BitmapFactory.decodeStream(is);

                            outputStream = context.openFileOutput(thumbnailSaveName, context.MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                        }
                        catch (MalformedURLException mue) {
                            mue.printStackTrace();
                            Log.e(LOG_TAG, mue.toString());
                            statusCode = SyncAdapter.STATUS_INVALID_URL;
                        } catch (SocketTimeoutException ste) {
                            ste.printStackTrace();
                            statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
                        } catch (SocketException se) {
                            statusCode = SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR;
                        }
                        catch (IOException ioe) {
                            ioe.printStackTrace();
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                    connection.disconnect();
                                    outputStream.close();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            }
                            if (statusCode == SyncAdapter.STATUS_NETWORK_CONNECTION_ERROR) {
                                return statusCode;
                            }
                        }

                    } //end of if(thumbnailUrl!=null)

                } //end of if(thumbnailUrl!=null)

            }
        } // end of if(passedUrlList !=null)

        return statusCode;

    } // end of downloadAndSaveTrailerThumbnails()


} //end of class

