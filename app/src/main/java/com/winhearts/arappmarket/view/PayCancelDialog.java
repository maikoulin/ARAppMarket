package com.winhearts.arappmarket.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.winhearts.arappmarket.R;


/**
 * 支付取消对话框
 */
public class PayCancelDialog extends Dialog {
    private Button cancelButton;
    private Button okButton;
    private View view;
    private TextView titleTextView;

    public PayCancelDialog(Context context) {
        super(context, R.style.dialog_half_transparent);
        // TODO Auto-generated constructor stub

        setCancelable(false);

        view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_cancel, null);
        cancelButton = (Button) view.findViewById(R.id.btn_dialog_cancel);
        okButton = (Button) view.findViewById(R.id.btn_dialog_ok);
        titleTextView = (TextView) view.findViewById(R.id.tv_dialog_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(view);

    }

    public void setHintCancel(boolean isHint) {
        if (!isHint) {
            view.findViewById(R.id.ly_diaolog_pay_cancel).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.ly_diaolog_pay_cancel).setVisibility(View.GONE);
        }

    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    public Button getOkButton() {
        return okButton;
    }

    public void setOkButton(Button okButton) {
        this.okButton = okButton;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }


}
