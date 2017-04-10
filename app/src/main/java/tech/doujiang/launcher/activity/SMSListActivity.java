package tech.doujiang.launcher.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.SMSListAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.SMSBean;

public class SMSListActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;

    private List<SMSBean> smsList;
    private MyDatabaseHelper dbHelper;
    private SMSListAdapter smsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);
        RecyclerView smsRecyclerView = (RecyclerView)findViewById(R.id.sms_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        smsRecyclerView.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
        }
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        smsList = dbHelper.getSMS();
        smsListAdapter = new SMSListAdapter(smsList);
        smsRecyclerView.setAdapter(smsListAdapter);
    }
}
