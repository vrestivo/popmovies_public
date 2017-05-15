package com.example.android.popmoviesstage2;

import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    private TrailerRecycleViewAdapter mTrailerRvAdapter;
    //TODO delete when done
    private TrailerAdapter trailerAdapter;


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
        mContext = getActivity().getApplicationContext();
        Bundle arguments = getArguments();
        mMovieId = arguments.getLong(ARG_MOVIE_ID);

        //trailerAdapter = new TrailerAdapter(mContext, null);

        //View rootView = inflater.inflate(R.layout.trailers_layout, container, false);
        //ListView listView = (ListView) rootView.findViewById(R.id.trailer_list);
        //listView.setAdapter(trailerAdapter);

        mTrailerRvAdapter = new TrailerRecycleViewAdapter();

        View rootView = inflater.inflate(R.layout.trailers_rv_layout, container, false);
        RecyclerView trailerRv = (RecyclerView) rootView.findViewById(R.id.trailer_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        trailerRv.setAdapter(mTrailerRvAdapter);
        trailerRv.setLayoutManager(linearLayoutManager);

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
