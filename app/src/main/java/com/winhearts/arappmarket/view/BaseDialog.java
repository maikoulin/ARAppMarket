package com.winhearts.arappmarket.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.ScreenUtil;

/**
 * 应用详情页dialog基类
 * Created by lmh on 2017/3/16.
 */

abstract class BaseDialog extends AlertDialog {
    private onKeyBack onKeyBack;
    View mRootView;

    BaseDialog(Context context) {
        super(context);
        initView();
    }

    BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    BaseDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
    }

    void setWindowContentView(View view, int width, int height) {
        Window window = getWindow();
        if (window != null) {
            window.setContentView(view);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = ScreenUtil.dip2px(getContext(), width);
            params.height = ScreenUtil.dip2px(getContext(), height);
            window.setAttributes(params);
            window.setBackgroundDrawableResource(R.color.transparent);

        }
    }

   protected abstract void initView();

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onKeyBack != null) {
                onKeyBack.keyBack(false);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnKeyBack(BaseDialog.onKeyBack onKeyBack) {
        this.onKeyBack = onKeyBack;
    }

    public onKeyBack getOnKeyBack() {
        return onKeyBack;
    }

    public interface onKeyBack {
        void keyBack(boolean isUpdate);
    }

}