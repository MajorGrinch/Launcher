package tech.doujiang.launcher.fragment;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.ContactListStraggeredAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;

public class ContactStraggeredFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_READ_CONTACT = 100;

    private View view;
    private ContactListStraggeredAdapter straggeredAdapter;
    private RecyclerView contactStraggeredView;
    private static final String TAG = "ContactStraggeredFrag";


    private MyDatabaseHelper dbHelper;

    public List<ContactBean> contactList;

    public ContactStraggeredFragment() {
    }

    public static ContactStraggeredFragment newInstance() {
        ContactStraggeredFragment fragment = new ContactStraggeredFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_straggered, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACT);
        }
        Log.d(TAG, "onCreateView");
        return view;
    }

    private void init() {
        contactStraggeredView = (RecyclerView) view.findViewById(R.id.contact_straggered_view);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        contactStraggeredView.setLayoutManager(layoutManager);
        dbHelper = MyDatabaseHelper.getDBHelper(getContext());
        contactList = dbHelper.getContact();
        Log.d(TAG, "contactlist size: "+contactList.size());
        if (contactList.size() > 0) {
            setAdapter(contactList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        Log.d(TAG, "onResume");
    }

    private void setAdapter(List<ContactBean> contactList) {
        straggeredAdapter = new ContactListStraggeredAdapter(contactList);
        contactStraggeredView.setAdapter(straggeredAdapter);
    }
}
