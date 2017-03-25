package tech.doujiang.launcher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import tech.doujiang.launcher.R;

public class MmsReceiver extends BroadcastReceiver {
    public static final String TAG = "MmsReceiver";

    public MmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        Log.i(TAG, "MmsReceiver: " + intent);
    }
}
