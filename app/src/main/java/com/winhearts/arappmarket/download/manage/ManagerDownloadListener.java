package com.winhearts.arappmarket.download.manage;

/**
 * Description:
 * Created by lmh on 2016/3/30.
 */
public interface ManagerDownloadListener {

    void onDownloadCompleted();
    void onDownloadError();
    void onTaskError();
    void onTastCancel();
    void onTaskCreate();
    void onTaskWait();

    void onInstallStart();

    void onInstallCompleted(boolean isSuccess, String packageName);
    void onProgressPlay(int max, int current);

}
