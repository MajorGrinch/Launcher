package tech.doujiang.launcher.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.util.ArrayList;
import java.util.Date;

import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.model.SystemSMSBean;

/**
 * Created by kirk on 4/25/17.
 */

public class SMSOpUtil {
    private static final String TAG = "SMSOpUtil";
    public static void deleteSMS(String address) {
        String sms_path = "data/data/com.android.providers.telephony/databases/mmssms.db";
        try {
            Command command_777 = new Command(0, "chmod 777 " + sms_path);
            RootTools.getShell(true).add(command_777);
            SQLiteDatabase db = SQLiteDatabase.openDatabase(sms_path, null, SQLiteDatabase.OPEN_READWRITE);
            int n = db.delete("sms", "address= ? ", new String[]{address});
            Log.d("Delete", n + "rows");
            db.close();
            Command command_660 = new Command(0, "chmod 660" + sms_path);
            RootTools.getShell(true).add(command_660);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteSMS(int id){
        String sms_path = "data/data/com.android.providers.telephony/databases/mmssms.db";
        try {
            Command command_777 = new Command(0, "chmod 777 " + sms_path);
            RootTools.getShell(true).add(command_777);
            SQLiteDatabase db = SQLiteDatabase.openDatabase(sms_path, null, SQLiteDatabase.OPEN_READWRITE);
            int n = db.delete("sms", "_id = ? ", new String[]{String.valueOf(id)});
            Log.d("Delete", n + "rows");
            db.close();
            Command command_660 = new Command(0, "chmod 660" + sms_path);
            RootTools.getShell(true).add(command_660);
        } catch (Exception ex) {
            Log.e("SMSOputil","Exception");
        }
    }

    public static void addSMS(ArrayList<SystemSMSBean> systemSMSList){
        String sms_path = "data/data/com.android.providers.telephony/databases/mmssms.db";
        try {
            Command command_777 = new Command(0, "chmod 777 " + sms_path);
            RootTools.getShell(true).add(command_777);
            SQLiteDatabase db = SQLiteDatabase.openDatabase(sms_path, null, SQLiteDatabase.OPEN_READWRITE);
            for (SystemSMSBean systemSMS : systemSMSList) {
                ContentValues values = new ContentValues();
                values.put("thread_id", systemSMS.getThread_id());
                values.put("address", systemSMS.getAddress());
                values.put("person", systemSMS.getPerson());
                values.put("date", systemSMS.getDate());
                values.put("date_sent", systemSMS.getDate_sent());
                values.put("protocol", systemSMS.getProtocol());
                values.put("read", systemSMS.getRead());
                values.put("status", systemSMS.getStatus());
                values.put("type", systemSMS.getType());
                values.put("reply_path_present", systemSMS.getReply_path_present());
                values.put("subject", systemSMS.getSubject());
                values.put("body", systemSMS.getBody());
                values.put("service_center", systemSMS.getService_center());
                values.put("locked", systemSMS.getLocked());
                values.put("error_code", systemSMS.getError_code());
                values.put("seen", systemSMS.getSeen());
                Log.d(TAG, systemSMS.getBody());
                Log.d(TAG, "execute restore sms");
                long n = db.insertWithOnConflict("sms", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d(TAG, "insert successfully as row " + n);
            }
            db.close();
            Command command_660 = new Command(0, "chmod 660" + sms_path);
            RootTools.getShell(true).add(command_660);
            Log.d(TAG, "restore sms");
        } catch (Exception ex) {
            Log.e("SMSOputil","add SMS Exception");
        }
    }
}
