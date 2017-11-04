package com.winhearts.arappmarket.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.winhearts.arappmarket.activity.AppDetailActivity;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.TopicActivity;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.view.VerticalViewPager;
import com.winhearts.arappmarket.view.VerticalViewPager.OnPageChangeListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.utils.adapter.DefaultPullToRefreshAdapter;
import com.winhearts.arappmarket.utils.adapter.PagerAdapter;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.SoftwareTypeInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.HorizontalLayout.OnBorderListener;
import com.winhearts.arappmarket.view.LoadAndErrorView;
import com.winhearts.arappmarket.view.MarqueeTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 二级菜单
 */
public class SecondLevelMenuFragment extends MovableFragment implements OnPageChangeListener {

    private boolean DEBUG = false;
    private static final String TAG = "SecondLevelMenuFragment";
    private Context mContext;
    public final int PAGE_3x3 = 9;
    public final int PAGE_3x4 = 12;
    public final int PAGE_2x2 = 4;

    /**
     * {0,0,1,1} int startX, int startY, int endX, int endY 左上角右下角坐标
     */
    int[][] position_3x3 = new int[][]{{0, 0, 1, 1}, {1, 0, 2, 1}, {2, 0, 3, 1}, {0, 1, 1, 2},
            {1, 1, 2, 2}, {2, 1, 3, 2}, {0, 2, 1, 3}, {1, 2, 2, 3}, {2, 2, 3, 3},};

    int[][] position_3x4 = new int[][]{{0, 0, 1, 1}, {1, 0, 2, 1}, {2, 0, 3, 1}, {3, 0, 4, 1},
            {0, 1, 1, 2}, {1, 1, 2, 2}, {2, 1, 3, 2}, {3, 1, 4, 2}, {0, 2, 1, 3}, {1, 2, 2, 3},
            {2, 2, 3, 3}, {3, 2, 4, 3},};

    int[][] position_2x2 = new int[][]{{0, 0, 1, 1}, {0, 1, 1, 2}, {1, 0, 2, 1}, {1, 1, 2, 2}};

    private int[][] positionArray;

    private int PAGE_SIZE;

    private DefaultPullToRefreshAdapter mDefaultPullToRefreshAdapter;

    ArrayList<SoftwareInfo> mApps = new ArrayList<SoftwareInfo>();

    private float widthScale;


    private VerticalViewPager mViewPager;
    private ImageView mDownPullIv;
    LayoutInflater mLayoutInflater;
    TextView mPageIndictorTv;
    Handler mMainThreadHandler;
    MenuItem mParentMenu;
    MenuItem mCurrentMenu;
    int index;
    int tabCount;
    int mScreenWidth;
    int mScreenHeight;
    final static int TOPIC = 1;
    final static int TOPIC_COUNT = 2;
    final static int TYPE = 3;
    final static int TYPE_COUNT = 4;
    int topic2x2 = 0;
    int requestFocus;
    private String layoutId;
    private DownloadPath downloadPath;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    //线程池
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    private boolean mFlag = false; //用于是否是第一次显示并且非tab切换

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = this.getActivity();
        mScreenWidth = ScreenUtil.getScreenWidth(mContext);
        mScreenHeight = ScreenUtil.getScreenHeight(mContext);

        mLayoutInflater = inflater;
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.second_level_menu_fragment, container, false);
        }
        addLoadAndErrorView((ViewGroup) rootView.findViewById(R.id.rl_second_level_content_tips));
        mPageIndictorTv = (TextView) rootView.findViewById(R.id.pageIndictorTv);
        mDownPullIv = (ImageView) rootView.findViewById(R.id.downPullIv);
        mViewPager = (VerticalViewPager) rootView.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setFocusable(false);
        mViewPager.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.requestDisallowInterceptTouchEvent(true);
        MenuItem item = (MenuItem) this.getArguments().getSerializable("object");
        index = this.getArguments().getInt("index", 0);
        tabCount = this.getArguments().getInt("tab_count", 0);
        topic2x2 = this.getArguments().getInt("topic2x2", 0);
        layoutId = this.getArguments().getString("layoutId", "");
        requestFocus = this.getArguments().getInt("requestFocus", 0);
        downloadPath = (DownloadPath) this.getArguments().get("downloadPath");
        mParentMenu = item;
        mDefaultPullToRefreshAdapter = new DefaultPullToRefreshAdapter(mContext, mApps);
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        initView();
        return rootView;
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VolleyQueueController.getInstance().cancelAll(TAG);
    }

    public void initView() {
        if (topic2x2 == 1) {
            PAGE_SIZE = PAGE_2x2;
            positionArray = position_2x2;
        } else {
            PAGE_SIZE = PAGE_3x4;
            positionArray = position_3x4;
        }
        widthScale = (float) 15 / (float) 17;

        initContent(mParentMenu);

        mCurrentMenu = mParentMenu;
    }

    /**
     * 填充界面
     *
     * @param item
     */
    public void initContent(MenuItem item) {
        if ("TOPIC".equals(item.getMenuDataType())) {
            Topic mTopic = item.getTopicInfo();
            querySoftWareList(mContext, 1, PAGE_SIZE, null, TOPIC_COUNT, mTopic, null);

        } else {
            SoftwareTypeInfo type = item.getSoftwareTypeInfo();
            querySoftWareList(mContext, 1, PAGE_SIZE, null, TYPE_COUNT, null, type);
        }
    }

    /**
     * 生产gridView的每一item
     * 软件分类的数据填充
     */
    public void initGridPage(final ArrayList<SoftwareInfo> mApps, final HorizontalLayout layout, final int page) {

        if (mContext == null) {
            return;
        }

        int viewWidth = (int) (mScreenWidth * widthScale);

        int viewHeight = mScreenHeight * 8 / 14;
        if (PAGE_SIZE == PAGE_3x3) {
            layout.setSize(viewWidth, viewHeight, 3, 3);
        } else if (PAGE_SIZE == PAGE_3x4) {
            layout.setSize(viewWidth, viewHeight, 3, 4);
        } else if (PAGE_SIZE == PAGE_2x2) {
            layout.setSize(viewWidth, viewHeight, 2, 2);
        }

        if (mApps != null) {

            int offset = page * PAGE_SIZE;

            int count = (mApps.size() - offset) > PAGE_SIZE ? (PAGE_SIZE + offset) : (mApps.size());

            for (int i = 0; i < count; i++) {

                final SoftwareInfo item = mApps.get(i);
                View view;
                if (PAGE_SIZE == PAGE_2x2) {
                    view = getView(i);
                } else {
                    view = mDefaultPullToRefreshAdapter.getView(i, null, layout);
                }
                view.setTag(item);
                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Object object = arg0.getTag();
                        if (object == null) {
                            return;
                        }
                        SoftwareInfo softwareInfo = (SoftwareInfo) object;
                        Intent intent = new Intent(mContext, AppDetailActivity.class);
                        intent.putExtra("packageName", softwareInfo.getPackageName());
                        if (mContext instanceof MainActivity) {
                            DownloadPath downloadPath = new DownloadPath();
                            downloadPath.setLayoutId(layoutId);
                            if (mCurrentMenu.getParentMenuId() != null) {
                                downloadPath.setMenuId(mCurrentMenu.getParentMenuId());
                                downloadPath.setSubMenuId(mCurrentMenu.getMenuId());
                            } else {
                                downloadPath.setMenuId(mCurrentMenu.getMenuId());
                            }
                            intent.putExtra("downloadPath", downloadPath);
                        } else if ((mContext instanceof TopicActivity) && downloadPath != null) {
                            intent.putExtra("downloadPath", downloadPath);
                        }

                        mContext.startActivity(intent);
                    }
                });

                int j = i % PAGE_SIZE;
                layout.addItemView(view, 0, positionArray[j][0], positionArray[j][1], positionArray[j][2],
                        positionArray[j][3], page, count);
                OnFocusChangeListener oldOnFocusChangeListener = view.getOnFocusChangeListener();
                view.setOnFocusChangeListener(new NewOnFocusChangeListener(oldOnFocusChangeListener));
            }
        }
        if (requestFocus == 1 && page == 0) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    SecondLevelMenuFragment.this.focusMoveToLeft();
                    requestFocus = 0;
                }

            }, 10);

        }

    }

    //当是软件列表和专题时对焦点进行监听
    private class NewOnFocusChangeListener implements OnFocusChangeListener {
        OnFocusChangeListener oldOnFocusChangeListener;

        NewOnFocusChangeListener(OnFocusChangeListener oldOnFocusChangeListener) {
            this.oldOnFocusChangeListener = oldOnFocusChangeListener;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            oldOnFocusChangeListener.onFocusChange(view, b);
            MarqueeTextView marqueeTextView = (MarqueeTextView) view.findViewById(R.id.tv_app_card_name);
            if (marqueeTextView != null) {
                marqueeTextView.setMarquee(b);
            }
            if (mContext instanceof MainActivity && b) {
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

    public void showPageIndictor(boolean isShow) {
        if (isShow) {
            mPageIndictorTv.setVisibility(View.VISIBLE);
            mDownPullIv.setVisibility(View.VISIBLE);
        } else {
            mPageIndictorTv.setVisibility(View.INVISIBLE);
            mDownPullIv.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 专题和软件分类界面的填充
     * 初始话规则 gridView试图，数据需要分页加载（预加载一页）
     */

    public void initGridView(final int totalCount) {
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

        final int PageCount = pageCount;

        if (pageCount <= 1) {
            showPageIndictor(false);
        } else {
            showPageIndictor(true);
        }

        mPageIndictorTv.setText("1/" + PageCount);

        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return PageCount;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View mView = mLayoutInflater.inflate(R.layout.advert_item_layout, null);
                HorizontalLayout mAdvertLayout = (HorizontalLayout) mView.findViewById(R.id.metrolayout);
                mAdvertLayout.setPageCount(PageCount);
                mAdvertLayout.setOnBorderListener(mOnBorderListener);
                if (mCurrentMenu != null) {
                    if ("TOPIC".equals(mCurrentMenu.getMenuDataType())) {
                        Topic topic = mCurrentMenu.getTopicInfo();
                        querySoftWareList(mContext, position + 1, PAGE_SIZE,
                                mAdvertLayout, TOPIC, topic, null);
                    } else {
                        SoftwareTypeInfo info = mCurrentMenu.getSoftwareTypeInfo();
                        querySoftWareList(mContext, position + 1, PAGE_SIZE,
                                mAdvertLayout, TYPE, null, info);
                    }

                }
                container.addView(mView);
                mView.setTag(position);
                return mView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

        };
        mViewPager.setAdapter(mPagerAdapter);
    }

    //对布局上下左右进行广播监听
    OnBorderListener mOnBorderListener = new OnBorderListener() {

        @Override
        public boolean onKeyTopUp(int page, RectF rect) {
            if (page > 0) {
                if (rect != null) {
                    for (int i = 0; i < mViewPager.getChildCount(); i++) {
                        View child = mViewPager.getChildAt(i);
                        Object tag = child.getTag();
                        if (tag != null && (Integer) tag == (page - 1)) {
                            ((HorizontalLayout) child).requestFocusByRect(rect, 0);
                        }
                    }
                }
                mViewPager.setCurrentItem(page - 1);
            } else {

                HorizontalLayout child = (HorizontalLayout) mViewPager.getChildAt(0);
                child.setScaleUpDown();

            }
            return true;
        }

        @Override
        public boolean onKeyBottomDown(int page, int pageCount, RectF rect) {
            if (page < pageCount - 1) {
                if (rect != null) {
                    for (int i = 0; i < mViewPager.getChildCount(); i++) {
                        View child = mViewPager.getChildAt(i);
                        Object tag = child.getTag();
                        if (tag != null && (Integer) tag == (page + 1)) {

                            if (((HorizontalLayout) child).getChildCount() == 0) {
                                ToastUtils.show(mContext, "正在加载数据");
                                return true;
                            }

                            ((HorizontalLayout) child).requestFocusByRect(rect, 1);
                        }
                    }
                }
                mViewPager.setCurrentItem(page + 1);
            }
            return true;
        }

        @Override
        public boolean onKeyLeftEnd(int page) {

            return false;
        }

        @Override
        public boolean onKeyRightEnd(int page) {
            if (index == tabCount - 1) {
                HorizontalLayout child = (HorizontalLayout) mViewPager.getChildAt(page);
                if (child != null) {
                    child.setScaleUpDown();
                }

                LogDebugUtil.d("focusetest", "到这儿1");
                return true;
            }
            return false;
        }
    };

    HashMap<String, List<SoftwareInfo>> hashMap = new HashMap<>();

    public List<SoftwareInfo> getCache(String key) {
        if (hashMap.containsKey(key)) {
            return hashMap.get(key);
        }
        return null;
    }

    public void cacheDate(String key, List<SoftwareInfo> date) {

        for (SoftwareInfo soft : date) {
            if (mContext.getPackageManager().getLaunchIntentForPackage(soft.getPackageName()) == null) {
                soft.setInstalled(false);
            } else {
                soft.setInstalled(true);
            }
        }
        hashMap.put(key, date);
    }

    public String getKey(Object object, int offset) {

        if (object == null) {
            return null;
        }
        if (object instanceof Topic) {
            Topic topic = (Topic) object;
            return topic.getCode() + "_" + offset;
        } else {
            SoftwareTypeInfo softwareTypeInfo = (SoftwareTypeInfo) object;

            return softwareTypeInfo.getRootTypeCode() + "_" + softwareTypeInfo.getSubTypeCode() + "_"
                    + softwareTypeInfo.getOrderType() + "_" + softwareTypeInfo.getHandlerType() + "_" + offset;

        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            initGridPage(mApps, (HorizontalLayout) msg.obj, msg.arg1);
        }
    };

    @Override
    public void focusTab2Down() {
        LoadAndErrorView errorView = getTipsView();
        if (errorView != null && errorView.getVisibility() == View.VISIBLE) {
            errorView.RetryTvRequestFocus();
        } else {
            int index = mViewPager.getCurrentItem();
            for (int i = 0; i < mViewPager.getChildCount(); i++) {
                View view = mViewPager.getChildAt(i);
                Object obejct = view.getTag();
                if (index == (Integer) obejct) {
                    ((HorizontalLayout) view).focusFirstChildView();
                    return;
                }
            }
        }
    }

    @Override
    public void focusMoveToLeft() {
        LoadAndErrorView errorView = getTipsView();
        if (errorView != null && errorView.getVisibility() == View.VISIBLE) {
            errorView.RetryTvRequestFocus();
        } else {
            if (mFlag) {
                mFlag = false;
                return;
            }
            int index = mViewPager.getCurrentItem();
            for (int i = 0; i < mViewPager.getChildCount(); i++) {
                View view = mViewPager.getChildAt(i);
                Object obejct = view.getTag();
                if (index == (Integer) obejct) {
                    ((HorizontalLayout) view).focusMoveToLeft();
                    return;
                }
            }
        }
    }

    @Override
    public void focusMoveToRight() {
        LoadAndErrorView errorView = getTipsView();
        if (errorView != null && errorView.getVisibility() == View.VISIBLE) {
            errorView.RetryTvRequestFocus();
        } else {
            if (mFlag) {
                mFlag = false;

                return;
            }
            int index = mViewPager.getCurrentItem();
            for (int i = 0; i < mViewPager.getChildCount(); i++) {
                View view = mViewPager.getChildAt(i);
                Object obejct = view.getTag();
                if (index == (Integer) obejct) {
                    ((HorizontalLayout) view).focusMoveToRight();
                    return;
                }
            }
        }
    }

    @Override
    public void onScrollFirstPositionNoFocus() {
        LoadAndErrorView errorView = getTipsView();
        if (errorView != null && errorView.getVisibility() == View.VISIBLE) {
            errorView.RetryTvRequestFocus();
        } else {
            mFlag = false;
            int index = mViewPager.getCurrentItem();
            for (int i = 0; i < mViewPager.getChildCount(); i++) {
                View view = mViewPager.getChildAt(i);
                Object obejct = view.getTag();
                if (index == (Integer) obejct) {
                    ((HorizontalLayout) view).scrollFirstPosition();
                    return;
                }
            }
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        int pageCount = mViewPager.getAdapter().getCount();
        if (position < pageCount - 1) {
            mDownPullIv.setVisibility(View.VISIBLE);
        } else {
            mDownPullIv.setVisibility(View.INVISIBLE);
        }
        mPageIndictorTv.setText((position + 1) + "/" + mViewPager.getAdapter().getCount());
        for (int i = 0; i < mViewPager.getChildCount(); i++) {
            final HorizontalLayout child = (HorizontalLayout) mViewPager.getChildAt(i);
            Object tag = child.getTag();
            if (tag != null && (Integer) tag == (position)) {
                final View view = child.findFocus();
                if (view != null) {
                    child.setScaleDown(view);
                    mViewPager.setFilterUpEvent(true);
                    mViewPager.setFilterUpEvent(false);
                    final View current_view = child.findFocus();
                    if (view.equals(current_view)) {
                        child.setScaleUp(view);
                    }

                }
            }
        }
    }

	/*
     * 若当前页面加载失败则刷新
	 */

    public void refreshIfError() {
        if (mCurrentMenu != null) {
            initContent(mCurrentMenu);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public View getView(int position) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View convertView = mInflater.inflate(R.layout.topic_app_item, null);
        Button download = (Button) convertView.findViewById(R.id.down_load);
        TextView appName = (TextView) convertView.findViewById(R.id.app_name);
        TextView descTextView = (TextView) convertView.findViewById(R.id.app_desc);
        TextView versionTextView = (TextView) convertView.findViewById(R.id.app_version);
        TextView updateTimeTextView = (TextView) convertView.findViewById(R.id.update_time);
        TextView appSourceTextView = (TextView) convertView.findViewById(R.id.app_source);
        SimpleDraweeView imageView = (SimpleDraweeView) convertView.findViewById(R.id.app_icon);
        final SoftwareInfo softwareInfo = mApps.get(position);
        if (softwareInfo == null) {
            return convertView;
        }
        String name = softwareInfo.getName();
        String desc = softwareInfo.getDescription();
        String version = softwareInfo.getVersionName();
        String updateTime = softwareInfo.getUpdateTime();

        Date d1 = new Date(Long.parseLong(updateTime));
        String updateTimeStr = format.format(d1);

        String source = softwareInfo.getDevelopor();
        if (source != null && source.length() > 0) {
            appSourceTextView.setText("来源:" + source);
        }

        if (updateTime != null && updateTime.length() > 0) {
            updateTimeTextView.setText(updateTimeStr);
        }

        if (version != null && version.length() > 0) {
            versionTextView.setText(version);
        }

        if (desc != null && desc.length() > 0) {
            descTextView.setText(desc);
        }

        appName.setText(name);

        if (softwareInfo.isInstalled()) {
            download.setText("已安装");
        } else {
            download.setText("免费下载");
        }

        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.background_app_icon);
        hierarchy.setFailureImage(getResources().getDrawable(R.drawable.appicon_error));
        imageView.setHierarchy(hierarchy);
        imageView.setImageURI(Uri.parse(softwareInfo.getIcon()));
        return convertView;
    }

    /**
     * 请求页面 数据列表
     * 侧边标题栏切换的时候会请求，VerticalViewPager换页也会请求，并且预加载一页
     *
     * @param mContext
     * @param offset    偏移量，比如VerViewPager里有9页 里的第几页，预加载一页
     * @param limit
     * @param layout
     * @param queryType
     * @param mTopic
     * @param info
     */
    public void querySoftWareList(final Context mContext, final int offset, final int limit,
                                  final HorizontalLayout layout, final int queryType, final Topic mTopic, final SoftwareTypeInfo info) {
        if (queryType == TOPIC || queryType == TYPE) {
            String key;
            if (queryType == TOPIC) {
                key = getKey(mTopic, offset);
            } else {
                key = getKey(info, offset);

            }

            if (key == null) {
                LogDebugUtil.i(TAG, "key is null");
                showRequestNoData("没有找到相关应用");
                return;
            }
            List<SoftwareInfo> softwares;
            if ((softwares = getCache(key)) != null) {
                hideLoading();
                mApps.clear();
                mApps.addAll(softwares);
                initGridPage(mApps, layout, offset - 1);
                return;
            }
        } else {
            hashMap.clear();
            showLoading();
        }


//获取主题关联软件列表和获取软件列表
        if (queryType == TOPIC || queryType == TOPIC_COUNT) {
            ModeLevelAmsMenu.queryTopicSoftWareList(mContext, TAG, offset, limit, mTopic,
                    new ModeUserErrorCode<Softwares>() {

                        @Override
                        public void onRequestFail(int errorCode, Throwable e) {
                            showError(errorCode, getUserVisibleHint());
                        }

                        @Override
                        public void onJsonSuccess(Softwares t) {
                            if (t.getSoftwares() == null) {
                                showRequestNoData("没有找到相关应用");
                                return;

                            }
                            dealTopicSoftwareList(t, offset, limit, layout, queryType, mTopic);
                        }
                    });
        } else {
            ModeLevelAmsMenu.querySoftWareList(mContext, TAG, offset, limit, info, new ModeUserErrorCode<Softwares>() {

                @Override
                public void onJsonSuccess(Softwares t) {
                    dealSoftwareInfo(t, offset, limit, layout, queryType, mTopic, info);
                }

                @Override
                public void onRequestFail(int errorCode, Throwable e) {
                    showError(errorCode, getUserVisibleHint());
                }
            });
        }

    }

    private void dealTopicSoftwareList(Softwares response, final int offset, final int limit,
                                       final HorizontalLayout layout, final int queryType, final Topic mTopic) {

        List<SoftwareInfo> softwares = response.getSoftwares();
        int mTotal = response.getTotalCount();
        if (softwares == null) {
            return;
        }
        if (softwares.size() == 0) {
            showRequestNoData("没有找到相关应用");
            return;
        }
        hideLoading();
        String key = getKey(mTopic, offset);
        cacheDate(key, softwares);
        mApps.clear();
        mApps.addAll(softwares);

        if (queryType == TOPIC) {
            Runnable waitFor = new Runnable() {
                public void run() {

                    while (mViewPager.mScrollState != VerticalViewPager.SCROLL_STATE_IDLE) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Message msg = Message.obtain();
                    msg.obj = layout;
                    msg.arg1 = offset - 1;
                    myHandler.sendMessage(msg);
                }
            };

            executor.execute(waitFor);
        } else {
            initGridView(mTotal);
        }

    }

    private void dealSoftwareInfo(Softwares response, final int offset, final int limit,
                                  final HorizontalLayout layout, final int queryType, final Topic mTopic, final SoftwareTypeInfo info) {

        List<SoftwareInfo> softwares = response.getSoftwares();
        int totalCount = response.getTotalCount();
        if (ContainerUtil.isEmpty(softwares)) {
            showRequestNoData("没有找到相关应用");
            return;
        }
        hideLoading();
        String key = getKey(info, offset);
        cacheDate(key, softwares);
        mApps.clear();
        mApps.addAll(softwares);
        LogDebugUtil.d(DEBUG, TAG, "dealSoftwareInfo() queryType = " + queryType);
        if (queryType == TYPE) {
            Runnable waitFor = new Runnable() {
                public void run() {

                    while (mViewPager.mScrollState != VerticalViewPager.SCROLL_STATE_IDLE) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch
                            // block
                            e.printStackTrace();
                        }
                    }
                    Message msg = Message.obtain();
                    msg.obj = layout;
                    msg.arg1 = offset - 1;
                    myHandler.sendMessage(msg);
                }
            };
            executor.execute(waitFor);
        } else {

            initGridView(totalCount);
        }

    }

    @Override
    protected void onReTryClicked() {
        initContent(mCurrentMenu);
    }

    @Override
    protected boolean onRetryKeyClicked(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (index == 0) {
                    return true;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (index >= tabCount - 1) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
