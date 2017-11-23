package tech.doujiang.launcher.service;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


import com.jaredrummler.android.processes.AndroidProcesses;
import com.stericson.RootTools.RootTools;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.util.OpenfileUtil;

import static com.stericson.RootTools.RootTools.killProcess;

public class AppMonitorService extends Service {
    private static final String TAG = "AppMonitorService";
    private Timer mTimer;
    private LockTask lockTask;
    public AppMonitorService() {
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
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mTimer.cancel();
    }
    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            lockTask = new LockTask(this);
            mTimer.schedule(lockTask, 0, 5000);
        }
    }

    class LockTask extends TimerTask {

        private Context mContext;
        public LockTask(Context context) {
            Log.d(TAG, "LockTask is Created");
            mContext = context;
        }

        @Override
        public void run() {
            boolean isactive = OpenfileUtil.isServiceWork(MyApplication.getContext(),
                    "tech.doujiang.launcher.service.WorkDirService");
            Log.d(TAG, "workdirservice is " + isactive);
            if( !isactive){
                String workdirPath = Environment.getExternalStorageDirectory().getPath() + "/workdata";
                Log.d(TAG, workdirPath);
                Intent workdirserv = new Intent(mContext, WorkDirService.class);
                workdirserv.putExtra("dirpath", workdirPath);
                mContext.startService(workdirserv);
            }
            List<ActivityManager.RunningAppProcessInfo> processes =
                    AndroidProcesses.getRunningAppProcessInfo(mContext);
            for(ActivityManager.RunningAppProcessInfo process:processes){
                String processName = process.processName;
                if(processName.equals("com.martian.ttbook"))
                {
                    Log.d(TAG, "illegal software");
                    int pid = process.pid;
                    killProcess(String.valueOf(pid));
                }
            }
        }
    }
}
