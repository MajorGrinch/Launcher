package tech.doujiang.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import tech.doujiang.launcher.activity.ContactSMSActivityBeta;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MessageBean;
import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.model.SystemSMSBean;
import tech.doujiang.launcher.util.SMSOpUtil;
import tech.doujiang.launcher.util.SelfDestruction;

public class SMSBlockService extends Service {
    private MyDatabaseHelper dbHelper;
    private static final String TAG = "SMSBlockService";
    private SmsObserver smsObserver;
    private Uri smsUri = Uri.parse("content://sms/");
    private String username;
    private SelfDestruction selfDestruction;

    public SMSBlockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = MyDatabaseHelper.getDBHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyApplication.setContactList(dbHelper.getContact());
        MyApplication.setNumbers(dbHelper.getNumber());
        smsObserver = new SmsObserver(new Handler());
        username = intent.getStringExtra("username");
        if(username != null){
            selfDestruction = new SelfDestruction(username);
            selfDestruction.run();
        }
        getContentResolver().registerContentObserver(smsUri, true, smsObserver);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(smsObserver);
        Log.d(TAG, "unregisterCOntentObserver");
        selfDestruction.stop();
        //restore sms from live
        ArrayList<SystemSMSBean> systemSMSList = dbHelper.getSystemSMS();
        SMSOpUtil.addSMS(systemSMSList);
        dbHelper.clearSystemSMS();
        super.onDestroy();
    }

    class SmsObserver extends ContentObserver {
        private static final String TAG = "SmsObserver";

        private Uri sms_out = Uri.parse("content://sms/sent");
        private Uri sms_in = Uri.parse("content://sms/inbox");

        public SmsObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // Handle change.
            Log.d(TAG, "onChange(boolean selfChange, Uri uri). selfChange=" + selfChange + ", uri=" + uri);

            if (uri.toString().equals("content://sms/raw")) {
                Log.d(TAG, "execute raw");
                return;
            }
            if (uri == null) {
                uri = smsUri;
                Log.d(TAG, "execute uri==null");
            }
            Cursor cursor = MyApplication.getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                String content = cursor.getString(cursor.getColumnIndex("body"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                Long date = cursor.getLong(cursor.getColumnIndex("date"));
                if (protocol == null) {
                    Log.d(TAG, "outgoing sms");
                    if (MyApplication.getNumbers().contains(address)) {
                        Log.d(TAG, "sms to colleague");
                        SMSOpUtil.deleteSMS(address);

                    } else {
                        Log.d(TAG, "sms to live");
                    }
                } else {
                    Log.d(TAG, "incoming sms");
                    if (MyApplication.getNumbers().contains(address)) {
                        Log.d(TAG, "sms from colleague");
                        MessageBean message = new MessageBean();
                        message.setType(1);
                        message.setDate(date);
                        message.setContent(content);
                        message.setNumber(address);
                        ArrayList<ContactBean> contactList = MyApplication.getContactList();
                        int id = -1;
                        for (ContactBean contact : contactList) {
                            if (contact.getPhoneNum().equals(address)) {
                                id = contact.getContactId();
                                message.setId(id);
                                dbHelper.addMessage(message);
                                Intent intent = new Intent("xyz.majorgrinch.mobilesafe.ADD_COLLEAGUE_SMS");
                                LocalBroadcastManager localBroadcastManager =
                                        LocalBroadcastManager.getInstance(MyApplication.getContext());
                                localBroadcastManager.sendBroadcast(intent);
                                SMSOpUtil.deleteSMS(address);
                                break;
                            }
                        }
                    } else {
                        Log.d(TAG, "sms from live");
                        SystemSMSBean systemSMS = new SystemSMSBean();
                        systemSMS.setThread_id(cursor.getInt(cursor.getColumnIndex("thread_id")));
                        systemSMS.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                        systemSMS.setPerson(cursor.getString(cursor.getColumnIndex("person")));
                        systemSMS.setDate(cursor.getLong(cursor.getColumnIndex("date")));
                        systemSMS.setDate_sent(cursor.getLong(cursor.getColumnIndex("date_sent")));
                        systemSMS.setProtocol(cursor.getInt(cursor.getColumnIndex("protocol")));
                        systemSMS.setRead(cursor.getInt(cursor.getColumnIndex("read")));
                        systemSMS.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                        systemSMS.setType(cursor.getInt(cursor.getColumnIndex("type")));
                        systemSMS.setRead(cursor.getInt(cursor.getColumnIndex("reply_path_present")));
                        systemSMS.setSubject(cursor.getString(cursor.getColumnIndex("subject")));
                        systemSMS.setBody(cursor.getString(cursor.getColumnIndex("body")));
                        systemSMS.setService_center(cursor.getString(cursor.getColumnIndex("service_center")));
                        systemSMS.setLocked(cursor.getInt(cursor.getColumnIndex("locked")));
                        systemSMS.setError_code(cursor.getInt(cursor.getColumnIndex("error_code")));
                        systemSMS.setSeen(cursor.getInt(cursor.getColumnIndex("seen")));
                        dbHelper.addSystemSMS(systemSMS);
                        int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                        SMSOpUtil.deleteSMS(_id);
                        Log.d(TAG,"uri: " + uri);
                    }
                }
                Log.d(TAG, content);
            }
            cursor.close();
        }
    }
}

