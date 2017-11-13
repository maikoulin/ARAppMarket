package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.AccountUserInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAccount;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;

/**
 * 账号管理页
 *
 * @author liw
 */
public class AccountManagerActivity extends BaseActivity {
    public final static int REQUEST_LOGIN = 0;
    public final static int ACK_LOGIN_COMPLETED = 1;

    private TextView tvNickname;
    private TextView tvWsNum;
    private TextView tvPhoneNum;
    private View tvHint;

    private Button changeAccountTv;

    private View loadView;
    private View stubView;
    private ViewStub errorViewStub;
    private LinearLayout llContent;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        context = this;
        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));
        initTitle();
        initView();
        judgeToken();

        ConstInfo.updateAccountId7Pref(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        loadView.setVisibility(View.VISIBLE);
        requestUserInf();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent arg2) {
        // TODO Auto-generated method stub
        LogDebugUtil.d("lee", "++++++++++" + result);
        if (ACK_LOGIN_COMPLETED == result) {
            loadView.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.INVISIBLE);
            requestUserInf();

        } else {
            if (TextUtils.isEmpty(ConstInfo.accountTokenId)) {
                finish();
            }

        }

        super.onActivityResult(request, result, arg2);
    }

    private void judgeToken() {
        // 未登陆
        if (TextUtils.isEmpty(ConstInfo.accountTokenId)) {
            loadView.setVisibility(View.GONE);
            Intent intent = new Intent(AccountManagerActivity.this, AccountLoginActivity.class);
            intent.putExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
            startActivityForResult(intent, REQUEST_LOGIN);

        } else {
            AccountUserInfo accountUserInfo = ConstInfo.updateAccountInfo7Pref(this);
            if (accountUserInfo == null || TextUtils.isEmpty(accountUserInfo.getwinId())) {
                requestUserInf();
            } else {
                loadView.setVisibility(View.GONE);
                llContent.setVisibility(View.VISIBLE);
                showAccountInfo(accountUserInfo);
                changeAccountTv.requestFocus();
            }
        }
    }

    private void requestUserInf() {
        ModeLevelAccount.getUserInfo(this, ConstInfo.accountWinId, ConstInfo.accountTokenId,
                new ModeUser<AccountUserInfo>() {

                    @Override
                    public void onRequestFail(Throwable e) {
                        // TODO 错误是显示还是干啥
                        loadView.setVisibility(View.GONE);
                        if ("unknown_fail".equals(e.getMessage())) {
                            ToastUtils.show(context, "数据加载失败");
                        } else {
                            ToastUtils.show(context, "" + e.getMessage());
                        }

                        Intent intent = new Intent(AccountManagerActivity.this, AccountLoginActivity.class);
                        intent.putExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
                        startActivityForResult(intent, REQUEST_LOGIN);
                    }

                    @Override
                    public void onJsonSuccess(AccountUserInfo t) {
                        // TODO Auto-generated method stub
                        loadView.setVisibility(View.GONE);
                        if (t != null) {
                            llContent.setVisibility(View.VISIBLE);
                            showAccountInfo(t);
                            changeAccountTv.requestFocus();
                        } else {
                            Intent intent = new Intent(AccountManagerActivity.this, AccountLoginActivity.class);
                            intent.putExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
                            startActivityForResult(intent, REQUEST_LOGIN);
                        }

                    }
                });
    }

    private void showAccountInfo(AccountUserInfo info) {
        LogDebugUtil.d("lee", "info = " + info.toString());
        String winId = "账号:  " + (TextUtils.isEmpty(info.getwinId()) ? "--" : info.getwinId());
        String nickname = "昵称:  " + (TextUtils.isEmpty(info.getNickName()) ? "--" : info.getNickName());
        tvPhoneNum.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        if (TextUtils.isEmpty(info.getPhoneNum())) {
            tvPhoneNum.setFocusable(true);
            tvPhoneNum.setClickable(true);
            tvPhoneNum.setText(getResources().getString(R.string.account_bind_phone));
            tvPhoneNum.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AccountManagerActivity.this, AccountSubmitActivity.class);
                    intent.putExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
                    intent.putExtra(Constant.PHONE_TYPE, Constant.BINDING);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
            });
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvPhoneNum.setFocusable(false);
            tvHint.setVisibility(View.INVISIBLE);
            tvPhoneNum.setText(info.getPhoneNum());
        }

        tvNickname.setText(nickname);
        tvWsNum.setText(winId);

    }

    private void initTitle() {
        ImageView icon = (ImageView) findViewById(R.id.iv_modlue_title);
        TextView title = (TextView) findViewById(R.id.tv_module_title);
        icon.setImageResource(R.drawable.com_icon_back);
        title.setText("账号管理");

    }

    private void initView() {
        tvNickname = (TextView) findViewById(R.id.tv_account_nickname);
        tvWsNum = (TextView) findViewById(R.id.tv_account_ws_num);
        tvPhoneNum = (TextView) findViewById(R.id.tv_account_phone_num);
        tvHint = findViewById(R.id.tv_account_phone_hint);
        loadView = findViewById(R.id.mode_load);
        loadView.setVisibility(View.VISIBLE);
        changeAccountTv = (Button) findViewById(R.id.btn_account_changes);
        llContent = (LinearLayout) findViewById(R.id.ll_account_content);
        llContent.setVisibility(View.INVISIBLE);
        changeAccountTv.setOnClickListener(onClickListener);
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_account_changes:
                    Intent intent = new Intent(AccountManagerActivity.this, AccountLoginActivity.class);
                    intent.putExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
                    startActivityForResult(intent, REQUEST_LOGIN);
                    break;
            }
        }
    };

}