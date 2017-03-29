package tech.doujiang.launcher.model;

import android.app.Application;
import android.content.Context;

/**
 * Created by grinch on 28/03/2017.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
