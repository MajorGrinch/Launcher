package com.aykuttasil.callrecord.receiver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.aykuttasil.callrecord.CallRecord;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.CallLogBean;
import tech.doujiang.launcher.model.ContactBean;

/**
 * Created by aykutasil on 19.10.2016.
 */
public class CallRecordReceiver extends PhoneCallReceiver {


    private static final String TAG = "CallRecordReceiver";

    public static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    public static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";

    private boolean isColleague;

    protected CallRecord callRecord;
    private static MediaRecorder recorder;
    private File audiofile;
    private boolean isRecordStarted = false;
    private MyDatabaseHelper dbHelper;

    public CallRecordReceiver(CallRecord callRecord) {
        isColleague = false;
        this.callRecord = callRecord;
    }

    @Override
    protected void onIncomingCallReceived(Context context, String number, Date start) {
        Log.d(TAG, number);
        Log.d(TAG, "onIncomingCallReceived");
        dbHelper = MyDatabaseHelper.getDBHelper(context);
        if( dbHelper.getContactId(number) != -1){
            isColleague = true;
        }
        if( !isColleague){
            Log.e(TAG, "end call" );
            endCall(context);
        }
    }

    @Override
    protected void onIncomingCallAnswered(Context context, String number, Date start) {
        Log.d(TAG, "onIncomingCallAnswered");
    }

    @Override
    protected void onIncomingCallEnded(Context context, String number, Date start, Date end) {
        Log.d(TAG, "onIncomingCallEnded");
        Log.d(TAG, number);
        updateCallLog(context, number);
    }

    @Override
    protected void onOutgoingCallStarted(Context context, String number, Date start) {
//        startRecord(context, "outgoing", number);
        Log.d(TAG, "outgoing call started");
        Log.d(TAG, number);
        dbHelper = MyDatabaseHelper.getDBHelper(context);
        if( dbHelper.getContactId(number) != -1){
            isColleague = true;
        }
        if( !isColleague){
            Log.e(TAG, "end call");
            endCall(context);
        }
    }

    @Override
    protected void onOutgoingCallEnded(Context context, String number, Date start, Date end) {
        //MakeCallLog(context, number);
        Log.d(TAG, "outgoing call ended: " + number);
        updateCallLog(context, number);
    }

    @Override
    protected void onMissedCall(Context context, String number, Date start) {
        Log.d(TAG, "onMissedCall");
        updateCallLog(context, number);
    }

    public void updateCallLog(Context mcontext, String incomingNumber) {
        Log.d(TAG, "updateCallLog");
        // Here add CallLog.
        Uri uri = CallLog.Calls.CONTENT_URI;
        ContentResolver cr = mcontext.getContentResolver();
        dbHelper = MyDatabaseHelper.getDBHelper(mcontext);
        ArrayList<ContactBean> contactList = dbHelper.getContact();
        int contactId = -1;
        for (ContactBean contact : contactList) {
            if (contact.getPhoneNum().equals(incomingNumber)) {
                contactId = contact.getContactId();
            }
        }
        if (contactId == -1) {
            return;
        }
        Log.d(TAG, "contactId is" + String.valueOf(contactId));
        // 查询的列
        String[] projection = {CallLog.Calls.DATE, // 日期
                CallLog.Calls.TYPE, // 类型
                CallLog.Calls.DURATION,
        };
        try {
            Cursor cursor = cr.query(uri, projection, "number = ?", new String[]{incomingNumber}, CallLog.Calls.DATE);
            if (cursor.moveToLast()) {
                CallLogBean callLog = new CallLogBean();
                callLog.setNumber(incomingNumber);
                callLog.setDate(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));
                callLog.setDuration(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION)));
                callLog.setType((cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))));
                callLog.setIsRead(0);
                callLog.setId(contactId);
                dbHelper.addCallLog(callLog);
            }
        } catch (SecurityException se) {
            Log.e(TAG, "SecurityException");
        }
        Log.e("Update Calllog", incomingNumber);
    }

    private void endCall(Context context) {
        TelephonyManager telMag = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method mthEndCall = null;
        try {
            mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            mthEndCall.setAccessible(true);
            ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
                    (Object[]) null);
            iTel.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
