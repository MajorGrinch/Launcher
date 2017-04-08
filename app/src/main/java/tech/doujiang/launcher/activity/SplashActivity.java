package tech.doujiang.launcher.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.service.CallSmsFirewallService;
import tech.doujiang.launcher.service.ReportLocationService;
import tech.doujiang.launcher.service.RequestFileService;
import tech.doujiang.launcher.service.ServerConnectService;
import tech.doujiang.launcher.util.Loginfo;
import tech.doujiang.launcher.util.Loginprocess;

public class SplashActivity extends AppCompatActivity {
    private TextView tv_splash_version;
    private static final int GET_INFO_SUCCESS = 10;
    private static final int SERVER_ERROR = 11;
    private static final int SERVER_URL_ERROR = 12;
    private static final int PROTOCOL_ERROR = 13;
    private static final int IO_ERROR = 14;
    private static final int XML_PARSE_ERROR = 15;
    private static final int DOWNLOAD_SUCCESS = 16;
    private static final int DOWNLOAD_ERROR = 17;
    protected static final String TAG = "SplashActivity";
    private long startTime;
    private RelativeLayout rl_splash;
    private long endTime;
    private ProgressDialog pd;
    private String useron;
    private Loginfo loginfo;
    private boolean confirmstatus = false;

    private void loadMainUI(String username) {
        if (confirmstatus) {
//            Intent intent = new Intent(this, CallSmsFirewallService.class);
//            startService(intent);
//            intent = new Intent(this, ReportLocationService.class);
//            intent.putExtra("username", username);
//            startService(intent);
//            intent = new Intent(this, RequestFileService.class);
//            intent.putExtra("username", username);
//            startService(intent);
//            intent = new Intent(this, ServerConnectService.class);
//            startService(intent);
            Intent intent = new Intent(this, LauncherActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        }
        else{
            Log.e("State: ", "Unauthorized account");
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        rl_splash.startAnimation(aa);
        useron = "hzton";
        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        String psw = intent.getStringExtra("psw");
        loginfo = new Loginfo( username, psw );

        Thread userconfirm = new Thread(new Runnable() {
            @Override
            public void run() {
                Loginprocess loginprocess = new Loginprocess(loginfo);
                confirmstatus = loginprocess.confirm();
                loadMainUI(username);
            }
        });
        userconfirm.start();

    }
}
