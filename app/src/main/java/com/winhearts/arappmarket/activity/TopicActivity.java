package com.winhearts.arappmarket.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.mvp.p.AppDownloadPresenter;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.modellevel.ModeLevelAms;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.ViewUtils;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.TopicCardView;

import java.util.List;

/**
 * 专题详情界面
 */

public class TopicActivity extends BaseActivity implements HorizontalLayout.OnBorderListener {
    final static private String TAG = "TopicActivity";
    Topic topic = null;
    Context mContext;
    SimpleDraweeView topicImage;
    private HorizontalLayout horizontalLayout;
    private boolean isBackMain = false;
    private DownloadPath downloadPath;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        Intent intent = this.getIntent();
        isBackMain = intent.getBooleanExtra("isBackMain", false);
        mContext = this;
        topic = (Topic) intent.getSerializableExtra("topic");
        downloadPath = (DownloadPath) intent.getSerializableExtra("downloadPath");
        initView();
        querysoftwareDetailByTopicCode(this);
        queryTopicSoftWareList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initView() {
        addLoadAndErrorView((ViewGroup) findViewById(R.id.rl_topic));
        topicImage = (SimpleDraweeView) findViewById(R.id.topic_pic);
        topicImage.setHierarchy(CommonHierarchy.setHierarchyDrawable(topicImage, R.drawable.background));
        horizontalLayout = (HorizontalLayout) this.findViewById(R.id.metrolayout);
        horizontalLayout.setFocusDrawable(R.drawable.comm_bg_card_focus);
        horizontalLayout.setIsAutoScrollPage(false);
        horizontalLayout.setLeftPadding(30);
        horizontalLayout.setOnBorderListener(this);
        int viewWidth = ScreenUtil.getScreenWidth(this);
        int viewHeight = ScreenUtil.getScreenHeight(this) * 6 / 13;
        horizontalLayout.setSize(viewWidth, viewHeight, 1, 6);
    }

    private void queryTopicSoftWareList() {
        showLoading();
        ModeLevelAmsMenu.queryTopocSoftWareList(mContext, TAG, 1, 30, topic,
                new ModeUserErrorCode<Softwares>() {

                    @Override
                    public void onRequestFail(int errorCode, Throwable e) {
                        showError(errorCode);
                    }

                    @Override
                    public void onJsonSuccess(Softwares t) {
                        if (t != null) {
                            hideLoading();
                            setMessage(t);
                        } else {
                            showRequestNoData("没有找到相关应用");
                        }
                    }
                });
    }

    private void setMessage(Softwares topicSoftwareList) {
        List<SoftwareInfo> softwareInfos = topicSoftwareList.getSoftwares();
        if (ContainerUtil.isEmptyOrNull(softwareInfos)) {
            showRequestNoData("没有找到相关应用");
            return;
        }
        int size = softwareInfos.size();

        for (int i = 0; i < size; i++) {
            SoftwareInfo softwareInfo = softwareInfos.get(i);
            TopicCardView topicCardView = new TopicCardView(TopicActivity.this).bindData(softwareInfo);
            topicCardView.setPresenterRelate(softwareInfo);
            topicCardView.setTag(softwareInfo);
            horizontalLayout.addSimpleItemView(topicCardView, i, 0, i + 1, 1, ScreenUtil.dip2px(TopicActivity.this, 10));
            topicCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((TopicCardView) v).downMenu()) {
                        return;
                    }
                    Intent intent = new Intent(TopicActivity.this, AppDetailActivity.class);
                    SoftwareInfo softwareInfo = (SoftwareInfo) v.getTag();
                    intent.putExtra("packageName", softwareInfo.getPackageName());
                    intent.putExtra("downloadPath", downloadPath);
                    startActivity(intent);

                }
            });
            View.OnFocusChangeListener oldOnFocusChangeListener = topicCardView.getOnFocusChangeListener();
            topicCardView.setOnFocusChangeListener(new NewOnFocusChangeListener(TopicActivity.this, oldOnFocusChangeListener));
        }
        horizontalLayout.getChildAt(0).requestFocus();
    }

    @Override
    public boolean onKeyBottomDown(int page, int pageCount, RectF rect) {
        return false;
    }

    @Override
    public boolean onKeyTopUp(int page, RectF rect) {
        return false;
    }

    @Override
    public boolean onKeyLeftEnd(int page) {
        return false;
    }

    @Override
    public boolean onKeyRightEnd(int page) {
        return false;
    }

    //获取焦点跑马灯
    private class NewOnFocusChangeListener implements View.OnFocusChangeListener {
        View.OnFocusChangeListener oldOnFocusChangeListener;
        Context context;

        NewOnFocusChangeListener(Context context, View.OnFocusChangeListener oldOnFocusChangeListener) {
            this.oldOnFocusChangeListener = oldOnFocusChangeListener;
            this.context = context;

        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            boolean isShowMenu = ((TopicCardView) view).getIsShowMenu();
            if (!isShowMenu) {
                oldOnFocusChangeListener.onFocusChange(view, hasFocus);
                ((TopicCardView) view).setMarquee(hasFocus);
            }
        }
    }

    private void querysoftwareDetailByTopicCode(final Context mContext) {
        ModeLevelAms.querysoftwareDetailByTopicCode(mContext, TAG, topic, new ModeUserErrorCode<Topic>() {
            @Override
            public void onJsonSuccess(Topic topic) {
                if (topic != null) {
                    String imageUrl = topic.getImageUrl();
                    topicImage.setImageURI(Uri.parse(imageUrl));
                }
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                LoggerUtil.d(TAG, code + e.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        int itemCount = horizontalLayout.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            TopicCardView topicCardView = (TopicCardView) horizontalLayout.getItemView(i);
            AppDownloadPresenter appDownloadPresenter = topicCardView.getPresenter();
            if (appDownloadPresenter != null) {
                appDownloadPresenter.destroy();
                topicCardView.setPresenter(null);
            }
        }
        ViewUtils.unbindDrawables(findViewById(R.id.main_tabs_container));
        VolleyQueueController.getInstance().cancelAll(mContext);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            int itemCount = horizontalLayout.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                TopicCardView topicCardView = (TopicCardView) horizontalLayout.getItemView(i);
                topicCardView.setPresenterRelate((SoftwareInfo) topicCardView.getTag());
            }
        }
    }

    @Override
    protected void onPause() {
        isFirst = false;
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isBackMain) {
            Intent intent = new Intent(TopicActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onReTryClicked() {
        querysoftwareDetailByTopicCode(TopicActivity.this);
        queryTopicSoftWareList();
    }
}
