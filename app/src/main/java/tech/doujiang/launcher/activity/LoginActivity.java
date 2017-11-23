package tech.doujiang.launcher.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.MyDatabaseHelper;
import tech.doujiang.launcher.model.MyApplication;
import tech.doujiang.launcher.service.AppMonitorService;
import tech.doujiang.launcher.service.ReportLocationService;
import tech.doujiang.launcher.service.RequestFileService;
import tech.doujiang.launcher.service.SMSBlockService;
import tech.doujiang.launcher.util.Loginfo;
import tech.doujiang.launcher.util.Loginprocess;
import tech.doujiang.launcher.util.RSAUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;


import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.aykuttasil.callrecord.service.CallRecordService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private EditText etAccount;
    private EditText etPW;
    private Button btnLogin;
    private Button btnExit;
    private CheckBox cbrp;
    private CheckBox cbal;
    private SharedPreferences sp;
    private Editor ed;
    private Context context;
    private boolean networkstatus = false;
    private MyDatabaseHelper dbHelper;
    private Loginfo loginfo;

    private ProgressDialog progressDialog;

    private JSONObject confirmstatus = null;
    //private WorkspaceDBHelper workHelper;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        etAccount = (EditText) findViewById(R.id.etaccount);
        etPW = (EditText) findViewById(R.id.etpw);
        cbrp = (CheckBox) findViewById(R.id.cbrp);
        cbal = (CheckBox) findViewById(R.id.cbal);
        btnLogin = (Button) findViewById(R.id.btnlogin);
        btnExit = (Button) findViewById(R.id.btnexit);
        dbHelper = MyDatabaseHelper.getDBHelper(this);
        sp = getSharedPreferences("users", MODE_PRIVATE);
        ed = sp.edit();
        Thread networktest = new Thread(new Runnable() {
            @Override
            public void run() {
                networkstatus = Loginprocess.networktest();
                Log.v("networktest", "thread over");
            }
        });
        networktest.start();
        if (sp.getBoolean("ISCHECK", false)) {
            cbrp.setChecked(true);
            etAccount.setText(sp.getString("oa_name", ""));
            etPW.setText(sp.getString("oa_pass", ""));
            if (sp.getBoolean("AUTO_ISCHECK", false)) {
                cbal.setChecked(true);
            }

        }
        btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Button", "login!");
                if (networkstatus) {
                    LoginMain();
                } else {
                    Toast.makeText(LoginActivity.this, "Server unreachable!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnExit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        cbrp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean isChecked1 = cbrp.isChecked();
                ed.putBoolean("ISCHECK", isChecked1);
                ed.commit();
            }
        });
        Boolean value1 = sp.getBoolean("AUTO_ISCHECK", false);
        cbal.setChecked(value1);
        cbal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isChecked2 = cbal.isChecked();
                ed.putBoolean("AUTO_ISCHECK", isChecked2);
                ed.commit();
            }
        });
        if (cbrp.isChecked() && cbal.isChecked()) {
            LoginMain();
        }

    }

    protected void LoginMain() {
        ed.putString("oa_name", etAccount.getText().toString());
        ed.putString("oa_pass", etPW.getText().toString());
        ed.commit();
        if (TextUtils.isEmpty(etAccount.getText().toString())) {
            Toast.makeText(this, R.string.empty_account, Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(etPW.getText().toString())) {
            Toast.makeText(this, R.string.empty_psw, Toast.LENGTH_LONG).show();
            return;
        }

        Log.v("Execute log", "LogMain");
        String username = etAccount.getText().toString();
        String psw = etPW.getText().toString();
        Log.v("Userinfo", username + " : " + psw);
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        Log.d("MacAddress", macAddress);
        loginfo = new Loginfo(username, psw, macAddress);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Thread userconfirm = new Thread(new Runnable() {
            @Override
            public void run() {
                Loginprocess loginprocess = new Loginprocess(loginfo);
                confirmstatus = loginprocess.confirm();
                if (confirmstatus != null) {
                    String name = null;
                    int userid = -1;
                    try {
                        name = confirmstatus.getString("name");
                        userid = confirmstatus.getInt("userid");
                    } catch (JSONException ex) {
                        Log.d(TAG, "return information error");
                    }


                    Intent intent = new Intent(LoginActivity.this, CallRecordService.class);
                    startService(intent);

                    intent = new Intent(LoginActivity.this, ReportLocationService.class);
                    intent.putExtra("username", name);
                    startService(intent);

                    updateRSA(name);

                    intent = new Intent(LoginActivity.this, SMSBlockService.class);
                    intent.putExtra("username", name);
                    startService(intent);

                    intent = new Intent(LoginActivity.this, AppMonitorService.class);
                    startService(intent);

                    intent = new Intent(LoginActivity.this, LauncherActivity.class);
                    intent.putExtra("username", name);
                    intent.putExtra("userid", userid);
                    Log.d(TAG, "username: " + name);
                    Log.d(TAG, "userid: " + userid);
                    progressDialog.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("State: ", "Unauthorized account");
                    progressDialog.setMessage("Unauthorized account");
                    progressDialog.dismiss();
                }
            }
        });
        userconfirm.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void loadMainUI(JSONObject confirm) {

    }

    private void updateRSA(final String username) {
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
                    Log.d(TAG, "Update KeyPair Successfully");
                    Intent intent = new Intent(MyApplication.getContext(), RequestFileService.class);
                    intent.putExtra("username", username);
                    startService(intent);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("Splash", "Update failed!");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
