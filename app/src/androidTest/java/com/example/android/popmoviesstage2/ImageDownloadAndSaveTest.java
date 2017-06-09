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
import android.support.v4.view.ScaleGestureDetectorCompat;
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
import java.util.List;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Testing image download and test
 */


@RunWith(JUnit4.class)
public class ImageDownloadAndSaveTest {

    private static final String cptAmericaMovieId = "271110";

    /**
     * Utility method that takes a list of URLs pointing to trailer thumbnail images,
     * converts them to a list of files expected to be downloaded and saved
     * and saved
     * @param thumbnaiilUrls
     * @return
     */
    public static ArrayList<String> getProjectedThumbnailList(ArrayList<String> thumbnaiilUrls){
        if(thumbnaiilUrls != null && thumbnaiilUrls.size()>0) {
            ArrayList<String> projectedThumbnails = new ArrayList<String>();
            for(String url : thumbnaiilUrls){
                if(url!=null && !url.isEmpty()){
                    projectedThumbnails.add(Utility.getThumbnailSaveName(url));
                }
            }
            return projectedThumbnails;
        }
        return null;
    }

    /**
     * A Utility method that deletes all trailer thumbnail images
     * @param context
     * @param thumbnailList list of thumbnail images to delete
     */
    public static void deleteAllThumbnailJpgs(Context context, ArrayList<String> thumbnailList) {
        if (thumbnailList != null && thumbnailList.size() > 0) {
            //TODO go through each file and delete it
            String filename = null;
            for (String url : thumbnailList) {
                if (url != null) {
                    filename = Utility.getThumbnailSaveName(url);
                    if (filename != null) {
                        context.deleteFile(filename);
                    }

                }
            }
        }
    }


    /**
     * test to validate the correct download and save procedures
     * for the trailer thumbnail images
     */
    @Test
    public void downloadAndSaveTrailerThumbnails() {
        final String LOG_TAG = "downloadAndSaveTrailerThumbnails";
        Context context = getTargetContext();

        //get trailer Thumbnail URL and delete existing thumbnails
        ArrayList<String> thumbNailUrls = Utility.getThumbnailUrlsFromDb(context);
        deleteAllThumbnailJpgs(context, thumbNailUrls);
        ArrayList<String> projectedSavedThumbnailList = getProjectedThumbnailList(thumbNailUrls);

        //download thumbnails and save the images
        FetchData.downloadAndSaveTrailerThumbnails(thumbNailUrls, context);

        File filesDir = context.getFilesDir();
        String[] listedFiles = filesDir.list();
        List<String> fileList = Arrays.asList(listedFiles);

        //check image names against the URLs
        for(String finename : projectedSavedThumbnailList){
            Log.v(LOG_TAG, "filename: " + finename);
            Assert.assertTrue("filename: " + finename + " not found", fileList.contains(finename));
        }

    }

}