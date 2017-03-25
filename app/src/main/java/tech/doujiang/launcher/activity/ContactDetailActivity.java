package tech.doujiang.launcher.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.fragment.ContactListFragment;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.model.SMSBean;
import tech.doujiang.launcher.util.BaseIntentUtil;

public class ContactDetailActivity extends AppCompatActivity {
    private int position;
    private ContactBean contact;

    private WorkspaceDBHelper dbHelper;
    private ImageView contactPhoto;
    private TextView contactName, contactNum;
    private Button deleteContact;
    private LinearLayout ll_call, ll_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        contact = ContactListFragment.contactList.get(position);
        dbHelper = WorkspaceDBHelper.getDBHelper(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        if (contact.getPhotoPath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(contact.getPhotoPath());
            contactPhoto.setImageBitmap(bitmap);
        } else {
            contactPhoto.setImageResource(R.drawable.contact);
        }
        contactName.setText(contact.getDisplayName());
        contactNum.setText(contact.getPhoneNum());
        Log.e("ContactDetail: ", Integer.toString(contact.getContactId()));
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteContact(contact.getContactId());
                finish();
            }
        });
        ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhoneNum()));
                startActivity(intent);
            }
        });
        ll_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("threadId", Integer.toString(contact.getContactId()));
                map.put("name", contact.getDisplayName());
                map.put("number", contact.getPhoneNum());
                BaseIntentUtil.intentSysDefault(ContactDetailActivity.this,
                        MessageBoxListActivity.class, map);
            }
        });
    }

    private void initView() {
        contactPhoto = (ImageView) findViewById(R.id.contact_photo);
        contactName = (TextView) findViewById(R.id.contact_name);
        contactNum = (TextView) findViewById((R.id.contact_number));
        deleteContact = (Button) findViewById(R.id.delete_contact);
        ll_call = (LinearLayout) findViewById(R.id.ll_call);
        ll_msg = (LinearLayout) findViewById(R.id.ll_msg);
    }
}
