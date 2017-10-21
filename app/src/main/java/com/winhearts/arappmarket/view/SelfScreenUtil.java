package com.winhearts.arappmarket.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.ActActivity;
import com.winhearts.arappmarket.activity.AppCategoryActivity;
import com.winhearts.arappmarket.activity.AppDetailActivity;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.activity.RankListActivity;
import com.winhearts.arappmarket.activity.TopicActivity;
import com.winhearts.arappmarket.activity.TopicListActivity;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.ActivityInfo;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.Element;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.RankListInfo;
import com.winhearts.arappmarket.model.ResInfoEntity;
import com.winhearts.arappmarket.model.Screen;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.SoftwareResInfo;
import com.winhearts.arappmarket.model.SoftwareType;
import com.winhearts.arappmarket.model.SoftwareTypesResInfo;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.model.TopicListInfo;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.ReflectUtil;
import com.winhearts.arappmarket.utils.SoftwareUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.utils.cust.ThirdAppOpenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义界面工具类，用于加载
 */
public class SelfScreenUtil {
    private static boolean DEBUG = false;
    private static String TAG = "SelfScreenUtil";

    public static void initScreenByElements(final Context mContext,
                                            final List<Screen> screens, int screenWidth, int screenHeight, HorizontalLayout layout,
                                            final MenuItem mCurrentMenu,
                                            final String layoutId) {
        if (screens == null || screens.isEmpty()) {
            return;
        }

        int rankNumber = 0;
        int countScreen = 0;
        for (Screen screen : screens) {
            countScreen++;
            ArrayList<Element> elements = screen.getElementResInfoList();

            if (elements != null) {
                for (int i = 0; i < elements.size(); i++) {
                    final Element element = elements.get(i);
                    int left = element.getpLeft();
                    int top = element.getpTop();
                    int height = element.getRowSpan();
                    int width = element.getColSpan();
                    final int bottom = top + height;
                    int right = left + width;
                    String imageUrl = null;
                    int realWidth = width * (screenWidth / screen.getCol());
                    int realHeight = height * (screenHeight / screen.getRow());
                    View.OnFocusChangeListener oldOnFocusChangeListener;
                    View view;
                    String resInfo = element.getResInfo();
                    if (element.getResType().equals("RANK_TYPE") && resInfo != null) {
                        SoftwareType softwareType = new Gson().fromJson(resInfo, SoftwareType.class);
                        view = new RankElementsView(mContext, countScreen).bindData(softwareType, Constant.rankTitle[rankNumber % 4], Constant.rankTitleDown[rankNumber % 4]);
                        rankNumber++;
                        ((RankElementsView) view).setHorizontalLayout(layout);
                        ((RankElementsView) view).setTitleName(element.getResName());
                        ((RankElementsView) view).setElementId(element.getElementId());
                        ((RankElementsView) view).setUploadDownPath(layoutId, mCurrentMenu);
                    } else if (element.getResType().equals("SOFTWARE") && resInfo != null) {
                        SoftwareResInfo softwareResInfo = new Gson().fromJson(resInfo, SoftwareResInfo.class);
                        String specialRecom = softwareResInfo.getSpecialRecom();
                        String loop = softwareResInfo.getLoop();
                        List<SoftwareInfo> softwareInfos = softwareResInfo.getSoftwareInfos();
                        //根据需求加载不同布局，减少加载复杂布局
                        if (!ContainerUtil.isEmptyOrNull(softwareInfos) && specialRecom != null && specialRecom.equals("1")) {
                            if (!TextUtils.isEmpty(loop) && loop.equals("1") && softwareInfos.size() > 1) {
                                view = new MainSelfSoftwareSpecialView(mContext).bindData(element, realWidth, realHeight);
                            } else {
                                SoftwareInfo softwareInfo = SoftwareUtil.getShowSoftware(mContext, softwareInfos);
                                if (softwareInfo != null) {
                                    imageUrl = softwareInfo.getIcon();
                                }
                                view = new RecommendCardView(
                                        mContext).bindData(imageUrl, realWidth, realHeight);
                                view.setTag(R.integer.tag_record_software, softwareInfo);
                            }

                        } else {
                            view = new MainSelfSoftwareView(mContext, height).bindData(element);
                        }
                    } else {
                        imageUrl = element.getResIcon();
                        view = new RecommendCardView(
                                mContext).bindData(imageUrl, realWidth, realHeight);
                    }
                    view.setTag(element);
                    layout.addItemView(view, 0, left - 1, top - 1, right - 1, bottom - 1, HorizontalLayout.DIVIDE_SIZE, HorizontalLayout.DIVIDE_SIZE, countScreen, screen.getCol(), screen.getRow());

                    oldOnFocusChangeListener = view.getOnFocusChangeListener();
                    view.setOnFocusChangeListener(new NewOnFocusChangeListener(oldOnFocusChangeListener, mContext, mCurrentMenu, element));

                    view.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Element element = (Element) view.getTag();
                            if (element == null) {
                                return;
                            }
                            String resInfo = element.getResInfo();
                            //上报促成下载时用
                            DownloadPath downloadPath = new DownloadPath();
                            downloadPath.setLayoutId(layoutId);
                            if (mCurrentMenu.getParentMenuId() != null) {
                                downloadPath.setMenuId(mCurrentMenu.getParentMenuId());
                                downloadPath.setSubMenuId(mCurrentMenu.getMenuId());
                            } else {
                                downloadPath.setMenuId(mCurrentMenu.getMenuId());
                            }
                            downloadPath.setModuleId(String.valueOf(element.getElementId()));
                            //跳到普通软件
                            if (element.getResType().equals("SOFTWARE") && resInfo != null) {
                                SoftwareResInfo softwareResInfo = new Gson().fromJson(resInfo, SoftwareResInfo.class);
                                List<SoftwareInfo> softwareInfos = softwareResInfo.getSoftwareInfos();
                                String specialRecom = softwareResInfo.getSpecialRecom();
                                String loop = softwareResInfo.getLoop();
                                String packageName;
                                SoftwareInfo softwareInfo;
                                if (softwareInfos != null && specialRecom != null && loop != null) {
                                    Intent intent = new Intent(mContext,
                                            AppDetailActivity.class);
                                    if (specialRecom.equals("1") && loop.equals("1") && softwareInfos.size() > 1) {
                                        ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.vf_self_software_icon);
                                        int i = viewFlipper.getDisplayedChild();
                                        softwareInfo = softwareInfos.get(i);
                                    } else if (specialRecom.equals("1")) {
                                        softwareInfo = (SoftwareInfo) view.getTag(R.integer.tag_record_software);
                                    } else {
                                        softwareInfo = (SoftwareInfo) view.getTag(R.integer.tag_record_software);
                                    }
                                    if (softwareInfo != null) {
                                        packageName = softwareInfo.getPackageName();
                                    } else {
                                        ToastUtils.show(mContext, "软件不存在");
                                        return;
                                    }
                                    intent.putExtra("packageName", packageName);
                                    downloadPath.setModulePath(packageName);
                                    intent.putExtra("downloadPath", downloadPath);
                                    mContext.startActivity(intent);
                                } else {
                                    ToastUtils.show(mContext, "软件不存在");
                                }
                            }
                            //跳到专题
                            else if (element.getResType().equals("TOPIC") && resInfo != null) {
                                Topic topic = new Gson().fromJson(resInfo, Topic.class);
                                if (topic != null) {
                                    Intent intent = new Intent(mContext,
                                            TopicActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("topic", topic);
                                    intent.putExtras(bundle);
                                    intent.putExtra("downloadPath", downloadPath);
                                    LogDebugUtil.d(DEBUG, TAG, "---------TopicActivity------------");
                                    mContext.startActivity(intent);
                                } else {
                                    ToastUtils.show(mContext, "专题不存在");
                                }
                            }
                            //跳到软件分类
                            else if (element.getResType().equals("SOFTWARE_TYPE") && resInfo != null) {
                                SoftwareTypesResInfo softwareTypesResInfo = new Gson().fromJson(resInfo, SoftwareTypesResInfo.class);
                                ArrayList<SoftwareType> softwareTypes = softwareTypesResInfo.getSoftwareTypes();
                                if (softwareTypes != null && softwareTypes.size() > 0) {
                                    LogDebugUtil.d("softwareTypes", softwareTypes.toString());
                                    Intent intent = new Intent(mContext,
                                            AppCategoryActivity.class);
                                    Bundle bundle = new Bundle();
                                    if (softwareTypes.size() > 1) {
                                        intent.putExtra("titleName", element.getResName());
                                    }
                                    bundle.putSerializable("softwareTypes", softwareTypes);
                                    intent.putExtra("orderType", softwareTypesResInfo.getOrderType());
                                    intent.putExtra("defaultIndex", softwareTypesResInfo.getDefaultIndex());
                                    intent.putExtras(bundle);
                                    intent.putExtra("downloadPath", downloadPath);
                                    mContext.startActivity(intent);
                                } else {
                                    ToastUtils.show(mContext, "软件类型不存在");
                                }
                            }
                            //跳到专题列表
                            else if (element.getResType().equals("TOPIC_LIST") && resInfo != null) {

                                TopicListInfo topicListInfo = new Gson().fromJson(resInfo, TopicListInfo.class);
                                Intent intent = new Intent(mContext, TopicListActivity.class);
                                intent.putExtra("titleName", element.getResName());
                                intent.putExtra("topicListInfo", topicListInfo);
                                intent.putExtra("downloadPath", downloadPath);
                                mContext.startActivity(intent);

                            }
                            //跳到排行榜列表
                            else if (element.getResType().equals("RANK_LIST") && resInfo != null) {
                                RankListInfo rankListInfo = new Gson().fromJson(resInfo, RankListInfo.class);
                                Intent intent = new Intent(mContext, RankListActivity.class);
                                intent.putExtra("titleName", element.getResName());
                                intent.putExtra("rankListInfo", rankListInfo);
                                intent.putExtra("downloadPath", downloadPath);
                                mContext.startActivity(intent);
                            } else if (element.getResType().equals("ACTION")) {
                                //执行父activity定义的某个方法。。。。
                                ReflectUtil.execute(mContext, element.getResName());
                            } else if (element.getResType().equals("ACTIVITY_TYPE") && resInfo != null) {
                                ActivityInfo activityInfo = new Gson().fromJson(resInfo, ActivityInfo.class);
                                if (activityInfo != null && !TextUtils.isEmpty(activityInfo.getUrl())) {
                                    Intent intent = new Intent(mContext, ActActivity.class);
                                    intent.putExtra("actInfo", activityInfo);
                                    mContext.startActivity(intent);
                                } else {
                                    LogDebugUtil.e(TAG, "activityInfo为null 或 活动地址为空");
                                }
                            } else if (element.getResType().equals("APP") && resInfo != null) {
                                //第三方app 跳转
                                LogDebugUtil.d(TAG, "resInfo = " + resInfo);
                                if (!TextUtils.isEmpty(resInfo)) {
                                    ResInfoEntity resInfoEntity = new Gson().fromJson(resInfo, ResInfoEntity.class);
                                    if (resInfoEntity != null) {
                                        List<ResInfoEntity.AppsBean> apps = resInfoEntity.getApps();
                                        if (!ContainerUtil.isEmptyOrNull(apps)) {
                                            if (apps.size() == 1) {
                                                ResInfoEntity.AppsBean bean = apps.get(0);
                                                if (!TextUtils.isEmpty(bean.getPackageName())) {
                                                    if (ManagerUtil.isPackageAppExist(mContext, bean.getPackageName())) {
                                                        if (!ThirdAppOpenUtil.openApp(mContext, bean)) {
                                                            ToastUtils.show(mContext, "" + bean.getName() + " 打开失败");
                                                        }

                                                        return;

                                                    }
                                                }

                                            }

                                            PackageAppOpenDialog dialog = new PackageAppOpenDialog(mContext);
                                            dialog.setContent(resInfo);
                                            dialog.setDownloadBundle(downloadPath);
                                            dialog.show();

                                        }
                                    }


                                }


                            }
                        }
                    });
                }
            }
        }

    }

    //当是软件列表和专题时对焦点进行监听
    private static class NewOnFocusChangeListener implements View.OnFocusChangeListener {
        View.OnFocusChangeListener oldOnFocusChangeListener;
        Context context;
        MenuItem mCurrentMenu;
        Element element;

        NewOnFocusChangeListener(View.OnFocusChangeListener oldOnFocusChangeListener, Context context, MenuItem mCurrentMenu, Element element) {
            this.oldOnFocusChangeListener = oldOnFocusChangeListener;
            this.context = context;
            this.mCurrentMenu = mCurrentMenu;
            this.element = element;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if (!element.getResType().equals("RANK_TYPE")) {
                oldOnFocusChangeListener.onFocusChange(view, b);
            }
            if (view.getClass() == MainSelfSoftwareView.class) {
                MarqueeTextView marqueeTextView = (MarqueeTextView) view.findViewById(R.id.tv_app_card_name);
                if (marqueeTextView != null) {
                    marqueeTextView.setMarquee(b);
                }
            }

            setIsRecord(context, mCurrentMenu, b);
        }

        private void setIsRecord(Context context, MenuItem mCurrentMenu, boolean b) {
            if (context instanceof MainActivity && b) {
                if (mCurrentMenu.getParentMenuId() != null) {
                    if (TextUtils.isEmpty(MainActivity.oldMenuId) || !MainActivity.oldMenuId.equals(mCurrentMenu.getParentMenuId())) {
                        MainActivity.oldMenuId = mCurrentMenu.getParentMenuId();
                    }
                    if (TextUtils.isEmpty(MainActivity.oldSubMenuId) || !MainActivity.oldSubMenuId.equals(mCurrentMenu.getMenuId())) {
                        MainActivity.oldSubMenuId = mCurrentMenu.getMenuId();
                    }
                } else if (mCurrentMenu.getMenuId() != null && (TextUtils.isEmpty(MainActivity.oldMenuId) || !MainActivity.oldMenuId.equals(mCurrentMenu.getMenuId()))) {
                    MainActivity.oldMenuId = mCurrentMenu.getMenuId();
                }
            }
        }
    }


}


