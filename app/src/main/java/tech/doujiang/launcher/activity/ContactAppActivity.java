package tech.doujiang.launcher.activity;

import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.astuetz.PagerSlidingTabStrip;
import com.aykuttasil.callrecord.CallRecord;
import com.aykuttasil.callrecord.MyCallRecordReceiver;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.ContactListStraggeredAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.fragment.CallLogListFragment;
import tech.doujiang.launcher.fragment.ContactListFragment;
import tech.doujiang.launcher.fragment.ContactStraggeredFragment;

public class ContactAppActivity extends FragmentActivity {

    private MyDatabaseHelper dbHelper;

    private Fragment callLogFragment;
    private Fragment contactStraggeredFragment;
    private Fragment contactListFragment;
    private SearchView searchView;
    private static final String TAG = "ContactAppActivity";
    private CallRecord callRecord;
    private StateListDrawable mIcon;

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
        searchView = (SearchView) findViewById(R.id.search_contact);
        searchView.setFocusable(false);
        searchView.requestFocusFromTouch();
        ViewPager pager = (ViewPager) findViewById(R.id.phone_content);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.phone_content_tab);
        tabs.setViewPager(pager);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 1:
                    return CallLogListFragment.newInstance();
                case 2:
                    return ContactListFragment.newInstance();
                default:
                case 0:
                    return ContactStraggeredFragment.newInstance();

            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                default:
                    return "Recent";
                case 1:
                    return "Call Log";
                case 2:
                    return "All";

            }
        }
    }

}
