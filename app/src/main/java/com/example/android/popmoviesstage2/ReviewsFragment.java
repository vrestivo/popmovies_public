package com.example.android.popmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmoviesstage2.data.DataContract;

import java.util.ArrayList;

import static com.example.android.popmoviesstage2.FragmentMain.ARG_MOVIE_ID;

/**
 * Created by devbox on 12/15/16.
 */


public class ReviewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final int LOADER_ID = 12345;
    private final String KEY_REVIEW_EXPANSION_TRACKER = "KEY_REVIEW_EXPANSION_TRACKER";
    private Context mContext;
    private long mMovieId = 0;
    private ReviewAdapter mReviewAdapter = null;
    private TextView mNoReviewsMsg;
    private ArrayList<Boolean> mReviewListExpansionTracker = null;

    public static ReviewsFragment newInstance(long movieId) {

        Bundle args = new Bundle();
        args.putLong(ARG_MOVIE_ID, movieId);
        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        Bundle arguments = getArguments();

        Log.v(LOG_TAG, "_fragment id: " + this.getId());

        if (arguments.containsKey(ARG_MOVIE_ID)){
            mMovieId = arguments.getLong(ARG_MOVIE_ID);
        }


        View rootView = inflater.inflate(R.layout.reviews_layout, container, false);
        mNoReviewsMsg = (TextView) rootView.findViewById(R.id.msg_no_reviews);
        RecyclerView reviewList = (RecyclerView) rootView.findViewById(R.id.review_list);
        mReviewAdapter = new ReviewAdapter(mContext);

        if(savedInstanceState!= null && savedInstanceState.containsKey(KEY_REVIEW_EXPANSION_TRACKER)) {
            mReviewAdapter.setExpansionTracker((ArrayList<Boolean>) savedInstanceState.getSerializable(KEY_REVIEW_EXPANSION_TRACKER));
        }

        reviewList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        reviewList.setAdapter(mReviewAdapter);

        Log.v(LOG_TAG, "_movie id: " + mMovieId);

        getLoaderManager().initLoader(LOADER_ID, arguments, this);


        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        if (args != null) {
            Log.v("_inside reviewLoader", "args not null");

            Uri request = DataContract.Reviews.buildReviewsByMovieIdUri((mMovieId));

            return new CursorLoader(
                    getActivity(),
                    request,
                    DataContract.Reviews.defaultReviewsProjection,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mReviewAdapter!=null){
            mReviewListExpansionTracker = mReviewAdapter.getExpansionTracker();
            outState.putSerializable(KEY_REVIEW_EXPANSION_TRACKER, mReviewListExpansionTracker);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor!=null && cursor.moveToFirst()) {
            if(mNoReviewsMsg.getVisibility() == View.VISIBLE){
                mNoReviewsMsg.setVisibility(View.GONE);
            }
            mReviewAdapter.swapCursor(cursor);
        }
        else {
            //TODO make the no data message visible
            mNoReviewsMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mReviewAdapter.swapCursor(null);
    }
}
