package tech.doujiang.launcher.activity;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import tech.doujiang.launcher.adapter.MessageBoxListAdapter;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.model.MessageBean;
import tech.doujiang.launcher.R;
import tech.doujiang.launcher.R.layout;
import tech.doujiang.launcher.util.Constant;

public class MessageBoxListActivity extends AppCompatActivity {

    private ListView talkView;
    private SimpleDateFormat sdf;
    private TextView nameView;
    private EditText inputText;
    private ImageButton goBack, btnCall, sendMsg;
    private MessageBoxListAdapter msgAdapter;

    private List<MessageBean> messages;
    private WorkspaceDBHelper dbHelper;
    private Constant constant;
    private String thread;
    private String name;
    private String number;
    private String defaultSmsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_list_view);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        thread = getIntent().getStringExtra("threadId");
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        dbHelper = WorkspaceDBHelper.getDBHelper(this);
        constant = new Constant();
        initViews();
        init();
        initEvents();
    }

    private void initEvents() {
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number == null) {
                    Toast.makeText(MessageBoxListActivity.this, "Number does not exist!", Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(intent);
                }
            }
        });
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputText.getText().toString().isEmpty()) {
                    Toast.makeText(MessageBoxListActivity.this, "Text is empty!", Toast.LENGTH_SHORT);
                } else if (number == null) {
                    Toast.makeText(MessageBoxListActivity.this, "Number does not exist!", Toast.LENGTH_SHORT);
                } else {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(getApplicationContext());
                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getApplicationContext().getPackageName());
                        startActivity(intent);
                    }

                    android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                    List<String> divideContents = smsManager.divideMessage(inputText.getText().toString());
                    for (String text : divideContents) {
                        smsManager.sendTextMessage(number, null, text, null, null);
                    }
                    MessageBean message = new MessageBean();
                    message.setId(Integer.parseInt(thread));
                    message.setName(name);
                    message.setNumber(number);
                    message.setType(constant.LAYOUT_OUTGOING);
                    message.setDate(System.currentTimeMillis());
                    message.setText(inputText.getText().toString());
                    dbHelper.addMessage(message);
                    inputText.setText("");
                    refresh();

//                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
//                    if (phones == null) {
//                        Log.i("Phones: ", "NULL");
//                    }
//                    while (phones.moveToNext()) {
//                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        if (phoneNumber.equals(message.getNumber())) {
//                            String contactId = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                            Uri deleteContactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
//                            int id = getContentResolver().delete(deleteContactUri, null, null);
//                            Log.i("DeleteContact: ", Integer.toString(id));
//                        }
//                    }
//                    phones.close();
                    Uri deleteUri = Uri.parse("content://sms");
                    getContentResolver().delete(deleteUri, "address = ?", new String[]{number});

//                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//                        Log.e("DefaultSmsApp: ", defaultSmsApp);
//                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
//                        startActivity(intent);
//                        Log.e("Refresh: ", Telephony.Sms.getDefaultSmsPackage(getApplicationContext()));
//                    }
                }
            }
        });
    }

    private void initViews() {
        inputText = (EditText) findViewById(R.id.input_text);
        goBack = (ImageButton) findViewById(R.id.btn_go_back);
        btnCall = (ImageButton) findViewById(R.id.btn_call);
        sendMsg = (ImageButton) findViewById(R.id.send_msg);
        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(name);
    }

    private void init() {
        talkView = (ListView) findViewById(R.id.message_list);
        refresh();
    }

    private void refresh() {
        messages = dbHelper.getMessage(thread);
        msgAdapter = new MessageBoxListAdapter(this, messages);
        talkView.setAdapter(msgAdapter);
        talkView.setSelection(messages.size());
    }
}
