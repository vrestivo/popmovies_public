package com.example.android.popmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popmoviesstage2.data.DataContract;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.example.android.popmoviesstage2.FragmentMain.ARG_MOVIE_ID;


/**
 * Created by devbox on 5/3/17.
 */

public class OverviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        CompoundButton.OnCheckedChangeListener {


    public static final String FRAGMENT_TAG = "OVERVIEW_FRAGMENT";

    private Uri itemUri = null;

    //private final String URI_TAG = ""

    private static final int LOADER_ID = 123;

    private final String LOG_TAG = this.getClass().getSimpleName();


    //Define Data Fields for the fragment
    private TextView mTitle;
    private ImageView mMoviePoster;
    private TextView mReleaseDate;
    private TextView mRuntime;
    private TextView mRating;
    private TextView mOverview;
    private CheckBox mCheckBox;
    private String mMovieId;
    private long mMovieIdLong;
    private Toolbar mToolbar;
    private ActionBar mActionbar;
    private boolean mTowPane;

    private String mBundleUriKey = "uri";

    private FragmentTabHost mTabHost = null;


    //Mandatory empty constructore required by the fragment manager
    public OverviewFragment() {
    }


    public static OverviewFragment newInstance(long movieId) {

        Bundle args = new Bundle();
        args.putLong(ARG_MOVIE_ID, movieId);
        OverviewFragment fragment = new OverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Uri toggleUri;

        Bundle bundle = new Bundle();

        //FIXME
        //DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.movie_detail_container);

        Log.v(LOG_TAG, "_in onclick listener: fragment not null");

        if (isChecked) {
            //update favorite flag to 1 if favorite button is selected
            toggleUri = DataContract.Movies.buildToggleFavoritesUri(mMovieId, isChecked);
            bundle.putString(mBundleUriKey, toggleUri.toString());
            getLoaderManager().restartLoader(LOADER_ID, bundle, this);
        } else {
            //update favorite flag to 0 if favorite button is not selected
            toggleUri = DataContract.Movies.buildToggleFavoritesUri(mMovieId, isChecked);
            bundle.putString(mBundleUriKey, toggleUri.toString());
            getLoaderManager().restartLoader(LOADER_ID, bundle, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        mTowPane = MainActivity.isTwoPane();

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_MOVIE_ID)) {
            mMovieIdLong = args.getLong(ARG_MOVIE_ID);
            mMovieId = String.valueOf(mMovieIdLong);
        }

        View rootView;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //FIXME inflate lanscape overview fragment
            rootView = inflater.inflate(R.layout.fragment_detail_landscape, container, false);

        } else {
            rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        }


        //TODO delete when done
        Log.v(LOG_TAG, "_in onCreateView()");
        Log.v(LOG_TAG, "_fragment id: " + this.getId());


        //TODO DELETE WHEN DONE
/*
        Intent receivedIntent = getActivity().getIntent();

        if (receivedIntent != null && itemUri == null && receivedIntent.getData() != null) {
            itemUri = receivedIntent.getData();
            mMovieId = itemUri.getLastPathSegment();

        } else {
            //TODO decide if needed
            if (args != null) {
                String uri = args.getString(MainActivity.DETAIL_URI_TAG);
                if (uri != null) {
                    itemUri = itemUri.parse(uri);
                    mMovieId = itemUri.getLastPathSegment();
                }
            } else {
                Log.v(LOG_TAG, "_no arguments passed");
            }
        }
*/


        //get the view references for binding to data
        mMoviePoster = (ImageView) rootView.findViewById(R.id.detail_poster_image_view);
        mReleaseDate = (TextView) rootView.findViewById(R.id.detail_release_date_text_view);
        mRating = (TextView) rootView.findViewById(R.id.detail_rating_text_view);
        mOverview = (TextView) rootView.findViewById(R.id.detail_overview);
        mRuntime = (TextView) rootView.findViewById(R.id.detail_runtime_text_view);
        mCheckBox = (CheckBox) rootView.findViewById(R.id.detail_fav_button);
        mToolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);

        //set up title text view if using tabled layout
        //set up tool bar for detail activity if using phone layout
        if (mTowPane) {
            mTitle = (TextView) rootView.findViewById(R.id.detail_title_text_view);
        } else {
            //FIXME
            //set up the toolbar
            //activity.setSupportActionBar(mToolbar);
            //mActionbar = activity.getSupportActionBar();
            //mActionbar.setDisplayHomeAsUpEnabled(true);
        }
        mCheckBox.setOnCheckedChangeListener(this);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "_initializing loader");
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri request;

        //Normal fragment loading
        if (args == null) {
            request = DataContract.Movies.buildMovieWithIdUri(mMovieIdLong);

            Log.v(LOG_TAG, "_request: " + request);

            return new CursorLoader(
                    getActivity(),
                    request,
                    DataContract.Movies.defaultProjection,
                    null,
                    null,
                    null
            );

        }
        //The bundle only passed when the favorite button is pressed
        //in this case the bundle will contain a Uri to update the favorite
        //flag in SQLite database according to the button state
        else {
            request = Uri.parse(args.getString(mBundleUriKey));
            Log.v(LOG_TAG, "_request: " + request);

            return new CursorLoader(
                    getActivity(),
                    request,
                    DataContract.Movies.defaultProjection,
                    null,
                    null,
                    null
            );
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //        //Bind dataa to fields

        Context context = getActivity().getApplicationContext();

        if (data != null && data.moveToFirst()) {

            Log.v(LOG_TAG, "_non null data returned");

            if (mTowPane) {
                mTitle.setText(data.getString(DataContract.Movies.COL_TITLE_INDEX));
            } else {
                //mActionbar.setTitle(data.getString(DataContract.Movies.COL_TITLE_INDEX));
            }

            mReleaseDate.setText(
                    //Truncate data to first 4 characters, leaving only release year
                    Utility.truncateDate(
                            data.getString(DataContract.Movies.COL_REL_DATE_INDEX)
                    ));

            mRating.setText(data.getString(DataContract.Movies.COL_VOTE_AVG_INDEX));
            mOverview.setText(data.getString(DataContract.Movies.COL_OVERVIEW_INDEX));

            int favFlag = data.getInt(DataContract.Movies.COL_FAVORITES_INDEX);

            if (favFlag > 0) {
                mCheckBox.setChecked(true);
            }

            String runtime = data.getString(DataContract.Movies.COL_RUNTIME_INDEX);
            if (runtime != null) {
                mRuntime.setText(runtime + " min");
            }

            //get movie poster
            String posterPath = data.getString(DataContract.Movies.COL_POSTER_PATH_INDEX);

            //load the movie poster if exists
            if (!posterPath.isEmpty()) {
                String imageName = Uri.parse(posterPath).getLastPathSegment();

                String posterFilePath = context.getFilesDir().toString() + "/" + imageName;

                File posterFile = new File(posterFilePath);


                if (posterFile.isFile() && posterFile.exists()) {
                    Picasso.with(context).load(posterFile).into((ImageView) mMoviePoster);
                }
            }

        } else {
            Log.v(LOG_TAG, "_null data returned");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
