package tech.doujiang.launcher.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import tech.doujiang.launcher.R;
import tech.doujiang.launcher.database.WorkspaceDBHelper;

public class LauncherActivity extends Activity  implements OnClickListener {
    private List<ResolveInfo> mApps;
    private List<String> forbiddenPackage;
    private WorkspaceDBHelper dbHelper;
    GridView mGrid;

    private int PRIORITY = 100;
    ImageButton phone, message, db_interact, add_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadApps();
        setContentView(R.layout.activity_launcher);
        getForbiddenPackage();

        phone = (ImageButton) findViewById(R.id.phone_app);
        message = (ImageButton) findViewById(R.id.message_app);

        phone.setOnClickListener(this);
        message.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.phone_app:
                intent = new Intent(this, PhoneAppActivity.class);
                startActivity(intent);
                break;
            case R.id.message_app:
                intent = new Intent(this, SMSListActivity.class);
                startActivity(intent);
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
