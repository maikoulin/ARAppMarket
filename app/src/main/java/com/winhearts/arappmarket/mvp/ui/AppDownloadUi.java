package com.winhearts.arappmarket.mvp.ui;

import android.view.ViewGroup;

import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;

/**
 * Description: App下载ui接口
 * Created by lmh on 2016/4/6.
 */
public interface AppDownloadUi<T extends ViewGroup>{
    T getVar();

    void showText(String tip);
    void showBarText(String barTip);
    void setBarMax(int max);
    void setBarProgress(int current);
    void showCompletedInstall();


    /**
     * 获取状态  只有两个安装完成，初始状态
     * INITIAL
     * INSTALL_COMPLETE
     * @return
     */
    ManageDownloadStatus getCompletedInstallStatus();

    void setCompletedInstallStatus(ManageDownloadStatus status);

    void setPresenter(AppDownloadPresenter presenter);
    AppDownloadPresenter getPresenter();

}
