package com.winhearts.arappmarket.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.winhearts.arappmarket.R;


/**
 * 加载对话框
 */
public class LoadDialog extends Dialog {
    private View view;
    private TextView infoTextView;

    public LoadDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        initView();
    }

    public LoadDialog(Context context) {
        super(context, R.style.dialog_half_transparent);
        // TODO Auto-generated constructor stub
        initView();
    }

    public LoadDialog(Context context, String info) {
        super(context, R.style.dialog_half_transparent);
        // TODO Auto-generated constructor stub
        initView();
        infoTextView.setText(info);
    }

    public LoadDialog(Context context, boolean isShow) {
        super(context, R.style.dialog_half_transparent);
        // TODO Auto-generated constructor stub
        initView();
        if (!isShow) {
            infoTextView.setVisibility(View.GONE);
        }
    }

    public LoadDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        initView();
    }

    public void setInfo(String info) {
        infoTextView.setText(info);
    }

    public void setShowAble(boolean is) {
        if (is) {
            infoTextView.setVisibility(View.VISIBLE);
        } else {
            infoTextView.setVisibility(View.GONE);
        }
    }

    private void initView() {
        setContentView(R.layout.widget_load);
        infoTextView = (TextView) findViewById(R.id.tv_load_info);

    }

}
