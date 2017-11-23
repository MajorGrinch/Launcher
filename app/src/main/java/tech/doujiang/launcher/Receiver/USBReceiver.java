package tech.doujiang.launcher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import tech.doujiang.launcher.service.WorkDirService;

public class USBReceiver extends BroadcastReceiver {
    private static final String TAG = "USBReceiver";
    private Intent workdirserv;

    public USBReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        boolean isConnected = intent.getBooleanExtra("connected", false);
//        if (isConnected) {
//            Log.d(TAG, "USB Connected");
//            String workdirPath = Environment.getExternalStorageDirectory().getPath() + "/workdata";
//            Log.d(TAG, workdirPath);
//            workdirserv = new Intent(context, WorkDirService.class);
//            workdirserv.putExtra("dirpath", workdirPath);
//            context.startService(workdirserv);
//
//        } else {
//            Log.d(TAG, "USB Disconnected");
//            Intent stopworkServ = new Intent(context, WorkDirService.class);
//            context.stopService(stopworkServ);
//
//        }
        Log.d(TAG, "onReceive: nothing todo");
    }
}
