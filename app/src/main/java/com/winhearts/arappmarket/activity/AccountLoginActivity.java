package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.event.SubmitCompletedEvent;
import com.winhearts.arappmarket.model.AccountIDToken;
import com.winhearts.arappmarket.modellevel.ModeLevelAccount;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.common.PhoneCheckUtil;
import com.winhearts.arappmarket.utils.common.RxBus;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.view.LoadDialog;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 账号登录页
 *
 * @author liw
 */
public class AccountLoginActivity extends BaseActivity implements OnClickListener {


    private EditText idEditText;
    private EditText keyEditText;
    private Context mContext;
    private LoadDialog dialog;

    int fromType;// 出发点
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_login);

        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));
        mContext = this;
        initTitle();
        initView();

        fromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
        initEvent();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


    private void initEvent() {
        subscription = RxBus.getDefault()
                .toObservable(SubmitCompletedEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SubmitCompletedEvent>() {
                    @Override
                    public void call(SubmitCompletedEvent submitCompletedEvent) {
                        finish();
                    }

                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        finish();
                    }
                });
    }

    private void initTitle() {
        ImageView icon = (ImageView) findViewById(R.id.iv_modlue_title);
        TextView title = (TextView) findViewById(R.id.tv_module_title);
        icon.setImageResource(R.drawable.account_icon);
        title.setText("账号");

    }

    private void initView() {
        findViewById(R.id.tv_account_submit).setOnClickListener(this);
        findViewById(R.id.btn_account_login).setOnClickListener(this);

        idEditText = (EditText) findViewById(R.id.edt_account_id);
        keyEditText = (EditText) findViewById(R.id.edt_account_key);
    }

    private void requestLogin() {
        dialog = new LoadDialog(this);
        dialog.show();
        dialog.setInfo("登录中...");
        String id = idEditText.getText().toString().trim();
        String key = keyEditText.getText().toString().trim();

        ModeLevelAccount.login(this, id, key, fromType, new ModeUserErrorCode<AccountIDToken>() {

            @Override
            public void onRequestFail(int code, Throwable e) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                ToastUtils.show(mContext, code, e);
            }

            @Override
            public void onJsonSuccess(AccountIDToken t) {
                // TODO Auto-generated method stub
                if (t != null) {

                    dialog.dismiss();
                    login2jump(t);

                }

            }
        });
    }

    private void exitOld(String oldWinId, String oldTokenId) {
        ConstInfo.setAccountId2Pref(mContext, "", "");
        if (TextUtils.isEmpty(oldWinId) || TextUtils.isEmpty(oldTokenId)) {
            return;
        }
        ModeLevelAccount.exit(this, oldWinId, oldTokenId, new ModeUser<String>() {

            @Override
            public void onJsonSuccess(String t) {
                // ToastUtils.show(mContext, "注销成功");
            }

            @Override
            public void onRequestFail(Throwable e) {
                // ToastUtils.show(mContext, "注销失败");
            }
        });
    }

    private void login2jump(AccountIDToken t) {
        // 注销
        exitOld(ConstInfo.accountWinId, ConstInfo.accountTokenId);

        // 保存
        ConstInfo.setAccountId2Pref(mContext, t.getWinId(), t.getLoginToken());
        ToastUtils.show(mContext, "登录成功");

        setResult(AccountManagerActivity.ACK_LOGIN_COMPLETED);
        finish();

    }

    private boolean checkPhone() {
        String phoneString = idEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phoneString)) {
            ToastUtils.show(mContext, "请输入手机号码");
            return false;
        }

        if (!PhoneCheckUtil.isMobile(phoneString)) {
            ToastUtils.show(mContext, "请输入正确的手机号");
            return false;
        }

        if (TextUtils.isEmpty(keyEditText.getText().toString().trim())) {
            ToastUtils.show(mContext, "请输入密码");
            return false;
        }

        return true;
    }

    private boolean checkKey() {
        String content = keyEditText.getText().toString().trim();
        if (!PhoneCheckUtil.isKeyCode(content)) {
            ToastUtils.show(mContext, getResources().getString(R.string.account_key_tips));
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_account_submit:

                Intent submitIntent = new Intent(mContext, AccountSubmitActivity.class);
                int fromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
                submitIntent.putExtra(Constant.FROM_TYPE, fromType);
                submitIntent.putExtra(Constant.PHONE_TYPE, Constant.REGIESTER);
                mContext.startActivity(submitIntent);

                break;
            case R.id.btn_account_login:
                if (checkPhone()) {
                    requestLogin();
                }

                break;

            default:
                break;
        }
    }
}
