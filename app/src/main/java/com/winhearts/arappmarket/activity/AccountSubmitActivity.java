package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.modellevel.ModeLevelAccount;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.common.KeyBoardUtils;
import com.winhearts.arappmarket.utils.common.PhoneCheckUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.view.LoadDialog;

/**
 * 账号注册页1
 *
 * @author liw
 */
public class AccountSubmitActivity extends BaseActivity implements OnClickListener {
    private static String TAG = "AccountSubmitActivity";
    private Context mContext;
    private EditText phoneEditText;
    private CheckBox isAcceptCheckBox;
    private TextView agreementTextView;
    private int type;
    private WebView webView;
    private View mPopView;
    private PopupWindow mPopupWindow;
    private FrameLayout agreementContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_account_submit1);
        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));
        initTitle();
        initView();


    }


    private void requestSecCode() {
        final LoadDialog loadDialog = new LoadDialog(this);
        loadDialog.setShowAble(false);
        loadDialog.show();
        final String id = phoneEditText.getText().toString().trim();
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
                switch (code) {
                    case SubVolleyResponseHandler.REQUEST_FAIL:
                        ToastUtils.show(mContext, getResources().getString(R.string.connection_fail));
                        break;
                    default:
                        String value = e.getMessage().contains("refused") ? getResources().getString(R.string.connection_fail) : e.getMessage();
                        ToastUtils.show(mContext, value);
                        break;
                }
            }

            @Override
            public void onJsonSuccess(String t) {
                // TODO Auto-generated method stub
                loadDialog.dismiss();
                Intent intent = new Intent(mContext, AccountSubmit2Activity.class);
                intent.putExtra("id", id);
                int fromType = getIntent().getIntExtra(Constant.FROM_TYPE, Constant.FROM_NORMAL);
                intent.putExtra(Constant.FROM_TYPE, fromType);
                int type = getIntent().getIntExtra(Constant.PHONE_TYPE, Constant.REGIESTER);
                intent.putExtra(Constant.PHONE_TYPE, type);
                mContext.startActivity(intent);
                finish();
            }
        });
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
        findViewById(R.id.btn_account_seccode).setOnClickListener(this);
        phoneEditText = (EditText) findViewById(R.id.edt_account_id);
        isAcceptCheckBox = (CheckBox) findViewById(R.id.cb_account_accept);
        agreementTextView = (TextView) this.findViewById(R.id.account_tv_agreement);
        isAcceptCheckBox.setChecked(true);
        final Spannable spannable = new SpannableString(getResources().getString(R.string.account_accept_protol));
        //根据是否有交点改变协议字体颜色
        spannable.setSpan(new UnderlineSpan(), 4, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        agreementTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_drak)), 4, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                } else {
                    spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 4, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                agreementTextView.setText(spannable);
            }
        });
        agreementTextView.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (agreementContent != null) {
            agreementContent.removeView(webView);
            if (webView != null) {
                webView.onPause();
                webView.removeAllViews();
                webView.destroy();
                webView = null;
                mPopupWindow = null;
            }
        }
    }

    private boolean checkPhone() {
        String phoneString = phoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phoneString)) {
            ToastUtils.show(mContext, "手机号码不能为空");
            return false;
        }


        if (!PhoneCheckUtil.isMobile(phoneString)) {
            ToastUtils.show(mContext, "请输入正确的手机号码");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_account_seccode:

                if (isAcceptCheckBox.isChecked()) {

                    if (checkPhone()) {
                        requestSecCode();
                    }

                } else {
                    ToastUtils.show(mContext, "请同意用户注册协议");
                }


                break;
            case R.id.account_tv_agreement:
                if (mPopView == null) {
                    //初始化popwindow
                    mPopView = LayoutInflater.from(AccountSubmitActivity.this).inflate(R.layout.popwindow_agreement, null);
                    agreementContent = (FrameLayout) mPopView.findViewById(R.id.agreement_wv_content);
                    webView = new WebView(getApplicationContext());
                    agreementContent.addView(webView);
                    webView.loadUrl("file:///android_asset/" + "wangsuagreement.htm");
                    mPopupWindow = new PopupWindow(mPopView, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            true);
                    // 这两句加了，才能点击并且响应
                    mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
                    mPopupWindow.setOutsideTouchable(true);// 点击外部按钮 取消。按返回按键也取消
                }
                // 显示视图与指定视图之间的偏差,弹出popwindow
                mPopupWindow.showAtLocation(agreementTextView, Gravity.CENTER, 0, 0);//
                KeyBoardUtils.closeKeybord(phoneEditText);
                break;
            default:
                break;
        }
    }
}
