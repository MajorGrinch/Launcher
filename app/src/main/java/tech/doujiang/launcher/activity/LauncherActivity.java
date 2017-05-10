package tech.doujiang.launcher.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aykuttasil.callrecord.service.CallRecordService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.model.SystemSMSBean;
import tech.doujiang.launcher.service.AppMonitorService;
import tech.doujiang.launcher.service.ReportLocationService;
import tech.doujiang.launcher.service.RequestFileService;
import tech.doujiang.launcher.service.SMSBlockService;
import tech.doujiang.launcher.util.OpenfileUtil;
import tech.doujiang.launcher.util.RSAUtil;
import tech.doujiang.launcher.util.SMSOpUtil;

public class LauncherActivity extends AppCompatActivity {

    private List<String> forbiddenPackage;
    private WorkspaceDBHelper workHelper;
    private List<ResolveInfo> mApps;
    private MyDatabaseHelper dbHelper;
    private String username;
    private int userid;
    private static final String TAG = "LauncherActivity";


    private DrawerLayout mDrawerLayout;

    public static final int UPDATE_KEY_SUCCESS = 1;

    public static final int UPDATE_KEY_FAILED = 2;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_KEY_SUCCESS:
                    Toast.makeText(LauncherActivity.this,
                            "Update Key Successfully", Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_KEY_FAILED:
                    Toast.makeText(LauncherActivity.this,
                            "Update Key Failed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        MyApplication.setNumbers(dbHelper.getNumber());
        username = getIntent().getStringExtra("username");
        userid = getIntent().getIntExtra("userid", -1);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_contacts);
        if(username == null){
            Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            Intent intent;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_exit:
                        AlertDialog.Builder confirmExit = new AlertDialog.Builder(LauncherActivity.this);
                        confirmExit.setTitle("Stark Tech Launcher");
                        confirmExit.setMessage("Are you sure to exit?");
                        confirmExit.setCancelable(false);
                        confirmExit.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        confirmExit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        confirmExit.show();
                        break;
                    case R.id.nav_contacts:
                        intent = new Intent(MyApplication.getContext(), ContactAppActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_sms:
                        intent = new Intent(MyApplication.getContext(), SMSListActivityBeta.class);
                        startActivity(intent);
                        break;
                    case R.id.update_rsa:
                        AlertDialog.Builder updateRSADialog = new AlertDialog.Builder(LauncherActivity.this);
                        updateRSADialog.setTitle("Stark Tech Launcher");
                        updateRSADialog.setMessage("Are you sure to update your key");
                        updateRSADialog.setCancelable(false);
                        updateRSADialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateRSAKeyPair(username);
                            }
                        });
                        updateRSADialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        updateRSADialog.show();
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                TextView nav_header_username = (TextView)findViewById(R.id.username);
                TextView nav_header_userid = (TextView)findViewById(R.id.userid);
                nav_header_username.setText(username);
                nav_header_userid.setText(String.valueOf(userid));
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        Intent intent = new Intent(this, CallRecordService.class);
        stopService(intent);
        intent = new Intent(this, ReportLocationService.class);
        stopService(intent);
        intent = new Intent(this, SMSBlockService.class);
        stopService(intent);
        intent = new Intent(this, RequestFileService.class);
        stopService(intent);
        intent = new Intent(this, AppMonitorService.class);
        stopService(intent);String cacDir = new String(Environment.getDataDirectory().toString())
                + "/data/com.ostrichmyself.txtReader/cache";
        File dir = new File(cacDir);
//        if (dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++) {
//                new File(dir, children[i]).delete();
//            }
//        }
    }

    public void updateRSAKeyPair(String name){
        try {
            Map<String, byte[]> keyMap = RSAUtil.generateKeyBytes();
            dbHelper.addKey("PublicKey",
                    Base64.encodeToString(keyMap.get(RSAUtil.PUBLIC_KEY), Base64.NO_WRAP));
            dbHelper.addKey("PrivateKey",
                    Base64.encodeToString(keyMap.get(RSAUtil.PRIVATE_KEY), Base64.NO_WRAP));
            String pubkey = Base64.encodeToString(keyMap.get(RSAUtil.PUBLIC_KEY), Base64.NO_WRAP);
            RSAUtil.UpdateRSAKey(pubkey, name, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("LauncherActivity", "Update KeyPair Successfully");
                    Message message = new Message();
                    message.what = UPDATE_KEY_SUCCESS;
                    handler.sendMessage(message);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("LauncherActivity", "Update failed!");
                    Message message = new Message();
                    message.what = UPDATE_KEY_FAILED;
                    handler.sendMessage(message);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
