package tech.doujiang.launcher.Receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import static android.content.Context.ACTIVITY_SERVICE;

public class SelfDestructionReceiver extends BroadcastReceiver {
    public SelfDestructionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Self Destruction", Toast.LENGTH_SHORT).show();
        ActivityManager actman = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        File dir = new File(Environment.getExternalStorageDirectory() + "/workdata");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        actman.clearApplicationUserData();
    }
}
