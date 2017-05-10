package tech.doujiang.launcher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import tech.doujiang.launcher.service.ServerConnectService;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        *   开机启动防火墙服务
        * */
//        Log.e("Receiver", "StartCallSmsFirewallService");
//        service = new Intent(context, ServerConnectService.class);
//        context.startService(service);
//        Log.e("Receiver", "ServerConnectService");
    }
}
