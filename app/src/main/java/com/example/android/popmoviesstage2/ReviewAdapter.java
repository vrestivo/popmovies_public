package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import at.blogc.android.views.ExpandableTextView;
import android.widget.TextView;
import com.example.android.popmoviesstage2.data.DataContract;

/**
 * Created by devbox on 12/15/16.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private Cursor mCursor = null;
    private Context mContext = null;



    public ReviewAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View reviewItem = LayoutInflater.from(mContext).inflate(R.layout.review_item_layout, parent, false);
        return new ReviewViewHolder(reviewItem);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        if(mCursor!=null && mCursor.moveToPosition(position)){
            String reviewText = mCursor.getString(DataContract.Reviews.COL_CONTENT_INDEX);
            //TODO delete logs
            Log.v(LOG_TAG, "_review text: " + reviewText);

            //set text view on container, since setting text o the inner
            //TextView will not work as per documentation
            holder.mmReviewTextView.setText(reviewText);
        }

    }

    @Override
    public int getItemCount() {
        if(mCursor!=null){
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor cursor){
        mCursor=cursor;
        notifyDataSetChanged();
    }


    class ReviewViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        private final String LOG_TAG = this.getClass().getSimpleName();
        private final float ROTATE_BY = 180f;

        public ExpandableTextView mmReviewTextView;
        public ImageButton mmExpandButton;

        //TODO set onClickListener

        public ReviewViewHolder(View itemView) {
            super(itemView);
            Log.v(LOG_TAG, "_in ReviewViewHolder");

            mmReviewTextView = (ExpandableTextView) itemView.findViewById(R.id.expandable_text_view);
            mmExpandButton = (ImageButton) itemView.findViewById(R.id.button_toggle);

            mmExpandButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.animate().rotationBy(ROTATE_BY);
            mmReviewTextView.toggle();
        }
    }


}
