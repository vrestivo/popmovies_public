package com.example.android.popmoviesstage2;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.popmoviesstage2.data.DataContract;
import com.example.android.popmoviesstage2.data.MovieDbHelper;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Testing image download and test
 */



@RunWith(JUnit4.class)
public class ImageDownloadAndSaveTest {

    public ArrayList<String> jpgHitList = new ArrayList<String>();


    private static final String cptAmericaMovieId = "271110";


//    @Test
//    public void deleteSelectImages(){
//        final String LOG_TAG = "deleteSelected Items";
//
//        Context context = getTargetContext();
//
//        File fileDir = context.getFilesDir();
//
//        String[] fileList = fileDir.list();
//
//        Assert.assertTrue("file list is empty", fileList.length>0);
//
//        ArrayList<String> fileArrayList = new ArrayList<String>(Arrays.asList(fileList));
//
//        for(String filename : fileArrayList){
//            Log.v(LOG_TAG, "JPG: " + filename);
//        }
//
//        jpgHitList.add("43Gr00IiZtq2dOtYZQVOTwMf3kI.jpg");
//        jpgHitList.add("4Iu5f2nv7huqvuYkmZvSPOtbFjs.jpg");
//        jpgHitList.add("5N20rQURev5CNDcMjHVUZhpoCNC.jpg");
//
//        for(String filename : jpgHitList){
//            Log.v(LOG_TAG, "hitlist member: " + filename);
//        }
//
//        fileArrayList.removeAll(jpgHitList);
//
//        for(String filename : fileArrayList){
//            Log.v(LOG_TAG, "survivor : " + filename);
//        }
//
//
//    }


    /**
     * tests a download functionality for a single image
     */


//    @Test
//    public void downloadMultipleImagesTest() {
//        final String LOG_TAG = "_dMultipleImagesTest: ";
//        Context context = getTargetContext();
//
//        ArrayList<String> urlList = getPosterUrlsFromDb(context);
//
//        Assert.assertTrue("Url List is null", !urlList.isEmpty());
//
//        ConnectivityManager connectivityManager = (ConnectivityManager)
//                context.getSystemService(context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//
//        if (networkInfo != null && networkInfo.isConnected()) {
//            try {
//                downloadAndSaveMoviePosters(urlList, context);
//            } catch (IOException ioe) {
//                Log.v(LOG_TAG, ioe.toString());
//                ioe.printStackTrace();
//            }
//        } else {
//            Log.v(LOG_TAG, "_no network connection");
//        }
//
//    }


//    @Test
//    public void fileListTest(){
//        final String LOG_TAG = "fileListTest: ";
//        Context context = getTargetContext();
//
//        File file = context.getFilesDir();
//
//        String[] fileList = file.list();
//
//        Assert.assertTrue("No files listed: ", fileList.length > 0);
//
//        Log.v(LOG_TAG, "_JPG resultsFollow: ");
//        for(String filename : fileList){
//            if(filename.matches(".*\\.jpg")) {
//                Log.v(LOG_TAG, "JPG: " + filename);
//            }
//        }
//
//    }

    public static void downloadAndSaveMultipleImages(ArrayList<String> passedUrlList, Context context) throws IOException {
        //TODO update method

    }

    @Test
    public void downloadAndSaveTrailerThumbnails() {
        Context context = getTargetContext();

        try {
            FetchData.downloadAndSaveTrailerThumbnails(Utility.getThumbnailUrlsFromDb(context), context);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

/*

    */
/**
 * test deletion of movie posters and records for movies not marked
 * as favorite
 *//*

    @Test
    public void deletNonFavoriteJpgandMoviesTest(){
        final String LOG_TAG = "deletNonFavJPGTest: ";

        Context context = getTargetContext();

        int deletedItems = Utility.deleteNonFavoriteJPGsAndMovieRecords(context);

        Assert.assertTrue("No items were deleted", deletedItems>0);


    }
*/


}