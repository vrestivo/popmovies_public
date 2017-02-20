package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmoviesstage2.data.DataContract;

/**
 * Created by devbox on 12/15/16.
 */

public class ReviewAdapter extends CursorAdapter {
    public ReviewAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.review_item_layout,
                parent, false);


    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.review_text_view);

        String review = cursor.getString(DataContract.Reviews.COL_CONTENT_INDEX);
        textView.setText(review);


    }

}
