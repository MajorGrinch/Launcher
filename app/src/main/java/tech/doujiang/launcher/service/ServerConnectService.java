package tech.doujiang.launcher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ServerConnectService extends Service {
    private final String TAG = "ServerConnectService";
    private Timer mTimer;
    private LockTask lockTask;
    private int mId = 1235;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "ServerConnectService is Created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.e(TAG, "ServerConnectService is Started");
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

        Log.d(TAG, "Service is Destroyed");
        super.onDestroy();
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            lockTask = new LockTask(this);
            mTimer.schedule(lockTask, 0, 5000);
        }
    }

    class LockTask extends TimerTask {

        private Context context;

        LockTask lockTask = this;

        public LockTask(Context context) {
            Log.d(TAG, "LockTask is Created");
        }

        @Override
        public void run() {

        }

    }
}