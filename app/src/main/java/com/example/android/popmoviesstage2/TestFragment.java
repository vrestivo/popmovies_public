package com.example.android.popmoviesstage2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by devbox on 5/3/17.
 */

public class TestFragment extends Fragment {


    private final String LOG_TAG = this.getClass().getSimpleName();

    public static String ARG_TEST = "ARG_TEST";

    public static TestFragment newInstance(int pos) {

        Bundle args = new Bundle();
        args.putInt(ARG_TEST, pos);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.test_fragment, container, false);

        Bundle args = getArguments();

        Log.v(LOG_TAG, "_inside woozaaah onCreateView()");

        if(args!= null){
            TextView textView  = (TextView) rootView.findViewById(R.id.test_text_view);
            int value = args.getInt(TestFragment.ARG_TEST);
            Log.v(LOG_TAG, "_inside long value " + value);
            textView.setText("WAAAAAAAA");
        }

        return rootView;

    }






}
