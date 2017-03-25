package tech.doujiang.launcher.util;

import java.io.*;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

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
    public Des mine_encry = new Des("TPSECRET");
    public String cache_name = "cache.txt";
    public File cache;
    private String content;
    public int namestart;
    public String sign = new String("Stark Tech");
    public String surfix = new String(".txt");

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
//                    XposedBridge.log("HOOK SUCCESSFULLY!");
                    Log.v("extdir", extDir);
                    while ((i = reader.read()) != -1) {
                        char c = (char) i;
                        content = content + c;
                    } //all the inputstream has been read
                    Log.v("Hook Successfully!!!", content.substring(0, 10));
                    if (content.substring(0, 10).compareTo(sign) == 0)  //belong to Stark Tech
                    {
                        String cacDir = new String(Environment.getDataDirectory().toString());
                        cacDir += "/data/com.ostrichmyself.txtReader/cache";
                        int index_surfix = content.indexOf(surfix); //find the ".txt"
                        Log.v("index surfix", "surpos" + index_surfix);
                        int x = index_surfix;   // index of the .txt, specifically is the '.'
                        while (x != -1) {
                            if (content.charAt(x) == '/') {
                                namestart = x;
                                break;
                            }
                            x--;
                        }

                        String filename = content.substring(namestart + 1, index_surfix + 4);
                        int pathstart = content.indexOf('/');
                        Log.v("pathstart", String.valueOf(pathstart));
                        String rela_path = content.substring(pathstart, index_surfix + 4);
                        Log.v("rela_path", rela_path);
                        Log.v("file name", filename);
                        String filepath = extDir + rela_path;
                        Log.v("file path", filepath);
                        Log.v("cache dir", cacDir);
                        cache = new File(cacDir, filename);
                        Log.v("Exist cache?", String.valueOf(cache.exists()));
                        if (!cache.exists()) {
                            try {
                                cache.createNewFile();
                            } catch (IOException ex) {
                                Log.v("Crate failed", ex.toString());
                            }
                            try {
                                Log.v("cache path", cache.getAbsolutePath());
                                Log.v("file path", filepath);
                                mine_encry.decrypt(index_surfix + 6, filepath, cache.getAbsolutePath());
                            } catch (IOException ex) {
                                Log.v("Something wrong", ex.toString());

                            }
                        }
                        param.args[0] = new FileReader(cache);
                    } else   // private text
                    {
                        Log.v("SD_PATH", extDir);
                        cache = new File(extDir, cache_name);
                        if (!cache.exists()) {
                            try {
                                cache.createNewFile();
                                Log.v("Outcome", String.valueOf(cache.exists()));
                            } catch (IOException e) {

                                Log.v("Exception", e.toString());
                            }
                        }
                        try {
                            FileWriter w = new FileWriter(cache);
                            w.write(content);
                            w.close();
                        } catch (IOException ex) {
                            Log.v("Exception", ex.toString());
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
