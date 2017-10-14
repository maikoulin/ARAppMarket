package com.winhearts.arappmarket.mvp.p;

import android.content.Context;
import android.view.ViewGroup;

import com.winhearts.arappmarket.mvp.ui.AppDownloadUi;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.download.manage.IManageDownload;
import com.winhearts.arappmarket.download.manage.ManageDownloadStatus;
import com.winhearts.arappmarket.download.manage.ManagerDownloadImpl;
import com.winhearts.arappmarket.download.manage.ManagerDownloadListener;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.SoftwareInfo;

/**
 * Description:app下载持有者  实现类
 * Created by lmh on 2016/4/6.
 */
public class AppDownloadPresenterImpl implements AppDownloadPresenter {

    private AppDownloadUi<ViewGroup> ui;
    //    private List<IManageDownload> downloadList = new ArrayList<IManageDownload>();
    private IManageDownload managerDownload;
    private AppDownloadInstallCompletedListener listener;

    public AppDownloadPresenterImpl(Context context, AppDownloadUi<ViewGroup> ui, String name, String packageName, String icon) {
        this.ui = ui;
        register(context, name, packageName, icon);
        setDownloadListener();
        ui.getVar().setTag(R.id.id_tag_download, ManageDownloadStatus.INITIAL);
    }


    @Override
    public void destroy() {
        if (managerDownload != null) {
            managerDownload.unregister();
            managerDownload = null;
        }
    }


    @Override
    public void startAndGetUrl2Download() {
        managerDownload.startGetUrl2Down();
    }

    @Override
    public void start(String url) {
        managerDownload.start(url);
    }


    @Override
    public void pause() {
        managerDownload.pause();
    }

    @Override
    public void cancelAndDelete() {
        managerDownload.cancelAndDelete();
    }

    @Override
    public AppDownloadUi<ViewGroup> getUI() {
        return ui;
    }


    @Override
    public ManageDownloadStatus getCompletedInstallStatus() {
        return (ManageDownloadStatus) ui.getVar().getTag(R.id.id_tag_download);
    }

    @Override
    public void setCompletedInstallStatus(ManageDownloadStatus status) {
        ui.getVar().setTag(R.id.id_tag_download, status);
    }

    @Override
    public ManageDownloadStatus getRealtimeStatus() {
        return managerDownload.getStatus();
    }

    @Override
    public void setOnInstallCompletedListener(AppDownloadInstallCompletedListener listener) {
        this.listener = listener;
    }


    private void setDownloadListener() {
        managerDownload.setDownloadListener(new ManagerDownloadListener() {


            @Override
            public void onDownloadError() {
                ui.setCompletedInstallStatus(ManageDownloadStatus.DOWNLOAD_FAIL);
                ui.showBarText("下载失败");
                ui.showText("重试");
            }

            @Override
            public void onTaskError() {
                ui.setCompletedInstallStatus(ManageDownloadStatus.TASK_CREATE_FAIL);
                ui.showBarText("任务失败");
                ui.showText("重试");
            }

            @Override
            public void onTastCancel() {
                ui.setCompletedInstallStatus(ManageDownloadStatus.TASK_CANCEL);

            }

            @Override
            public void onTaskCreate() {
                ui.setCompletedInstallStatus(ManageDownloadStatus.TASK_CREATE);

            }

            @Override
            public void onTaskWait() {
                ui.setCompletedInstallStatus(ManageDownloadStatus.TASK_WAIT);
            }

            @Override
            public void onInstallStart() {
                ui.showBarText("安装中..");
            }

            @Override
            public void onDownloadCompleted() {
                //ui.showBarText("安装中...");

            }

            @Override
            public void onInstallCompleted(boolean isSuccess, String packageName) {
                ViewGroup viewGroup = ui.getVar();
                if (isSuccess) {
                    //几种情况的显示不一样
                    ui.showCompletedInstall();
                    viewGroup.setTag(R.id.id_tag_download, ManageDownloadStatus.INSTALL_COMPLETE);
                    destroy();
                } else {
                    Object tag = viewGroup.getTag();
                    if (tag instanceof SoftwareInfo) {
                        SoftwareInfo softwareInfo = (SoftwareInfo) viewGroup.getTag();
                        DownRecordInfo recordInfo = softwareInfo.getDownRecordInfo();
                        if (recordInfo != null) {
                            recordInfo.setDownlength(recordInfo.getFileSize());
                            recordInfo.setState(DownloadManager.INSTALL_FAIL);
                        } else {
                            recordInfo = new DownRecordInfo();
                            recordInfo.setState(DownloadManager.INSTALL_FAIL);
                            softwareInfo.setDownRecordInfo(recordInfo);
                        }
                    }
                    ui.setCompletedInstallStatus(ManageDownloadStatus.INSTALL_NONE);
                    viewGroup.setTag(R.id.id_tag_download, ManageDownloadStatus.INSTALL_NONE);
                    ui.showBarText("安装失败");
                    ui.showText("重试");
                }
                if (listener != null) {
                    listener.onCopmletedListener();
                }
            }

            @Override
            public void onProgressPlay(int max, int current) {

                if (max != 0) {
                    ui.setBarMax(max);
                }
                if (current != 0) {
                    ui.showBarText("下载中");
                    ui.setBarProgress(current);
                }
            }
        });
    }


    @Override
    public void addClickedListenerLogic() {
    }


    /**
     * 注册下载过程
     *
     * @param context
     * @param name
     * @param packageName
     * @param icon
     */
    private void register(Context context, String name, String packageName, String icon) {
        managerDownload = new ManagerDownloadImpl(context, name, packageName, icon);
        managerDownload.register();

    }
}
