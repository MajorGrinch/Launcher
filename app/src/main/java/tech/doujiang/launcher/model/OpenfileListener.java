package tech.doujiang.launcher.model;

/**
 * Created by grinch on 28/03/2017.
 */

public interface OpenfileListener {
    void onFinish(String response);

    void onError(Exception ex);
}
