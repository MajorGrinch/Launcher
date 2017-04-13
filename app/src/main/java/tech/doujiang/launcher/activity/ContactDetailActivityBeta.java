package tech.doujiang.launcher.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.CallLogListAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.fragment.ContactListFragment;
import tech.doujiang.launcher.model.ContactBean;

public class ContactDetailActivityBeta extends AppCompatActivity {

    private ContactBean contact;

    private MyDatabaseHelper dbHelper;
    private AppCompatImageView contactPhoto;
    private AppCompatTextView contactNum, contactName;
    private CallLogListAdapter callLogListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail_beta);
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        RecyclerView contactDetailCallLog = (RecyclerView) findViewById(R.id.contact_detail_callLog);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactDetailCallLog.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        int pos = intent.getIntExtra("position", -1);
        if (pos != -1) {
            contact = ContactListFragment.contactList.get(pos);
        }
        callLogListAdapter = new CallLogListAdapter(dbHelper.getCallLog(contact.getContactId()));
        contactDetailCallLog.setAdapter(callLogListAdapter);
        initView();
        initEvent();
    }

    private void initEvent() {
        if (contact.getPhotoPath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(contact.getPhotoPath());
            contactPhoto.setImageBitmap(bitmap);
        } else {
            contactPhoto.setImageResource(R.drawable.ic_person_black);
        }
        contactName.setText(contact.getDisplayName());
        contactNum.setText(contact.getPhoneNum());
        Log.e("ContactDetail: ", Integer.toString(contact.getContactId()));
    }

    private void initView() {
        contactPhoto = (AppCompatImageView) findViewById(R.id.contact_detail_photo);
        contactName = (AppCompatTextView) findViewById(R.id.contact_detail_name);
        contactNum = (AppCompatTextView) findViewById((R.id.contact_detail_number));
    }
}
