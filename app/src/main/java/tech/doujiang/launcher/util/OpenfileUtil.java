package tech.doujiang.launcher.util;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.security.PrivateKey;
import java.util.List;

import tech.doujiang.launcher.model.OpenfileListener;

/**
 * Created by grinch on 28/03/2017.
 */

public class OpenfileUtil {
    public static void openfile(final Uri uri, final String filename, final OpenfileListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isactive = OpenfileUtil.isServiceWork(AndroidAppHelper.currentApplication().getApplicationContext(),
                            "tech.doujiang.launcher.service.ReportLocationService");
                    String key = "unauthorizedopen";
                    if(!isactive && listener != null){
                        Log.d("open file unauthorized", "not online");
                        listener.onFinish(key);
                        return;
                    }
                    String aeskey=null;
                    Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                    Log.d("context is ", (context==null) ? "null" : "not null");

                    Cursor cursor = context.getContentResolver().query(uri,
                            new String[]{"keycontent"}, "filename=?",
                            new String[]{filename}, "filename");
                    Log.d("The cursor is ", (cursor==null) ? "null" : "not null");
                    if( cursor.moveToFirst() ) {
                        aeskey = cursor.getString(cursor.getColumnIndex("keycontent"));
                    }
                    if (listener != null) {
                        Log.d("key is", aeskey);
                        listener.onFinish(aeskey);
                    }
                    cursor.close();
                } catch (Exception ex) {
                    Log.e("get key", "exception!");
                    if (listener != null) {
                        listener.onError(ex);
                    }
                }
            }
        }).start();
    }
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            Log.d("Running service", mName);
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
