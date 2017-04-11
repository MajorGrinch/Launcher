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
    }

    public void addCallLog(CallLogBean callLog) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO CallLog(id, date, duration, type, isRead) VALUES(?, ?, ?, ?, ?)",
                    new String[]{Integer.toString(callLog.getId()), Long.toString(callLog.getDate())
                            , Integer.toString(callLog.getDuration()), Integer.toString(callLog.getType())
                            , Integer.toString(callLog.isRead())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void addMessage(MessageBean message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO Message(id, date, content, type) VALUES(?, ?, ?, ?)",
                    new String[]{Integer.toString(message.getId()), Long.toString(message.getDate()),
                            message.getContent(), Integer.toString(message.getType())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
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
        Cursor cursor = this.getWritableDatabase().rawQuery("select * from Contact order by pinYin asc", null);
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
                        "SELECT * FROM CallLog LEFT OUTER JOIN Contact ON CallLog.id=Contact.id ORDER BY date DESC", null);
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
            db.execSQL("DELETE FROM Contact where id = ?", new String[]{Integer.toString(contactId)});
        } finally {
        }
        db.close();
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