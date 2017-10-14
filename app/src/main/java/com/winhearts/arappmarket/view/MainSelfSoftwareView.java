package com.winhearts.arappmarket.view;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.Element;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.SoftwareResInfo;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.SoftwareUtil;

import java.util.List;

/**
 * 主页
 * Created by lmh on 2016/3/4.
 */
public class MainSelfSoftwareView extends RelativeLayout {
    private static final String TAG = "MainSelfSoftwareView";

    private RelativeLayout rlContent;
    private SimpleDraweeView ivApp;
    private SimpleDraweeView ivAppTag;
    private MarqueeTextView tvApp;
    private ScoreView svStar;
    private int height;

    public MainSelfSoftwareView(Context context, int height) {
        super(context);
        this.height = height;
        initView();
    }

    public MainSelfSoftwareView(Context context) {
        super(context);
        initView();
    }

    public MainSelfSoftwareView bindData(Element element) {
        String resInfo = element.getResInfo();
        SoftwareResInfo softwareResInfo = new Gson().fromJson(resInfo, SoftwareResInfo.class);
        List<SoftwareInfo> softwareInfos = softwareResInfo.getSoftwareInfos();
        if (!ContainerUtil.isEmptyOrNull(softwareInfos)) {
            rlContent.setVisibility(VISIBLE);
            SoftwareInfo softwareInfo = SoftwareUtil.getShowSoftware(getContext(), softwareInfos);

            setMessage(softwareInfo);
            this.setTag(R.integer.tag_record_software, softwareInfo);
        } else {
            LogDebugUtil.w(TAG, "ContainerUtil.isEmptyOrNull(softwareInfos)");
        }

        return this;
    }

    public MainSelfSoftwareView bindData(SoftwareInfo softwareInfo) {
        if (softwareInfo != null) {
            rlContent.setVisibility(VISIBLE);

            setMessage(softwareInfo);
            this.setTag(R.integer.tag_record_software, softwareInfo);
        } else {
            LogDebugUtil.w(TAG, "ContainerUtil.isEmptyOrNull(softwareInfos)");
        }

        return this;
    }


    /**
     * 设置普通推荐的信息
     *
     * @param softwareInfo
     */
    private void setMessage(SoftwareInfo softwareInfo) {

        if (softwareInfo.getIcon() != null) {
//            CommonHierarchy.showAppIcon(getContext(), Uri.parse(softwareInfo.getIcon()), ivApp);
            ivApp.setImageURI(Uri.parse(softwareInfo.getIcon()));
        }
        tvApp.setText(softwareInfo.getName());
        String star = softwareInfo.getStar();
        float score;
        if (TextUtils.isEmpty(star)) {
            score = 5;
        } else {
            score = Float.valueOf(star);
        }
        try {
            svStar.setScore(score, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(softwareInfo.getTag())) {
            setImgTag(ivAppTag, softwareInfo.getTag());
        }
    }

    private void setImgTag(SimpleDraweeView ivAppTag, String tag) {
        if (tag.equals("最新")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_newest);
        } else if (tag.equals("最热")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_hottest);
        } else if (tag.equals("活动")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_activity);
        } else if (tag.equals("更新")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_update);
        } else if (tag.equals("首发")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_starting);
        } else if (tag.equals("专题")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_special);
        } else if (tag.equals("推荐")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_recommend);
        }
    }

    private void initView() {
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.setFocusable(false);
        this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_app_card, this);
        rlContent = (RelativeLayout) view.findViewById(R.id.ll_content);
        rlContent.setFocusable(false);
        ivApp = (SimpleDraweeView) view.findViewById(R.id.rv_app_card_icon);
        GenericDraweeHierarchy hierarchy = ivApp.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.background_app_icon);
        hierarchy.setFailureImage(view.getResources().getDrawable(R.drawable.appicon_error));
        ivAppTag = (SimpleDraweeView) view.findViewById(R.id.rv_app_card_tag);
        tvApp = (MarqueeTextView) view.findViewById(R.id.tv_app_card_name);
        if (height <= 3) {
            tvApp.setTextSize(18);
            ViewGroup.LayoutParams layoutParams = ivApp.getLayoutParams();
            layoutParams.height = 80;
            layoutParams.width = 80;
            ivApp.setLayoutParams(layoutParams);
        }
        svStar = (ScoreView) view.findViewById(R.id.sv_app_card_star);
    }
}
