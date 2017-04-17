package tech.doujiang.launcher.activity;

import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
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

import java.util.Date;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.ContactSMSAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.MessageBean;

public class ContactSMSActivityBeta extends AppCompatActivity {

    private List<MessageBean> msgList;

    private EditText inputText;

    private Button send;

    private RecyclerView contactSMSRecyclerView;

    private ContactSMSAdapter contactSMSAdapter;

    private MyDatabaseHelper dbHelper;

    private String defaultSmsApp;

    private ContactBean contact;
    private static final String TAG = "ContactSMSActivityBeta";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sms_beta);
        final Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final int contactId = intent.getIntExtra("contactId", -1);
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());
                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getApplicationContext().getPackageName());
                        startActivity(intent);
                    }

                    android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                    List<String> divideContents = smsManager.divideMessage(inputText.getText().toString());
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
                }
                contactSMSRecyclerView.scrollToPosition(msgList.size() - 1);
            }
        });
    }
}
