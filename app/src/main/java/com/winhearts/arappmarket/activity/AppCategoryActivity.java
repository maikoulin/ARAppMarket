package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.wrapper.PageAdapterInstantiateListener;
import com.winhearts.arappmarket.view.VerticalViewPager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.utils.adapter.CommonPagerAdapter;
import com.winhearts.arappmarket.utils.adapter.DefaultPullToRefreshAdapter;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.RankElements;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.SoftwareType;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.SoftwaresByMultiTypeEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.ViewUtils;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.MarqueeTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 应用分类
 * Created by lmh on 2016/2/24.
 */
public class AppCategoryActivity extends BaseActivity implements View.OnClickListener, PageAdapterInstantiateListener, HorizontalLayout.OnBorderListener, VerticalViewPager.OnPageChangeListener, View.OnFocusChangeListener {

    private static final String TAG = "AppCategoryActivity";
    private boolean isBackMain = false;
    private int pageNo = 1;
    private TextView tvHottest, tvNewest, tvGrade, tvTitleName;
    private LinearLayout llLeftMenu, llTopMenu;
    private TextView tvPageNumber;
    private ImageView ivMore;
    private TextView tvCurrentLeftItem = null;
    private Runnable mRunnable;
    private Handler mMainThreadHandler;
    private VerticalViewPager mVerticalViewPager;
    private View tvCurrentTopItem = null;
    private DefaultPullToRefreshAdapter mDefaultPullToRefreshAdapter;
    private RelativeLayout rlContent;
    private ArrayList<SoftwareInfo> mApps = new ArrayList<>();
    private HashMap<String, List<SoftwareInfo>> hashMap = new HashMap<>();
    private int PAGE_SIZE;
    private int mScreenWidth;
    private int mScreenHeight;
    private float widthScale;
    private int[][] positionArray;
    private DownloadPath downloadPath;
    private boolean requestFocus = true;
    private boolean isRank = false;
    private String initOrderType;
    private String orderType;
    private String defaultIndex;
    private View view;
    ArrayList<SoftwareType> softwareTypes = null;
    List<RankElements> rankElements = null;
    //线程池
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_app_category, null);
        if (Build.VERSION.SDK_INT <= 15) {
            view.setLayerType(View.LAYER_TYPE_NONE, null);
        }
        setContentView(view);
        CommonHierarchy.setBgImage((SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg));
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        mDefaultPullToRefreshAdapter = new DefaultPullToRefreshAdapter(this, mApps);
        mDefaultPullToRefreshAdapter.setIsShowDownload(true);
        mScreenWidth = ScreenUtil.getScreenWidth(this);
        mScreenHeight = ScreenUtil.getScreenHeight(this);
        initView();
        setMenu();
        setAdapter();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        addLoadAndErrorView((ViewGroup) findViewById(R.id.rl_app_category_content_wrap));
        tvTitleName = (TextView) this.findViewById(R.id.tv_app_category_one_title);
        tvHottest = (TextView) this.findViewById(R.id.tv_app_category_hottest);
        tvHottest.setOnClickListener(this);
        tvHottest.setOnFocusChangeListener(this);
        tvNewest = (TextView) this.findViewById(R.id.tv_app_category_newest);
        tvNewest.setOnClickListener(this);
        tvNewest.setOnFocusChangeListener(this);
        tvGrade = (TextView) this.findViewById(R.id.tv_app_category_grade);
        tvGrade.setOnClickListener(this);
        tvGrade.setOnFocusChangeListener(this);
        llLeftMenu = (LinearLayout) this.findViewById(R.id.ll_app_category_left_menu);
        llTopMenu = (LinearLayout) this.findViewById(R.id.ll_app_category_top_menu);
        tvPageNumber = (TextView) this.findViewById(R.id.tv_app_category_page_number);
        mVerticalViewPager = (VerticalViewPager) this.findViewById(R.id.vp_app_category_content);
        mVerticalViewPager.setOnPageChangeListener(this);
        mVerticalViewPager.setOffscreenPageLimit(1);
        mVerticalViewPager.setFocusable(false);
        mVerticalViewPager.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        mVerticalViewPager.requestDisallowInterceptTouchEvent(true);
        ivMore = (ImageView) this.findViewById(R.id.iv_app_category_more);
        rlContent = (RelativeLayout) this.findViewById(R.id.rl_app_category_content);

    }

    /**
     * 获取菜单加载数据
     */
    private void setMenu() {
        Intent intent = this.getIntent();
        downloadPath = (DownloadPath) intent.getSerializableExtra("downloadPath");
        orderType = intent.getStringExtra("orderType");
        defaultIndex = intent.getStringExtra("defaultIndex");
        initOrderType = orderType;
        isBackMain = intent.getBooleanExtra("isBackMain", false);
        String titleName = intent.getStringExtra("titleName");
        isRank = intent.getBooleanExtra("isRank", false);
        if (isRank) {
            llTopMenu.setVisibility(View.INVISIBLE);
            tvHottest.setFocusable(false);
            tvGrade.setFocusable(false);
            tvNewest.setFocusable(false);
        }
        softwareTypes = (ArrayList<SoftwareType>) intent.getSerializableExtra("softwareTypes");
        if (softwareTypes != null) {
            if (TextUtils.isEmpty(titleName)) {
                llLeftMenu.setVisibility(View.GONE);
                SoftwareType softwareType = softwareTypes.get(0);
                if (softwareType != null) {
                    tvTitleName.setText(softwareType.getName());
                    tvTitleName.setTag(softwareType);
                }
            } else {
                llLeftMenu.setVisibility(View.VISIBLE);
                tvTitleName.setText(titleName);
            }
            if (!isRank) {
                setTopMenuState(orderType);
            }

            if (softwareTypes.size() > 1) {
                int size = softwareTypes.size();

                for (int i = 0; i < size; i++) {
                    TextView tvLestItem = createLeftMenuItem(softwareTypes.get(i));
                    llLeftMenu.addView(tvLestItem);
                }
            }
        }
    }

    /**
     * 设置加载内容
     */
    private void setAdapter() {
        showLoading();
        rlContent.setVisibility(View.INVISIBLE);
        if (softwareTypes != null) {
            if (softwareTypes.size() > 1) {
                requestFocus = true;
                PAGE_SIZE = Constant.PAGE_3x3;
                positionArray = Constant.position_3x3;
                widthScale = (float) 12.3 / (float) 17;
                if (TextUtils.isEmpty(defaultIndex)) {
                    llLeftMenu.getChildAt(0).requestFocus();
                    getSoftwareList(pageNo, Constant.PAGE_3x3, softwareTypes.get(0));
                } else {
                    int index = Integer.valueOf(defaultIndex);
                    llLeftMenu.getChildAt(index).requestFocus();
                    getSoftwareList(pageNo, Constant.PAGE_3x3, softwareTypes.get(index));
                }

            } else {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlContent.getLayoutParams();
                layoutParams.setMargins(50, 0, 0, 0);
                tvCurrentLeftItem = tvTitleName;
                requestFocus = true;
                widthScale = (float) 15 / (float) 17;
                PAGE_SIZE = Constant.PAGE_3x4;
                positionArray = Constant.position_3x4;
                getSoftwareList(pageNo, Constant.PAGE_3x4, softwareTypes.get(0));
            }
        }
    }

    /**
     * 设置顶部菜单的状态
     */
    private void setTopMenuState(String orderType) {

        LogDebugUtil.d("orderType", orderType);
        if (TextUtils.isEmpty(orderType)) {
            orderType = "DOWNLOAD";
        }
        switch (orderType) {
            case "UPLOADTIME":
                tvHottest.setSelected(false);
                tvGrade.setSelected(false);
                tvNewest.setSelected(true);
                tvCurrentTopItem = tvNewest;

                break;
            case "STAR":
                tvCurrentTopItem = tvGrade;
                tvHottest.setSelected(false);
                tvGrade.setSelected(true);
                tvNewest.setSelected(false);
                break;
            default:
                tvCurrentTopItem = tvHottest;
                tvHottest.setSelected(true);
                tvGrade.setSelected(false);
                tvNewest.setSelected(false);
                break;
        }
        if (requestFocus && softwareTypes.size() == 1) {
            tvCurrentTopItem.requestFocus();
        }
    }

    /**
     * 创建侧边栏菜单
     *
     * @param softwareType
     * @return
     */
    private TextView createLeftMenuItem(SoftwareType softwareType) {
        TextView menuItem = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtil.getScreenHeight(this) / 10);
        lp.gravity = Gravity.CENTER;
        lp.topMargin = 10;
        menuItem.setLayoutParams(lp);
        menuItem.setBackgroundResource(R.drawable.category_left_menu_selector);
        menuItem.setTextColor(getResources().getColor(R.color.white));
        menuItem.setSingleLine();
        menuItem.setFocusable(true);
        menuItem.setTag(softwareType);
        menuItem.setText(softwareType.getName());
        menuItem.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.nav_tab_second_text_size));
        menuItem.setGravity(Gravity.CENTER);

        menuItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    if (tvCurrentLeftItem != null) {
                        tvCurrentLeftItem.setSelected(false);
                    }
                    view.setSelected(true);
                    onFocusChangeEvent(view);
                } else {
                    if (view != tvCurrentLeftItem) {
                        view.setSelected(false);
                    } else {
                        view.setSelected(true);
                    }
                }
            }
        });
        return menuItem;
    }

    /**
     * 左边菜单的焦点移动监听
     *
     * @param v
     */
    private void onFocusChangeEvent(final View v) {
        if (tvCurrentLeftItem != null && tvCurrentLeftItem == v) {
            return;
        }
        if (tvCurrentLeftItem != null) {
            showLoading();
            rlContent.setVisibility(View.INVISIBLE);
            if (mRunnable != null) {
                mMainThreadHandler.removeCallbacks(mRunnable);
            }
            final SoftwareType softwareType = (SoftwareType) v.getTag();

            mRunnable = new Runnable() {
                @Override
                public void run() {
                    VolleyQueueController.getInstance().cancelAll(TAG);
                    getSoftwareList(pageNo, PAGE_SIZE, softwareType);
                }
            };

            mMainThreadHandler.postDelayed(mRunnable, 500);
        }
        tvCurrentLeftItem = (TextView) v;
    }

    /**
     * 获取软件分类内容
     *
     * @param pageNo       当前页
     * @param pageSize     当前页大小
     * @param softwareType 软件分类内容
     */
    private void getSoftwareList(final int pageNo, final int pageSize, final SoftwareType softwareType) {

        String start = String.valueOf(1);
        String end;
        SoftwaresByMultiTypeEntity softwaresByMultiTypeEntity = new SoftwaresByMultiTypeEntity();
        softwaresByMultiTypeEntity.setStart(start);

        softwaresByMultiTypeEntity.setFirstTypeCodes(softwareType.getFirstTypeCodes());
        softwaresByMultiTypeEntity.setChildTypeCodes(softwareType.getChildTypeCodes());
        softwaresByMultiTypeEntity.setDeviceTypes(softwareType.getDeviceTypes());
        softwaresByMultiTypeEntity.setOrderType(orderType);
        rankElements = softwareType.getRankElements();
        int limit = 0;
        if (!TextUtils.isEmpty(softwareType.getLimit())) {
            limit = Integer.valueOf(softwareType.getLimit());
        }
        if (!TextUtils.isEmpty(orderType) && orderType.equals(initOrderType)) {
            if (rankElements != null && !rankElements.isEmpty()) {
                //移除超出限制限制范围的插入数据
                List<RankElements> removes = new ArrayList<RankElements>();
                Collections.sort(rankElements);
                List<String> excludePackages = new ArrayList<String>();
                for (RankElements rankElements1 : rankElements) {
                    excludePackages.add(rankElements1.getSoftwareInfo().getPackageName());
                    if (limit > 0 && Integer.valueOf(rankElements1.getIndex()) > limit) {
                        removes.add(rankElements1);
                    }
                }
                rankElements.removeAll(removes);
                softwaresByMultiTypeEntity.setExcludePackages(excludePackages);
                if (limit > 0 && limit < pageSize) {
                    end = String.valueOf(limit - rankElements.size());
                } else {
                    end = String.valueOf(pageSize - rankElements.size());
                }
            } else {
                if (limit > 0 && limit < pageSize) {
                    end = String.valueOf(Integer.valueOf(limit));
                } else {
                    end = String.valueOf(pageSize);
                }
            }
            if (end.equals("0")) {
                List<SoftwareInfo> softwareInfos = new ArrayList<SoftwareInfo>();
                if (rankElements != null && !rankElements.isEmpty()) {
                    for (RankElements rankElements1 : rankElements) {
                        softwareInfos.add(Integer.valueOf(rankElements1.getIndex()) - 1, rankElements1.getSoftwareInfo());
                    }
                    hideLoading();
                    rlContent.setVisibility(View.VISIBLE);
                    String key = getKey(softwareType, pageNo);
                    hashMap.clear();
                    hashMap.put(key, softwareInfos);
                    mApps.clear();
                    mApps.addAll(softwareInfos);
                    initGridView(rankElements.size());
                }
                return;
            }
        } else {
            if (limit > 0 && limit < pageSize) {
                end = String.valueOf(Integer.valueOf(limit));
            } else {
                end = String.valueOf(pageSize);
            }
        }

        softwaresByMultiTypeEntity.setEnd(end);
        ModeLevelAmsMenu.querySoftwaresByMultiType(this, TAG, softwaresByMultiTypeEntity, new ModeUserErrorCode<Softwares>() {

            @Override
            public void onJsonSuccess(Softwares t) {
                if (t != null) {
                    int totalCount;
                    List<SoftwareInfo> softwareInfos = t.getSoftwares();
                    int limit = 0;
                    if (!TextUtils.isEmpty(softwareType.getLimit())) {
                        limit = Integer.valueOf(softwareType.getLimit());
                    }

                    if (!TextUtils.isEmpty(orderType) && orderType.equals(initOrderType)) {
                        if (rankElements != null && !rankElements.isEmpty()) {
                            for (RankElements rankElements1 : rankElements) {
                                int index = Integer.valueOf(rankElements1.getIndex());
                                softwareInfos.add(index - 1, rankElements1.getSoftwareInfo());
                            }
                            totalCount = t.getTotalCount() + rankElements.size();

                        } else {
                            totalCount = t.getTotalCount();
                        }
                        if (limit > 0) {
                            totalCount = limit;
                        }
                    } else {
                        if (limit > 0) {
                            totalCount = limit;
                        } else {
                            totalCount = t.getTotalCount();
                        }
                    }
                    if (ContainerUtil.isEmpty(softwareInfos)) {
                        showRequestNoData("没有找到相关应用");
                        return;
                    }
                    hideLoading();
                    rlContent.setVisibility(View.VISIBLE);
                    String key = getKey(softwareType, pageNo);
                    hashMap.clear();
                    hashMap.put(key, softwareInfos);
                    mApps.clear();
                    mApps.addAll(softwareInfos);
                    initGridView(totalCount);
                } else {
                    showRequestNoData("没有找到相关应用");
                }
            }

            @Override
            public void onRequestFail(int errorCode, Throwable e) {
                showError(errorCode);
            }
        });
    }


    private void initGridView(final int totalCount) {
        int pageCount;
        if (totalCount <= PAGE_SIZE) {
            pageCount = 1;
        } else {
            if (totalCount % PAGE_SIZE == 0) {
                pageCount = (totalCount / PAGE_SIZE);
            } else {
                pageCount = (totalCount / PAGE_SIZE) + 1;
            }
        }

        if (pageCount <= 1) {
            showPageIndictor(false);
        } else {
            showPageIndictor(true);
        }

        tvPageNumber.setText(String.format("1/%d", pageCount));

        CommonPagerAdapter appCategoryAdapter = new CommonPagerAdapter(AppCategoryActivity.this, pageCount, this);
        mVerticalViewPager.setAdapter(appCategoryAdapter);
        if (Build.VERSION.SDK_INT <= 15) {
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    public void showPageIndictor(boolean isShow) {
        if (isShow) {
            tvPageNumber.setVisibility(View.VISIBLE);
            ivMore.setVisibility(View.VISIBLE);
        } else {
            tvPageNumber.setVisibility(View.INVISIBLE);
            ivMore.setVisibility(View.INVISIBLE);
        }
    }

    private String getKey(Object object, int offset) {
        if (object == null) {
            return null;
        }
        SoftwareType softwareType = (SoftwareType) object;
        return softwareType.getFirstTypeCodes() + "_" + softwareType.getChildTypeCodes() + "_"
                + softwareType.getOrderType() + "_" + softwareType.getDeviceTypes() + "_" + offset;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyQueueController.getInstance().cancelAll(TAG);
        ViewUtils.unbindDrawables(findViewById(R.id.main_tabs_container));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isBackMain) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private List<SoftwareInfo> getCache(String key) {
        if (hashMap.containsKey(key)) {
            return hashMap.get(key);
        }
        return null;
    }

    /**
     * 添加页内容
     *
     * @param position         需要加载第几页
     * @param horizontalLayout 加载的布局
     */
    @Override
    public void addInstantiateItem(final int position, final HorizontalLayout horizontalLayout) {
        horizontalLayout.setOnBorderListener(this);
        final SoftwareType softwareType = (SoftwareType) tvCurrentLeftItem.getTag();
        final String key = getKey(softwareType, position + 1);
        if (key == null) {
            showOtherError("null");
            return;
        }
        final List<SoftwareInfo> softwares;
        if ((softwares = getCache(key)) != null) {
            hideLoading();
            rlContent.setVisibility(View.VISIBLE);
            mApps.clear();
            mApps.addAll(softwares);
            initGridPage(mApps, horizontalLayout, position);
            return;
        }
        String start;
        String end;
        SoftwaresByMultiTypeEntity softwaresByMultiTypeEntity = new SoftwaresByMultiTypeEntity();

        softwaresByMultiTypeEntity.setFirstTypeCodes(softwareType.getFirstTypeCodes());
        softwaresByMultiTypeEntity.setChildTypeCodes(softwareType.getChildTypeCodes());
        softwaresByMultiTypeEntity.setDeviceTypes(softwareType.getDeviceTypes());
        softwaresByMultiTypeEntity.setOrderType(orderType);
        rankElements = softwareType.getRankElements();
        String limit = softwareType.getLimit();
        if (!TextUtils.isEmpty(orderType) && orderType.equals(initOrderType)) {
            if (rankElements != null && !rankElements.isEmpty()) {
                List<String> excludePackages = new ArrayList<String>();
                for (RankElements rankElements1 : rankElements) {
                    excludePackages.add(rankElements1.getSoftwareInfo().getPackageName());
                }
                softwaresByMultiTypeEntity.setExcludePackages(excludePackages);
                start = String.valueOf(position * PAGE_SIZE - rankElements.size() + 1);
                if (!TextUtils.isEmpty(limit) && Integer.valueOf(limit) < (position + 1) * PAGE_SIZE) {
                    end = String.valueOf(Integer.valueOf(limit) - rankElements.size());
                } else {
                    end = String.valueOf((position + 1) * PAGE_SIZE - rankElements.size());
                }
            } else {
                start = String.valueOf(position * PAGE_SIZE + 1);
                if (!TextUtils.isEmpty(limit) && Integer.valueOf(limit) < (position + 1) * PAGE_SIZE) {
                    end = String.valueOf(Integer.valueOf(limit));
                } else {
                    end = String.valueOf((position + 1) * PAGE_SIZE);
                }
            }
        } else {
            start = String.valueOf(position * PAGE_SIZE + 1);
            if (!TextUtils.isEmpty(limit) && Integer.valueOf(limit) < (position + 1) * PAGE_SIZE) {
                end = String.valueOf(Integer.valueOf(limit));
            } else {
                end = String.valueOf((position + 1) * PAGE_SIZE);
            }
        }
        softwaresByMultiTypeEntity.setStart(start);
        softwaresByMultiTypeEntity.setEnd(end);
        ModeLevelAmsMenu.querySoftwaresByMultiType(this, TAG, softwaresByMultiTypeEntity, new ModeUserErrorCode<Softwares>() {

            @Override
            public void onJsonSuccess(Softwares t) {
                // TODO Auto-generated method stub
                if (t != null) {
                    List<SoftwareInfo> softwareInfos = t.getSoftwares();
                    hideLoading();
                    rlContent.setVisibility(View.VISIBLE);
                    String key = getKey(softwareType, position + 1);
                    hashMap.put(key, softwareInfos);
                    mApps.clear();
                    mApps.addAll(softwareInfos);
                    Runnable waitFor = new Runnable() {
                        public void run() {
                            while (mVerticalViewPager.mScrollState != VerticalViewPager.SCROLL_STATE_IDLE) {
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch
                                    e.printStackTrace();
                                }
                            }
                            Message msg = Message.obtain();
                            msg.obj = horizontalLayout;
                            msg.arg1 = position;
                            myHandler.sendMessage(msg);

                        }
                    };
                    executor.execute(waitFor);
                }
            }

            @Override
            public void onRequestFail(int errorCode, Throwable e) {
                // TODO Auto-generated method stub


            }
        });

    }

    private final MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<AppCategoryActivity> mActivity;

        MyHandler(AppCategoryActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AppCategoryActivity activity = mActivity.get();
            if (activity != null) {
                activity.initGridPage(activity.mApps, (HorizontalLayout) msg.obj, msg.arg1);
            }
        }

    }

    /**
     * 生产gridView的每一item
     * 软件分类的数据填充
     */
    public void initGridPage(final ArrayList<SoftwareInfo> mApps, final HorizontalLayout layout, final int page) {
        mScreenWidth = ScreenUtil.getScreenWidth(this);
        mScreenHeight = ScreenUtil.getScreenHeight(this);
        int viewWidth = (int) (mScreenWidth * widthScale);
        int viewHeight = mScreenHeight * 8 / 13;
        if (PAGE_SIZE == Constant.PAGE_3x3) {
            layout.setSize(viewWidth, viewHeight, 3, 3);
        } else if (PAGE_SIZE == Constant.PAGE_3x4) {
            layout.setSize(viewWidth, viewHeight, 3, 4);
        }
        if (mApps != null) {
            int offset = page * PAGE_SIZE;
            int count = (mApps.size() - offset) > PAGE_SIZE ? (PAGE_SIZE + offset) : (mApps.size());
            for (int i = 0; i < count; i++) {

                final SoftwareInfo item = mApps.get(i);
                View view = mDefaultPullToRefreshAdapter.getView(i, null, layout);
                view.setTag(item);
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Object object = arg0.getTag();
                        if (object == null) {
                            return;
                        }
                        SoftwareInfo softwareInfo = (SoftwareInfo) object;
                        Intent intent = new Intent(AppCategoryActivity.this, AppDetailActivity.class);
                        if (downloadPath != null) {
                            if (llLeftMenu.getVisibility() == View.VISIBLE && tvCurrentLeftItem != null) {
                                SoftwareType softwareType = (SoftwareType) tvCurrentLeftItem.getTag();
                                if (softwareType != null) {
                                    downloadPath.setModulePath(softwareType.getId());
                                }
                            } else if (!isRank) {
                                downloadPath.setModulePath("1");
                            }
                            intent.putExtra("downloadPath", downloadPath);
                        }
                        intent.putExtra("packageName", softwareInfo.getPackageName());
                        AppCategoryActivity.this.startActivity(intent);
                    }
                });

                int j = i % PAGE_SIZE;
                layout.addItemView(view, 0, positionArray[j][0], positionArray[j][1], positionArray[j][2],
                        positionArray[j][3], page, count);
                View.OnFocusChangeListener oldOnFocusChangeListener = view.getOnFocusChangeListener();
                view.setOnFocusChangeListener(new NewOnFocusChangeListener(AppCategoryActivity.this, oldOnFocusChangeListener));


            }
        }
        if (requestFocus && page == 0) {
            mVerticalViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int index = mVerticalViewPager.getCurrentItem();
                    View view = mVerticalViewPager.getChildAt(index);
                    ((HorizontalLayout) view).focusMoveToLeft();
                    requestFocus = false;
                }
            }, 10);
        }
    }

    //当是软件列表和专题时对焦点进行监听
    private class NewOnFocusChangeListener implements View.OnFocusChangeListener {
        View.OnFocusChangeListener oldOnFocusChangeListener;
        Context context;

        NewOnFocusChangeListener(Context context, View.OnFocusChangeListener oldOnFocusChangeListener) {
            this.oldOnFocusChangeListener = oldOnFocusChangeListener;
            this.context = context;

        }

        @Override
        public void onFocusChange(View view, boolean b) {
            oldOnFocusChangeListener.onFocusChange(view, b);
            MarqueeTextView marqueeTextView = (MarqueeTextView) view.findViewById(R.id.tv_app_card_name);
            if (marqueeTextView != null) {
                marqueeTextView.setMarquee(b);
            }
        }
    }

    @Override
    public boolean onKeyBottomDown(int page, int pageCount, RectF rect) {
        if (page < pageCount - 1) {
            if (rect != null) {
                for (int i = 0; i < mVerticalViewPager.getChildCount(); i++) {
                    View child = mVerticalViewPager.getChildAt(i);
                    Object tag = child.getTag();
                    if (tag != null && (Integer) tag == (page + 1)) {

                        if (((HorizontalLayout) child).getChildCount() == 0) {
                            ToastUtils.show(this, "正在加载数据");
                            return true;
                        }

                        ((HorizontalLayout) child).requestFocusByRect(rect, 1);
                    }
                }
            }
            mVerticalViewPager.setCurrentItem(page + 1);
        }
        return true;
    }

    @Override
    public boolean onKeyTopUp(int page, RectF rect) {
        if (page > 0) {
            if (rect != null) {
                for (int i = 0; i < mVerticalViewPager.getChildCount(); i++) {
                    View child = mVerticalViewPager.getChildAt(i);
                    Object tag = child.getTag();
                    if (tag != null && (Integer) tag == (page - 1)) {
                        ((HorizontalLayout) child).requestFocusByRect(rect, 0);
                    }
                }
            }
            mVerticalViewPager.setCurrentItem(page - 1);
        } else {
            if (tvCurrentTopItem != null) {
                tvCurrentTopItem.requestFocus();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyLeftEnd(int page) {

        if (tvCurrentLeftItem != null) {
            tvCurrentLeftItem.requestFocus();
        }
        return true;
    }

    @Override
    public boolean onKeyRightEnd(int page) {
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int pageCount = mVerticalViewPager.getAdapter().getCount();
        if (position < pageCount - 1) {
            ivMore.setVisibility(View.VISIBLE);
        } else {
            ivMore.setVisibility(View.INVISIBLE);
        }
        tvPageNumber.setText((position + 1) + "/" + mVerticalViewPager.getAdapter().getCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            if (llLeftMenu.getChildCount() > 0 && llLeftMenu.getChildAt(0).hasFocus()) {
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN)) {
            if (llLeftMenu.getChildCount() > 0 && llLeftMenu.getChildAt(llLeftMenu.getChildCount() - 1).hasFocus()) {
                return true;
            }
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN
                && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT)) {
            View currentView = getCurrentFocus();
            if (currentView == tvHottest) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFocusChange(final View v, boolean hasFocus) {
        if (hasFocus) {
            if (tvCurrentTopItem != null) {
                tvCurrentTopItem.setSelected(false);
            }
            v.setSelected(true);
            if (tvCurrentTopItem != null && tvCurrentTopItem == v) {
                return;
            }
            final SoftwareType softwareType = (SoftwareType) tvCurrentLeftItem.getTag();
            if (tvCurrentTopItem != null && softwareType != null) {
                showLoading();
                rlContent.setVisibility(View.INVISIBLE);
                if (mRunnable != null) {
                    mMainThreadHandler.removeCallbacks(mRunnable);
                }
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int id = v.getId();
                        switch (id) {
                            case R.id.tv_app_category_hottest:
                                setTopMenuState("DOWNLOAD");
                                VolleyQueueController.getInstance().cancelAll(TAG);
                                if (initOrderType.contains("DOWNLOAD")) {
                                    orderType = initOrderType;
                                } else {
                                    orderType = "DOWNLOAD";
                                }
                                getSoftwareList(pageNo, PAGE_SIZE, softwareType);
                                break;
                            case R.id.tv_app_category_newest:
                                setTopMenuState("UPLOADTIME");
                                VolleyQueueController.getInstance().cancelAll(TAG);
                                orderType = "UPLOADTIME";
                                getSoftwareList(pageNo, PAGE_SIZE, softwareType);
                                break;
                            case R.id.tv_app_category_grade:
                                setTopMenuState("STAR");
                                VolleyQueueController.getInstance().cancelAll(TAG);
                                orderType = "STAR";
                                getSoftwareList(pageNo, PAGE_SIZE, softwareType);
                                break;
                        }
                    }
                };
                mMainThreadHandler.postDelayed(mRunnable, 500);
            }
            tvCurrentTopItem = v;
        } else {
            if (v != tvCurrentTopItem) {
                v.setSelected(false);
            }
        }
    }

    @Override
    protected void onReTryClicked() {
        setAdapter();
    }

    @Override
    protected boolean onRetryKeyClicked(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && (tvCurrentLeftItem != null) && tvCurrentLeftItem.isFocusable()) {
                tvCurrentLeftItem.requestFocus();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                tvHottest.requestFocus();
                return true;
            }
        }
        return super.onRetryKeyClicked(v, keyCode, event);
    }
}

