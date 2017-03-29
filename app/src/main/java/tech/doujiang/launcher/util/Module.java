package tech.doujiang.launcher.util;

import java.io.*;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.model.OpenfileListener;
import tech.doujiang.launcher.model.OpenfileListener;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by jcase on 9/9/15.
 * hook BufferedReader
 */

public class Module implements IXposedHookLoadPackage {

    //set targetPackage to a specific application, leave empty to target all
    public String targetPackage = "";
    public String packageName = null;
    public String extDir = Environment.getExternalStorageDirectory().getPath();
    public String cache_name = "cache.txt";
    public File cache;
    private String content;
    public int namestart;
    public String sign = new String("Stark Tech");
    public String surfix = new String(".txt");
    private String content_uri = "content://tech.doujiang.provider/KeyTable";

    Context context = MyApplication.getContext();

    @SuppressLint("NewApi")
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        packageName = lpparam.packageName;
//        XposedBridge.log("Loaded app:" + packageName);
        if (!targetPackage.isEmpty() && !targetPackage.equals(packageName)) {
            XposedBridge.log("!targetPackage.isEmpty() && !targetPackage.equals(packageName)");
            return;
        }
        XposedHelpers.findAndHookConstructor("java.io.BufferedReader", lpparam.classLoader, Reader.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (packageName.indexOf("com.ostrichmyself.txtReader") != -1) {
                    InputStreamReader reader = (InputStreamReader) param.args[0];
                    content = "";
                    int i;
                    XposedBridge.log("HOOK SUCCESSFULLY!");

                    while ((i = reader.read()) != -1) {
                        char c = (char) i;
                        content = content + c;
                    } //all the inputstream has been read

                    if (content.substring(0, 10).compareTo(sign) == 0)  //belong to Stark Tech
                    {
                        String cacDir = new String(Environment.getDataDirectory().toString())
                                + "/data/com.ostrichmyself.txtReader/cache";
                        int index_surfix = content.indexOf(surfix); //find the ".txt"
                        Log.d("index surfix", "surpos" + index_surfix);
                        int x = index_surfix;   // index of the .txt, specifically is the '.'
                        while (x != -1) {
                            if (content.charAt(x) == '/') {
                                namestart = x;
                                break;
                            }
                            x--;
                        }
                        //Myaes.init("123456789abcdefg");
                        String filename = content.substring(namestart + 1, index_surfix + 4);
                        Uri uri = Uri.parse(content_uri);
                        OpenfileUtil.openfile(uri, filename, new OpenfileListener() {
                            @Override
                            public void onFinish(String response) {
                                Log.d("Response: ", (response == null) ? "Null" : response);
                                String kkk = response;
                                try {
                                    Myaes.init(kkk);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(Exception ex) {
                                ex.printStackTrace();
                                Log.e("Callback: ", "onError");
                            }
                        });
                        int pathstart = content.indexOf('/');
                        Log.d("pathstart", String.valueOf(pathstart));
                        String rela_path = content.substring(pathstart, index_surfix + 4);
                        Log.d("rela_path", rela_path);
                        Log.d("file name", filename);
                        String filepath = extDir + rela_path;
                        Log.d("file path", filepath);
                        Log.d("cache dir", cacDir);
                        cache = new File(cacDir, filename);
                        Log.d("Exist cache?", String.valueOf(cache.exists()));
                        if (cache.createNewFile()) {
                            try {
                                Log.d("cache path", cache.getAbsolutePath());
                                Log.d("file path", filepath);
                                Myaes.decryptFile(index_surfix + 5, filepath, cache.getAbsolutePath());
                            } catch (IOException ex) {
                                Log.d("Something wrong", ex.toString());

                            }
                        }else{
                            try{
                                cache.delete();
                                cache.createNewFile();
                                Myaes.decryptFile(index_surfix+5, filepath, cache.getAbsolutePath());
                            }catch (IOException ex){
                                Log.e("Something wrong", ex.toString());
                            }
                        }
                        param.args[0] = new FileReader(cache);
                    } else   // private text
                    {
                        Log.d("SD_PATH", extDir);
                        cache = new File(extDir, cache_name);
                        if (!cache.exists()) {
                            try {
                                cache.createNewFile();
                                Log.d("Outcome", String.valueOf(cache.exists()));
                            } catch (IOException e) {

                                Log.d("Exception", e.toString());
                            }
                        }
                        try {
                            FileWriter w = new FileWriter(cache);
                            w.write(content);
                            w.close();
                        } catch (IOException ex) {
                            Log.d("Exception", ex.toString());
                        }
                        param.args[0] = new FileReader(cache);
                        //cache.deleteOnExit();
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (packageName.indexOf("com.ostrichmyself.txtReader") != -1) {
                    //cache.delete();
                }

            }
        });
    }
}
