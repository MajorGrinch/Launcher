package tech.doujiang.launcher.model;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by grinch on 28/03/2017.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }


    private static ArrayList<String> mynumbers = new ArrayList<String>();

    private static ArrayList<ContactBean> myContactList = new ArrayList<ContactBean>();


    public static ArrayList<String> getNumbers(){
        return mynumbers;
    }

    public static void setNumbers(ArrayList<String> numbers){
        mynumbers = numbers;
    }

    public static ArrayList<ContactBean> getContactList(){
        return myContactList;
    }

    public static void setContactList( ArrayList<ContactBean> contactList){
        myContactList = contactList;
    }

}
