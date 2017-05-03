package com.example.android.popmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

/**
 *
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri itemUri = null;

    //private final String URI_TAG = ""

    private static final int LOADER_ID = 123;

    private static final String LOG_TAG = "DetailFragment: ";

    //Define Data Fields for the fragment
    private TextView mTitle;
    private ImageView mMoviePoster;
    private TextView mReleaseDate;
    private TextView mRuntime;
    private TextView mRating;
    private TextView mOverview;
    private CheckBox mCheckBox;
    private String mMovieId;
    private Toolbar mToolbar;
    private ActionBar mActionbar;
    private boolean mTowPane;

    private String mBundleUriKey = "uri";

    private FragmentTabHost mTabHost = null;

    public DetailFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        mTowPane = MainActivity.isTwoPane();

        Intent receivedIntent = activity.getIntent();

        View rootView;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView = inflater.inflate(R.layout.fragment_detail_landscape, container, false);

        } else {
            rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        }


        if (receivedIntent != null && itemUri == null && receivedIntent.getData() != null) {
            itemUri = receivedIntent.getData();
            mMovieId = itemUri.getLastPathSegment();

        } else {

            Bundle args = getArguments();

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
        }
        else {
            //set up the toolbar
            activity.setSupportActionBar(mToolbar);
            mActionbar = activity.getSupportActionBar();
            mActionbar.setDisplayHomeAsUpEnabled(true);
        }





        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                Uri toggleUri;

                Bundle bundle = new Bundle();

                DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.movie_detail_container);


                if (detailFragment != null) {

                    if (b) {
                        //update favorite flag to 1 if favorite button is selected
                        toggleUri = DataContract.Movies.buildToggleFavoritesUri(mMovieId, b);
                        bundle.putString(mBundleUriKey, toggleUri.toString());
                        getLoaderManager().restartLoader(LOADER_ID, bundle, detailFragment);
                    } else {
                        //update favorite flag to 0 if favorite button is not selected
                        toggleUri = DataContract.Movies.buildToggleFavoritesUri(mMovieId, b);
                        bundle.putString(mBundleUriKey, toggleUri.toString());
                        getLoaderManager().restartLoader(LOADER_ID, bundle, detailFragment);
                    }
                }
            }
        });




        //TODO DELETE WHEN DONE
        /***** OLD  TABHOST STUFF *****/
/*

        //Create a bundle with movie ID and pass it to tabs
        Bundle tabContentArgs = new Bundle();
        tabContentArgs.putString(DataContract.KEY_MOVIE_ID, mMovieId);

        //create and settup tabs for selection between viewing trailers and reviews
        //in the detail view
        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        //connect tabhost to the FrameLayout which will hold the content
        mTabHost.setup(activity, getChildFragmentManager(), android.R.id.tabcontent);
        //add trailers tab
        mTabHost.addTab(mTabHost.newTabSpec("trailers").setIndicator("Trailers"),
                TrailerTabContent.class, tabContentArgs);
        //add reviews tab
        mTabHost.addTab(mTabHost.newTabSpec("reviews").setIndicator("Reviews"),
                ReviewsTabContent.class, tabContentArgs);
*/

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri request;

        if (args != null) {

            request = Uri.parse(args.getString(mBundleUriKey));
            return new CursorLoader(
                    getActivity(),
                    request,
                    DataContract.Movies.defaultProjection,
                    null,
                    null,
                    null
            );

        }

        if (itemUri != null) {
            return new CursorLoader(
                    getActivity(),
                    itemUri,
                    DataContract.Movies.defaultProjection,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //        //Bind dataa to fields

        Context context = getActivity().getApplicationContext();

        if (data != null && data.moveToFirst()) {

            if (mTowPane) {
                mTitle.setText(data.getString(DataContract.Movies.COL_TITLE_INDEX));
            } else {
                mActionbar.setTitle(data.getString(DataContract.Movies.COL_TITLE_INDEX));
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
