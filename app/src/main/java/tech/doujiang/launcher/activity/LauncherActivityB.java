package tech.doujiang.launcher.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.util.RSAUtil;
import tech.doujiang.launcher.util.TempHelper;

public class LauncherActivityB extends AppCompatActivity {
    private List<String> forbiddenPackage;
    private WorkspaceDBHelper workHelper;
    private List<ResolveInfo> mApps;
    private MyDatabaseHelper dbHelper;
    GridView mGrid;
    private String username;

    private int PRIORITY = 100;
    ImageButton phone, message, db_interact, add_contact;
    Button updatekey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loadApps();
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //getForbiddenPackage();
    }

//
//
//
//    private ArrayList<String> getForbiddenPackage() {
//        ArrayList<String> forbiddenPackage = new ArrayList<String>();
//        // Just a sample.
//        forbiddenPackage.add("com.guoshisp.mobilesafe");
//        return forbiddenPackage;
//    }
//
//    private void loadApps() {
//        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
//        mApps.clear();
//    }

}
