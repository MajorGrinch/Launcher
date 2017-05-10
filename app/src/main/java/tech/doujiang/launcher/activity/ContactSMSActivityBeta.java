package tech.doujiang.launcher.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.ContactSMSAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MessageBean;

public class ContactSMSActivityBeta extends AppCompatActivity {

    private ArrayList<MessageBean> msgList;

    private EditText inputText;

    private Button send;

    private IntentFilter intentFilter;

    private RecyclerView contactSMSRecyclerView;

    private ContactSMSAdapter contactSMSAdapter;

    private LocalSMSReceiver localSMSReceiver;

    private LocalBroadcastManager localBroadcastManager;

    private MyDatabaseHelper dbHelper;

    private int contactId;

    private ContactBean contact;
    private static final String TAG = "ContactSMSActivityBeta";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sms_beta);
        final Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        contactId = intent.getIntExtra("contactId", -1);
        if (contactId == -1) {
            return;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_sms_toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        inputText = (EditText) findViewById(R.id.sms_input_text_beta);
        send = (Button) findViewById(R.id.send_sms_beta);
        contactSMSRecyclerView = (RecyclerView) findViewById(R.id.contact_sms_recyclerview_beta);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactSMSRecyclerView.setLayoutManager(layoutManager);
        msgList = dbHelper.getMessage(String.valueOf(contactId));
        contact = dbHelper.getContact(contactId).get(0);
        contactSMSAdapter = new ContactSMSAdapter(msgList);
        contactSMSRecyclerView.setAdapter(contactSMSAdapter);
        contactSMSRecyclerView.scrollToPosition(msgList.size()-1);
        inputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                contactSMSRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contactSMSRecyclerView.scrollToPosition(msgList.size()-1);
                    }
                },100);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!content.equals("")) {
                    android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                    ArrayList<String> divideContents = smsManager.divideMessage(inputText.getText().toString());
                    for (String text : divideContents) {
                        smsManager.sendTextMessage(contact.getPhoneNum(), null, text, null, null);
                    }
                    MessageBean message = new MessageBean();
                    message.setId(contactId);
                    message.setType(2);
                    message.setContent(content);
                    message.setDate(new Date().getTime());
                    dbHelper.addMessage(message);
                    inputText.setText("");
                    msgList.add(message);
                    contactSMSAdapter.notifyItemInserted(msgList.size()-1);
                }
                contactSMSRecyclerView.scrollToPosition(msgList.size() - 1);
            }
        });
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("xyz.majorgrinch.mobilesafe.ADD_COLLEAGUE_SMS");
        localSMSReceiver = new LocalSMSReceiver();
        localBroadcastManager.registerReceiver(localSMSReceiver, intentFilter);
    }

    protected class LocalSMSReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<MessageBean> messageList = dbHelper.getMessage(String.valueOf(contactId));
            contactSMSAdapter.swap(messageList);
            contactSMSRecyclerView.scrollToPosition(messageList.size()-1);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localSMSReceiver);
    }
}
