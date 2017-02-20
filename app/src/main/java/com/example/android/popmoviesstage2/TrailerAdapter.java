package com.example.android.popmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmoviesstage2.data.DataContract;

/**
 * Created by devbox on 12/15/16.
 */

public class TrailerAdapter extends CursorAdapter {

    public TrailerAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.trailer_item_layout,
                parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        int itemNumber = cursor.getPosition() + 1;

        final String key = cursor.getString(DataContract.Trailers.COL_KEY_INDEX);

        //generate Youtube trailer link
        final String youtubeLink = Utility.getTrailerYoutubeLink(key, context);

        final TextView textView = (TextView) view.findViewById(R.id.trailer_item);
        textView.setText("YouTube Trailer " + itemNumber);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (youtubeLink != null) {

                    Intent trailerIntent = new Intent();
                    trailerIntent.setAction(Intent.ACTION_VIEW);
                    trailerIntent.setData(Uri.parse(youtubeLink));

                    Intent chooser = Intent.createChooser(trailerIntent,
                            context.getString(R.string.intent_chooser_message));

                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (chooser.resolveActivity(context.getPackageManager()) != null) {

                        context.startActivity(chooser);
                    }


                }

            }
        });


    }
}
