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
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.popmoviesstage2.data.DataContract;

/**
 * Created by devbox on 12/15/16.
 */

public class TrailerTabContent extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = "TrailerTabContent";
    private final int LOADER_ID = 1234;
    private Context mContext;
    private String mMovieId;
    private TrailerAdapter trailerAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        Bundle bundle = getArguments();
        mMovieId = bundle.getString(DataContract.KEY_MOVIE_ID);


        trailerAdapter = new TrailerAdapter(mContext, null);

        View rootView = inflater.inflate(R.layout.tab_content_layout, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.tab_content_listview);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listView.setAdapter(trailerAdapter);



        Log.v(LOG_TAG, "_movie id: " + mMovieId);

        getLoaderManager().initLoader(LOADER_ID, bundle, this);


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (bundle != null) {

            Uri request = DataContract.Trailers.buildTrailersByMovieIdUri(Long.parseLong(mMovieId));


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
        trailerAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        trailerAdapter.swapCursor(null);

    }
}
