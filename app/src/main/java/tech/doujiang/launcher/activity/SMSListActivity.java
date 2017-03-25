package tech.doujiang.launcher.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import tech.doujiang.launcher.adapter.SMSAdapter;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.model.SMSBean;
import tech.doujiang.launcher.util.BaseIntentUtil;
import tech.doujiang.launcher.util.RexseeSMS;
import tech.doujiang.launcher.R;
import tech.doujiang.launcher.R.id;

public class SMSListActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;

    private ListView smsListView;
    private List<SMSBean> smsList;
    private WorkspaceDBHelper dbHelper;
    private SMSAdapter smsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list_view);
        smsListView = (ListView) findViewById(R.id.sms_list);
        smsAdapter = new SMSAdapter(SMSListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
        }
        dbHelper = WorkspaceDBHelper.getDBHelper(this);
        smsList = dbHelper.getSMS();
        smsAdapter.assignment(smsList);
        smsListView.setAdapter(smsAdapter);
        smsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, String> map = new HashMap<String, String>();
                SMSBean sb = (SMSBean) smsAdapter.getItem(position);
                map.put("name", sb.getName());
                map.put("number", sb.getNumber());
                map.put("threadId", Integer.toString(sb.getThread_id()));
                BaseIntentUtil.intentSysDefault(SMSListActivity.this,
                        MessageBoxListActivity.class, map);
            }
        });
    }

    @Override
    protected void onResume() {
        smsList = dbHelper.getSMS();
        smsAdapter.assignment(smsList);
        smsListView.setAdapter(smsAdapter);
        smsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, String> map = new HashMap<String, String>();
                SMSBean sb = (SMSBean) smsAdapter.getItem(position);
                map.put("name", sb.getName());
                map.put("number", sb.getNumber());
                map.put("threadId", Integer.toString(sb.getThread_id()));
                BaseIntentUtil.intentSysDefault(SMSListActivity.this,
                        MessageBoxListActivity.class, map);
            }
        });
        super.onResume();
    }
}
