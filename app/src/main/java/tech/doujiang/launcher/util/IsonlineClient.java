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
import java.util.ArrayList;
import java.util.List;
import tech.doujiang.launcher.R;
import tech.doujiang.launcher.R.string;

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

    public void onlineconnect(final String username, boolean online, boolean infoerase, boolean islost, double longitude, double latitude) {
        try {

            final PostInfo postInfo = new PostInfo();
            postInfo.setUsername(username);
            postInfo.setOnline(online);
            postInfo.setInfoerase(infoerase);
            postInfo.setIslost(islost);
            postInfo.setLongitude(longitude);
            postInfo.setLatitude(latitude);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient client = new DefaultHttpClient();

                        HttpPost httppost = new HttpPost(connectionurl);
                        List<NameValuePair> params = new ArrayList<NameValuePair>(4);
                        params.add(new BasicNameValuePair("username", postInfo.getUsername()));
                        params.add(new BasicNameValuePair("isonline", postInfo.getOnline()));
                        params.add(new BasicNameValuePair("infoerase", postInfo.getInfoerase()));
                        params.add(new BasicNameValuePair("islost", postInfo.getIslost()));
                        params.add(new BasicNameValuePair("longitude", postInfo.getLongitude()));
                        params.add(new BasicNameValuePair("latitude", postInfo.getLatitude()));
                        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

                        client.execute(httppost);
                        Log.e(TAG, "client.execute(httppost)");
                        Log.e(TAG, postInfo.getUsername());
                        HttpResponse response = client.execute(httppost);
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String outcome = getInfo(response);
                            Log.e(TAG + "Response", outcome.toString());
                        } else {
                            Log.e(TAG + "Response", getInfo(response));
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

    private class PostInfo {
        private String username;
        private String online;
        private String infoerase;
        private String islost;
        private String longitude;
        private String latitude;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = Boolean.toString(online);
        }

        public String getInfoerase() {
            return infoerase;
        }

        public void setInfoerase(boolean infoerase) {
            this.infoerase = Boolean.toString(infoerase);
        }

        public String getIslost() {
            return islost;
        }

        public void setIslost(boolean islost) {
            this.islost = Boolean.toString(islost);
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = Double.toString(longitude);
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = Double.toString(latitude);
        }
    }

    public void update() {

    }

    private static String getInfo(HttpResponse response) throws Exception {

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
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
