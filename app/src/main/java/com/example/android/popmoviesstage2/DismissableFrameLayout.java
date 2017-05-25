package com.example.android.popmoviesstage2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * This class intercepts swipe events and handles them if they
 * meet the dismiss criteria
 */
public class DismissableFrameLayout extends FrameLayout {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private VelocityTracker mVelocityTracker = null;

    //motion tracking variables
    private float mStartX;
    private float mStartY;

    private float mEndX;

    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    /*****  DISMISSAL SWIPE CRITERIAL   *****/
    //the swipe has to at least 3000 pixels/second
    private final int mMyMinFling = 3000;
    //the swipe gesture has to cover at least 50%
    //of the view, IOT prevent double triggers
    private final float SWIPE_THRESHOLD = 0.2f;
    //the swipe gesture has to start in the left most
    //20% of the screen
    private final float SWIPE_START_THRESHOLD = 0.20f;
    private boolean mStartThresholdMet = false;
    private boolean mDismissed = false;

    private int mSlop;

    //Mandatory Constructor
    public DismissableFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        ViewConfiguration vc = ViewConfiguration.get(context);
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mSlop = vc.getScaledTouchSlop();

    }


    /**
     * check if the event meets swipe dismiss criteria,
     * if it does, it passes the event to the onTouch()
     * by returning true, false if the event does not.
     * If false is returned, the movement event handling
     * is delegated to the child view which catches it
     *
     * @param ev
     * @return true if event matches criteria
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
        if(this.getVisibility() == View.VISIBLE) {

            mDismissed=false;

            int action = ev.getAction();
            switch (action) {

                case MotionEvent.ACTION_DOWN: {
                    Log.v(LOG_TAG, "_in onInterceptTouchEvent");

                    mStartX = ev.getX();
                    mStartY = ev.getY();

                    //c
                    mStartThresholdMet = mStartX/getWidth() <= SWIPE_START_THRESHOLD;

                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }

                    mVelocityTracker.addMovement(ev);

                    //returning false to further monitor for
                    //swipe threshold indicators
                    return false;
                }

                case MotionEvent.ACTION_MOVE: {
                    mVelocityTracker.addMovement(ev);
                    mEndX = ev.getX();

                    //check if gesture is outside of slop area
                    if(Math.abs(mStartX - mEndX) > mSlop) {
                        //value of 1000 sets units to pixels per second
                        mVelocityTracker.computeCurrentVelocity(1000);
                        float xVelocity =  Math.abs(mVelocityTracker.getXVelocity());

                        //TODO delete logging
                        Log.v(LOG_TAG, "_in move X velocity: " + xVelocity);
                        Log.v(LOG_TAG, "_in move fling velocity Min/Max: " + mMinFlingVelocity + "/" +mMaxFlingVelocity);

                        //check if minimum velocity and gesture start location
                        //criteria a met
                        if(xVelocity > mMyMinFling && mStartThresholdMet){
                            float width = getWidth();
                            float deltaX = mEndX - mStartX;
                            float screenThreshold = deltaX/width;
                            float startThreshold = mStartX/width;

                            //TODO delete logging
                            Log.v(LOG_TAG, "_in move detlaX: " + deltaX);
                            Log.v(LOG_TAG, "_in move width/height: " + width + "/" + getHeight());
                            Log.v(LOG_TAG, "_in move screenT: " + screenThreshold);
                            Log.v(LOG_TAG, "_in move startT: " + startThreshold);

                            if( screenThreshold > SWIPE_THRESHOLD && mStartThresholdMet) {
                                //intercept gesture if criteria are met
                                return true;
                                //return false;
                            }
                        }
                    }

                    //do not intercept gesture if criteria are not met
                    return false;
                }

                case MotionEvent.ACTION_UP: {
                    mEndX = ev.getX();
                    float deltaX = mEndX - mStartX;
                    Log.v(LOG_TAG, "_in UP deltaX : " + deltaX);

                    return false;
                }

                case MotionEvent.ACTION_CANCEL: {
                    return false;
                }
            }
        }
        return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(LOG_TAG, "_in onTouchEvent " + mDismissed);

        MainActivity mainActivity = (MainActivity) getContext();

        //catch double triggers
        if(getVisibility() == VISIBLE && !mDismissed) {
            mainActivity.onBackPressed();
            mDismissed = true;
            return  true;
        }
        else {
            return false;
        }
    }
}
