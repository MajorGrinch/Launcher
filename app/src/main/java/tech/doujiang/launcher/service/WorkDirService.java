package tech.doujiang.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import tech.doujiang.launcher.util.WorkDirListener;

public class WorkDirService extends Service {
    private WorkDirListener workDirListener;
    private static final String TAG = "WorkDirService";
    public WorkDirService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String dirpath = intent.getStringExtra("dirpath");
        Log.d(TAG, dirpath);
        workDirListener = new WorkDirListener(dirpath);
        workDirListener.startWatching();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(workDirListener != null){
            workDirListener.stopWatching();
        }
    }
}
