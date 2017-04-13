package tech.doujiang.launcher.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.AddContactActivityBeta;
import tech.doujiang.launcher.adapter.ContactListAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.ContactBean;

/**
 * Created by grinch on 08/04/2017.
 */

public class ContactListFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_READ_CONTACT = 100;

    private View view;
    private ContactListAdapter contactListAdapter;
    private RecyclerView contactListView;
//    private WorkspaceDBHelper dbHelper;


    private MyDatabaseHelper dbHelper;

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        contactListView = (RecyclerView) view.findViewById(R.id.contact_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        contactListView.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACT);
        }
        init();
        FloatingActionButton addContact = (FloatingActionButton)view.findViewById(R.id.add_contact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddContactActivityBeta.class);
                getContext().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        contactList = dbHelper.getContact();
        setAdapter(contactList);
    }

    private void init() {
        dbHelper = MyDatabaseHelper.getDBHelper(getActivity());
        contactList = dbHelper.getContact();
        if (contactList.size() > 0) {
            setAdapter(contactList);
        }
    }

    private void setAdapter(List<ContactBean> contactList) {
        contactListAdapter = new ContactListAdapter(contactList);
        contactListView.setAdapter(contactListAdapter);
    }
}
