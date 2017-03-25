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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
                return 0;
            }
            username = intent.getStringExtra("username");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (has_file) {
                    try {
                        Log.e(TAG, String.format("Thread %d created", i++));
                        HttpClient client = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(connectionurl);
                        // Request parameters and other properties.
                        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                        params.add(new BasicNameValuePair("username", username));
                        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                        // 请求超时
                        Log.e(TAG, "PostEntity has already been filled!");
                        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                        // 读取超时
                        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
                        HttpResponse response = client.execute(httppost);
                        Log.e(TAG, "Reponse Code: " + response.getStatusLine().getStatusCode());
                        // 判断是否成功收取信息
                        if (response.getStatusLine().getStatusCode() == 200) {
                            getInfo(response);
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

    private void getInfo(HttpResponse response) throws Exception {

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        // 将输入流转化为byte型
        byte[] data = read(is);
        // 转化为字符串

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

    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }

}
