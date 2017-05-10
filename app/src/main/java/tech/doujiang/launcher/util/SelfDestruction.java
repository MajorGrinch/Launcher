package tech.doujiang.launcher.util;

import android.app.ActivityManager;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import tech.doujiang.launcher.model.MyApplication;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by kirk on 4/23/17.
 */

public class SelfDestruction extends WebSocketListener {
    private String username;

    public SelfDestruction(String username) {
        this.username = username;
    }

    private static final String TAG = "SelfDestruction";
    private OkHttpClient client;
    private WebSocket selfDestructionSocket;

    public void run() {
        client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://192.168.1.101:8080/MobileSafeServer/SelfDestruction/" + username)
                .build();
        selfDestructionSocket = client.newWebSocket(request, this);

        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher().executorService().shutdown();
    }

    public void stop(){
        Log.d(TAG, "stop");
        selfDestructionSocket.close(1000, "Exit System");
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send("hello...");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "onMessage: " + text);
        if (text.equals("Erase")) {
            Intent intent = new Intent("xyz.majorgrinch.mobilesafe.SELF_DESTRUCTION");
            MyApplication.getContext().sendBroadcast(intent);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(TAG, "onMessage ByteString: " + bytes.utf8());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        Log.d(TAG, "onClosing: " + code + " " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        Log.d(TAG, "onClosed");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.d(TAG, "onFailure");
    }
}
