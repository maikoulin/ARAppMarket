package com.winhearts.arappmarket.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;


import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

/**
 * 新用户退出提示对话框
 */
public class NewUserExitDialog extends Dialog {


    private final Context mContext;
    private final LayoutParams lp;


    public NewUserExitDialog(Context context) {
        super(context, R.style.dialog);
        // 初始
        this.mContext = context;

        setContentView(R.layout.dialog_new_user_exit);
        // 初始UI
        initView();

        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        // lp.dimAmount = 0; // 去背景遮盖
        // lp.alpha = 1.0f;
        // 重设宽度
//		lp.width = (int) (ScreenUtil.getScreenWidth((Activity) this.mContext) / 2);
        lp.width = ScreenUtil.getScreenWidth(context);
        lp.height = ScreenUtil.getScreenHeight(context);
        getWindow().setAttributes(lp);
    }


    /**
     * 根据type初始化
     */
    private void initView() {
        findViewById(R.id.bt_dialog_new_user_exit_stay_more).requestFocus();
        findViewById(R.id.bt_dialog_new_user_exit_stay_more).setOnClickListener(onClickListener);
        findViewById(R.id.bt_dialog_new_user_exit).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_dialog_new_user_exit_stay_more:
                    dismiss();
                    break;
                case R.id.bt_dialog_new_user_exit:
                    ((Activity) mContext).finish();
                    PrefNormalUtils.putString(PrefNormalUtils.IS_NEW_USER_exit, "0");
                    break;
            }
        }
    };


}
