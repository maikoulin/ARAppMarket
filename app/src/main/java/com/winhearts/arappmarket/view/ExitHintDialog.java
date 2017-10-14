package com.winhearts.arappmarket.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.winhearts.arappmarket.R;


/**
 * 退出提示对话框
 */
public class ExitHintDialog extends Dialog {


    private final Context mContext;
    private final LayoutParams lp;


    public ExitHintDialog(Context context) {
        super(context, R.style.dialog);
        // 初始
        this.mContext = context;

        setContentView(R.layout.dialog_exit_hint);
        // 初始UI
        initView();

        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        // lp.dimAmount = 0; // 去背景遮盖
        // lp.alpha = 1.0f;
        // 重设宽度
//		lp.width = (int) (ScreenUtil.getScreenWidth((Activity) this.mContext) / 2);
        lp.width = mContext.getResources().getDimensionPixelSize(R.dimen.exit_dialog_w);
        lp.height = mContext.getResources().getDimensionPixelSize(R.dimen.exit_dialog_h);
        getWindow().setAttributes(lp);
    }


    /**
     * 根据type初始化
     */
    private void initView() {
        findViewById(R.id.bt_dialog_new_user_install).requestFocus();
        findViewById(R.id.bt_dialog_new_user_install).setOnClickListener(onClickListener);
        findViewById(R.id.bt_dialog_new_user_no_thank).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_dialog_new_user_install:
                    dismiss();
                    break;
                case R.id.bt_dialog_new_user_no_thank:
                    ((Activity) mContext).finish();
                    System.exit(0);
                    break;
            }
        }
    };


}
