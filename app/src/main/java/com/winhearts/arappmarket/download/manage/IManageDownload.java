package com.winhearts.arappmarket.download.manage;

/**
 * Description:
 * Created by lmh on 2016/3/30.
 */
public interface IManageDownload {

    void register();

    void unregister();


    void start();
    void start(String url);
    void startGetUrl2Down();

    void pause();

    void cancelAndDelete();

    //void delete();

    void installStart();


    void setDownloadListener(ManagerDownloadListener listener);

    ManageDownloadStatus getStatus();
}
