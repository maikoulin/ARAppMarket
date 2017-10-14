package com.winhearts.arappmarket.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.AppCategoryActivity;
import com.winhearts.arappmarket.activity.AppDetailActivity;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.RankElements;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.SoftwareType;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.SoftwaresByMultiTypeEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排行榜
 * Created by lmh on 2016/1/19.
 */
public class RankElementsView extends FrameLayout implements View.OnFocusChangeListener, View.OnClickListener {


    private static final String TAG = "RankElementsView";
    private Context context;
    private int page = 0;
    private MarqueeTextView tvFirstPlace, tvSecondPlace, tvThirdPlace, tvFourPlace, tvFivePlace, tvMore;
    private SimpleDraweeView sdFirstPlace, sdSecondPlace, sdThirdPlace, sdFourPlace, sdFivePlace;
    private RankElementsItemView llFirstPlace;
    private RankElementsItemView llMore;
    private RankElementsItemView llSecondPlace;
    private RankElementsItemView llThirdPlace;
    private RankElementsItemView llFourPlace;
    private RankElementsItemView llFivePlace;
    private View llfirstContent;
    private HorizontalLayout horizontalLayout;
    private TextView tvTitleName;
    private String layoutId;
    private MenuItem mCurrentMenu;
    private String elementID;
    private DownloadPath downloadPath;

    public RankElementsView(Context context) {
        super(context);
        this.context = context;
        this.page = 1;
        init(context);
    }

    public RankElementsView(Context context, int page) {
        super(context);
        this.context = context;
        this.page = page;
        init(context);
    }

    public void setUploadDownPath(String layoutId, MenuItem mCurrentMenu) {
        this.layoutId = layoutId;
        this.mCurrentMenu = mCurrentMenu;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VolleyQueueController.getInstance().cancelAll(TAG + context.toString());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setDownloadPath(DownloadPath downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void setTitleName(String resName) {
        if (!TextUtils.isEmpty(resName)) {
            tvTitleName.setText(resName);
        }
    }

    public void setElementId(String elementID) {
        this.elementID = elementID;
    }

    public RankElementsView bindData(SoftwareType softwareType, int titlePath, int titleDownPath) {

        List<RankElements> rankElements = softwareType.getRankElements();
        final List<RankElements> copyRankElements = new ArrayList<RankElements>();
        tvTitleName.setBackgroundResource(titlePath);
        llfirstContent.setBackgroundResource(titleDownPath);
        SoftwaresByMultiTypeEntity softwaresByMultiTypeEntity = new SoftwaresByMultiTypeEntity();
        softwaresByMultiTypeEntity.setStart("1");
        softwaresByMultiTypeEntity.setEnd("5");
        softwaresByMultiTypeEntity.setFirstTypeCodes(softwareType.getFirstTypeCodes());
        softwaresByMultiTypeEntity.setChildTypeCodes(softwareType.getChildTypeCodes());
        softwaresByMultiTypeEntity.setDeviceTypes(softwareType.getDeviceTypes());
        softwaresByMultiTypeEntity.setOrderType(softwareType.getOrderType());
        if (rankElements != null && !rankElements.isEmpty()) {
            Collections.sort(rankElements);
            copyRankElements.addAll(rankElements);
            //移除超出限制限制范围的插入数据
            List<RankElements> removes = new ArrayList<RankElements>();
            List<String> excludePackages = new ArrayList<String>();
            for (RankElements rankElement : copyRankElements) {
                excludePackages.add(rankElement.getSoftwareInfo().getPackageName());
                if (Integer.valueOf(rankElement.getIndex()) > 5) {
                    removes.add(rankElement);
                }
            }
            copyRankElements.removeAll(removes);

            softwaresByMultiTypeEntity.setExcludePackages(excludePackages);
        }
        llMore.setTag(softwareType);
        if (!ContainerUtil.isEmptyOrNull(copyRankElements) && copyRankElements.size() >= 5) {
            List<SoftwareInfo> softwareInfos = new ArrayList<SoftwareInfo>();
            for (int i = 0; i < copyRankElements.size(); i++) {
                softwareInfos.add(copyRankElements.get(i).getSoftwareInfo());
            }
            setViewMessage(softwareInfos);
            return this;
        }
        ModeLevelAmsMenu.querySoftwaresByMultiType(context, TAG + context.toString(), softwaresByMultiTypeEntity, new ModeUserErrorCode<Softwares>() {
            @Override
            public void onJsonSuccess(Softwares softwares) {
                if (softwares != null) {
                    List<SoftwareInfo> softwareInfos = softwares.getSoftwares();
                    if (!ContainerUtil.isEmptyOrNull(softwareInfos) && !ContainerUtil.isEmptyOrNull(copyRankElements)) {
                        for (int i = 0; i < copyRankElements.size(); i++) {
                            int index = Integer.parseInt(copyRankElements.get(i).getIndex());
                            softwareInfos.add(index - 1, copyRankElements.get(i).getSoftwareInfo());
                        }
                    } else {
                        LogDebugUtil.d(TAG, ContainerUtil.isEmptyOrNull(softwareInfos) ? "softwareInfos==null" : "copyRankElements==null");
                    }
                    setViewMessage(softwareInfos);
                } else {
                    LogDebugUtil.d(TAG, "softwares==null");
                }
            }

            @Override
            public void onRequestFail(int errorCode, Throwable e) {
                if (!ContainerUtil.isEmptyOrNull(copyRankElements)) {
                    List<SoftwareInfo> softwareInfos = new ArrayList<SoftwareInfo>();
                    for (int i = 0; i < copyRankElements.size(); i++) {
                        softwareInfos.add(copyRankElements.get(i).getSoftwareInfo());
                    }
                    setViewMessage(softwareInfos);
                }
            }
        });


        return this;
    }

    private void setViewMessage(List<SoftwareInfo> SoftwareInfos) {
        if (!ContainerUtil.isEmptyOrNull(SoftwareInfos)) {
            int size = SoftwareInfos.size();
            if (size > 0) {
                llFirstPlace.setTag(SoftwareInfos.get(0));
                tvFirstPlace.setText(SoftwareInfos.get(0).getName());
                CommonHierarchy.showAppIcon(getContext(), Uri.parse(SoftwareInfos.get(0).getIcon()), sdFirstPlace);
//                sdFirstPlace.setImageURI(Uri.parse(SoftwareInfos.get(0).getIcon()));
            }
            if (size > 1) {
                llSecondPlace.setTag(SoftwareInfos.get(1));
                tvSecondPlace.setText(SoftwareInfos.get(1).getName());
                CommonHierarchy.showAppIcon(getContext(), Uri.parse(SoftwareInfos.get(1).getIcon()), sdSecondPlace);
//                sdSecondPlace.setImageURI(Uri.parse(SoftwareInfos.get(1).getIcon()));
            }
            if (size > 2) {
                llThirdPlace.setTag(SoftwareInfos.get(2));
                tvThirdPlace.setText(SoftwareInfos.get(2).getName());
                CommonHierarchy.showAppIcon(getContext(), Uri.parse(SoftwareInfos.get(2).getIcon()), sdThirdPlace);

//                sdThirdPlace.setImageURI(Uri.parse(SoftwareInfos.get(2).getIcon()));

            }
            if (size > 3) {
                llFourPlace.setTag(SoftwareInfos.get(3));
                tvFourPlace.setText(SoftwareInfos.get(3).getName());
                CommonHierarchy.showAppIcon(getContext(), Uri.parse(SoftwareInfos.get(3).getIcon()), sdFourPlace);
//                sdFourPlace.setImageURI(Uri.parse(SoftwareInfos.get(3).getIcon()));

            }
            if (size > 4) {
                llFivePlace.setTag(SoftwareInfos.get(4));
                tvFivePlace.setText(SoftwareInfos.get(4).getName());
                CommonHierarchy.showAppIcon(getContext(), Uri.parse(SoftwareInfos.get(4).getIcon()), sdFivePlace);
//                sdFivePlace.setImageURI(Uri.parse(SoftwareInfos.get(4).getIcon()));
            }
        }
    }

    private void init(final Context context) {
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.setFocusable(false);
        this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_rank_elements, this);

        tvTitleName = (TextView) view.findViewById(R.id.tv_rank_title_name);
        llfirstContent = view.findViewById(R.id.ll_rank_first_content);

        tvFirstPlace = (MarqueeTextView) view.findViewById(R.id.tv_rank_first);
        sdFirstPlace = (SimpleDraweeView) view.findViewById(R.id.sd_rank_first);

        tvSecondPlace = (MarqueeTextView) view.findViewById(R.id.tv_rank_second);
        sdSecondPlace = (SimpleDraweeView) view.findViewById(R.id.sd_rank_second);

        tvThirdPlace = (MarqueeTextView) view.findViewById(R.id.tv_rank_third);
        sdThirdPlace = (SimpleDraweeView) view.findViewById(R.id.sd_rank_third);

        tvFourPlace = (MarqueeTextView) view.findViewById(R.id.tv_rank_four);
        sdFourPlace = (SimpleDraweeView) view.findViewById(R.id.sd_rank_four);

        tvFivePlace = (MarqueeTextView) view.findViewById(R.id.tv_rank_five);
        sdFivePlace = (SimpleDraweeView) view.findViewById(R.id.sd_rank_five);

        tvMore = (MarqueeTextView) view.findViewById(R.id.tv_rank_more);

        llFirstPlace = (RankElementsItemView) view.findViewById(R.id.ll_rank_first);
        llFirstPlace.setTag(R.integer.tag_record_page, page - 1);
        llSecondPlace = (RankElementsItemView) view.findViewById(R.id.ll_rank_second);
        llSecondPlace.setTag(R.integer.tag_record_page, page - 1);
        llThirdPlace = (RankElementsItemView) view.findViewById(R.id.ll_rank_third);
        llThirdPlace.setTag(R.integer.tag_record_page, page - 1);
        llFourPlace = (RankElementsItemView) view.findViewById(R.id.ll_rank_four);
        llFourPlace.setTag(R.integer.tag_record_page, page - 1);
        llFivePlace = (RankElementsItemView) view.findViewById(R.id.ll_rank_five);
        llFivePlace.setTag(R.integer.tag_record_page, page - 1);
        llMore = (RankElementsItemView) view.findViewById(R.id.ll_rank_more);
        llMore.setTag(R.integer.tag_record_page, page - 1);
        llFirstPlace.setOnFocusChangeListener(this);
        llSecondPlace.setOnFocusChangeListener(this);
        llThirdPlace.setOnFocusChangeListener(this);
        llFourPlace.setOnFocusChangeListener(this);
        llFivePlace.setOnFocusChangeListener(this);
        llMore.setOnFocusChangeListener(this);

        llFirstPlace.setOnClickListener(this);
        llFivePlace.setOnClickListener(this);
        llSecondPlace.setOnClickListener(this);
        llThirdPlace.setOnClickListener(this);
        llFourPlace.setOnClickListener(this);
        llMore.setOnClickListener(this);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mCurrentMenu != null && mCurrentMenu.getMenuId() != null && (TextUtils.isEmpty(MainActivity.oldMenuId) || !MainActivity.oldMenuId.equals(mCurrentMenu.getMenuId()))) {
            MainActivity.oldMenuId = mCurrentMenu.getMenuId();
        }

        if (horizontalLayout != null) {
            horizontalLayout.onFocusChange(v, hasFocus);
        }
        if (v == llFirstPlace) {
            tvFirstPlace.setMarquee(hasFocus);
        }
        if (v == llSecondPlace) {
            tvSecondPlace.setMarquee(hasFocus);
        }
        if (v == llThirdPlace) {
            tvThirdPlace.setMarquee(hasFocus);
        }
        if (v == llFourPlace) {
            tvFourPlace.setMarquee(hasFocus);
        }
        if (v == llFivePlace) {
            tvFivePlace.setMarquee(hasFocus);
        }
    }

    public void focusMoveToTop() {
        if (llFirstPlace != null) {
            llFirstPlace.requestFocus();
        }
    }

    public void focusMoveToDown() {
        if (llMore != null) {
            llMore.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        if (downloadPath == null) {
            downloadPath = new DownloadPath();
            if (!TextUtils.isEmpty(layoutId) && mCurrentMenu != null) {
                downloadPath.setLayoutId(layoutId);
                if (mCurrentMenu.getParentMenuId() != null) {
                    downloadPath.setMenuId(mCurrentMenu.getParentMenuId());
                    downloadPath.setSubMenuId(mCurrentMenu.getMenuId());
                } else {
                    downloadPath.setMenuId(mCurrentMenu.getMenuId());
                }
                downloadPath.setModuleId(elementID);
            }
        }
        if (v.getId() == R.id.ll_rank_more) {
            SoftwareType softwareType = (SoftwareType) llMore.getTag();
            if (softwareType != null) {
                Intent intent = new Intent(context,
                        AppCategoryActivity.class);
                ArrayList<SoftwareType> softwareTypes = new ArrayList<SoftwareType>();
                softwareTypes.add(softwareType);
                if (mCurrentMenu == null) {
                    downloadPath.setModulePath(softwareType.getId());
                } else {
                    downloadPath.setModulePath("more");
                }
                intent.putExtra("downloadPath", downloadPath);
                intent.putExtra("orderType", softwareType.getOrderType());
                intent.putExtra("softwareTypes", softwareTypes);
                intent.putExtra("isRank", true);
                context.startActivity(intent);
            } else {
                ToastUtils.show(context, "软件类型不存在");
            }

        } else {
            SoftwareInfo softWareInfo = (SoftwareInfo) v.getTag();
            if (softWareInfo != null) {
                String packageName = softWareInfo
                        .getPackageName();
                if (packageName != null) {
                    Intent intent = new Intent(context,
                            AppDetailActivity.class);
                    SoftwareType softwareType = (SoftwareType) llMore.getTag();
                    if (mCurrentMenu == null && softwareType != null) {
                        downloadPath.setModulePath(softwareType.getId());
                    } else {
                        downloadPath.setModulePath(packageName);
                    }
                    intent.putExtra("downloadPath", downloadPath);
                    intent.putExtra("packageName", packageName);
                    context.startActivity(intent);
                }
            } else {
                ToastUtils.show(context, "软件不存在");
            }
        }

    }

    public HorizontalLayout getHorizontalLayout() {
        return horizontalLayout;
    }

    public void setHorizontalLayout(HorizontalLayout horizontalLayout) {
        this.horizontalLayout = horizontalLayout;
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {

        if (llFirstPlace != null) {
            llFirstPlace.requestFocus();
            return true;
        }

        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }
}
