package com.winhearts.arappmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.model.AccountUserInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAccount;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.ViewUtils;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.SettingItemView;
import com.winhearts.arappmarket.view.SettingPopwindow;

/**
 * @author hedy 应用分类 ，进来后需要跳转到 指定
 **/

public class SettingActivity extends BaseActivity {
    private final static String TAG = "SettingActivity";
    private HorizontalLayout mAdvertLayout;
    private SettingItemView managerUser, phoneHelper, info;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));
        mAdvertLayout = (HorizontalLayout) this.findViewById(R.id.vl_setting_content);
        mAdvertLayout.setVisibility(View.VISIBLE);
        int viewWidth = (ScreenUtil.getScreenWidth(this) * 15 / 17);
        int viewHeight = ScreenUtil.getScreenHeight(this) * 7 / 11;

        managerUser = new SettingItemView(this).bindData(R.drawable.manager_backgroud, R.drawable.manager_head_portrait, R.string.manage_user);
        phoneHelper = new SettingItemView(this).bindData(R.drawable.phone_helper_backgroud, R.drawable.setting_phone_helper, R.string.manage_phone_helper);
        info = new SettingItemView(this).bindData(R.drawable.info_backgroud, R.drawable.info, R.string.manage_info);

        mAdvertLayout.setSize(viewWidth, viewHeight, 1, 3);

        mAdvertLayout.addItemView(managerUser, 0, 0, 0, 1, 1, 0, 3);
        mAdvertLayout.addItemView(phoneHelper, 0, 1, 0, 2, 1, 0, 3);
        mAdvertLayout.addItemView(info, 0, 2, 0, 3, 1, 0, 3);

        managerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpAccountManager();
            }
        });
        phoneHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                jumpPhoneHelper();
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                jumpUpdateWindow();
            }
        });

    }

    private void getManagerInfo() {
        if (!TextUtils.isEmpty(ConstInfo.accountWsId) && !TextUtils.isEmpty(ConstInfo.accountTokenId)) {
            ModeLevelAccount.getUserInfo(this, ConstInfo.accountWsId, ConstInfo.accountTokenId, new ModeUser<AccountUserInfo>() {
                @Override
                public void onJsonSuccess(AccountUserInfo accountUserInfo) {
                    String credits = accountUserInfo.getCredits();
                    Spannable numberSpannable = new SpannableString("积分：" + (credits == null ? 0 : credits));
                    numberSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    managerUser.setCredits(numberSpannable);
                    if (TextUtils.isEmpty(accountUserInfo.getNickName())) {
                        managerUser.setNickname(accountUserInfo.getWsId());
                    } else {
                        managerUser.setNickname(accountUserInfo.getNickName());
                    }
                }

                @Override
                public void onRequestFail(Throwable e) {
                    managerUser.setNickname(getResources().getString(R.string.manage_user));
                }
            });

        } else {
            managerUser.setNickname(getResources().getString(R.string.manage_user));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    managerUser.requestFocus();
                    isFirst = false;
                }
            }, 400);

        }
        getManagerInfo();
    }

    private void jumpUpdateWindow() {
        SettingPopwindow upPopwindow = new SettingPopwindow(SettingActivity.this, SettingPopwindow.UPDATE_WINDOW);
        upPopwindow.show(mAdvertLayout);
    }

    private void jumpPhoneHelper() {
        SettingPopwindow upPopwindow = new SettingPopwindow(SettingActivity.this, SettingPopwindow.PHONE_HEPLPER);
        upPopwindow.show(mAdvertLayout);
    }

    private void jumpAccountManager() {
        Intent intent = new Intent(SettingActivity.this, AccountManagerActivity.class);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdvertLayout.clearItems();
        ViewUtils.unbindDrawables(findViewById(R.id.main_tabs_container));
    }
}
