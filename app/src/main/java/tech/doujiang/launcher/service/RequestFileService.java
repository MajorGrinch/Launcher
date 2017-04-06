package tech.doujiang.launcher.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import tech.doujiang.launcher.database.MyDatabaseHelper;

import tech.doujiang.launcher.util.TempHelper;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class RequestFileService extends Service {
    private static String TAG = "RequestFileService";
    private String username = null;
    private static boolean has_file = true;
    private static String connectionurl = TempHelper.server_url + "/Docissue";
    private boolean lock_status = false;
    private int i;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        boolean DEVELOPER_MODE = true;
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate();
        Log.e(TAG, "Service is created");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        i = 0;
        if (username == null) {
            if (intent == null || intent.getStringExtra("username") == null) {
                return Service.START_STICKY_COMPATIBILITY;
            }
            username = intent.getStringExtra("username");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (has_file) {
                    try {
                        Log.e(TAG, String.format("Thread %d created", i++));
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formBody = new FormBody.Builder()
                                .add("username", username)
                                .build();
                        Request request = new Request.Builder()
                                .url(connectionurl)
                                .post(formBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        // 判断是否成功收取信息
                        if (response.isSuccessful()) {
                            getInfo(response.body().bytes());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        has_file = false;
                    }
                }
            }
        }).start();
        Log.e(TAG, "All file requested!");
        return START_STICKY;
    }

    private void getInfo(byte[] data) throws Exception {
        Log.e(TAG, "Length: " + data.length);
        String content = new String(data, "UTF-8");
        Log.e(TAG, String.valueOf(content.equals("No_More_File")));
        if (content.equals(new String("No_More_File"))) {
            Log.e(TAG, "execute no more file");
            has_file = false;
            return;
        }

        int pathend = content.indexOf(".txt") + 4;
        int pathstart = content.indexOf('/');
        String substr = content.substring(0, pathend);
        int filest = substr.lastIndexOf('/');
        String filename = substr.substring(filest+1, pathend);

        OkHttpClient subclient = new OkHttpClient();
        RequestBody subBody = new FormBody.Builder()
                                .add("filename", filename)
                                .add("username", username)
                                .build();
        Request subreq = new Request.Builder()
                            .url(TempHelper.server_url+"/DistributeKey")
                            .post(subBody)
                            .build();
        Response subresp = subclient.newCall(subreq).execute();

        MyDatabaseHelper dbHelper = MyDatabaseHelper.getDBHelper(this);
        String kkk = subresp.body().string();
        dbHelper.addKey(filename, kkk);
        Log.d("AESKeyByte", kkk);


        String rela_path = content.substring(pathstart, pathend);
        String exter_path = Environment.getExternalStorageDirectory().getPath();
        String filepath = exter_path + rela_path;
        Log.e(TAG, filepath);
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();
        } catch (Exception ex) {
            ex.toString();
        }
    }
}
