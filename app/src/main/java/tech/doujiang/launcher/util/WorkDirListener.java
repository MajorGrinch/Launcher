package tech.doujiang.launcher.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.FileObserver;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.doujiang.launcher.model.MyApplication;

/**
 * Created by kirk on 5/2/17.
 */

public class WorkDirListener extends FileObserver {
    private static final String TAG = "WorkDirListener";

    Context mcontext;
    ClipboardManager clipboardManager;
    WifiManager wifiManager;
    boolean isactive = false;

    public WorkDirListener(String path) {
        super(path);
        mcontext = MyApplication.getContext();
        clipboardManager = (ClipboardManager) mcontext.getSystemService(Context.CLIPBOARD_SERVICE);
        wifiManager = (WifiManager) mcontext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onEvent(int event, String path) {
        int el = event & FileObserver.ALL_EVENTS;
        isactive = OpenfileUtil.isServiceWork(MyApplication.getContext(),
                "tech.doujiang.launcher.service.ReportLocationService");
        Log.d(TAG, "isactive " + isactive);
        Log.d(TAG, "Event " + event);
        Log.d(TAG, "el " + String.valueOf(el));
        switch (el) {
            case FileObserver.ACCESS:
                Log.d(TAG, "File Access");
                clearClipboard();
                if (!isactive) {
                    Log.d(TAG, "suspicious behavior");
                    WifiInfo wInfo = wifiManager.getConnectionInfo();
                    final String macAddress = wInfo.getMacAddress();
                    Log.d(TAG, macAddress);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String connectionurl = TempHelper.server_url + "/audit";
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(5, TimeUnit.SECONDS)
                                        .build();
                                RequestBody formBody = new FormBody.Builder()
                                        .add("behavior", "attmpt to access file when not at work")
                                        .add("macaddress", macAddress)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(connectionurl)
                                        .post(formBody)
                                        .build();
                                Response response = client.newCall(request).execute();
                                if (response.isSuccessful()) {
                                    String rsp = response.body().string();
                                    Log.d(TAG, rsp);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                }

                // TODO: 2017/5/26 check if the app is running to distinguish suspicious behavior
                break;
            case FileObserver.OPEN:
                Log.d(TAG, "File Open");

                clearClipboard();
            default:
                break;
        }
    }

    public void clearClipboard() {
        ClipData clipData = ClipData.newPlainText("", "");
        clipboardManager.setPrimaryClip(clipData);
    }
}