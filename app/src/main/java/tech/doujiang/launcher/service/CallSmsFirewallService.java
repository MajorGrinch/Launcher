package tech.doujiang.launcher.service;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.LauncherActivity;
import tech.doujiang.launcher.activity.LoginActivity;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.model.CallLogBean;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.util.Constant;


/*
* 分为两种工作情况：
*   1. 在工作区内：
*       （1）对来电、去电进行检查，不在工作区通讯录内即挂断
*       （2）对发送、接收短信进行检查，不在工作区通讯录内的取消短信通知；
*           在通讯录内的，删除系统短信记录，存入工作区数据库
*       （3）工作区内禁用复制、粘贴（剪切板），禁止写入sd卡、禁止USB
*   2. 在非工作区下：
*       （1）对来电、去电进行检查，在工作区通讯录内即挂断
*       （2）对发送、接收短信进行检查，在工作区通讯录内即删除另存到工作区数据库
*      */
public class CallSmsFirewallService extends Service {
    private static final String TAG = "CallSmsFirewallService";
    private int PRIORITY = 100;
    private int mId = 1234;

    private ArrayList<ContactBean> contactList;
    private ArrayList<String> numbers;
    private WorkspaceDBHelper dbHelper;

    private PhoneListener listener;
    private TelephonyManager tm;
    private Constant constant = new Constant();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("CallSmsFirewallService", "onCreate");
        dbHelper = WorkspaceDBHelper.getDBHelper(this);
        contactList = dbHelper.getContact();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Build Foreground Service.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.info)
                .setContentTitle("MobileSafe")
                .setContentText("Your phone is under protection.");
        Intent resultIntent = new Intent(this, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LauncherActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);//服务关闭的时候就取消电话状态的监听
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class PhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.e("onCallStateChanged: ", Integer.toString(state) + " inComingNumber:" + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // 空闲状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    callManager(incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    callManager(incomingNumber);
                    break;
                default:
                    break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }

        private void callManager(String incomingNumber) {
            Log.e("ServiceRun: ", Boolean.toString(constant.run(getApplicationContext())));
            Log.e("IncomingNUmber: ", incomingNumber);
            numbers = dbHelper.getNumber();
            if (constant.run(getApplicationContext())) {
                if (numbers.contains(incomingNumber)) {
                    Log.e("SafeLaunher: ", incomingNumber);
                    Uri uri = Uri.parse("content://call_log/calls");
                    getContentResolver().registerContentObserver(uri, true,
                            new CallLogObserver(new Handler(), incomingNumber));
                } else {
                    endCall(incomingNumber);
                }
            } else {
                if (numbers.contains(incomingNumber)) {
                    endCall(incomingNumber);
                    Uri uri = Uri.parse("content://call_log/calls");
                    getContentResolver().registerContentObserver(uri, true,
                            new CallLogObserver(new Handler(), incomingNumber));
                }
            }
        }

    }

    private class CallLogObserver extends ContentObserver {
        private String incomingNumber;

        public CallLogObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        // 当观察到数据发生改变的时候调用的方法.
        @Override
        public void onChange(boolean selfChange) {
            Log.i(TAG, "呼叫记录变化了.");
            deleteCallLog(incomingNumber);
            getContentResolver().unregisterContentObserver(this);//删除完通话记录之后就立马进行移除内容观察者
            super.onChange(selfChange);
        }
    }

    /**
     * 挂断电话的方法.
     *
     * @param incomingNumber
     */
    public void endCall(String incomingNumber) {
        try {
            Log.e("endCall: ",incomingNumber);
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null,
                    new Object[] { TELEPHONY_SERVICE });
            ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 移除呼叫记录
     *
     * @param incomingNumber
     */
    public void deleteCallLog(String incomingNumber) {
        // Here add CallLog.
        Uri uri = CallLog.Calls.CONTENT_URI;
        ContentResolver cr = getContentResolver();
        contactList = dbHelper.getContact();
        // 查询的列
        String[] projection = { CallLog.Calls.DATE, // 日期
                CallLog.Calls.TYPE, // 类型
                CallLog.Calls.DURATION
        };
        Cursor cursor = cr.query(uri, projection, "number = ?", new String[]{incomingNumber}, null);
        while (cursor.moveToNext()) {
            CallLogBean callLog = new CallLogBean();
            callLog.setNumber(incomingNumber);
            callLog.setDate(System.currentTimeMillis());
            callLog.setDuration(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION)));
            callLog.setType((cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))));
            callLog.setIsRead(0);
            for (ContactBean contact : contactList) {
                if (contact.getPhoneNum().equals(incomingNumber)) {
                    callLog.setId(contact.getContactId());
                    break;
                }
            }
            dbHelper.addCallLog(callLog);
        }
        Log.e("deleteCallLog: ", incomingNumber);
        Uri deleteUri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(deleteUri, "number = ?", new String[] { incomingNumber });
        getContentResolver().delete(Uri.parse("content://sms"), "address = ?", new String[] { incomingNumber });
    }
}
