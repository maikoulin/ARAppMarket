package com.winhearts.arappmarket.download.manage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.winhearts.arappmarket.model.StateInfo;
import com.winhearts.arappmarket.utils.DisplayConfig;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.common.PackageUtils;
import com.winhearts.arappmarket.utils.common.RxBus;

/**
 * Description:
 * Created by lmh on 2016/3/30.
 */
public class ManagerReceiver {
    private String TAG = "ManagerDownload";
    Context mContext;

    public ManagerReceiver(Context context) {
        mContext = context;
    }

    public void register() {
        // 注册广播
        IntentFilter statusFilter = new IntentFilter();
        //该过滤广播就是安装广播，已经在AppInstallReceiver 广播中进行转换
        statusFilter.addAction(PackageUtils.INSTALL);
        statusFilter.addAction(DisplayConfig.ACTION_RESULT_MESSAGE);
        mContext.registerReceiver(statusBroadCast, statusFilter);


        //注册安装广播
//        IntentFilter addFilter = new IntentFilter();
//        addFilter.addAction("android.intent.action.PACKAGE_REMOVED");
//        addFilter.addAction("android.intent.action.PACKAGE_ADDED");
//        addFilter.addDataScheme("package");
//        mContext.registerReceiver(installReceiver, addFilter);
    }

    public void unregister() {
        mContext.unregisterReceiver(statusBroadCast);
//        mContext.unregisterReceiver(installReceiver);
    }

    private void sendEventDownload(ManagerDownloadEvent event) {
        RxBus.getDefault().post(event);
    }

    private BroadcastReceiver statusBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String actionString = intent.getAction();

            LogDebugUtil.d(TAG, "action = " + actionString);
            if (TextUtils.isEmpty(actionString)) {
                return;
            }

            //下载操作广播
            if (actionString.equals(DisplayConfig.ACTION_RESULT_MESSAGE)) {
                String packageNameString = intent.getStringExtra("packageName");

                StateInfo info = new StateInfo();
                info.setId(intent.getIntExtra("appId", 0));
                info.setFileSize(intent.getIntExtra("fileSize", 0));
                info.setDownloadSize(intent.getIntExtra("downloadSize", 0));
                info.setState(intent.getIntExtra("state", ManageDownloadStatus.INITIAL.value));
                info.setPackageName(intent.getStringExtra("packageName"));
                sendEventDownload(new ManagerDownloadEvent(ManagerDownloadEvent.TYPE_DOWNLOAD, info, packageNameString, DisplayConfig.ACTION_RESULT_MESSAGE));


            } else if (actionString.equals(PackageUtils.INSTALL)) {
                String actString = intent.getStringExtra(PackageUtils.ACT);
                String packageNameString = intent.getStringExtra("packageName");
                LogDebugUtil.d(TAG,
                        "-intall-state---" + actionString);

                sendEventDownload(new ManagerDownloadEvent(ManagerDownloadEvent.TYPE_INSTALL, null, packageNameString, actString));
            }
        }

    };

//    public BroadcastReceiver installReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String actionString = intent.getAction();
//            //卸载的广播
//            if (actionString.equals("android.intent.action.PACKAGE_REMOVED")) {
//                //包名
//                String lPackageName = intent.getDataString().substring(8);
//                if (lPackageName != null) {
//                    //TODO：更新内存list
//
//                    sendEventDownload(new ManagerDownloadEvent(ManagerDownloadEvent.TYPE_REMOVE, null, lPackageName, "android.intent.action.PACKAGE_ADDED"));
//                }
//
//            }
//            //安装新app的广播
//            else if (actionString.equals("android.intent.action.PACKAGE_ADDED")) {
//                String lPackageName = intent.getDataString().substring(8);
//                if (lPackageName != null) {
//                    //TODO：更新内存list
//
//                    sendEventDownload(new ManagerDownloadEvent(ManagerDownloadEvent.TYPE_ADD, null, lPackageName, "android.intent.action.PACKAGE_ADDED"));
//                }
//            }
//
//        }
//    };


}
