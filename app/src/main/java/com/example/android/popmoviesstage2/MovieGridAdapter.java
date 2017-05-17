package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmoviesstage2.data.DataContract;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.view.LayoutInflater.from;

/**
 * Created by devbox on 5/17/17.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieItemViewHolder> {

    private Cursor mCursor;
    private Context mContext = null;


    public MovieGridAdapter(@NonNull Context context) {
        super();
        mContext = context;
    }

    @Override
    public MovieItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_thumbnail_item, parent, false);
        MovieItemViewHolder movieItemViewHolder =  new MovieItemViewHolder(view);
        return movieItemViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieItemViewHolder holder, int position) {
        if(mCursor!=null && mCursor.moveToPosition(position)){
            //Get movie poster url and load it into image view
            String posterUrl = mCursor.getString(DataContract.Movies.COL_POSTER_PATH_INDEX);
            if(posterUrl!=null && !posterUrl.isEmpty()){
                String filePath = mContext.getFilesDir().toString()+ "/"
                        + Uri.parse(posterUrl).getLastPathSegment();
                File imageFile = new File(filePath);

                if(imageFile.exists() && imageFile.isFile()){
                    Picasso.with(mContext).load(imageFile).into(holder.mmPoster);
                }
                else {
                    //TODO load a placeholder
                }
            }

            holder.mmTitle.setText(mCursor.getString(DataContract.Movies.COL_TITLE_INDEX));

        }

    }

    @Override
    public int getItemCount() {

        if(mCursor!=null){
            return mCursor.getCount();
        }
        return 0;

    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }


    class MovieItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView mmPoster;
        private TextView mmTitle;
        private TextView mmRating;


        public MovieItemViewHolder(View itemView) {
            super(itemView);

            mmPoster = (ImageView) itemView.findViewById(R.id.movie_thumbnail_item_poster);
            mmTitle = (TextView) itemView.findViewById(R.id.movie_thumbnail_item_title);
            mmRating = (TextView) itemView.findViewById(R.id.movie_thumbnail_item_rating);

        }

    }


}
