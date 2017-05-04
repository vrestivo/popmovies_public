package com.example.android.popmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import junit.framework.Test;

/**
 * Created by devbox on 5/3/17.
 */

public class ViewPagetTabFragment extends Fragment {

    private ViewPager mViewPager;
    private boolean mTwoPane;

    private String mMovieId;
    private Toolbar mToolbar;
    private boolean mTowPane;
    private Uri itemUri = null;

    private long mMovieIdLong = 0l;

    private final String LOG_TAG = this.getClass().getSimpleName();



    private String mBundleUriKey = "uri";

    //TODO check


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        mTowPane = MainActivity.isTwoPane();

        Intent receivedIntent = activity.getIntent();

        Log.v(LOG_TAG, "_in onCreateView()");


        if (receivedIntent != null && itemUri == null && receivedIntent.getData() != null) {
            itemUri = receivedIntent.getData();
            mMovieId = itemUri.getLastPathSegment();
            mMovieIdLong = Long.parseLong(mMovieId);

        } else {

            Bundle args = getArguments();

            if (args != null) {
                String uri = args.getString(MainActivity.DETAIL_URI_TAG);
                if (uri != null) {
                    itemUri = itemUri.parse(uri);
                    mMovieId = itemUri.getLastPathSegment();
                    mMovieIdLong = Long.parseLong(mMovieId);
                }
            } else {
                Log.v(LOG_TAG, "_no arguments passed");
            }
        }


        //inflate the layout
        View rootView = inflater.inflate(R.layout.view_pager_fragment, container, false);

        //needed for the ViewPager render correctly
        NestedScrollView nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nested_scrollview);
        nestedScrollView.setFillViewport(true);

        //FragmentManager fm = getActivity().getSupportFragmentManager();
        mViewPager = (ViewPager) rootView.findViewById(R.id.detail_view_pager);
        FragmentManager fm = getChildFragmentManager();
        DetailViewPagerAdapter adapter  = new DetailViewPagerAdapter(fm);

        mViewPager.setAdapter(adapter);
        //mViewPager.setCurrentItem(0);

        //TODO ADD Tabs



        return rootView;

    }

    class DetailViewPagerAdapter extends FragmentStatePagerAdapter {
        private final String LOG_TAG = this.getClass().getSimpleName();
        private final int NUM_ITEMS = 3;

        public DetailViewPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.v(LOG_TAG, "_in constructor");

        }

        @Override
        public Fragment getItem(int position) {

            //TODO return a new fragment instance based on item it
            Fragment fragment = null;

            switch (position){
                case 0: {
                    //TODO return overview fragment
                    Log.v(LOG_TAG, "_in case 0");
                    fragment = OverviewFragment.newInstance(mMovieIdLong);

                    break;
                }
                case 1: {
                    //TODO return trailer fragment
                    Log.v(LOG_TAG, "_in case 1");

                    fragment = TrailerFragment.newInstance(mMovieIdLong);

                    //return fragment;
                    break;
                }
                case 2: {
                    //TODO return review fragment
                    Log.v(LOG_TAG, "_in case 2");

                    //fragment = TestFragment.newInstance(position);
                    fragment = ReviewsFragment.newInstance(mMovieIdLong);

                    //return fragment;
                    break;
                }
            }


        return fragment;

        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
    }


}
