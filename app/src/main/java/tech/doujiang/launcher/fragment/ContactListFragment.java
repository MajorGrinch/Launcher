package tech.doujiang.launcher.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.ContactDetailActivity;
import tech.doujiang.launcher.adapter.ContactListAdapter;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.model.ContactBean;
import tech.doujiang.launcher.view.QuickAlphabeticBar;

public class ContactListFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_READ_CONTACT = 100;

    private View view;
    private ContactListAdapter adapter;
    private ListView contactListView;
    private WorkspaceDBHelper dbHelper;
    private QuickAlphabeticBar alphabeticBar;

    public static List<ContactBean> contactList;

    public ContactListFragment() {
    }

    public static ContactListFragment newInstance(String param1, String param2) {
        ContactListFragment fragment = new ContactListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        contactListView = (ListView) view.findViewById(R.id.contact_list);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        alphabeticBar = (QuickAlphabeticBar) view.findViewById(R.id.fast_scroller);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACT);
        }
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        contactList = dbHelper.getContact();
//        if (contactList.size() > 0) {
            setAdapter(contactList);
//        } else {
            // Here contactlist should be cleared.
//            Toast.makeText(getContext(), "none", Toast.LENGTH_SHORT);
//        }
    }

    private void init() {
        dbHelper = WorkspaceDBHelper.getDBHelper(getActivity());
        contactList = dbHelper.getContact();
        if (contactList.size() > 0) {
            setAdapter(contactList);
        }
    }

    private void setAdapter(List<ContactBean> contactList) {
        adapter = new ContactListAdapter(getActivity(), contactList, alphabeticBar);
        contactListView.setAdapter(adapter);
        alphabeticBar.init(view);
        alphabeticBar.setListView(contactListView);
        alphabeticBar.setHeight(alphabeticBar.getHeight());
        alphabeticBar.setVisibility(View.VISIBLE);
    }

}
