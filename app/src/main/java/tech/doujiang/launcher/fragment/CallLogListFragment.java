package tech.doujiang.launcher.fragment;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.activity.PhoneAppActivity;
import tech.doujiang.launcher.adapter.DialAdapter;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.model.CallLogBean;

public class CallLogListFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 100;

    private ListView callLogListView;
    private WorkspaceDBHelper dbHelper;
    private DialAdapter adapter;

    public List<CallLogBean> callLogs;

    public CallLogListFragment() {

    }

    public static CallLogListFragment newInstance(String param1, String param2) {
        CallLogListFragment fragment = new CallLogListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_log_list, container, false);
        callLogListView = (ListView) view.findViewById(R.id.call_log_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSIONS_REQUEST_READ_CALL_LOG);
        }
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        callLogs = dbHelper.getCallLog();
        if (callLogs.size() > 0) {
            setAdapter(callLogs);
        }
    }

    private void init() {
        dbHelper = WorkspaceDBHelper.getDBHelper(getActivity());
        callLogs = dbHelper.getCallLog();
        dbHelper.close();
        if (callLogs.size() > 0) {
            setAdapter(callLogs);
        }
    }

    private void setAdapter(List<CallLogBean> callLogs) {
        adapter = new DialAdapter(getActivity(), callLogs);
        callLogListView.setAdapter(adapter);
    }

}
