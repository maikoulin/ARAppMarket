package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.wrapper.PageAdapterInstantiateListener;
import com.winhearts.arappmarket.view.VerticalViewPager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.utils.adapter.CommonPagerAdapter;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.model.TopicList;
import com.winhearts.arappmarket.model.TopicListInfo;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.view.CommonTitle;
import com.winhearts.arappmarket.view.CustNineDrawable;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.MarqueeTextView;
import com.winhearts.arappmarket.view.TopicListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 专题列表
 * Created by lmh on 2016/4/25.
 */
public class TopicListActivity extends BaseActivity implements PageAdapterInstantiateListener, HorizontalLayout.OnBorderListener, VerticalViewPager.OnPageChangeListener {
    private TopicListInfo topicListInfo;
    private String titleName;
    private DownloadPath downloadPath;
    private SimpleDraweeView sdbg;
    private CommonTitle commonTitle;
    private int pageNo = 1;
    private VerticalViewPager verticalViewPager;
    private TextView tvPageNumber;
    private ImageView ivMore;
    private int PAGE_SIZE;
    private int mScreenWidth;
    private int mScreenHeight;
    private float widthScale;
    private int[][] positionArray;
    private boolean requestFocus = false;
    private String sortType = "PRIORITY";
    private HashMap<String, List<Topic>> hashMap = new HashMap<String, List<Topic>>();
    private List<Topic> selfTopics;
    //线程池
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);
        Intent intent = getIntent();
        titleName = intent.getStringExtra("titleName");
        downloadPath = (DownloadPath) intent.getSerializableExtra("downloadPath");
        topicListInfo = (TopicListInfo) intent.getSerializableExtra("topicListInfo");
        mScreenWidth = ScreenUtil.getScreenWidth(this);
        mScreenHeight = ScreenUtil.getScreenHeight(this);
        initView();
        setData();
    }

    private void initView() {
        addLoadAndErrorView((ViewGroup) findViewById(R.id.rl_topic_list_content));
        sdbg = (SimpleDraweeView) this.findViewById(R.id.sd_topic_list_bg);
        commonTitle = (CommonTitle) this.findViewById(R.id.common_title_topic_list_name);
        verticalViewPager = (VerticalViewPager) this.findViewById(R.id.vp_topic_list_content);
        tvPageNumber = (TextView) this.findViewById(R.id.tv_topic_list_page_number);
        ivMore = (ImageView) this.findViewById(R.id.iv_topic_list_more);
        verticalViewPager.setOnPageChangeListener(this);
        verticalViewPager.setOffscreenPageLimit(1);
        verticalViewPager.setFocusable(false);
        verticalViewPager.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        verticalViewPager.requestDisallowInterceptTouchEvent(true);
        requestFocus = true;
        widthScale = (float) 15 / (float) 17;
        PAGE_SIZE = Constant.PAGE_2x3;
        positionArray = Constant.position_2x3;
        if (!TextUtils.isEmpty(titleName)) {
            commonTitle.setTitle(titleName);
        }
        CommonHierarchy.setBgImage(sdbg);
    }

    private void setData() {
        if (topicListInfo != null) {
            String type = topicListInfo.getType();
            if (!TextUtils.isEmpty(type) && type.equals("SELF_DEFINE")) {
                selfTopics = topicListInfo.getTopics();
                if (!ContainerUtil.isEmptyOrNull(selfTopics)) {
                    showLoading();
                    int totalCount = selfTopics.size();
                    hashMap.clear();
                    boolean isPut = true;
                    while (isPut) {
                        List<Topic> topics = new ArrayList<Topic>();
                        if (selfTopics.size() > PAGE_SIZE) {
                            topics.addAll(selfTopics.subList(0, 6));
                            hashMap.put(String.valueOf(pageNo), topics);
                        } else {
                            topics.addAll(selfTopics);
                            hashMap.put(String.valueOf(pageNo), topics);
                            isPut = false;
                        }
                        pageNo++;
                        selfTopics.removeAll(topics);
                    }
                    initGridView(totalCount);
                } else {
                    showRequestNoData("没有找到相关应用");
                }

            } else {
                showLoading();
                if (!TextUtils.isEmpty(topicListInfo.getOrderType())) {
                    sortType = topicListInfo.getOrderType();
                }
                getTopicList(pageNo, PAGE_SIZE, sortType);
            }
        } else {
            showRequestNoData("没有找到相关应用");
        }

    }

    private void getTopicList(final int pageNo, int pageSize, String sortType) {

        ModeLevelAmsMenu.queryTopicList(this, String.valueOf(pageNo), String.valueOf(pageSize), sortType, new ModeUserErrorCode<TopicList>() {
            @Override
            public void onJsonSuccess(TopicList topicList) {
                if (topicList != null) {
                    List<Topic> topics = topicList.getTopicList();
                    if (ContainerUtil.isEmptyOrNull(topics)) {
                        showRequestNoData("没有找到相关应用");
                        return;
                    }
                    hideLoading();
                    hashMap.clear();
                    hashMap.put(String.valueOf(pageNo), topics);
                    initGridView(topicList.getTotalCount());
                } else {
                    showRequestNoData("没有找到相关应用");
                }

            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                showError(code);
            }
        });


    }

    int pageCount = 0;

    private void initGridView(final int totalCount) {
        pageCount = 0;
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

        tvPageNumber.setText("1/" + pageCount);

        CommonPagerAdapter appCategoryAdapter = new CommonPagerAdapter(TopicListActivity.this, pageCount, this);
        appCategoryAdapter.setFocusDrawable(R.color.transparent);
        verticalViewPager.setAdapter(appCategoryAdapter);
        hideLoading();

    }

    /**
     * 生产gridView的每一item
     * 软件分类的数据填充
     */
    public void initGridPage(final List<Topic> mApps, final HorizontalLayout layout, final int page) {
        mScreenWidth = ScreenUtil.getScreenWidth(this);
        mScreenHeight = ScreenUtil.getScreenHeight(this);
        int viewWidth = (int) (mScreenWidth * widthScale);
        int viewHeight = mScreenHeight * 9 / 13;
        int realWidth = viewWidth / 3;
        int realHeight = viewHeight / 2;
        layout.setSize(viewWidth, viewHeight, 2, 3);
        if (mApps != null) {
            int offset = page * PAGE_SIZE;
            int count = (mApps.size() - offset) > PAGE_SIZE ? (PAGE_SIZE + offset) : (mApps.size());
            for (int i = 0; i < count; i++) {
                Topic item = mApps.get(i);
                TopicListView view = new TopicListView(this);
                view.setData(item, realWidth, realHeight);
                view.setTag(item);
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Object object = arg0.getTag();
                        if (object == null) {
                            return;
                        }
                        Topic topic = (Topic) object;
                        Intent intent = new Intent(TopicListActivity.this, TopicActivity.class);
                        if (downloadPath != null) {
                            downloadPath.setModulePath(topic.getCode());
                            intent.putExtra("downloadPath", downloadPath);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("topic", topic);
                        intent.putExtras(bundle);
                        TopicListActivity.this.startActivity(intent);
                    }
                });

                int j = i % PAGE_SIZE;
                layout.addItemView(view, 0, positionArray[j][0], positionArray[j][1], positionArray[j][2],
                        positionArray[j][3], page, count);
                View.OnFocusChangeListener oldOnFocusChangeListener = view.getOnFocusChangeListener();
                view.setOnFocusChangeListener(new NewOnFocusChangeListener(TopicListActivity.this, oldOnFocusChangeListener));


            }
        }
        if (requestFocus && page == 0) {
            verticalViewPager.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    int index = verticalViewPager.getCurrentItem();
                    View view = verticalViewPager.getChildAt(index);
                    ((HorizontalLayout) view).focusMoveToLeft();
                    requestFocus = false;
                }

            }, 10);
        }
    }

    //当是软件列表和专题时对焦点进行监听
    class NewOnFocusChangeListener implements View.OnFocusChangeListener {
        View.OnFocusChangeListener oldOnFocusChangeListener;
        Context context;

        public NewOnFocusChangeListener(Context context, View.OnFocusChangeListener oldOnFocusChangeListener) {
            this.oldOnFocusChangeListener = oldOnFocusChangeListener;
            this.context = context;

        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            oldOnFocusChangeListener.onFocusChange(view, hasFocus);
            View itemView = view.findViewById(R.id.sd_topic_list_icon_bg);
            MarqueeTextView tvName = (MarqueeTextView) view.findViewById(R.id.tv_topic_list_name);
            if (tvName != null) {
                tvName.setMarquee(hasFocus);
            }
            if (hasFocus) {
                if (custNineDrawable == null) {
                    custNineDrawable = new CustNineDrawable(TopicListActivity.this);
                }
                itemView.setBackgroundDrawable(custNineDrawable);
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }
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

    CustNineDrawable custNineDrawable;

    @Override
    public boolean onKeyBottomDown(int page, int pageCount, RectF rect) {
        if (page < TopicListActivity.this.pageCount - 1) {
            if (rect != null) {
                for (int i = 0; i < verticalViewPager.getChildCount(); i++) {
                    View child = verticalViewPager.getChildAt(i);
                    Object tag = child.getTag();
                    if (tag != null
                            && (Integer) tag == (page + 1)) {
                        ((HorizontalLayout) child)
                                .requestFocusByRect(rect, 1);
                    }
                }
            }
            verticalViewPager.setCurrentItem(page + 1);
        }
        return true;
    }

    @Override
    public boolean onKeyTopUp(int page, RectF rect) {
        if (page > 0) {
            if (rect != null) {
                for (int i = 0; i < verticalViewPager.getChildCount(); i++) {
                    View child = verticalViewPager.getChildAt(i);
                    Object tag = child.getTag();
                    if (tag != null
                            && (Integer) tag == (page - 1)) {
                        ((HorizontalLayout) child)
                                .requestFocusByRect(rect, 0);
                    }
                }
            }
            verticalViewPager.setCurrentItem(page - 1);
        }
        return true;
    }

    @Override
    public boolean onKeyLeftEnd(int page) {
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
        int pageCount = verticalViewPager.getAdapter().getCount();
        if (position < pageCount - 1) {
            ivMore.setVisibility(View.VISIBLE);
        } else {
            ivMore.setVisibility(View.INVISIBLE);
        }
        tvPageNumber.setText((position + 1) + "/" + verticalViewPager.getAdapter().getCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void addInstantiateItem(final int position, final HorizontalLayout horizontalLayout) {
        horizontalLayout.setOnBorderListener(this);
        List<Topic> topics;
        if ((topics = getCache(String.valueOf(position + 1))) != null) {
            hideLoading();
            initGridPage(topics, horizontalLayout, position);
            return;
        }

        ModeLevelAmsMenu.queryTopicList(this, String.valueOf(position + 1), String.valueOf(PAGE_SIZE), sortType, new ModeUserErrorCode<TopicList>() {
            @Override
            public void onJsonSuccess(TopicList topicList) {
                if (topicList != null) {
                    List<Topic> topics = topicList.getTopicList();
                    hideLoading();
                    hashMap.put(String.valueOf(position + 1), topics);
                    Runnable waitFor = new Runnable() {
                        public void run() {
                            while (verticalViewPager.mScrollState != VerticalViewPager.SCROLL_STATE_IDLE) {
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
            public void onRequestFail(int code, Throwable e) {
            }
        });


    }

    Handler myHandler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            List<Topic> topics;
            if ((topics = getCache(String.valueOf(msg.arg1 + 1))) != null) {
                initGridPage(topics, (HorizontalLayout) msg.obj, msg.arg1);
            }
        }
    };

    private List<Topic> getCache(String key) {
        if (hashMap.containsKey(key)) {
            return hashMap.get(key);
        }
        return null;
    }

    @Override
    protected void onReTryClicked() {
        setData();
    }
}
