package tech.doujiang.launcher.util;

import android.os.FileObserver;
import android.util.Log;
import android.widget.Toast;

import tech.doujiang.launcher.model.MyApplication;

/**
 * Created by kirk on 5/2/17.
 */

public class WorkDirListener extends FileObserver {
    private static final String TAG = "WorkDirListener";

    public WorkDirListener(String path) {
        super(path);
    }

    @Override
    public void onEvent(int event, String path) {
        int el = event & FileObserver.ALL_EVENTS;
        Log.d(TAG, "Event " + event);
        Log.d(TAG, "el " + String.valueOf(el));
        switch (el) {
            case FileObserver.ACCESS:
                Log.d(TAG, "onEvent");
                break;
            default:
                break;
        }
    }
}