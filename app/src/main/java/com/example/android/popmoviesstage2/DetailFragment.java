package com.example.android.popmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by devbox on 5/26/17.
 */

public class DetailFragment extends Fragment {


    private final String LOG_TAG = this.getClass().getSimpleName();
    private boolean mTwoPane;
    private String mMovieId;
    private Uri mItemUri;
    private long mMovieIdLong = 0l;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private ActionBar mActonBar;

    //fragment variables
    public final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAG_TAG";
    public final String TRAILERS_FRAGMENT_TAG = "TRAILERS_FRAG_TAG";
    public final String REVIEWS_FRAGMENT_TAG = "REVIEWS_FRAG_TAG";

    private OverviewFragment mOverviewFragment;
    private TrailerFragment mTrailerFragment;
    private ReviewsFragment mReviewsFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        mTwoPane = MainActivity.isTwoPane();

        Intent receivedIntent = activity.getIntent();
        Log.v(LOG_TAG, "_in onCreateView()");
        Log.v(LOG_TAG, "_fragment id: " + this.getId());


        if (receivedIntent != null && mItemUri == null && receivedIntent.getData() != null) {
            mItemUri = receivedIntent.getData();
            mMovieId = mItemUri.getLastPathSegment();
            mMovieIdLong = Long.parseLong(mMovieId);
        } else {

            Bundle args = getArguments();

            if (args != null) {
                String uri = args.getString(MainActivity.DETAIL_URI_TAG);
                if (uri != null) {
                    mItemUri = mItemUri.parse(uri);
                    mMovieId = mItemUri.getLastPathSegment();
                    mMovieIdLong = Long.parseLong(mMovieId);
                }
            } else {
                Log.v(LOG_TAG, "_no arguments passed");
            }
        }


        //inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //create fragments
        if(savedInstanceState == null) {
            mOverviewFragment = OverviewFragment.newInstance(mMovieIdLong);
            mTrailerFragment = TrailerFragment.newInstance(mMovieIdLong);
            mReviewsFragment = ReviewsFragment.newInstance(mMovieIdLong);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.overview_container, mOverviewFragment, OVERVIEW_FRAGMENT_TAG).commit();
            fragmentManager.beginTransaction().replace(R.id.trailers_container, mTrailerFragment, TRAILERS_FRAGMENT_TAG).commit();
            fragmentManager.beginTransaction().replace(R.id.reviews_container, mReviewsFragment, REVIEWS_FRAGMENT_TAG).commit();

        }
        //TODO fix toolbar title change
        if(!mTwoPane) {
            //TODO get Toolbar, set movie title, add back button
            mToolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
            mToolbarTitle = (TextView) mToolbar.findViewById(R.id.detail_toolbar_title_text_view);
            activity.setSupportActionBar(mToolbar);
            mActonBar = activity.getSupportActionBar();
            mActonBar.setDisplayHomeAsUpEnabled(true);
            mActonBar.setDisplayShowHomeEnabled(true);
        }

        //TODO inflate other fragments
        return rootView;

    }


    public void setToolbarTitle(String title){
        if(mToolbarTitle!=null && title!=null){
            mToolbarTitle.setText(title);
        }
    }



}
