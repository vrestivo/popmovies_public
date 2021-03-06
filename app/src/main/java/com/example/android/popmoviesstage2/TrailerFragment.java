package com.example.android.popmoviesstage2;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.example.android.popmoviesstage2.data.DataContract;

import static com.example.android.popmoviesstage2.FragmentMain.ARG_MOVIE_ID;

/**
 * Created by devbox on 12/15/16.
 */

public class TrailerFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, TrailerRecycleViewAdapter.TrailerOnClickListener {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private final int LOADER_ID = 1234;
    private Context mContext;
    private long mMovieId;
    private TrailerRecycleViewAdapter mTrailerRvAdapter;
    private TextView mNoTrailersMsg;


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

        mTrailerRvAdapter = new TrailerRecycleViewAdapter(this);

        View rootView = inflater.inflate(R.layout.trailers_rv_layout, container, false);
        RecyclerView trailerRv = (RecyclerView) rootView.findViewById(R.id.trailer_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mNoTrailersMsg = (TextView) rootView.findViewById(R.id.msg_no_trailers);

        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        trailerRv.setAdapter(mTrailerRvAdapter);
        trailerRv.setLayoutManager(linearLayoutManager);

        Log.v(LOG_TAG, "_fragment id: " + this.getId());
        Log.v(LOG_TAG, "_movie id: " + mMovieId);

        getLoaderManager().initLoader(LOADER_ID, arguments, this);

        final DetailFragment detailFragment = (DetailFragment) getParentFragment();

        trailerRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detailFragment.setAffectedByUser();
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onTrailerClick(@NonNull String trailerKey) {

        String youtubeLink = Utility.getTrailerYoutubeLink(trailerKey, mContext);

        if (youtubeLink != null) {
            Intent trailerIntent = new Intent();
            trailerIntent.setAction(Intent.ACTION_VIEW);
            trailerIntent.setData(Uri.parse(youtubeLink));

            Intent chooser = Intent.createChooser(trailerIntent,
                    mContext.getString(R.string.intent_chooser_message));

            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (chooser.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(chooser);
            }

        }


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
        if(cursor!=null && cursor.moveToFirst()) {
            if(mNoTrailersMsg.getVisibility() == View.VISIBLE){
                mNoTrailersMsg.setVisibility(View.GONE);
            }
            mTrailerRvAdapter.swapCursor(cursor);
        }
        else {
            mNoTrailersMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //trailerAdapter.swapCursor(null);
        mTrailerRvAdapter.swapCursor(null);
    }
}
