package tech.doujiang.launcher.util;

import android.nfc.Tag;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Created by grinch on 29/03/2017.
 */

public class SimpleEncrypt {
    public static void encrypt(File file, File destFile) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fos.write(buffer);
            byte[] simple = "simple".getBytes("utf-8");
            fos.write(simple);
        } catch (Exception ex) {
            Log.e("Simple Decrypt", Log.getStackTraceString(ex));
        }
    }

    public static void decrypt(File file, File destFile) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(destFile);
            int len = fis.available();
            byte[] buffer = new byte[len];
            fis.read(buffer);
            fos.write(buffer, 0, len - 6);
        } catch (Exception ex) {
            Log.e("Simple Encrypt", Log.getStackTraceString(ex));
        }
    }
}
