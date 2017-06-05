package com.example.android.popmoviesstage2.data_sync;

/**
 * Created by devbox on 6/4/17.
 */

public class TmdbResults {

    private String mJsonString;
    private int mStatusCode;

    public TmdbResults() {
        super();
        mJsonString = null;
        mStatusCode = SyncAdapter.STATUS_UNKNOWN_ERROR;
    }

    //setters

    public void setJsonString(String newJsonStaring){
        mJsonString = newJsonStaring;
    }

    public void setStatusCode(int newStatusCode){
        mStatusCode = newStatusCode;
    }

    //getters
    public String getJsonString(){
        return mJsonString;
    }

    public int getStatusCode(){
        return mStatusCode;
    }

}
