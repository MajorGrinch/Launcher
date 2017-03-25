package tech.doujiang.launcher.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.util.IsonlineClient;
import tech.doujiang.launcher.util.Loginprocess;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


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

            sp = getSharedPreferences("users", MODE_WORLD_READABLE);
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
                    Log.v("Button", "login!");
                    if (networkstatus){
                        LoginMain();
                    } else {
                        Toast.makeText(LoginActivity.this, "Server unreachable!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnExit.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//                     IsonlineClient isonlineClient = new IsonlineClient();
//                     isonlineClient.offlineconnect(etAccount.getText().toString(),false);
//                    serverconnectconfig.setisonline(false);
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
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("psw", psw);
            startActivity(intent);
        }
}
