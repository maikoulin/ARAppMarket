package com.winhearts.arappmarket.view;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.widget.TextView;


import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.SoftwareInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 应用详情更多弹窗
 * Created by lmh on 2017/3/16.
 */

public class AppDetailSynopsisDialog extends BaseDialog {
    private TextView tvPopVersion, tvPopUpdateTime, tvPopDeveloper, tvPopSynopsis, tvPopUpdateDescription;
    private SoftwareInfo softwareInfo;

    public AppDetailSynopsisDialog(Context context) {
        super(context);
    }

    protected AppDetailSynopsisDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected AppDetailSynopsisDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_app_detail_more, null);
        tvPopVersion = (TextView) mRootView.findViewById(R.id.tv_pop_app_detail_version);
        tvPopUpdateTime = (TextView) mRootView.findViewById(R.id.tv_pop_app_detail_update_time);
        tvPopDeveloper = (TextView) mRootView.findViewById(R.id.tv_pop_app_detail_developer);
        tvPopSynopsis = (TextView) mRootView.findViewById(R.id.tv_pop_app_detail_Synopsis);
        tvPopUpdateDescription = (TextView) mRootView.findViewById(R.id.tv_pop_app_detail_update_description);
    }

    public void setData(SoftwareInfo softwareInfo) {
        this.softwareInfo = softwareInfo;
        tvPopVersion.setText(String.format("版本： %s", softwareInfo.getVersionName().intern()));
        long time = Long.valueOf(softwareInfo.getUpdateTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        tvPopUpdateTime.setText(String.format("更新日期： %s", sdf.format(new Date(time))));
        tvPopDeveloper.setText(String.format("开发者： %s", softwareInfo.getDevelopor()));
        tvPopSynopsis.setText(softwareInfo.getDescription());
        tvPopUpdateDescription.setText(softwareInfo.getUpdateDescription());
        setWindowContentView(mRootView, 620, 620);
    }
}
