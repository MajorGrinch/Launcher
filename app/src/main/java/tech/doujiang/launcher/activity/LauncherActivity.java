package tech.doujiang.launcher.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

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

public class LauncherActivity extends Activity  implements OnClickListener {
    private List<ResolveInfo> mApps;
    private List<String> forbiddenPackage;
    private WorkspaceDBHelper workHelper;
    private MyDatabaseHelper dbHelper;
    GridView mGrid;
    private String username;

    private int PRIORITY = 100;
    ImageButton phone, message,db_interact, add_contact;
    Button updatekey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadApps();
        setContentView(R.layout.activity_launcher);
        getForbiddenPackage();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        phone = (ImageButton) findViewById(R.id.phone_app);
        message = (ImageButton) findViewById(R.id.message_app);
        updatekey = (Button)findViewById(R.id.update_rsa);


        phone.setOnClickListener(this);
        message.setOnClickListener(this);
        updatekey.setOnClickListener(this);

        dbHelper = MyDatabaseHelper.getDBHelper(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.phone_app:
                intent = new Intent(this, ContactAppActivity.class);
                startActivity(intent);
                break;
            case R.id.message_app:
                intent = new Intent(this, SMSListActivity.class);
                startActivity(intent);
                break;
            case R.id.update_rsa:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Map<String, byte[]> keyMap = RSAUtil.generateKeyBytes();
                            dbHelper.addKey("PublicKey",
                                    Base64.encodeToString(keyMap.get(RSAUtil.PUBLIC_KEY), Base64.NO_WRAP));
                            dbHelper.addKey("PrivateKey",
                                    Base64.encodeToString(keyMap.get(RSAUtil.PRIVATE_KEY),Base64.NO_WRAP));
                            OkHttpClient client = new OkHttpClient();
                            RequestBody reqBody = new FormBody.Builder()
                                    .add("PublicKey",
                                            Base64.encodeToString(keyMap.get(RSAUtil.PUBLIC_KEY),Base64.NO_WRAP))
                                    .add("username", username)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(TempHelper.server_url + "/UpdateRSA")
                                    .post(reqBody)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                Log.d("update rsa", response.body().string());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }



    private ArrayList<String> getForbiddenPackage() {
        ArrayList<String> forbiddenPackage = new ArrayList<String>();
        // Just a sample.
        forbiddenPackage.add("com.guoshisp.mobilesafe");
        return forbiddenPackage;
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
        mApps.clear();
    }

}
