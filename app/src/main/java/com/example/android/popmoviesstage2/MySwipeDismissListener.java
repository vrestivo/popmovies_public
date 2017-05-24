package com.example.android.popmoviesstage2;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import org.jetbrains.annotations.NotNull;

/**
 * Created by devbox on 5/24/17.
 */

public class MySwipeDismissListener implements View.OnTouchListener {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private int mSlop;
    private VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;

    //touch guesture start point coordinates
    private float mDownX;
    private float mDownY;

    //

    //view to dissmiss
    private View mView;

    //state tracking variables
    private boolean mSwipe;


    public MySwipeDismissListener(@NotNull View view) {
        mView = view;

        //setup velocity tracker
        mVelocityTracker = VelocityTracker.obtain();
        ViewConfiguration viewCofig = ViewConfiguration.get(view.getContext());
        mMaxFlingVelocity = viewCofig.getScaledMaximumFlingVelocity();
        mMinFlingVelocity = viewCofig.getScaledMinimumFlingVelocity();

        //
        mSlop = viewCofig.getScaledTouchSlop();

        //TODO setup callbacks


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();

        switch (action){

            case MotionEvent.ACTION_DOWN: {
                Log.v(LOG_TAG, "_in onTouch DOWN");
                //TODO add event to motion tracker
                //TODO get initial coordinates
                //

            }

            case MotionEvent.ACTION_UP: {
                Log.v(LOG_TAG, "_in onTouch UP");

                //TODO add event to motion tracker
                //TODO get final coordinates
                //TODO calculate direction
                //TODO calculate speed
                //TODO caldulate swipe threshold
                //TODO decide if view will be dismissed

            }

            case MotionEvent.ACTION_CANCEL: {
                Log.v(LOG_TAG, "_in onTouch CANCEL");

                //TODO clean up
            }

        }

        return false;
    }
}
