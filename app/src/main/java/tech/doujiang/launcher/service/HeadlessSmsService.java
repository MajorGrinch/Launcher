package tech.doujiang.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeadlessSmsService extends Service {
    public HeadlessSmsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
