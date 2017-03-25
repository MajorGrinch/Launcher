package tech.doujiang.launcher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;

public class CallReceiver extends BroadcastReceiver {
    private PhoneStateListener mPhoneListener = null;

    public CallReceiver() {

    }

    public CallReceiver(PhoneStateListener phoneListener) {
        this.mPhoneListener = phoneListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();

    }
}
