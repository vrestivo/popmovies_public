package com.example.android.popmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by devbox on 5/3/17.
 */

public class ViewPagetTabFragment extends Fragment {

    private ViewPager mViewPager;

    private String mMovieId;
    private boolean mTwoPane;
    private Uri itemUri = null;
    private long mMovieIdLong = 0l;


    private Toolbar mToolbar;
    private ActionBar mActonBar;
    private TextView mToolbarTitle;

    private final String LOG_TAG = this.getClass().getSimpleName();



    private String mBundleUriKey = "uri";

    //TODO check


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        mTwoPane = MainActivity.isTwoPane();

        Intent receivedIntent = activity.getIntent();
        Log.v(LOG_TAG, "_in onCreateView()");
        Log.v(LOG_TAG, "_fragment id: " + this.getId());



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

        if(!mTwoPane) {
            //TODO get Toolbar, set movie title, add back button
            mToolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
            mToolbarTitle = (TextView) mToolbar.findViewById(R.id.detail_toolbar_title_text_view);
            activity.setSupportActionBar(mToolbar);
            mActonBar = activity.getSupportActionBar();
            mActonBar.setDisplayHomeAsUpEnabled(true);
            mActonBar.setDisplayShowHomeEnabled(true);
        }

        //setup ViewPager
        mViewPager = (ViewPager) rootView.findViewById(R.id.detail_view_pager);
        FragmentManager fm = getChildFragmentManager();
        DetailViewPagerAdapter adapter = new DetailViewPagerAdapter(fm);
        mViewPager.setAdapter(adapter);


        //add tabs and hook up to ViewPager
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.pager_tabs);
        tabLayout.setupWithViewPager(mViewPager);


        return rootView;

    }


    public void setToolbarTitle(String title){
        if(mToolbarTitle!=null && title!=null){
            mToolbarTitle.setText(title);
        }
    }


    class DetailViewPagerAdapter extends FragmentStatePagerAdapter {
        private final String LOG_TAG = this.getClass().getSimpleName();
        private final int NUM_ITEMS = 3;
        private ArrayList<String> TabTitles = new ArrayList<>();


        public DetailViewPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.v(LOG_TAG, "_in constructor");

            TabTitles.add(getString(R.string.tab_overview));
            TabTitles.add(getString(R.string.tab_trailers));
            TabTitles.add(getString(R.string.tab_reviews));

        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return super.getPageTitle(position);

            return TabTitles.get(position);
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

                    Log.v(LOG_TAG, "_fragment id: " + fragment.getId());

                    break;
                }
                case 1: {
                    //TODO return trailer fragment
                    Log.v(LOG_TAG, "_in case 1");
                    fragment = TrailerFragment.newInstance(mMovieIdLong);

                    Log.v(LOG_TAG, "_fragment id: " + fragment.getId());


                    //return fragment;
                    break;
                }
                case 2: {
                    //TODO return review fragment
                    Log.v(LOG_TAG, "_in case 2");

                    //fragment = TestFragment.newInstance(position);
                    fragment = ReviewsFragment.newInstance(mMovieIdLong);
                    Log.v(LOG_TAG, "_fragment id: " + fragment.getId());


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
