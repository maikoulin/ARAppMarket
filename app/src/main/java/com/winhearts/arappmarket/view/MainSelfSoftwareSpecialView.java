package com.winhearts.arappmarket.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.DisplayItem;
import com.winhearts.arappmarket.model.Element;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.SoftwareResInfo;

import java.util.List;

/**
 * 主页自定义view,显示特别推荐的软件
 * Created by lmh on 2016/3/11.
 */
public class MainSelfSoftwareSpecialView extends RelativeLayout {
    private ViewFlipper vfIcon;
    private int width;
    private int height;

    public MainSelfSoftwareSpecialView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.setFocusable(false);
        this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_self_software, this);
        vfIcon = (ViewFlipper) view.findViewById(R.id.vf_self_software_icon);
        vfIcon.setFocusable(false);
    }


    public MainSelfSoftwareSpecialView bindData(Element element, int width, int height) {
        this.width = width;
        this.height = height;
        String resInfo = element.getResInfo();
        SoftwareResInfo softwareResInfo = new Gson().fromJson(resInfo, SoftwareResInfo.class);
        List<SoftwareInfo> softwareInfos = softwareResInfo.getSoftwareInfos();
        if (softwareInfos != null) {
            setSpecialMessage(softwareInfos, true);
        }
        return this;
    }

    /**
     * 设置特殊推荐的信息
     */
    private void setSpecialMessage(List<SoftwareInfo> softwareInfos, boolean isLoop) {
        int size = softwareInfos.size();
        for (int i = 0; i < size; i++) {
            DisplayItem item = new DisplayItem();
            item.imageUrl = softwareInfos.get(i).getIcon();
            RecommendCardView recommendCardView = new RecommendCardView(getContext(), Constant.Horizontal, 0, 0).bindData(item, width, height);
            vfIcon.addView(recommendCardView);
        }
        vfIcon.setFlipInterval(10000);
        vfIcon.startFlipping();
    }
}
