package com.winhearts.arappmarket.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import com.winhearts.arappmarket.download.loader.UpdateAsyncTaskUtil;
import com.winhearts.arappmarket.model.AppUpdate;
import com.winhearts.arappmarket.modellevel.ModeLevelVms;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.view.UpdateHintDialog;

/**
 * 应用升级工具类
 */
public class AppUpdateUtil {


    public static void appUpdate(final Context mContext, final Handler handler) {


        ModeLevelVms.appUpdate(mContext, new ModeUser<AppUpdate>() {
            @Override
            public void onJsonSuccess(AppUpdate appUpdate) {
                if ("1".equals(appUpdate.getResult())) {
                    askUpdate(mContext, appUpdate, handler);
                }
            }

            @Override
            public void onRequestFail(Throwable e) {

            }
        });
    }

    public static void askUpdate(final Context mContext,
                                 final AppUpdate appUpdate, final Handler handler) {

        final UpdateHintDialog dialog = new UpdateHintDialog(mContext,
                UpdateHintDialog.Type_Can_Update, appUpdate.getVersionDesc());
        dialog.setUpdateButtonClick(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String filepathString = appUpdate.getUpdateUrl();
                if (handler != null) {
                    update(mContext, filepathString, Util.APK_NAME, handler);
                }
                dialog.dismiss();
            }

        });
        if (mContext != null) {
            dialog.show();
        }

    }

    public static void update(final Context mContext, final String url,
                              String apkName, final Handler handler) {
        if (StorageUtils.isExternalStorageWritable()) {

            final UpdateAsyncTaskUtil updateAsyncTaskUtil = new UpdateAsyncTaskUtil(
                    mContext, true);
            if (handler != null) {
                updateAsyncTaskUtil.setHandler(handler);
            }
            updateAsyncTaskUtil.execute(url, apkName);

        } else {
            ToastUtils.show(mContext, "没有存储卡，不能升级！");
        }
    }

}
