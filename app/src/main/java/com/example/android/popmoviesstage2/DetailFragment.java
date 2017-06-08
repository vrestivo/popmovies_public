package com.example.android.popmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by devbox on 5/26/17.
 */

public class DetailFragment extends Fragment
{

    private final String LOG_TAG = this.getClass().getSimpleName();
    private boolean mTwoPane;
    private String mMovieId;
    private Uri mItemUri;
    private long mMovieIdLong = 0l;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private ActionBar mActonBar;
    private NestedScrollView mNestedScrollView;
    private float mYPosition;


    //used for scroll position retention on config change
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    //used to for setAffectedByUser()
    //see the method for details
    private boolean mAffectedByUser =false;
    private boolean mConfigChange = false;


    //fragment variables
    public final String OVERVIEW_FRAGMENT_TAG = "OVERVIEW_FRAG_TAG";
    public final String TRAILERS_FRAGMENT_TAG = "TRAILERS_FRAG_TAG";
    public final String REVIEWS_FRAGMENT_TAG = "REVIEWS_FRAG_TAG";
    public final String KEY_SCROLLVIEW_SCROLL_POS = "KEY_SCROLLVIEW_SCROLL_POS";

    private OverviewFragment mOverviewFragment;
    private TrailerFragment mTrailerFragment;
    private ReviewsFragment mReviewsFragment;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

        mNestedScrollView = (NestedScrollView) rootView.findViewById(R.id.detail_nested_scroll_view);


        //create fragments
        if (savedInstanceState == null) {
            mConfigChange = false;
            mOverviewFragment = OverviewFragment.newInstance(mMovieIdLong);
            mTrailerFragment = TrailerFragment.newInstance(mMovieIdLong);
            mReviewsFragment = ReviewsFragment.newInstance(mMovieIdLong);

            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.reviews_container, mReviewsFragment, REVIEWS_FRAGMENT_TAG).commit();
            fragmentManager.beginTransaction().replace(R.id.trailers_container, mTrailerFragment, TRAILERS_FRAGMENT_TAG).commit();
            fragmentManager.beginTransaction().replace(R.id.overview_container, mOverviewFragment, OVERVIEW_FRAGMENT_TAG).commit();
        }
        else {
            mConfigChange = true;
        }

        if (!mTwoPane) {
            mToolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
            mToolbarTitle = (TextView) mToolbar.findViewById(R.id.detail_toolbar_title_text_view);
            activity.setSupportActionBar(mToolbar);
            mActonBar = activity.getSupportActionBar();
            mActonBar.setDisplayHomeAsUpEnabled(true);
            mActonBar.setDisplayShowHomeEnabled(true);

            //needed for parent fragment/activity to restore state correctly
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SCROLLVIEW_SCROLL_POS)) {
            //int[] pos = savedInstanceState.getIntArray(KEY_SCROLLVIEW_SCROLL_POS);
            mYPosition = savedInstanceState.getFloat(KEY_SCROLLVIEW_SCROLL_POS);
            Log.v(LOG_TAG, "_y in scroll pos: " + mYPosition);
        }

        return rootView;

    }

    /**"remembers" that the last layout change was
    /* caused by user interaction
     */
     public void setAffectedByUser() {
        mAffectedByUser = true;
         Log.v(LOG_TAG, "_y is affected by user: " + mAffectedByUser);
    }


    @Override
    public void onResume() {
        super.onResume();

        /**
         * The following code is used to facilitate the scroll position
         * of the last scroll position before the config change.
         *
         * The layout pass is done multiple times as fragments
         * are inflated.  This is caused by changes in views sizes due to adapter
         * data binding.  Since there is no way to determine which layout pass was
         * the last one, we will set the OnGlobalLayoutListener to listen for layout
         * changes and scroll to last remembered position on every layout change.
         * This will eventually land us on the last scroll position
         * prior to config change.
         *
         * Since we are only interested in the initial layout changes occurring as layout structure
         * is re-inflated after a config change, we will remove the OnGlobalLayoutListener
         * when layout changes are caused by user interaction.
         */

        //reset the variable
        mAffectedByUser = false;
        final View child  = mNestedScrollView.getChildAt(0);

        mNestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setAffectedByUser();
                return false;
            }
        });

        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.v(LOG_TAG, "_y onGlobalLayout, affected by user?:" + mAffectedByUser);
                child.post(new Runnable()  {
                               @Override
                               public void run() {
                                   int height = child.getHeight();
                                   int yScrollPos = Math.round(height * mYPosition);
                                   Log.v(LOG_TAG, "_y height/ypos: " + height + "/" + yScrollPos);
                                   //only scroll on layout inflation related changes
                                   if(!mAffectedByUser) {
                                       mNestedScrollView.scrollTo(0, yScrollPos);
                                   }
                                   //remove the observer when changes are caused by user interaction
                                   else {
                                       child.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
                                   }
                               }
                           }
                );

            }
        };
        //set the above defined observer only after config change
        if(mConfigChange) {
            child.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mNestedScrollView != null) {

            int height = mNestedScrollView.getChildAt(0).getMeasuredHeight();
            int scrollY = mNestedScrollView.getScrollY();

            //save Y position as a percentage offset
            mYPosition = (float)  mNestedScrollView.getScrollY()/height;
            outState.putFloat(KEY_SCROLLVIEW_SCROLL_POS,  mYPosition );
            //TODO delete logging
            Log.v(LOG_TAG, "_y out h/scrollY/ypos: " + height + "/" + scrollY + "/" + mYPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void setToolbarTitle(String title) {
        if (mToolbarTitle != null && title != null) {
            mToolbarTitle.setText(title);
        }
    }

}
