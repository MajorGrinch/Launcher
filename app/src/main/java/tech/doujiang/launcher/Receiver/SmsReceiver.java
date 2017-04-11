package tech.doujiang.launcher.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.LauncherActivityB;
import tech.doujiang.launcher.activity.LoginActivity;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MessageBean;
import tech.doujiang.launcher.util.Constant;

public class SmsReceiver extends BroadcastReceiver {
    private MyDatabaseHelper dbHelper;
    private ArrayList<ContactBean> contactList;
    private Constant constant;
    private ArrayList<String> numbers;
    private String defaultSmsApp;
    private int mId = 1236;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (dbHelper == null) {
            dbHelper = MyDatabaseHelper.getDBHelper(context);
        }

        Object [] pdus= (Object[]) intent.getExtras().get("pdus");
        for(Object pdu:pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String content = smsMessage.getMessageBody();
            long date = System.currentTimeMillis();
            Date timeDate = new Date(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(timeDate);

            Long maxDate = dbHelper.getMaxDate();
            numbers = dbHelper.getNumber();
            constant = new Constant();
            if (numbers.contains(sender) && smsMessage.getTimestampMillis() > maxDate + 10) {
                    MessageBean message = new MessageBean();
                    message.setType(constant.LAYOUT_INCOMING);
                    message.setContent(content);
                    message.setDate(smsMessage.getTimestampMillis());
                    contactList = dbHelper.getContact();
                    for (ContactBean contact : contactList) {
                        if (contact.getPhoneNum().equals(sender)) {
                            message.setId(contact.getContactId());
                            break;
                        }
                    }
                    dbHelper.addMessage(message);

                Log.e("MessageId: ", Integer.toString(message.getId()));
                Log.e("MessageDate: ", Long.toString(message.getDate()));
                Log.e("MessageText: ", message.getContent());
                Log.e("MessageType: ", Integer.toString(message.getType()));
            }

            Log.e("短信来自:", sender);
            Log.e("短信内容:", content);
            Log.e("短信时间:", time);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

                defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);
                Intent smsIntent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                smsIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
                Log.e("DefaultSmsApp: ", Telephony.Sms.getDefaultSmsPackage(context));
            }
            abortBroadcast();
            makeNotification(context);
            delete(context, sender);
            Log.e("deleteSMS: ", sender);
        }
    }

    public void makeNotification(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.info)
                .setContentTitle("MobileSafe")
                .setContentText("Your phone is under protection.");
        Intent resultIntent = new Intent(context, LoginActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LauncherActivityB.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public void delete(Context context, String sender) {
//        Uri deleteContactUri = Uri.parse("content://contacts");
//        context.getContentResolver().delete(deleteContactUri, "phoneNumber=" + sender, null);
        Uri deleteUri = Uri.parse("content://sms");
        context.getContentResolver().delete(deleteUri, "address=" + sender, null);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//                Log.e("Refresh: ", defaultSmsApp);
//                Intent smsIntent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                smsIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
//                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(smsIntent);
//            }
    }
}
