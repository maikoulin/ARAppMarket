package com.winhearts.arappmarket.view;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;

/**
 * 设置界面子项
 * Created by lmh on 2016/1/27.
 */
public class SettingItemView extends RelativeLayout {
    private SimpleDraweeView sdIcon, sdBg;
    private TextView tvMassage, tvCredits;

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_setting_item, this);
        sdIcon = (SimpleDraweeView) view.findViewById(R.id.sd_setting_item_icon);
        sdBg = (SimpleDraweeView) view.findViewById(R.id.sd_setting_item_bg);
        RoundingParams roundingParams = sdBg.getHierarchy().getRoundingParams();
        if (roundingParams == null) {
            roundingParams = new RoundingParams();
        }
        roundingParams.setCornersRadius(6);
        sdBg.getHierarchy().setRoundingParams(roundingParams);
        tvMassage = (TextView) view.findViewById(R.id.tv_setting_item_message);
        tvCredits = (TextView) view.findViewById(R.id.tv_setting_item_credits);

    }

    public SettingItemView bindData(int backId, int iconPath, int message) {
        sdBg.setImageURI(Uri.parse("res://drawable/" + backId));
        sdIcon.setImageURI(Uri.parse("res://drawable/" + iconPath));
        tvMassage.setText(getResources().getString(message));
        return this;
    }

    public SettingItemView bindData(int backId) {
        sdBg.setImageURI(Uri.parse("res://drawable/" + backId));
        sdIcon.setVisibility(View.GONE);
        tvMassage.setVisibility(View.GONE);
        return this;
    }

    public void setCredits(Spannable numberSpannable) {
        tvCredits.setText(numberSpannable);
    }

    public void setNickname(String nickname) {
        tvMassage.setText(nickname);
    }

}
