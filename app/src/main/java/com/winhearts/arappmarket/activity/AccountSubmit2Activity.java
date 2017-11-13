package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.event.SubmitCompletedEvent;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.AccountIDToken;
import com.winhearts.arappmarket.modellevel.ModeLevelAccount;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.common.PhoneCheckUtil;
import com.winhearts.arappmarket.utils.common.RxBus;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.view.LoadDialog;

/**
 * 账号注册页2
 *
 * @author liw
 */
public class AccountSubmit2Activity extends BaseActivity implements OnClickListener {
    private CountTimer countTimer;
    private Context mContext;

    private Button timerButton;
    private Button okButton;
    private EditText srcCodeEditText;
    private EditText keyEditText;

    private String id;
    private String key;
    private String srcCode;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_submit2);
        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));
        mContext = this;
        initTitle();
        initView();

        id = getIntent().getStringExtra("id");

        startCountTimer();
    }

    private void startCountTimer() {
        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }
        countTimer = new CountTimer(timerButton, 60 * 1000, 1000);
        countTimer.start();
        timerButton.setFocusable(false);
    }

    private void endCountTimer() {
        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }
    }

    private void initTitle() {
        ImageView icon = (ImageView) findViewById(R.id.iv_modlue_title);
        TextView title = (TextView) findViewById(R.id.tv_module_title);
        icon.setImageResource(R.drawable.com_icon_back);
        type = getIntent().getIntExtra(Constant.PHONE_TYPE, Constant.REGIESTER);
        if (type == Constant.BINDING) {
            title.setText("绑定手机");
        } else {
            title.setText("注册");
        }

    }

    private void initView() {
        srcCodeEditText = (EditText) findViewById(R.id.edt_account_id);
        keyEditText = (EditText) findViewById(R.id.edt_account_key);
        timerButton = (Button) findViewById(R.id.btn_account_timer);
        okButton = (Button) findViewById(R.id.btn_account_ok);

        timerButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
    }

    public class CountTimer extends CountDownTimer {

        private final String tag = "CountDownUtils";
        private Button tv_display;

        public CountTimer(Button textView, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub

            tv_display = textView;
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            timerButton.setFocusable(true);
            tv_display.setTextColor(Color.WHITE);
            tv_display.setText("重新发送");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub
            tv_display.setTextColor(getResources().getColor(R.color.titletext_unsel_color));
            tv_display.setTextColor(Color.WHITE);
            tv_display.setText("重新发送\n" + "(" + (millisUntilFinished / 1000 - 1) + " s)");

        }

    }

    private void bindingPhone() {
        final LoadDialog loadDialog = new LoadDialog(this, false);
        loadDialog.show();
        key = keyEditText.getText().toString();
        srcCode = srcCodeEditText.getText().toString();
        ModeLevelAccount.bindPhone(this, id, key, srcCode, new ModeUserErrorCode<String>() {
            @Override
            public void onJsonSuccess(String s) {
                loadDialog.dismiss();
                ToastUtils.show(mContext, "绑定成功");
                int fromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
                if (fromType == Constant.FROM_NORMAL) {
                    Intent intent = new Intent(AccountSubmit2Activity.this, AccountManagerActivity.class);
                    RxBus.getDefault().post(new SubmitCompletedEvent());
                    startActivity(intent);
                    finish();
                } else if (fromType == Constant.FROM_PAY_GET_UID || fromType == Constant.FROM_PAY_ORDER) {
                    RxBus.getDefault().post(new SubmitCompletedEvent());
                    finish();
                }
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                ToastUtils.show(mContext, "绑定失败" + e.getMessage());
                loadDialog.dismiss();
            }
        });

    }

    private void requestSubmit() {
        final LoadDialog loadDialog = new LoadDialog(this, false);
        loadDialog.show();
        key = keyEditText.getText().toString();
        srcCode = srcCodeEditText.getText().toString();
        ModeLevelAccount.submit(this, id, key, srcCode, new ModeUserErrorCode<AccountIDToken>() {

            @Override
            public void onRequestFail(int code, Throwable e) {
                // TODO Auto-generated method stub
                loadDialog.dismiss();
                ToastUtils.show(mContext, code, e);
                srcCodeEditText.requestFocus();
            }

            @Override
            public void onJsonSuccess(AccountIDToken t) {
                // TODO Auto-generated method stub
                loadDialog.dismiss();
                ToastUtils.show(mContext, "注册成功");

                // finish();

                requestLogin();// 登录
            }
        });
    }

    private void requestSecCode() {
        final LoadDialog loadDialog = new LoadDialog(mContext);
        loadDialog.setShowAble(false);
        loadDialog.show();
        String typeString;
        if (type == Constant.BINDING) {
            typeString = "BINDING_PHONE";
        } else {
            typeString = "REGISTER";
        }
        ModeLevelAccount.getSecurityCode(this, typeString, id, new ModeUserErrorCode<String>() {

            @Override
            public void onRequestFail(int code, Throwable e) {
                // TODO Auto-generated method stub
                loadDialog.dismiss();
                ToastUtils.show(mContext, code, e);
            }

            @Override
            public void onJsonSuccess(String t) {
                // TODO Auto-generated method stub
                loadDialog.dismiss();
                startCountTimer();

            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_account_ok:
                if (TextUtils.isEmpty(srcCodeEditText.getText().toString().trim())) {
                    ToastUtils.show(mContext, "请输入验证码");
                    return;
                }
                if (TextUtils.isEmpty(keyEditText.getText().toString().trim())) {
                    ToastUtils.show(mContext, "请输入密码");
                    return;
                }

                String content = keyEditText.getText().toString().trim();
                if (TextUtils.isEmpty(content) || !PhoneCheckUtil.isKeyCode(content)) {
                    ToastUtils.show(mContext, getResources().getString(R.string.account_key_tips));
                    return;
                }
                if (type == Constant.BINDING) {
                    bindingPhone();
                } else {
                    requestSubmit();
                }
                break;

            case R.id.btn_account_timer:
                requestSecCode();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        endCountTimer();
    }

    private void requestLogin() {
        final LoadDialog dialog = new LoadDialog(this);
        dialog.show();
        dialog.setInfo("登录中...");
        final int fromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);

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
                    login2jump(t, fromType);

                }

            }
        });
    }

    private void login2jump(AccountIDToken t, int fromType) {
        // 注销
        ConstInfo.setAccountId2Pref(mContext, "", "");
        ModeLevelAccount.exit(this, ConstInfo.accountWinId, ConstInfo.accountTokenId, null);

        // 保存
        ConstInfo.setAccountId2Pref(mContext, t.getWinId(), t.getLoginToken());
        ToastUtils.show(mContext, "登录成功");

        if (fromType == Constant.FROM_NORMAL) {
            Intent intent = new Intent(this, AccountManagerActivity.class);
            RxBus.getDefault().post(new SubmitCompletedEvent());
            startActivity(intent);
            finish();
        } else if (fromType == Constant.FROM_PAY_GET_UID || fromType == Constant.FROM_PAY_ORDER) {

            RxBus.getDefault().post(new SubmitCompletedEvent());
            finish();
        }

    }
}
