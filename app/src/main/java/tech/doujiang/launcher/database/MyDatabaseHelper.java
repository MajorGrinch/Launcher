package tech.doujiang.launcher.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import tech.doujiang.launcher.model.CallLogBean;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MessageBean;
import tech.doujiang.launcher.model.SMSBean;
import tech.doujiang.launcher.model.SystemSMSBean;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "StarkTech.db";
    public static final int DB_VERSION = 1;
    private static MyDatabaseHelper dbHelper;

    private static Context mContext;
    private static final String CREATE_CONTACT = "create table Contact("
            + "id integer primary key autoincrement,"
            + "name string not null,"
            + "number string not null,"
            + "photoPath string,"
            + "email string not null,"
            + "pinYin string not null"
            + ");";
    private static final String CREATE_CALLLOG = "create table CallLog("
            + "id integer not null,"
            + "date integer not null,"
            + "duration integer not null,"
            + "type integer not null," // 1. INCOMING_TYPE 2. OUTGOING_TYPE 3. MISSED_TYPE
            + "isRead integer not null,"
            + "primary key(id, date, duration, type),"
            + "foreign key(id) references Contact(id)"
            + " );";
    private static final String CREATE_MESSAGE = "create table Message("
            + "id integer not null, "
            + "date integer not null,"
            + "content text not null,"
            + "type integer not null," // 1. INCOMING_TYPE 2. OUTGOING_TYPE
            + "primary key(id, date, content, type),"
            + "foreign key(id) references Contact(id)"
            + ");";

    private static final String CREATE_KEY = "create table KeyTable("
            + "filename text primary key, "
            + "keycontent text"
            + ");";

    private static final String CREATE_SMS = "create table Sms("
            + "thread_id integer,"
            + "address text,"
            + "person text,"
            + "date integer,"
            + "date_sent integer,"
            + "protocol integer,"
            + "read integer,"
            + "status integer,"
            + "type integer,"
            + "reply_path_present integer,"
            + "subject text,"
            + "body text,"
            + "service_center text,"
            + "locked integer,"
            + "error_code integer,"
            + "seen integer,"
            + "primary key(address, date)"
            + ");";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_CONTACT);
            Log.e("db: ", CREATE_CONTACT);
            db.execSQL(CREATE_CALLLOG);
            Log.e("db: ", CREATE_CALLLOG);
            db.execSQL(CREATE_MESSAGE);
            Log.e("db: ", CREATE_MESSAGE);
            db.execSQL(CREATE_KEY);
            Log.e("db: ", CREATE_KEY);
            db.execSQL(CREATE_SMS);
            Log.e("db", CREATE_SMS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static MyDatabaseHelper getDBHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new MyDatabaseHelper(context, DB_NAME, null, DB_VERSION);
        }
        return dbHelper;
    }

    public void addSystemSMS(SystemSMSBean systemSMS){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try{
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
            db.insertWithOnConflict("Sms", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("Mydatabase", "addSystemSMS");
            db.setTransactionSuccessful();
        }catch (Exception ex){
        }
        finally {
            db.endTransaction();
            db.close();
        }
        this.close();
    }

    public void clearSystemSMS(){
        try {
            this.getWritableDatabase().delete("Sms", null, null);
        }catch (Exception ex){
        }
        this.close();
        Log.d("Mydatabase", "clearSystemSMS");
    }

    public ArrayList<SystemSMSBean> getSystemSMS(){
        ArrayList<SystemSMSBean> systemSMSList = new ArrayList<SystemSMSBean>();
        try{
            Cursor cursor = this.getWritableDatabase().query("Sms", null, null, null, null, null, null);
            while(cursor.moveToNext()){
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
                systemSMSList.add(systemSMS);
            }
            cursor.close();
        }catch (Exception ex){
            Log.d("Mydatabase", "getSystemSMS Exception");
        }
        this.close();
        return systemSMSList;
    }

    // Contact, CallLog, Message Interaction
    public void addContact(ContactBean contact) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO Contact(name, number, photoPath, email, pinYin) VALUES(?, ?, ?, ?, ?)",
                    new String[]{contact.getDisplayName(), contact.getPhoneNum(), contact.getPhotoPath(), contact.getEmail(), contact.getPinYin()});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
        this.close();
    }

    public void addCallLog(CallLogBean callLog) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", callLog.getId());
            values.put("date", callLog.getDate());
            values.put("duration", callLog.getDuration());
            values.put("type", callLog.getType());
            values.put("isRead", callLog.isRead());
            db.insertOrThrow("CallLog", null, values);
            db.setTransactionSuccessful();
            Log.d("Mydatabase", "addCallLog");
        } finally {
            db.endTransaction();
        }
        db.close();
        this.close();
    }

    public void addMessage(MessageBean message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", message.getId());
            values.put("date", message.getDate());
            values.put("content", message.getContent());
            values.put("type", message.getType());
            db.insertWithOnConflict("Message", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
        this.close();
    }

    public void addKey(String filename, String keycontent) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("keycontent", keycontent);
            values.put("filename", filename);
            long result = db.insertWithOnConflict("KeyTable", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (result != -1)
                db.setTransactionSuccessful();
            else
                throw new Exception("Insert" + filename + "failed");
            Log.d("addkey: ", "successfully!");
        } catch (Exception ex) {
            Log.e("addkey exception", ex.getMessage());
        } finally {
            db.endTransaction();
        }
        db.close();
        this.close();
    }


    public String getKey(String filename) {
        String result = null;
        Cursor cursor = this.getWritableDatabase().rawQuery("select * from KeyTable where filename = ?", new String[]{filename});
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("keycontent"));
        }
        cursor.close();
        dbHelper.close();
        return result;
    }


    public ArrayList<ContactBean> getContact() {
        ArrayList<ContactBean> contacts = new ArrayList<ContactBean>();
        Cursor cursor = null;
        try {
            cursor = this.getWritableDatabase().rawQuery("select * from Contact order by pinYin asc", null);
            while (cursor.moveToNext()) {
                ContactBean contact = new ContactBean();
                contact.setContactId(cursor.getInt(cursor.getColumnIndex("id")));
                contact.setDisplayName(cursor.getString(cursor.getColumnIndex("name")));
                contact.setPhoneNum(cursor.getString(cursor.getColumnIndex("number")));
                contact.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
                contact.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                contact.setPinYin(cursor.getString(cursor.getColumnIndex("pinYin")));
                contacts.add(contact);
            }
        } catch (Exception ex) {
            Log.e("Mydatahelper", "security exception");
        }finally {
            cursor.close();
        }
        this.close();
        return contacts;
    }

    public ArrayList<ContactBean> getContact(int contactId) {
        ArrayList<ContactBean> contacts = new ArrayList<ContactBean>();
        Cursor cursor = this.getWritableDatabase()
                .rawQuery("select * from Contact WHERE id=?  order by pinYin asc", new String[]{String.valueOf(contactId)});
        while (cursor.moveToNext()) {
            ContactBean contact = new ContactBean();
            contact.setContactId(cursor.getInt(cursor.getColumnIndex("id")));
            contact.setDisplayName(cursor.getString(cursor.getColumnIndex("name")));
            contact.setPhoneNum(cursor.getString(cursor.getColumnIndex("number")));
            contact.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
            contact.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            contact.setPinYin(cursor.getString(cursor.getColumnIndex("pinYin")));
            contacts.add(contact);
        }
        cursor.close();
        this.close();
        return contacts;
    }

    public int getContactId(String number){
        String[] projection = new String[]{"id"};
        Cursor cursor = this.getWritableDatabase().query("Contact", projection, "number=?", new String[]{number}, null, null, null);
        int contactId = -1;
        if(cursor.moveToFirst()){
            contactId = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        this.close();
        return contactId;
    }


    public ArrayList<String> getNumber() {
        ArrayList<String> numbers = new ArrayList<String>();
        Cursor cursor = this.getWritableDatabase().rawQuery("select distinct number from Contact", null);
        while (cursor.moveToNext()) {
            numbers.add(cursor.getString(cursor.getColumnIndex("number")));
        }
        cursor.close();
        this.close();
        return numbers;
    }

    public ArrayList<CallLogBean> getCallLog() {
        ArrayList<CallLogBean> callLogs = new ArrayList<CallLogBean>();
        Cursor cursor = this.getWritableDatabase()
                .rawQuery(
                        "SELECT * FROM CallLog, Contact ON CallLog.id=Contact.id ORDER BY date DESC", null);
        while (cursor.moveToNext()) {
            CallLogBean callLog = new CallLogBean();
            callLog.setId(cursor.getInt(cursor.getColumnIndex("id")));
            callLog.setName(cursor.getString(cursor.getColumnIndex("name")));
            callLog.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            callLog.setDate(cursor.getLong(cursor.getColumnIndex("date")));
            callLog.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
            callLog.setType(cursor.getInt(cursor.getColumnIndex("type")));
            callLogs.add(callLog);
        }
        cursor.close();
        this.close();
        return callLogs;
    }

    public ArrayList<CallLogBean> getCallLog(int userid) {
        ArrayList<CallLogBean> callLogs = new ArrayList<CallLogBean>();
        Cursor cursor = this.getWritableDatabase()
                .rawQuery(
                        "SELECT * FROM CallLog, Contact ON CallLog.id=Contact.id WHERE Contact.id=? ORDER BY date DESC",
                        new String[]{String.valueOf(userid)});
        while (cursor.moveToNext()) {
            CallLogBean callLog = new CallLogBean();
            callLog.setId(cursor.getInt(cursor.getColumnIndex("id")));
            callLog.setName(cursor.getString(cursor.getColumnIndex("name")));
            callLog.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            callLog.setDate(cursor.getLong(cursor.getColumnIndex("date")));
            callLog.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
            callLog.setType(cursor.getInt(cursor.getColumnIndex("type")));
            callLogs.add(callLog);
        }
        cursor.close();
        this.close();
        return callLogs;
    }

    public ArrayList<SMSBean> getSMS() {
        ArrayList<SMSBean> sms = new ArrayList<SMSBean>();
        Cursor cursor = this.getWritableDatabase().rawQuery(
                " SELECT Contact.id AS cid, name, number, count(*) AS count, date, content "
                        + " FROM Contact, Message ON Contact.id = Message.id "
                        + " GROUP BY Contact.id", null);
        while (cursor.moveToNext()) {
            SMSBean smsBean = new SMSBean();
            smsBean.setThread_id(cursor.getInt(cursor.getColumnIndex("cid")));
            smsBean.setName(cursor.getString(cursor.getColumnIndex("name")));
            smsBean.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            smsBean.setMsg_count(cursor.getInt(cursor.getColumnIndex("count")));
            smsBean.setDate(cursor.getLong(cursor.getColumnIndex("date")));
            smsBean.setMsg_snippet(cursor.getString(cursor.getColumnIndex("content")));
            sms.add(smsBean);
        }
        cursor.close();
        this.close();
        return sms;
    }

    public long getMaxDate() {
        Cursor cursor = this.getWritableDatabase().rawQuery("select max(date) as date from Message", null);
        long ret = 0;
        while (cursor.moveToNext()) {
            ret = cursor.getLong(cursor.getColumnIndex("date"));
        }
        cursor.close();
        this.close();
        return ret;
    }

    public ArrayList<MessageBean> getMessage(String id) {
        ArrayList<MessageBean> messages = new ArrayList<MessageBean>();
        Cursor cursor = this.getWritableDatabase().rawQuery(
                " SELECT Contact.id as cid, name, number, date, content, type "
                        + "FROM Contact, Message "
                        + " ON Contact.id = Message.id "
                        + " WHERE Contact.id = ? ORDER BY date ASC", new String[]{id});
        while (cursor.moveToNext()) {
            MessageBean message = new MessageBean();
            message.setId(cursor.getInt(cursor.getColumnIndex("cid")));
            message.setName(cursor.getString(cursor.getColumnIndex("name")));
            message.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            message.setDate(cursor.getLong(cursor.getColumnIndex("date")));
            message.setContent(cursor.getString(cursor.getColumnIndex("content")));
            message.setType(cursor.getInt(cursor.getColumnIndex("type")));
            messages.add(message);
        }
        cursor.close();
        this.close();
        return messages;
    }

    public void deleteContact(int contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM Contact where id = ?", new String[]{String.valueOf(contactId)});
        } finally {
            db.close();
        }
    }


    public void updateContact(ContactBean contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("UPDATE Contact SET name = ?, number = ?, photoPath = ?, email = ?, pinYin = ? WHERE id = ?",
                    new String[]{contact.getDisplayName(), contact.getPhoneNum(), contact.getPhotoPath(), contact.getEmail(),
                            contact.getPinYin(), Integer.toString(contact.getContactId())});
        } finally {
            db.close();
        }
    }
}