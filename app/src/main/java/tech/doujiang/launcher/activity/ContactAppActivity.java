package tech.doujiang.launcher.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.fragment.CallLogListFragment;
import tech.doujiang.launcher.fragment.ContactListFragment;
import tech.doujiang.launcher.fragment.ContactStraggeredFragment;

public class ContactAppActivity extends FragmentActivity {

    private MyDatabaseHelper dbHelper;

    private Fragment callLogFragment;
    private Fragment contactStraggeredFragment;
    private Fragment contactListFragment;


    private LinearLayout phone_contact_recent;
    private LinearLayout phone_call_log;
    private LinearLayout phone_contact_all;

    private SearchView searchView;
    private static final String TAG = "ContactAppActivity";

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_app);
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        searchView = (SearchView)findViewById(R.id.search_contact);
        searchView.setFocusable(false);
        searchView.requestFocusFromTouch();
        this.phone_call_log = (LinearLayout) findViewById(R.id.phone_call_log);
        this.phone_contact_recent = (LinearLayout) findViewById(R.id.phone_contact_recent);
        this.phone_contact_all = (LinearLayout)findViewById(R.id.phone_contact_all);

        phone_contact_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(0);
            }
        });

        phone_contact_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click the contact straggered");
                initFragment(1);
            }
        });

        phone_call_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(2);
            }
        });
        initFragment(1);
        Log.d(TAG, "onCreate");
    }

    private void initFragment(int index) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        hideFragment(transaction);
        switch (index) {
            case 0:
                if (contactListFragment == null) {
                    contactListFragment = new ContactListFragment();
                    transaction.add(R.id.phone_content, contactListFragment);
                } else {
                    transaction.show(contactListFragment);
                }
                break;
            case 1:
                if (contactStraggeredFragment == null) {
                    contactStraggeredFragment = new ContactStraggeredFragment();
                    transaction.add(R.id.phone_content, contactStraggeredFragment);
                } else {
                    transaction.show(contactStraggeredFragment);
                }
                break;
            case 2:
                if(callLogFragment == null){
                    callLogFragment = new CallLogListFragment();
                    transaction.add(R.id.phone_content, callLogFragment);
                }else{
                    transaction.show(callLogFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (callLogFragment != null) {
            transaction.hide(callLogFragment);
        }
        if (contactStraggeredFragment != null) {
            transaction.hide(contactStraggeredFragment);
        }
        if(contactListFragment != null){
            transaction.hide(contactListFragment);
        }
    }

}
