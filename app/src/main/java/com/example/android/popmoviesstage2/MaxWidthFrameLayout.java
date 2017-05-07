package com.example.android.popmoviesstage2;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by devbox on 5/7/17.
 */

public class MaxWidthFrameLayout extends FrameLayout {

    private final int mMaxWidth;

    public MaxWidthFrameLayout(@NonNull Context context) {
        super(context);
        mMaxWidth = 0;

    }

    public MaxWidthFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxWidthFrameLayout);
        mMaxWidth = typedArray.getDimensionPixelSize(R.styleable.MaxWidthFrameLayout_MaxWidth, 0);
        typedArray.recycle();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(mMaxWidth > 0 && mMaxWidth < measuredWidth){
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, measureMode);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
