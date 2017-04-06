package tech.doujiang.launcher.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.R.string;
import tech.doujiang.launcher.model.IsonlineInfo;

/**
 * Created by Grinch on 16/8/8.
 */
public class IsonlineClient {
    //    http://23.83.251.48:8080/YunMobileSafe/home.jsp
    private static String TAG = "IsonlineClient";
    private String serverurl;
    private static String connectionurl = TempHelper.server_url + "/Isonline";

    public IsonlineClient() {
    }

    public void onlineconnect(final String username, final boolean online, final boolean infoerase, final boolean islost, final double longitude, final double latitude) {
        try {

            final IsonlineInfo isonlineInfo = new IsonlineInfo();
            isonlineInfo.setUsername(username);
            isonlineInfo.setOnline(online);
            isonlineInfo.setInfoerase(infoerase);
            isonlineInfo.setIslost(islost);
            isonlineInfo.setLongitude(longitude);
            isonlineInfo.setLatitude(latitude);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(connectionurl);
                        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                        httpURLConnection.setConnectTimeout(5000);
                        httpURLConnection.setReadTimeout(5000);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setRequestProperty("Content-type",
                                "application/x-java-serialized-object");
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.connect();
                        OutputStream os= httpURLConnection.getOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(os);
                        oos.writeObject(isonlineInfo);
                        oos.flush();
                        oos.close();
                        os.close();
                        if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                            String content = getInfo(httpURLConnection.getInputStream());
                            Log.d(TAG, content);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getInfo(InputStream is) throws Exception {
        byte[] data = read(is);
        return new String(data, "UTF-8");
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
