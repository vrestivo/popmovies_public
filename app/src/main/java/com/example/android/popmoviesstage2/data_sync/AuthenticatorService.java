package com.example.android.popmoviesstage2.data_sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Required service class, returns Ibinder of Authenticator object
 */

public class AuthenticatorService extends Service {


    //storagefor authenticator object
    private MovieSyncAuthenticator movieSyncAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        movieSyncAuthenticator = new MovieSyncAuthenticator(this);
    }

    /**
     * returns authenticator's Ibinder, as required by framework
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return movieSyncAuthenticator.getIBinder();
    }
}
