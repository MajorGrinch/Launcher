package tech.doujiang.launcher.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.CallLogListAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;

public class ContactDetailActivityBeta extends AppCompatActivity {

    private ContactBean contact;
    private int contactId;

    private MyDatabaseHelper dbHelper;
    private AppCompatImageView contactPhoto, deleteContact, editContact,call, sms;
    private AppCompatTextView contactNum, contactName;
    private CallLogListAdapter callLogListAdapter;
    private RecyclerView contactDetailCallLog;
    private static final String TAG = "ContactDetailActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail_beta);
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        contactDetailCallLog = (RecyclerView) findViewById(R.id.contact_detail_callLog);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contactDetailCallLog.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        contactId = intent.getIntExtra("contactId", -1);
        initView();
    }

    private void initEvent() {
        if (contactId == -1) {
            return;
        }
        contact = dbHelper.getContact(contactId).get(0);
        if (contact.getPhotoPath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(contact.getPhotoPath());
            contactPhoto.setImageBitmap(bitmap);
        } else {
            contactPhoto.setImageResource(R.drawable.ic_person_black);
        }
        contactName.setText(contact.getDisplayName());
        contactNum.setText(contact.getPhoneNum());
        Log.e("ContactDetail: ", Integer.toString(contact.getContactId()));
        callLogListAdapter = new CallLogListAdapter(dbHelper.getCallLog(contact.getContactId()));
        contactDetailCallLog.setAdapter(callLogListAdapter);

        editContact.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editContact.setSelected(event.getAction()==MotionEvent.ACTION_DOWN);
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d(TAG, "onTouch");
                    Intent intent = new Intent(ContactDetailActivityBeta.this
                            , UpdateContactActivity.class);
                    intent.putExtra("contactId",contact.getContactId());
                    startActivity(intent);
                }
                return true;
            }
        });

        deleteContact.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                deleteContact.setSelected(event.getAction()==MotionEvent.ACTION_DOWN);
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    dbHelper.deleteContact(contact.getContactId());
                    ContactDetailActivityBeta.this.finish();
                }
                return true;
            }
        });

        call.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+contact.getPhoneNum()));
                    startActivity(intent);
                }
                return true;
            }
        });
        sms.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    Intent intent = new Intent
                            (ContactDetailActivityBeta.this, ContactSMSActivityBeta.class);
                    intent.putExtra("name", contact.getDisplayName());
                    intent.putExtra("contactId",contact.getContactId());
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void initView() {
        sms = (AppCompatImageView)findViewById(R.id.contact_detail_sms);
        call = (AppCompatImageView)findViewById(R.id.contact_detail_call);
        editContact = (AppCompatImageView)findViewById(R.id.contact_detail_edit);
        deleteContact = (AppCompatImageView)findViewById(R.id.contact_detail_delete);
        contactPhoto = (AppCompatImageView) findViewById(R.id.contact_detail_photo);
        contactName = (AppCompatTextView) findViewById(R.id.contact_detail_name);
        contactNum = (AppCompatTextView) findViewById((R.id.contact_detail_number));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        initEvent();
    }
}
