package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.logic.CheckLoginLogic;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.modellevel.ModeLevelFile;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.Util;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 引导页，直接调用应用管理界面时会先调用此activity
 */
public class LoadActivity1 extends BaseActivity {
    private static final String TAG = "LoadActivity1";
    Context mContext;
    private TextView versionView;
    Subscription subscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load1);
        mContext = this;
        ModeLevelFile.syncBgImageFile(null);
        initView();
        updateLayout(null);
        CheckLoginLogic.getInstance().checkLogin(false);
        delayIntent();
    }

    private void initView() {
        versionView = (TextView) findViewById(R.id.tv_version);
        versionView.setText(Util.getVersionName(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


    private void delayIntent() {
        String layoutString = Pref.getString(Pref.LAYOUT_STRING, mContext, "");
        int showTime;
        if (!TextUtils.isEmpty(layoutString)) {
            Layout layout = new Gson().fromJson(layoutString, Layout.class);
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
        if (showTime == 0) {
            Intent intent = new Intent(LoadActivity1.this, MainActivity.class);
            intent.putExtra("isMyApp", true);
            startActivity(intent);
            finish();
        }
        subscription = Observable.timer(showTime, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        loadFinish();
                    }
                });
    }

    private void loadFinish() {
        Intent intent = new Intent(LoadActivity1.this, MainActivity.class);
        intent.putExtra("isMyApp", true);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 在欢迎界面屏蔽BACK键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 取消所有任务
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        isCleanBackRecord = true;
        super.onResume();
    }

}
