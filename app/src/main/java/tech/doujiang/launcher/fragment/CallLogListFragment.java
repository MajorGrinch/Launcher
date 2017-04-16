package tech.doujiang.launcher.fragment;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.adapter.CallLogListAdapter;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.CallLogBean;

public class CallLogListFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_READ_CALL_LOG = 100;

    private RecyclerView callLogListView;
    private MyDatabaseHelper dbHelper;
    private CallLogListAdapter adapter;

    public List<CallLogBean> callLogs;

    public CallLogListFragment() {

    }

    public static CallLogListFragment newInstance() {
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
        callLogListView = (RecyclerView) view.findViewById(R.id.calllog_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        callLogListView.setLayoutManager(layoutManager);
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
        dbHelper = MyDatabaseHelper.getDBHelper(getActivity());
        callLogs = dbHelper.getCallLog();
        dbHelper.close();
        if (callLogs.size() > 0) {
            setAdapter(callLogs);
        }
    }

    private void setAdapter(List<CallLogBean> callLogs) {
        adapter = new CallLogListAdapter(callLogs);
        callLogListView.setAdapter(adapter);
    }

}
