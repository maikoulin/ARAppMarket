package com.winhearts.arappmarket.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.logic.CheckLoginLogic;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.modellevel.ModeLevelFile;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 引导页，正常的APP调用
 */
public class LoadActivity extends BaseActivity {
    private Context mContext;
    private static final String TAG = "LoadActivity";
    private Subscription subscription;
    private String mAssociate;
    private String mLayoutCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        verifyStoragePermissions(this);
        //初始化界面
        initView();

    }


    private void initData() {
        String versionCode = PrefNormalUtils.getString(mContext, PrefNormalUtils.APP_UPDATE, "");
        if (!TextUtils.isEmpty(versionCode)) {
            String code = Util.getVersionCode(mContext);
            if (versionCode.equals(code)) {
                LoggerUtil.e(mContext.getString(R.string.appmarket_install_error), code);
            } else {
                //升级成功强制清理布局版本，获取新布局
                Pref.saveString(Pref.LAYOUT_VERSION, "", mContext);
            }
            PrefNormalUtils.putString(mContext, PrefNormalUtils.APP_UPDATE, "");
        }
        mLayoutCode = getIntent().getStringExtra("layoutCode");
        VpnStoreApplication.getApp().setLayoutCode(mLayoutCode);
        updateLayout(mLayoutCode);
        int showTime;
        if (TextUtils.isEmpty(mLayoutCode)) {
            CheckLoginLogic.getInstance().checkLogin(false);
            String layoutString = Pref.getString(Pref.LAYOUT_STRING, mContext, "");
            if (!TextUtils.isEmpty(layoutString)) {
                Layout layout = new Gson().fromJson(layoutString, Layout.class);
                mAssociate = layout.getAssociate();
                try {
                    showTime = Integer.parseInt(layout.getDisplayTime());
                    if (showTime < 0) {
                        showTime = 3;
                    }
                } catch (NumberFormatException e) {
                    showTime = 3;
                }
            } else {
                showTime = 3;
            }
        } else {
            showTime = 0;
        }
        ModeLevelFile.syncBgImageFile(mLayoutCode);
        initBgImage();
        if (showTime == 0) {
            intentMain();
        } else {
            delayIntent(showTime);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_load);
        TextView versionView = (TextView) findViewById(R.id.tv_version);
        versionView.setText(Util.getVersionName(this));
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(R.id.simpleDraweeView_load_bg);
        CommonHierarchy.setLoadImage(simpleDraweeView, mLayoutCode);
    }


    private void initBgImage() {
        if (PrefNormalUtils.getBoolean(PrefNormalUtils.URL_BG_IMG_IS_CHANGE, false)) {
            File file = ModeLevelFile.getBgImageFile(mLayoutCode);
            if (file != null && file.exists()) {
                Fresco.getImagePipeline().evictFromCache(Uri.fromFile(file));
                ImageRequest imageRequest = ImageRequest.fromUri(Uri.fromFile(file));
                ImagePipelineFactory.getInstance().getImagePipeline().prefetchToBitmapCache(imageRequest, this);
            }
            PrefNormalUtils.putBoolean(PrefNormalUtils.URL_BG_IMG_IS_CHANGE, false);
        }
    }

    @Override
    protected void onDestroy() {
        VolleyQueueController.getInstance().cancelAll(TAG);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void delayIntent(int showTime) {
        subscription = Observable.timer(showTime, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        intentMain();
                    }
                });
    }


    private void intentMain() {
        Intent intent = new Intent(LoadActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 在欢迎界面屏蔽BACK键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (!TextUtils.isEmpty(mAssociate)) {
                if (subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                }
                Intent intent = new Intent(this, AppDetailActivity.class);
                intent.putExtra("packageName", mAssociate);
                intent.putExtra("isBackMain", true);
                startActivity(intent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        isCleanBackRecord = true;
        super.onResume();
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                initData();
            }
        } catch (Exception e) {
            LogDebugUtil.e("==========",e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initData();
                } else {
                    finish();
                    System.exit(0);
                }
                break;
        }
    }
}
