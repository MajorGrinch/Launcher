package tech.doujiang.launcher.service;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.LauncherActivity;
import tech.doujiang.launcher.activity.LoginActivity;
import tech.doujiang.launcher.util.Constant;

public class ServerConnectService extends Service {
    private final String TAG = "ServerConnectService";
    private Timer mTimer;
    private LockTask lockTask;
    private int mId = 1235;
    private Constant constant;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        constant = new Constant();
        Log.v(TAG, "ServerConnectService is Created");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Build Foreground Service.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.info)
                .setContentTitle("MobileSafe")
                .setContentText("Keep tracing location.");
        Intent resultIntent = new Intent(this, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LauncherActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());

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
        private PackageManager packageManager;
        private ActivityManager activityManager;
        LockTask lockTask = this;

        public LockTask(Context context) {
            this.context = context;
            this.packageManager = context.getPackageManager();
            activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Log.d(TAG, "LockTask is Created");
        }

        @Override
        public void run() {
            if (constant.run(context)) {
                List<String> packages = constant.getPackageNames(context);
                String launcher = context.getPackageName();
                for (String pac : packages) {
                    if (pac.contains("com.android") || pac.equals(launcher))
                        continue;
                    activityManager.killBackgroundProcesses(pac);
                    Log.e("KillProcess: ", pac);
                }
            }
        }

    }
}