package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmoviesstage2.data.DataContract;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by devbox on 5/15/17.
 */

public class TrailerRecycleViewAdapter extends RecyclerView.Adapter<TrailerRecycleViewAdapter.TrailerViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View trailerCard = inflater.inflate(R.layout.trailer_card, parent, false);
        TrailerViewHolder trailerViewHolder = new TrailerViewHolder(trailerCard);

        return trailerViewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if(mCursor!=null && mCursor.moveToPosition(position)) {
            //TODO
            holder.mMovieId = mCursor.getLong(DataContract.Trailers.COL_MOVIE_ID_INDEX);
            holder.mTrailerKey = mCursor.getString(DataContract.Trailers.COL_KEY_INDEX);

            if (holder.mTrailerKey != null) {
                String fileName = holder.mTrailerKey + "_" + mContext.getString(R.string.youtube_thumbnail_default_file_suffix);
                String filePath = mContext.getFilesDir().toString() + "/" + fileName;

                File thumbnailFile = new File(filePath);

                if (thumbnailFile.exists() && thumbnailFile.isFile()) {
                    Picasso.with(mContext).load(thumbnailFile).into(holder.mThumbnail);
                }
                //TODO else set default placeholder

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

        private ImageView mThumbnail;
        private TextView mTrailerDescription;
        public long mMovieId;
        public String mTrailerKey;


        public TrailerViewHolder(View itemView) {
            super(itemView);
            mThumbnail = (ImageView) itemView.findViewById(R.id.trailer_card_thumbnail);
            mTrailerDescription = (TextView) itemView.findViewById(R.id.trailer_card_t_description);
        }

        @Override
        public void onClick(View v) {
            //TODO launch activity to watch trailer
        }


    }



}
