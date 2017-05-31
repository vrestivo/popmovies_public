package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmoviesstage2.data.DataContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.view.LayoutInflater.from;

/**
 * Created by devbox on 5/17/17.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieItemViewHolder> {

    private Cursor mCursor;
    private Context mContext = null;
    private GridItemClickListener mListener;

    //interface to be implemented by FragmentMain

    /**
     * click listener interface for the main fragment
     */
    interface GridItemClickListener {
        /**
         * @param movieId         used for uri generation for the activity intent/data query
         * @param posterImageView used for transistion between activities
         */
        void onGridItemClick(long movieId, @Nullable View posterImageView);
    }


    public MovieGridAdapter(@NonNull Context context, GridItemClickListener listener) {
        super();
        mContext = context;
        mListener = listener;
    }

    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_thumbnail_item, parent, false);
        MovieItemViewHolder movieItemViewHolder = new MovieItemViewHolder(view, mListener);
        return movieItemViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            //Get movie poster url and load it into image view
            String posterUrl = mCursor.getString(DataContract.Movies.COL_POSTER_PATH_INDEX);
            if (posterUrl != null && !posterUrl.isEmpty()) {
                String filePath = mContext.getFilesDir().toString() + "/"
                        + Uri.parse(posterUrl).getLastPathSegment();
                final File imageFile = new File(filePath);

                if (imageFile.exists() && imageFile.isFile()) {
                    //final variable of inner Callback class
                    final MovieItemViewHolder innerHolder = holder;

                    Picasso.with(mContext)
                            .load(imageFile)
                            .noPlaceholder()
                            .into(holder.mmPoster, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Picasso.with(mContext)
                                            .load(imageFile)
                                            .into(innerHolder.mmPoster);
                                }

                                @Override
                                public void onError() {
                                    Picasso.with(mContext)
                                            .load(R.drawable.ic_broken_image_48px)
                                            .into(innerHolder.mmPoster);
                                }
                            });

                }
            }

            holder.mmTitle.setText(mCursor.getString(DataContract.Movies.COL_TITLE_INDEX));
            holder.mmMovieId = mCursor.getLong(DataContract.Movies.COL_ID_INDEX);

        }

    }

    @Override
    public int getItemCount() {

        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;

    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public int getCurrentPosition() {
        if (mCursor != null && mCursor.getCount() > 0) {
            return mCursor.getPosition();

        }
        return 0;
    }

    class MovieItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener

    {

        private final String LOT_TAG = this.getClass().getSimpleName();

        private ImageView mmPoster;
        private TextView mmTitle;
        //TODO use or delete
        private TextView mmRating;
        private long mmMovieId;
        private GridItemClickListener mmListener;


        public MovieItemViewHolder(View itemView, GridItemClickListener listener) {
            super(itemView);
            mmListener = listener;
            mmPoster = (ImageView) itemView.findViewById(R.id.movie_thumbnail_item_poster);
            mmTitle = (TextView) itemView.findViewById(R.id.movie_thumbnail_item_title);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mmListener.onGridItemClick(mmMovieId, mmPoster);
            Log.v(LOT_TAG, "moviedID: " + mmMovieId);
        }
    }


}
