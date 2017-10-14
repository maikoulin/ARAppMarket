package com.winhearts.arappmarket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.winhearts.arappmarket.service.DownloadService;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.utils.DisplayConfig;
import com.winhearts.arappmarket.utils.common.PackageUtils;

/**
 * 广播接收者：应用安装，应用卸载
 * Created by lmh on 2015/10/22.
 */
public class AppInstallReceiver extends BroadcastReceiver {

    public static String handUninstallAppName = "";  //用来区分手动卸载及强制卸载、升级

    @Override
    public void onReceive(Context context, final Intent intent) {

        DownloadService downloadService = DownloadService.getInstance();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (downloadService != null) {
                downloadService.receiverFlash(packageName, 4);//4 安装成功
            }
            Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_MYAPP);
            context.sendBroadcast(intent1);
            PackageUtils.completedInstallBroadcast(context, packageName);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (downloadService != null) {
                downloadService.receiverFlash(packageName, 3);
            }
            if (!TextUtils.isEmpty(handUninstallAppName) && handUninstallAppName.equals(packageName)) {
                ModeLevelAmsUpload.uploadUninstallApp(context, packageName, null);//上报卸载
                handUninstallAppName = "";
            }
            Intent intent1 = new Intent(DisplayConfig.ACTION_UPDATE_MYAPP);
            context.sendBroadcast(intent1);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
        }
    }
}
