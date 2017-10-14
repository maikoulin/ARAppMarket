package com.winhearts.arappmarket.download.manage;

import android.content.Context;
import android.text.TextUtils;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.download.DownloadManager;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.DownloadInfo;
import com.winhearts.arappmarket.model.StateInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsInstall;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.PackageUtils;
import com.winhearts.arappmarket.utils.common.RxBus;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Description: 下载工具类 抽象层封装  原来的那层有点乱
 * Created by lmh on 2016/3/30.
 */
public class ManagerDownloadImpl implements IManageDownload {
    private String TAG = "ManagerDownloadImpl";

    private DownRecordInfo mRecord;
    private Context mContext;
    private String mPackageName;
    private String mUrl;
    private String mAppName;
    private String mIconUrl;
    private Subscription subscription = null;
    private ManagerDownloadListener mListener;
    private ManageDownloadStatus currentStatus = ManageDownloadStatus.INITIAL;


    public ManagerDownloadImpl(Context context, String appName, String packageName, String iconUrl) {

        this.mContext = context;
        this.mPackageName = packageName;
        this.mAppName = appName;
//        this.mUrl = url;
        this.mIconUrl = iconUrl;

    }

    @Override
    public void register() {
        subscription = RxBus.getDefault().
                toObservable(ManagerDownloadEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ManagerDownloadEvent>() {
                    @Override
                    public void call(ManagerDownloadEvent managerDownloadEvent) {
                        if (managerDownloadEvent == null) {
                            return;
                        }
                        switch (managerDownloadEvent.type) {
                            case ManagerDownloadEvent.TYPE_ADD:
                                //dealAdd(event);
                                break;
                            case ManagerDownloadEvent.TYPE_INSTALL:

                                dealInstall(managerDownloadEvent);
                                break;
                            case ManagerDownloadEvent.TYPE_REMOVE:

                                cancelAndDelete();
                                break;
                            case ManagerDownloadEvent.TYPE_DOWNLOAD:
                                dealDwonload(managerDownloadEvent);
                                break;
                            default:
                                break;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LoggerUtil.w(mContext.getResources().getString(R.string.app_download_error) + "下载状态"
                                , LoggerUtil.getErrStack(throwable));
                    }
                });
    }

    @Override
    public void unregister() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private boolean isValid() {
        return !(TextUtils.isEmpty(mUrl) || TextUtils.isEmpty(mPackageName));

    }

    public void startGetUrl2Down() {
        currentStatus = ManageDownloadStatus.REQUEST_URL;
        if (!TextUtils.isEmpty(mUrl)) {
            addRecord(mUrl, mAppName, mPackageName, "none_version", mIconUrl);
            start();
            return;
        }
        ModeLevelAmsInstall.querySoftwareDownload(mContext, mPackageName, new ModeUserErrorCode<DownloadInfo>() {
            @Override
            public void onJsonSuccess(DownloadInfo info) {
                String path = info.getDownloadUrl();
                mUrl = path;
                if (path == null) {
                    ToastUtils.show(mContext, "目前该应用没有下载源，暂不能下载！");
                    currentStatus = ManageDownloadStatus.REQUEST_URL_FAIL;
                    return;
                }
                currentStatus = ManageDownloadStatus.REQUEST_URL_OK;
                addRecord(path, mAppName, mPackageName, "none_version", mIconUrl);
                start();

            }

            @Override
            public void onRequestFail(int code, Throwable e) {

                currentStatus = ManageDownloadStatus.REQUEST_URL_FAIL;
            }
        });
    }


    @Override
    public void start() {
        if (!isValid()) {
            return;
        }

        DownloadManager.start(mContext, mUrl, mPackageName);
    }

    @Override
    public void start(String url) {
        addRecord(url, mAppName, mPackageName, "none_version", mIconUrl);
        DownloadManager.start(mContext, url, mPackageName);
    }

    @Override
    public void pause() {
        if (!isValid()) {
            return;
        }
        DownloadManager.pause(mContext, mUrl, mPackageName);
    }

    @Override
    public void cancelAndDelete() {
        mRecord = getRecord();
        if (mRecord != null) {
            mRecord.setState(DownloadManager.CANCEL_TASK);
            RxBus.getDefault().post(mRecord);
            DownloadManager.appRecordAndFileDelete(mContext, mRecord);
        }
    }

    @Override
    public void installStart() {
        // PackageUtils.install(context, fn, record.getAppName(), info.applicationInfo.packageName, new Handler(Looper.getMainLooper()) {

    }

    @Override
    public void setDownloadListener(ManagerDownloadListener listener) {
        this.mListener = listener;
    }

    @Override
    public ManageDownloadStatus getStatus() {
        return currentStatus;
    }

    private void dealInstall(ManagerDownloadEvent event) {

        String actString = event.getActString();
        String packageNameString = event.getPackageName();
        if (mPackageName.equals(packageNameString)) {
            LogDebugUtil.d(TAG,
                    "-intall-state---" + actString);
            if (PackageUtils.INSTALL_START.equals(actString)) {
                //安装中 (开始安装)
                mListener.onInstallStart();


            } else if (PackageUtils.INSTALL_ERROR.equals(actString)) {
                //安装失败
                mListener.onInstallCompleted(false, mPackageName);
            } else if (PackageUtils.INSTALL_COMPLETED.equals(actString)) {
                //安装成功

                // state = 4;//更新状态
                currentStatus = ManageDownloadStatus.DOWNLOAD_COMPLETE;
                mListener.onInstallCompleted(true, mPackageName);
                cancelAndDelete();
            }
        }


    }

    private void dealAdd(ManagerDownloadEvent event) {
        String lPackageName = event.getPackageName();
        if (lPackageName != null && lPackageName.equals(mPackageName)) {
            //TODO：更新内存list

            mListener.onInstallCompleted(true, mPackageName);
        }
    }

    private void dealDwonload(ManagerDownloadEvent event) {
        StateInfo info = event.getStateInfo();
        // LogDebugUtil.d(TAG, "onReceive--" + " +++ " + info.toString());

        if (info.getPackageName() != null && info.getPackageName().equals(mPackageName)) {
            // record 赋值
            mRecord = getRecord();
            if (mRecord != null) {
                mRecord.setState(info.getState());
            }

            ManageDownloadStatus s = ManageDownloadStatus.from(info.getState());
            currentStatus = s;
            switch (s) {
                //-1 最初状态
                case INITIAL:

                    break;
                //0未下载 -> 1下载
                case DOWNLOAD_NONE:
                    break;

                //1正在下载 -> 2暂停
                case DOWNLOAD_LOADING:
                    mListener.onProgressPlay(info.getFileSize(), info.getDownloadSize());
                    break;

                //2暂停 -> 继续
                case DOWNLOAD_PAUSE:
                    break;

                //3下载完成 -> 安装 (自动安装，该状态先不用)
                case DOWNLOAD_COMPLETE:
                    //TODO:原来废弃 现在开起来  注：是自动操作安装(顺序上有可能会出现 先4后3的情况所以慎用回调函数)
                    mListener.onDownloadCompleted();
                    break;

                //4 安装完成 -> 打开 | 卸载
                case INSTALL_COMPLETE:
                    mListener.onDownloadCompleted();
                    break;

                //5 下载失败
                case DOWNLOAD_FAIL:
                    mListener.onDownloadError();
                    break;

                //6 等待下载
                case TASK_WAIT:
                    mListener.onTaskWait();
                    break;

                // 7 创建任务失败
                case TASK_CREATE_FAIL:
                    mListener.onTaskError();
                    break;
                //8 创建中
                case TASK_CREATE:
                    mListener.onTaskCreate();
                    break;

                //9 任务取消
                case TASK_CANCEL:
                    mListener.onTastCancel();
                    break;

                default:
                    LogDebugUtil.w(TAG, "others status--");
                    break;

            }

        }


    }

    private void updateRecordList() {

    }

    private void setRecordListState(int state) {
        for (int i = 0; i < VpnStoreApplication.recordList.size(); i++) {
            if (VpnStoreApplication.recordList.get(i).getPackageName().equals(mPackageName)) {
                VpnStoreApplication.recordList.get(i).setState(state);
            }
        }
    }

    private DownRecordInfo getRecord() {
        for (DownRecordInfo recordinfo : VpnStoreApplication.recordList) {
            if (recordinfo.getPackageName().equals(mPackageName)) {
                return recordinfo;
            }
        }
        return null;
    }

    private void addRecord(String path, String appName, String packageName, String versionName, String icon) {
        VpnStoreApplication.updateRecordList();
        mRecord = getRecord();
        if (mRecord == null) {
            BasicDataInfo.addDownRecord(0, 0, path, appName,
                    icon, "", ManageDownloadStatus.TASK_CREATE.value, packageName, versionName);

            VpnStoreApplication.updateRecordList();
            mRecord = getRecord();
            RxBus.getDefault().post(mRecord);
        }

        currentStatus = ManageDownloadStatus.TASK_CREATE;
    }

}
