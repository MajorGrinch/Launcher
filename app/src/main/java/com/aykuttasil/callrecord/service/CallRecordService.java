package com.aykuttasil.callrecord.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aykuttasil.callrecord.CallRecord;
import com.aykuttasil.callrecord.helper.PrefsHelper;

/**
 * Created by aykutasil on 19.10.2016.
 */

public class CallRecordService extends Service {

    private static final String TAG = CallRecordService.class.getSimpleName();

    protected CallRecord mCallRecord;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand()");


        mCallRecord = new CallRecord.Builder(this)
                .setShowSeed(true)
                .setShowPhoneNumber(true)
                .build();
        mCallRecord.disableSaveFile();

        Log.i(TAG, "mCallRecord.startCallReceiver()");

        mCallRecord.startCallReceiver();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCallRecord.stopCallReceiver();
        Log.i(TAG, "onDestroy()");
    }
}
