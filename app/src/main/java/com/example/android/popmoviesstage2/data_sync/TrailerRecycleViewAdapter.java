package com.example.android.popmoviesstage2.data_sync;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by devbox on 5/15/17.
 */

public class TrailerRecycleViewAdapter extends RecyclerView.Adapter {

    private Cursor mCursor;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        public TrailerViewHolder(View itemView) {
            super(itemView);
        }


    }



}
