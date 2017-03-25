package tech.doujiang.launcher.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by doujiang_zheng on 16/8/8.
 */
public class Constant {
    private ActivityManager mActivityManager;

    public int LAYOUT_INCOMING = 1;
    public int LAYOUT_OUTGOING = 2;
    public String launcher = "tech.doujiang.launcher";

    public boolean run(Context context) {
        if (mActivityManager == null) {
            mActivityManager =  (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        }
        String[] activePackages;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            activePackages = getActivePackages();
        } else {
            activePackages = getActivePackagesCompat();
        }
        if (activePackages != null) {
            return Arrays.asList(activePackages).contains(launcher);
        }
        return false;
    }

    public List<String> getPackageNames(Context context) {
        if (mActivityManager == null) {
            mActivityManager =  (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        }
        String[] activePackages;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            activePackages = getActivePackages();
        } else {
            activePackages = getActivePackagesCompat();
        }
        if (activePackages != null) {
            return Arrays.asList(activePackages);
        }
        return null;
    }


    public String[]  getActivePackagesCompat() {
        final List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        final ComponentName componentName = taskInfo.get(0).topActivity;
        final String[] activePackages = new String[1];
        activePackages[0] = componentName.getPackageName();
        return activePackages;
    }

    public String[] getActivePackages() {
        final Set<String> activePackages = new HashSet<String>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }
}
