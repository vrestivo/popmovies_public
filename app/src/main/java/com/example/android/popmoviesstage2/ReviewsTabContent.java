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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.popmoviesstage2.data.DataContract;

/**
 * Created by devbox on 12/15/16.
 */


public class ReviewsTabContent extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = "ReviewsTabContent";
    private final int LOADER_ID = 12345;
    private Context mContext;
    private String mMovieId;
    private ReviewAdapter reviewAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        Bundle bundle = getArguments();

        mMovieId = bundle.getString(DataContract.KEY_MOVIE_ID);

        reviewAdapter = new ReviewAdapter(mContext, null, true);

        View rootView = inflater.inflate(R.layout.tab_content_layout, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.tab_content_listview);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listView.setAdapter(reviewAdapter);

        Log.v(LOG_TAG, "_movie id: " + mMovieId);

        getLoaderManager().initLoader(LOADER_ID, bundle, this);


        return rootView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        if (args != null) {
            Log.v("_inside reviewLoader", "args not null");

            Uri request = DataContract.Reviews.buildReviewsByMovieIdUri(Long.parseLong(mMovieId));

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        reviewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        reviewAdapter.swapCursor(null);
    }
}
