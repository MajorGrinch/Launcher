package tech.doujiang.launcher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import tech.doujiang.launcher.service.WorkDirService;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        *   开机启动防火墙服务
        * */
        String workdirPath = Environment.getExternalStorageDirectory().getPath() + "/workdata";
        Log.d(TAG, workdirPath);
        Intent workdirserv = new Intent(context, WorkDirService.class);
        workdirserv.putExtra("dirpath", workdirPath);
        context.startService(workdirserv);
    }
}
