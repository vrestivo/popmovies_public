package com.example.android.popmoviesstage2;

import android.content.Context;
import android.net.Uri;
import android.support.test.espresso.core.deps.guava.eventbus.AsyncEventBus;
import android.util.Log;

import com.example.android.popmoviesstage2.data.DataContract;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getTargetContext;

/**
 * Unit Test implementation for URL-generating methods
 */

@RunWith(JUnit4.class)
public class UrlGeneratorTest {

    private Context mTargetContext = getTargetContext();

    private final String mInterstellarYouTubeId = "Rt2LHkSwdPQ";
    private final String mInterstellarYouTubeUrl = "https://www.youtube.com/watch?v=Rt2LHkSwdPQ";
    private final int mTMDBInterstellarId = 157336;

    private final String mInterstellarTrailerThumbnailUri = "https://img.youtube.com/vi/Rt2LHkSwdPQ/0.jpg";


    /**
     * Tests generation of YouTube url referring to a trailer video
     */
    @Test
    public void testYoutubeUrlGenerator(){
        String result = Utility.getTrailerYoutubeLink(mInterstellarYouTubeId, mTargetContext);

        Assert.assertEquals("URLs don't match!!!", result, mInterstellarYouTubeUrl);
    }


    @Test
    public void testYoutubeThumbnailUrlGenerator(){
        Uri thumbnailUrl = DataContract.Trailers.buildYoutubeThumbnailUrl(getTargetContext(), mInterstellarYouTubeId, "0.jpg");
        Assert.assertEquals("Thubnail Uri generated incorrectly.", mInterstellarTrailerThumbnailUri, thumbnailUrl.toString());
    }


    @Test
    public void testThumbnailRetrievalFromDB(){
        ArrayList<String> list = Utility.getThumbnailUrlsFromDb(getTargetContext());

        Assert.assertNotNull("null list returned", list);

        for(String link : list){
            System.out.println(link);
        }

    }





    /**
     * test movie ID-based URL request generation for trailers and reviews
     */
/*

    @Test
    public void testTrailerAndReviewUrlGenerator(){
        Uri.Builder builder = new Uri.Builder();
        String workingUrl = builder
                .encodedPath(mTargetContext.getString(R.string.tmdb_full_detail_request_with_api_key))
                .build().toString();

        Log.v("_Test: ", workingUrl);

        String result = FetchData.generateDetailUrl(mTMDBInterstellarId, mTargetContext);

        String decodedResults = null;

        try {
            decodedResults = URLDecoder.decode(result, "utf-8");
        }
        catch (UnsupportedEncodingException uee){
            uee.getCause().printStackTrace();
        }

        System.out.println("W: "+workingUrl);
        System.out.println("R: "+decodedResults);

        Assert.assertEquals("URLs don't match!!!", workingUrl, decodedResults);
    }

*/



    @Test
    public void splitUrlTest(){
        Context context = getTargetContext();

        //get the working TMDB movie detail request, defied as a String resource
        String testUrl = context.getString(R.string.tmdb_full_detail_request_with_api_key);

        Assert.assertNotNull("null object returned", testUrl);

        String movieId = Utility.getMovieIdFromDetailUrl(testUrl);

        Assert.assertNotNull("MovieId is null", movieId);

        Log.v("MovieID: ", movieId);

    }


}
