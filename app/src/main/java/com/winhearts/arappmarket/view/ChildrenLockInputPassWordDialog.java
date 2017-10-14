package com.winhearts.arappmarket.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.ScreenUtil;


/**
 * 儿童锁密码输入对话框
 *
 * @author lmh
 */
public class ChildrenLockInputPassWordDialog extends Dialog {

    public static final int STYLE_PASSWORD = 1;
    public static final int STYLE_MENU = 2;
    public static final int STYLE_ENSURE = 3;


    private Context context;
    private LayoutParams lp;

    private TextView titleTv;
    private TextView subTitleTv;
    private TextView bottomTitleTv;

    private View passwordView;
    private View menuView;
    private View ensureView;

    private ChildrenLockInputPassWordView passwordView1;
    private ChildrenLockInputPassWordView passwordView2;
    private ChildrenLockInputPassWordView passwordView3;
    private ChildrenLockInputPassWordView passwordView4;


    public ChildrenLockInputPassWordDialog(Context context) {
        super(context, R.style.dialog);
        // 初始
        this.context = context;

        setContentView(R.layout.dialog_children_lock_input);

        View view = findViewById(R.id.ll_dialog_children_lock_root_view);
        view.measure(0, 0);
        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        // lp.dimAmount = 0; // 去背景遮盖
        // lp.alpha = 1.0f;
        // 重设宽度
        lp.width = view.getMeasuredWidth();
//		lp.height = ScreenUtil.dip2px(this.context, 130.0f);
//        lp.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    /**
     * 根据type初始化
     */
    public void initView(int style) {
        titleTv = (TextView) findViewById(R.id.tv_children_lock_input_dialog_title);
        passwordView = findViewById(R.id.ll_children_lock_password_input);
        subTitleTv = (TextView) findViewById(R.id.tv_children_lock_input_dialog_subtitle);
        switch (style) {
            case STYLE_PASSWORD:
                findViewById(R.id.btn_children_lock_input).setOnClickListener(onClickListener);
                bottomTitleTv = (TextView) findViewById(R.id.tv_children_lock_input_dialog_bottom_title);
                titleTv.setText("请输入儿童锁密码");

                passwordView1 = (ChildrenLockInputPassWordView) findViewById(R.id.children_lock_input_1);
                passwordView2 = (ChildrenLockInputPassWordView) findViewById(R.id.children_lock_input_2);
                passwordView3 = (ChildrenLockInputPassWordView) findViewById(R.id.children_lock_input_3);
                passwordView4 = (ChildrenLockInputPassWordView) findViewById(R.id.children_lock_input_4);

                break;
            case STYLE_MENU:
                titleTv.setText("帐户扣款儿童锁：已开启");
                passwordView.setVisibility(View.GONE);
                menuView = findViewById(R.id.ll_children_lock_menu);
                menuView.setVisibility(View.VISIBLE);
                subTitleTv.setVisibility(View.GONE);
                findViewById(R.id.btn_children_lock_changes_password).setOnClickListener(onClickListener);
                findViewById(R.id.btn_children_lock_close).setOnClickListener(onClickListener);
                break;
            case STYLE_ENSURE:
                titleTv.setText("提示");
                titleTv.setTextSize(40);
                subTitleTv.setVisibility(View.VISIBLE);
                subTitleTv.setTextSize(24);
                subTitleTv.setGravity(Gravity.LEFT);
                subTitleTv.setPadding(0, ScreenUtil.dip2px(context, 20), 0, ScreenUtil.dip2px(context, 20));
                subTitleTv.setText("是否确认关闭帐户扣款儿童锁，关闭后通过账户余额支付时无需输入密码即可完成支付。");
                passwordView.setVisibility(View.GONE);
                ensureView = findViewById(R.id.ll_children_lock_ensure);
                ensureView.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_children_lock_ensure).setOnClickListener(onClickListener);
                break;
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_children_lock_input:
                    if (onPasswordSubBtnClickListener != null) {

                        StringBuilder stringBuilder = new StringBuilder();

                        stringBuilder.append(passwordView1.getCurIndexValue());
                        stringBuilder.append(passwordView2.getCurIndexValue());
                        stringBuilder.append(passwordView3.getCurIndexValue());
                        stringBuilder.append(passwordView4.getCurIndexValue());

                        onPasswordSubBtnClickListener.onPasswordSubBtnClick(stringBuilder.toString());
                    }

                    break;
                case R.id.btn_children_lock_changes_password:
                    if (onMenuSubBtnClickListener != null)
                        onMenuSubBtnClickListener.onPasswordChangeBtnClick();
                    break;
                case R.id.btn_children_lock_close:
                    if (onMenuSubBtnClickListener != null)
                        onMenuSubBtnClickListener.onCloseLockClick();
                    break;
                case R.id.btn_children_lock_ensure:
                    if (onEnsureBtnClickListener != null)
                        onEnsureBtnClickListener.onEnsureBtnClick();
                    break;
            }
        }
    };

    public void showPasswordError() {
        titleTv.setTextColor(Color.parseColor("#ff1313"));
        titleTv.setText("密码错误请重新输入");
        bottomTitleTv.setVisibility(View.VISIBLE);
        bottomTitleTv.setText("忘记密码请拨打申述电话:0592-3764291");
    }

    public void showSetPassword() {
        titleTv.setText("帐户扣款儿童锁");
        titleTv.setTextColor(Color.WHITE);
        subTitleTv.setText("设置开启后,帐户扣款前需输入密码才能完成支付.");
        subTitleTv.setGravity(Gravity.CENTER);
        subTitleTv.setVisibility(View.VISIBLE);
        bottomTitleTv.setVisibility(View.GONE);

        findViewById(R.id.children_lock_input_1).requestFocus();
        passwordView1.reSetIndexValue();
        passwordView2.reSetIndexValue();
        passwordView3.reSetIndexValue();
        passwordView4.reSetIndexValue();
    }

    public void showInputPassword() {
        titleTv.setText("请输入儿童锁密码");
        titleTv.setTextColor(Color.WHITE);
        subTitleTv.setVisibility(View.GONE);
        bottomTitleTv.setVisibility(View.INVISIBLE);

        findViewById(R.id.children_lock_input_1).requestFocus();
        passwordView1.reSetIndexValue();
        passwordView2.reSetIndexValue();
        passwordView3.reSetIndexValue();
        passwordView4.reSetIndexValue();
    }

    public void showChangePassword() {
        titleTv.setText("请输入新的儿童锁密码");
        titleTv.setTextColor(Color.WHITE);
        subTitleTv.setVisibility(View.GONE);
        bottomTitleTv.setVisibility(View.INVISIBLE);
        findViewById(R.id.children_lock_input_1).requestFocus();

        passwordView1.reSetIndexValue();
        passwordView2.reSetIndexValue();
        passwordView3.reSetIndexValue();
        passwordView4.reSetIndexValue();
    }

    private OnPasswordSubBtnClickListener onPasswordSubBtnClickListener;

    public void setOnPasswordSubBtnClickListener(OnPasswordSubBtnClickListener onPasswordSubBtnClickListener) {
        this.onPasswordSubBtnClickListener = onPasswordSubBtnClickListener;

    }

    private OnMenuSubBtnClickListener onMenuSubBtnClickListener;

    public void setOnMenuSubBtnClickListener(OnMenuSubBtnClickListener onMenuSubBtnClickListener) {
        this.onMenuSubBtnClickListener = onMenuSubBtnClickListener;

    }

    private OnEnsureBtnClickListener onEnsureBtnClickListener;

    public void setOnEnsureBtnClickListener(OnEnsureBtnClickListener onEnsureBtnClickListener) {
        this.onEnsureBtnClickListener = onEnsureBtnClickListener;

    }

    public interface OnPasswordSubBtnClickListener {

        public void onPasswordSubBtnClick(String password);
    }

    public interface OnMenuSubBtnClickListener {
        public void onPasswordChangeBtnClick();

        public void onCloseLockClick();

    }

    public interface OnEnsureBtnClickListener {
        public void onEnsureBtnClick();

    }


}
