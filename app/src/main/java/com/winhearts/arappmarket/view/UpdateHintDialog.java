package com.winhearts.arappmarket.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;


/**
 * 更新相关的Dialog提示
 *
 * @author huyf
 */
public class UpdateHintDialog extends Dialog {

    public static final int Type_Can_Update = 1;// 可以升级
    public static final int Type_Lastest_Version = 2;// 最新版本
    public static final int Type_Update_Loading = 3;// 升级中

    private final int mType;// 当前类型
    private final String mInfo;// 信息hint

    private final LayoutParams lp;

    private TextView tvHint, tvHead;
    private Button btnUpdate, btnCancel, btnConfirm;
    private LinearLayout llOperation;
    private RelativeLayout rlConfirm;
    private ProgressBar pbUpdate;

    public UpdateHintDialog(Context context, int type, String info, boolean focus) {
        super(context, R.style.dialog);
        // 初始
        this.mType = type;
        this.mInfo = info;

        setContentView(R.layout.dialog_update_hint);
        // 初始UI
        initView(focus);

        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    public UpdateHintDialog(Context context, int type, String info) {
        super(context, R.style.dialog);
        // 初始
        this.mType = type;
        this.mInfo = info;

        setContentView(R.layout.dialog_update_hint);
        // 初始UI
        initView(false);

        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

    }

    /**
     * 根据type初始化
     */
    private void initView(boolean focus) {
        // 获得对应控件
        tvHead = (TextView) findViewById(R.id.tv_head);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        tvHint.setText(mInfo);
        // 升级、取消
        llOperation = (LinearLayout) findViewById(R.id.ll_update_operation);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        // 确定和立即升级
        rlConfirm = (RelativeLayout) findViewById(R.id.rl_confirm);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        // 进度框
        pbUpdate = (ProgressBar) findViewById(R.id.pb_update);

        switch (mType) {
            case Type_Can_Update:// 可以更新
                if (focus) {
                    llOperation.setVisibility(View.GONE);
                    rlConfirm.setVisibility(View.VISIBLE);
                    btnConfirm.setText("立即升级");
                    pbUpdate.setVisibility(View.GONE);
                } else {
                    llOperation.setVisibility(View.VISIBLE);
                    rlConfirm.setVisibility(View.GONE);
                    pbUpdate.setVisibility(View.GONE);
                }

                break;
            case Type_Lastest_Version:// 最新版本
                tvHead.setVisibility(View.GONE);
                llOperation.setVisibility(View.GONE);
                rlConfirm.setVisibility(View.VISIBLE);
                pbUpdate.setVisibility(View.GONE);
                break;
            case Type_Update_Loading:// 正在更新
                tvHead.setVisibility(View.GONE);
                llOperation.setVisibility(View.GONE);
                rlConfirm.setVisibility(View.GONE);
                pbUpdate.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
        // 取消和确定点击后对话框消失
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UpdateHintDialog.this.dismiss();
            }
        };
        btnCancel.setOnClickListener(onClickListener);
        btnConfirm.setOnClickListener(onClickListener);
    }

    /**
     * 设置PB当前进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        pbUpdate.setProgress(progress);
    }

    // 升级按钮事件
    public void setUpdateButtonClick(View.OnClickListener onClickListener) {
        btnUpdate.setOnClickListener(onClickListener);
    }

    // 确定按钮事件
    public void setConfirmButtonClick(View.OnClickListener onClickListener) {
        btnConfirm.setOnClickListener(onClickListener);
    }

    // 取消按钮事件
    public void setCancelButtonClick(View.OnClickListener onClickListener) {
        btnCancel.setOnClickListener(onClickListener);
    }


}
