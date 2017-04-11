package tech.doujiang.launcher.activity;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.database.WorkspaceDBHelper;
import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.util.RSAUtil;
import tech.doujiang.launcher.util.TempHelper;

public class LauncherActivity extends AppCompatActivity {

    private List<String> forbiddenPackage;
    private WorkspaceDBHelper workHelper;
    private List<ResolveInfo> mApps;
    private MyDatabaseHelper dbHelper;
    private String username;

    private DrawerLayout mDrawerLayout;

    private int PRIORITY = 100;

    public static final int UPDATE_KEY_SUCCESS = 1;

    public static final int UPDATE_KEY_FAILED = 2;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
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
        username = getIntent().getStringExtra("username");
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_contacts);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    Intent intent;

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_contacts:
                                intent = new Intent(MyApplication.getContext(), ContactAppActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_sms:
                                intent = new Intent(MyApplication.getContext(), SMSListActivityBeta.class);
                                startActivity(intent);
                                break;
                            case R.id.update_rsa:
                                try {
                                    Map<String, byte[]> keyMap = RSAUtil.generateKeyBytes();
                                    dbHelper.addKey("PublicKey",
                                            Base64.encodeToString(keyMap.get(RSAUtil.PUBLIC_KEY), Base64.NO_WRAP));
                                    dbHelper.addKey("PrivateKey",
                                            Base64.encodeToString(keyMap.get(RSAUtil.PRIVATE_KEY), Base64.NO_WRAP));
                                    String pubkey = Base64.encodeToString(keyMap.get(RSAUtil.PUBLIC_KEY), Base64.NO_WRAP);
                                    RSAUtil.UpdateRSAKey(pubkey, username, new Callback() {
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
                                break;
                            default:
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

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
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
        return true;
    }
}
