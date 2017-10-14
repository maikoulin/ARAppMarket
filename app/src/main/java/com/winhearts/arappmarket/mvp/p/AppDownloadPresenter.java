package com.winhearts.arappmarket.mvp.p;

import android.view.ViewGroup;

import com.winhearts.arappmarket.mvp.ui.AppDownloadUi;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;

/**
 * Description: app下载
 * Created by lmh on 2016/4/6.
 */
public interface AppDownloadPresenter {

    void addClickedListenerLogic();

    void destroy();

    void startAndGetUrl2Download();

    void start(String url);

    void pause();

    void cancelAndDelete();

    AppDownloadUi<ViewGroup> getUI();

    ManageDownloadStatus getCompletedInstallStatus();

    void setCompletedInstallStatus(ManageDownloadStatus status);

    ManageDownloadStatus getRealtimeStatus();

    void setOnInstallCompletedListener(AppDownloadInstallCompletedListener listener);

}
