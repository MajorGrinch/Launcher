package tech.doujiang.launcher.util;

import android.app.Application;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import tech.doujiang.launcher.R;

public class Loginprocess {

    private Loginfo myinfo;
    private static String TAG = "Loginprocess";
    private static String serverurl = TempHelper.server_url;

    public Loginprocess(Loginfo loginfo) {
        this.myinfo = loginfo;
    }

    public static boolean networktest() {
        try {
            String connectionurl = serverurl + "/networktest";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(connectionurl)
                    .build();
            Response response = client.newCall(request).execute();
            Log.d("Network test", String.valueOf(response.code()));
            if (response.code() == 200) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean confirm() {
        try {
            String username = myinfo.getUsername();
            String psw = myinfo.getPsw();
            String connectionurl = serverurl + "/Userconfirm";
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("psw", psw)
                    .build();
            Request request = new Request.Builder()
                    .url(connectionurl)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JSONObject result = new JSONObject(response.body().string());
                if (result.getString("userid") != null)
                    return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
