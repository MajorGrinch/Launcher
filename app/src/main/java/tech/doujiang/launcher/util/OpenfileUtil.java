package tech.doujiang.launcher.util;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.MyApplication;
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
                    String key = null;
                    Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                    Log.d("context is ", (context==null) ? "null" : "not null");

                    Cursor cursor = context.getContentResolver().query(uri,
                            new String[]{"keycontent"}, "filename=?",
                            new String[]{filename}, "filename");
                    Log.d("The cursor is ", (cursor==null) ? "null" : "not null");
                    if( cursor.moveToFirst() ){
                        key = cursor.getString(cursor.getColumnIndex("keycontent"));
                    }
                    if (listener != null) {
                        Log.d("key is", key);
                        listener.onFinish(key);
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
