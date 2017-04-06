package tech.doujiang.launcher.util;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.security.PrivateKey;

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
                    String aeskeystr = null, aeskey=null;
                    Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                    Log.d("context is ", (context==null) ? "null" : "not null");

                    Cursor cursor = context.getContentResolver().query(uri,
                            new String[]{"keycontent"}, "filename=?",
                            new String[]{filename}, "filename");
                    Log.d("The cursor is ", (cursor==null) ? "null" : "not null");
                    if( cursor.moveToFirst() ){
                        aeskeystr = cursor.getString(cursor.getColumnIndex("keycontent"));
                    }
                    Cursor ncursor = context.getContentResolver().query(uri,
                            new String[]{"keycontent"}, "filename=?",
                            new String[]{"PrivateKey"}, "filename");
                    if(ncursor.moveToFirst()){
                        String prikeystr = ncursor.getString(cursor.getColumnIndex("keycontent"));
                        PrivateKey privateKey = RSAUtil.restorePrivateKey(Base64.decode(prikeystr, Base64.NO_WRAP));
                        aeskey = RSAUtil.RSADecode(privateKey, Base64.decode(aeskeystr, Base64.NO_WRAP));
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
}
