package com.example.android.popmoviesstage2.data_sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Service to bind sync adapter into the framework
 */

public class SyncAdapterService extends Service {

    //stores SyncAcapter class reference
    private static SyncAdapter sSyncAdapter = null;
    //stores thread-safe lock
    private static final Object sSyncAdapterLock = new Object();


    @Override
    public void onCreate() {
        //super.onCreate();
        synchronized (sSyncAdapterLock){
            if(sSyncAdapter == null){
                //the second argument, causes to call setIsSyncable with 1;
                //which makes the tread syncable (requires WRITE_SYNC_SETTINGS permission)
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * returns an object throuch which
     * allows an external process to call onPerformSync()
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
