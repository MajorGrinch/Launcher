package tech.doujiang.launcher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.ContactSMSAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.MessageBean;

public class ContactSMSActivity extends AppCompatActivity {

    private List<MessageBean> msgList;

    private EditText inputText;

    private Button send;

    private TextView topName;

    private RecyclerView contactSMSRecyclerView;

    private ContactSMSAdapter contactSMSAdapter;

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sms);
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        inputText = (EditText)findViewById(R.id.sms_input_text);
        send = (Button)findViewById(R.id.send_sms);
        contactSMSRecyclerView = (RecyclerView)findViewById(R.id.contact_sms_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactSMSRecyclerView.setLayoutManager(layoutManager);
        final Intent intent = getIntent();
        final int contactId = intent.getIntExtra("contactId", -1);
        final String name = intent.getStringExtra("name");
        Long date = intent.getLongExtra("date", -1);
        if( contactId  == -1){
            return;
        }
        topName = (TextView)findViewById(R.id.contact_sms_name);
        topName.setText(name);

        msgList = dbHelper.getMessage(String.valueOf(contactId));
        contactSMSAdapter = new ContactSMSAdapter(msgList);
        contactSMSRecyclerView.setAdapter(contactSMSAdapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                contactSMSRecyclerView.scrollToPosition(msgList.size()-1);
                if(!content.equals("")){
                    MessageBean sms = new MessageBean();
                    sms.setContent(content);
                    sms.setType(2);
                    sms.setId(contactId);
                    sms.setDate(new Date().getTime());
                    msgList.add(sms);
                    contactSMSRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                }
            }
        });
    }
}
