package com.winhearts.arappmarket.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.event.InstallEvent;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.RxBus;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * 安装成功弹框提示服务
 * Created by lmh on 2016/4/1.
 */
public class InstallHintService extends Service {
    private Context context;
    private AlertDialog alertDialog = null;
    private CompositeSubscription compositeSubscription;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        compositeSubscription = new CompositeSubscription();
        initEvent();
    }

    private void initEvent() {
        Subscription subscription = RxBus.getDefault()
                .toObservable(InstallEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<InstallEvent>() {
                    @Override
                    public void call(InstallEvent installEvent) {
                        String showInstall = Pref.getString(Pref.SHOW_INSTALL_HINT, context, null);
                        String packageName = installEvent.getPackageName();
                        String appName = AppManager.getAppName(context, packageName);
                        if (TextUtils.isEmpty(showInstall) || showInstall.equals("0") || Constant.INSTALL_SHOW) {
                            ToastUtils.show(context, appName + "安装完成,可前往我的应用打开使用", Toast.LENGTH_LONG);
                            return;
                        }
                        if ((alertDialog != null && alertDialog.isShowing()) || Constant.DIALOG_SHOW) {
                            ToastUtils.show(context, appName + "安装完成,可前往我的应用打开使用", Toast.LENGTH_LONG);
                            return;
                        }
                        String topPackageName = ManagerUtil.getTopActivityName(context);
                        if (TextUtils.isEmpty(topPackageName) || (!topPackageName.equals(context.getPackageName()))) {
                            return;
                        }
                        if (alertDialog == null) {
                            alertDialog = new AlertDialog.Builder(context).create();
                        }
                        View view = DownloadService.getHintOpenDialogView(InstallHintService.this, alertDialog, packageName);
                        if (view != null) {
                            Window window = alertDialog.getWindow();
                            if (window != null) {
                                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                window.setBackgroundDrawableResource(R.color.transparent);
                            }

                            alertDialog.show();
                            alertDialog.setContentView(view);
                            if (window != null) {
                                window.setLayout(ScreenUtil.getScreenWidth(context), ScreenUtil.getScreenHeight(context));
                            }
                            Constant.INSTALL_SHOW = true;
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LoggerUtil.e("InstallHintService", throwable.getMessage());
                    }
                });

        compositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }

}
