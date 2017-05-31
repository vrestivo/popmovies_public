package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popmoviesstage2.data.DataContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by devbox on 5/15/17.
 */

public class TrailerRecycleViewAdapter extends RecyclerView.Adapter<TrailerRecycleViewAdapter.TrailerViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private TrailerOnClickListener mListener;

    public TrailerRecycleViewAdapter(TrailerOnClickListener listener) {
        super();
        mListener = listener;
    }

    interface TrailerOnClickListener {
        void onTrailerClick(@NonNull String trailerKey);
    }


    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View trailerCard = inflater.inflate(R.layout.trailer_card, parent, false);
        TrailerViewHolder trailerViewHolder = new TrailerViewHolder(trailerCard, mListener);

        return trailerViewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if(mCursor!=null && mCursor.moveToPosition(position)) {

            holder.mmMovieId = mCursor.getLong(DataContract.Trailers.COL_MOVIE_ID_INDEX);
            holder.mmTrailerKey = mCursor.getString(DataContract.Trailers.COL_KEY_INDEX);

            if (holder.mmTrailerKey != null) {
                String fileName = holder.mmTrailerKey + "_" + mContext.getString(R.string.youtube_thumbnail_default_file_suffix);
                String filePath = mContext.getFilesDir().toString() + "/" + fileName;

                final File thumbnailFile = new File(filePath);

                if (thumbnailFile.exists() && thumbnailFile.isFile()) {

                    //final variable needed for the inner callback class
                    final TrailerViewHolder innerHolder = holder;

                    Picasso.with(mContext)
                            .load(thumbnailFile)
                            .noPlaceholder()
                            .into(innerHolder.mmThumbnail, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Picasso.with(mContext)
                                            .load(thumbnailFile)
                                            .into(innerHolder.mmThumbnail);
                                }

                                @Override
                                public void onError() {
                                    Picasso.with(mContext)
                                            .load(R.drawable.ic_play_circle_outline_48px)
                                            .into(innerHolder.mmThumbnail);
                                }
                            });

                }
            }
        }
    }


    @Override
    public int getItemCount() {
        if(mCursor!=null){
            return mCursor.getCount();
        }
        else {
            return 0;
        }
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        private ImageView mmThumbnail;
        public long mmMovieId;
        public String mmTrailerKey;
        private TrailerOnClickListener mmListener;


        public TrailerViewHolder(View itemView, TrailerOnClickListener listener) {
            super(itemView);
            mmThumbnail = (ImageView) itemView.findViewById(R.id.trailer_card_thumbnail);
            mmListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mmListener.onTrailerClick(mmTrailerKey);
        }
    }


}
