package tech.doujiang.launcher.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.SMSListAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.MessageBean;
import tech.doujiang.launcher.model.SMSBean;

public class SMSListActivityBeta extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;

    private ArrayList<SMSBean> smsList;
    private MyDatabaseHelper dbHelper;
    private SMSListAdapter smsListAdapter;
    private LocalSMSReceiver localSMSReceiver;
    private IntentFilter intentFilter;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list_beta);
        Toolbar toolbar = (Toolbar)findViewById(R.id.sms_toolbar);
        setSupportActionBar(toolbar);
        RecyclerView smsRecyclerView = (RecyclerView)findViewById(R.id.sms_recyclerview_beta);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        smsRecyclerView.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
        }
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        smsList = dbHelper.getSMS();
        smsListAdapter = new SMSListAdapter(smsList);
        smsRecyclerView.setAdapter(smsListAdapter);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("xyz.majorgrinch.mobilesafe.ADD_COLLEAGUE_SMS");
        localSMSReceiver = new LocalSMSReceiver();
        localBroadcastManager.registerReceiver(localSMSReceiver, intentFilter);
    }
    protected class LocalSMSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<SMSBean> mySMSList = dbHelper.getSMS();
            smsListAdapter.swap(mySMSList);
        }
    }
}
