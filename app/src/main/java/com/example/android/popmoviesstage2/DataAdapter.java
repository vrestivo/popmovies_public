package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import com.example.android.popmoviesstage2.data.DataContract;

import java.io.File;

/**
 * Created by devbox on 11/22/16.
 */

public class DataAdapter extends CursorAdapter {

    private final String LOG_TAG = "DataAtapter: ";

    Context mContext;
    LayoutInflater mLayoutInflater;

    public DataAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        Log.v(LOG_TAG, "_after super");
    }

    /**
     * this method inflates the layout to be used in data binding
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_image_view, parent, false);
        return view;
    }

    /**
     * this method takes imaged downloaded during the data sync
     * and loads them into image view
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Get poster URL
        String posterUrl = cursor.getString(DataContract.Movies.COL_POSTER_PATH_INDEX);
        String imageName = null;

        if (!posterUrl.isEmpty()) {
            imageName = Uri.parse(posterUrl).getLastPathSegment();
            String filePath = context.getFilesDir().toString() + "/" + imageName;
            File posterFile = new File(filePath);

            if (view != null && (view instanceof ImageView) && posterFile.exists() && posterFile.isFile()) {
                Picasso.with(context).load(posterFile)
                        .into((ImageView) view);
            }
        }
    }

    /**
     * Default constructor, calls superclass
     *
     * @param context
     * @param c
     * @param flags
     */
    public DataAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
}
