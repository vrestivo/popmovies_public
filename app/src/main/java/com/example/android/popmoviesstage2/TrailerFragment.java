package com.example.android.popmoviesstage2;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popmoviesstage2.data.DataContract;

import static com.example.android.popmoviesstage2.FragmentMain.ARG_MOVIE_ID;

/**
 * Created by devbox on 12/15/16.
 */

public class TrailerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final int LOADER_ID = 1234;
    private Context mContext;
    private long mMovieId;
    private RecyclerView mTrailerRv;
    private LinearLayoutManager mLinearLayoutManager;
    private TrailerRecycleViewAdapter mTrailerRvAdapter;
    //TODO delete when done


    public static TrailerFragment newInstance(long movieId) {

        Bundle args = new Bundle();
        args.putLong(ARG_MOVIE_ID, movieId);
        TrailerFragment fragment = new TrailerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        Bundle arguments = getArguments();
        mMovieId = arguments.getLong(ARG_MOVIE_ID);

        //get a root view
        View rootView = inflater.inflate(R.layout.trailers_rv_layout, container, false);

        //find the recycler view
        mTrailerRv = (RecyclerView) rootView.findViewById(R.id.trailer_rv);
        mTrailerRv.setHasFixedSize(true);

        //create an adapter
        mTrailerRvAdapter = new TrailerRecycleViewAdapter();

        //get layout manager for the recycler view
        //set horizontal scrolling on landscape orientation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mLinearLayoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                    );
        }
        else {
            mLinearLayoutManager = new LinearLayoutManager(mContext);
        }

        //TODO delete
        //NOTE if leave just a default constructor (vertical) the
        //thumbnails are showing in a normal vertical orientation
        //mLinearLayoutManager = new LinearLayoutManager(mContext);


        mTrailerRv.setAdapter(mTrailerRvAdapter);
        mTrailerRv.setLayoutManager(mLinearLayoutManager);

        Log.v(LOG_TAG, "_fragment id: " + this.getId());
        Log.v(LOG_TAG, "_movie id: " + mMovieId);


        getLoaderManager().initLoader(LOADER_ID, arguments, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (bundle != null) {
            Uri request = DataContract.Trailers.buildTrailersByMovieIdUri(mMovieId);

            return new CursorLoader(
                    getActivity(),
                    request,
                    DataContract.Trailers.defaultTrailerProjection,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //trailerAdapter.swapCursor(cursor);
        mTrailerRvAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //trailerAdapter.swapCursor(null);
        mTrailerRvAdapter.swapCursor(null);
    }
}
