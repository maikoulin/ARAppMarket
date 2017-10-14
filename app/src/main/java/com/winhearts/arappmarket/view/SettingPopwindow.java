package com.winhearts.arappmarket.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.AppUpdate;
import com.winhearts.arappmarket.modellevel.ModeLevelVms;
import com.winhearts.arappmarket.modellevel.ModeUser;
import com.winhearts.arappmarket.utils.AppUpdateUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.text.DecimalFormat;

/**
 *  设置界面的popup
 */
public class SettingPopwindow implements OnClickListener {
    public static final int DOWNLAOD_SUCCESS = 5;
    public static final int DOWNLAOD_FAIL = 6;
    public static final String PHONE_HEPLPER = "phoneHelper";
    public static final String UPDATE_WINDOW = "updateWindow";
    private String flag;
    private PopupWindow mPopupWindow;
    private TextView nameTextView;
    private TextView flockTextview;
    private TextView statusTextView;
    private Button updateBtn;
    private TextView contentTextView;
    private ImageView ivIcom;
    private TextView codeMessageTextView;
    private View mPopView;
    private Context mContext;
    private AppUpdate mAppUpdate;
    private SimpleDraweeView codeImageView;

    public SettingPopwindow(Context context, String flag) {
        // TODO Auto-generated constructor stub
        mContext = context;
        this.flag = flag;
        mPopView = LayoutInflater.from(context).inflate(R.layout.popwindow_setting, null);

        mPopupWindow = new PopupWindow(mPopView, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        initView();

        // 这两句加了，才能点击并且响应
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
        mPopupWindow.setOutsideTouchable(true);// 点击外部按钮 取消。按返回按键也取消
        float density = ScreenUtil.getDensity((Activity) context);
//		Log.w("lee", "density: " + density);

    }

    private void initView() {
        nameTextView = (TextView) mPopView.findViewById(R.id.txt_name);
        statusTextView = (TextView) mPopView.findViewById(R.id.txt_status);
        contentTextView = (TextView) mPopView.findViewById(R.id.txt_content);
        updateBtn = (Button) mPopView.findViewById(R.id.btn_update);
        ivIcom = (ImageView) mPopView.findViewById(R.id.iv_setting_pop_icon);
        flockTextview = (TextView) mPopView.findViewById(R.id.tv_pw_qq_flock);
        codeImageView = (SimpleDraweeView) mPopView.findViewById(R.id.iv_attention_code);
        codeMessageTextView = (TextView) mPopView.findViewById(R.id.tv_code_message);
        if (flag.equals(UPDATE_WINDOW)) {
            setUpdateWindow();
        } else {
            setPhoneHelper();
        }
    }

    private void setPhoneHelper() {
        nameTextView.setText("网宿电视助手");
        ivIcom.setImageResource(R.drawable.phone_helper);
        statusTextView.setVisibility(View.INVISIBLE);
        flockTextview.setTextSize(25);
        flockTextview.setText("立刻扫码下载吧!");
        codeMessageTextView.setText("目前仅支持安卓手机");
        contentTextView.setText(mContext.getResources().getString(R.string.phone_helper_desc));
        GenericDraweeHierarchy hierarchy = codeImageView.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.phone_helper_code);
        codeImageView.setHierarchy(hierarchy);
        String url = Pref.getString(Pref.PHONEHELPER_DOWNLOAD_URL, mContext, null);
        if (!TextUtils.isEmpty(url)) {
            codeImageView.setImageURI(Uri.parse(url));
        }
    }

    private void setUpdateWindow() {

        queryAppUpdate(mContext);
        updateBtn.setOnClickListener(this);
        nameTextView.setText(mContext.getResources().getString(R.string.app_name));
        ivIcom.setImageResource(R.drawable.ic_launcher);
        String qqGroup = Pref.getString(Pref.QQ_GROUP, mContext, "415228967");
        try {
            String appVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            String string = "<font color='#ffffff'>" + "版本号：" + "</font>" + "<font color='#ffc900'>" + appVersion + "</font>";
            String flockString = "<font color='#ffffff'>" + "QQ群：" + "</font>" + "<font color='#ffc900'>" + qqGroup + "</font>";
            statusTextView.setText(Html.fromHtml(string));
            flockTextview.setText(Html.fromHtml(flockString));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        statusTextView.setVisibility(View.INVISIBLE);
        contentTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * 获取版本信息网络请求
     */
    public void queryAppUpdate(final Context mContext) {

        ModeLevelVms.appUpdate(mContext, new ModeUser<AppUpdate>() {
            @Override
            public void onJsonSuccess(AppUpdate appUpdate) {
                mAppUpdate = appUpdate;

                if ("1".equals(appUpdate.getResult())) {
                    updateBtn.setVisibility(View.VISIBLE);
                    String sizeString = mAppUpdate.getPackageSize();
                    int size = Integer.valueOf(sizeString.trim());
                    double sizeFormat = size * 1.0 / 1024;
                    String string = "<font color='#ffffff'>" + "版本号：" + "</font>" + "<font color='#ffc900'>" + mAppUpdate.getVersionName()
                            + "</font>" + "&nbsp" + "&nbsp" + "&nbsp" + "&nbsp" + "<font color='#ffffff'>" + "大小：" + "</font>"
                            + "<font color='#ffc900'>" + new DecimalFormat("#.0").format(sizeFormat) + "MB" + "</font>";

                    statusTextView.setText(Html.fromHtml(string));

                    if (!TextUtils.isEmpty(appUpdate.getVersionDesc())) {
                        contentTextView.setText(appUpdate.getVersionDesc());
                    }

                    updateBtn.requestFocus();
                } else {
                    updateBtn.setVisibility(View.INVISIBLE);
                }
                statusTextView.setVisibility(View.VISIBLE);
                contentTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRequestFail(Throwable e) {
                updateBtn.setVisibility(View.INVISIBLE);
                statusTextView.setVisibility(View.VISIBLE);
                contentTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 显示试图
     *
     * @param parentView 父视图
     */
    public void show(View parentView) {

        // 显示视图与指定视图之间的偏差s
        mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);//
        // 指定视图偏移
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLAOD_SUCCESS:
                    ToastUtils.show(mContext, "安装包下载成功");
                    break;
                case DOWNLAOD_FAIL:
                    ToastUtils.show(mContext, "安装包下载失败");
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        mPopupWindow.dismiss();

        if (mAppUpdate != null) {
            if ("1".equals(mAppUpdate.getResult())) {
                if (!TextUtils.isEmpty(mAppUpdate.getUpdateUrl())) {
                    AppUpdateUtil.update(mContext, mAppUpdate.getUpdateUrl(), Util.APK_NAME, mHandler);
                }

            } else {
                String toast = "没有新版本";
                UpdateHintDialog dialog = new UpdateHintDialog(mContext, UpdateHintDialog.Type_Lastest_Version, toast);
                dialog.show();
            }
        } else {
            AppUpdateUtil.appUpdate(mContext, mHandler);
        }
    }

}
