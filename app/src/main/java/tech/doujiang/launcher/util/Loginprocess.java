package tech.doujiang.launcher.util;

import android.icu.util.TimeUnit;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Loginprocess {

    private Loginfo myinfo;
    private static String TAG = "Loginprocess";
    private static String serverurl = TempHelper.server_url;

    public Loginprocess(Loginfo loginfo) {
        this.myinfo = loginfo;
    }

    public static boolean networktest() {
//        try {
//            String connectionurl = serverurl + "/networktest";
//            OkHttpClient client = new OkHttpClient.Builder()
//                                    .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
//                                    .build();
//            Request request = new Request.Builder()
//                    .url(connectionurl)
//                    .build();
//            Response response = client.newCall(request).execute();
//            Log.d("Network test", String.valueOf(response.code()));
//            if (response.code() == 200) {
//                return true;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }//TODO: remeber to recovery this code and change the next line
        return true;
    }

    public boolean confirm() {
//        try {
//            String username = myinfo.getUsername();
//            String psw = myinfo.getPsw();
//            String connectionurl = serverurl + "/Userconfirm";
//            OkHttpClient client = new OkHttpClient.Builder()
//                                    .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
//                                    .build();
//            RequestBody formBody = new FormBody.Builder()
//                    .add("username", username)
//                    .add("psw", psw)
//                    .build();
//            Request request = new Request.Builder()
//                    .url(connectionurl)
//                    .post(formBody)
//                    .build();
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                JSONObject result = new JSONObject(response.body().string());
//                if (result.getString("userid") != null)
//                    return true;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return false; 
        // TODO: 08/04/2017 remember to recovery this code
        return true;
    }
}
