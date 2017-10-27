package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.db.BasicDataInfo;
import com.winhearts.arappmarket.download.manage.ManagerReceiver;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.utils.adapter.AppDetailEvaluateAdapter;
import com.winhearts.arappmarket.utils.adapter.DefaultPullToRefreshAdapter;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.DownRecordInfo;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.EvaluateEntity;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.modellevel.ModeLevelAms;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.modellevel.ModelevelEvaluate;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.DisplayConfig;
import com.winhearts.arappmarket.utils.MyBasePostprocessor;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.AppDetailMessageView;
import com.winhearts.arappmarket.view.AppDetailSynopsisDialog;
import com.winhearts.arappmarket.view.EvaluateListDialog;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.HorizontalScrollPageView;
import com.winhearts.arappmarket.view.MarqueeTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * 应用详情页
 * Created by lmh on 2016/4/27.
 */
public class AppDetailActivity extends BaseActivity implements View.OnClickListener, HorizontalLayout.OnBorderListener
        , AppDetailEvaluateAdapter.onItemClickListener<EvaluateEntity.EvaluateContent> {

    private final static String TAG = "AppDetailActivity";
    private LinearLayoutManager evaluateLayoutManager;

    private DownloadPath downloadPath;
    boolean isBackMain = false;
    private String packageName;
    private RecyclerView rvEvaluate;
    private AppDetailMessageView appDetailMessageView;

    private DefaultPullToRefreshAdapter mDefaultPullToRefreshAdapter;
    private TextView tvMoreEvaluate, tvMyEvaluate;
    private TextView tvSynopsis;
    private Context context;

    private HorizontalLayout horizontalLayout;
    private HorizontalScrollPageView appContentView;

    private List<EvaluateEntity.EvaluateContent> comments = new ArrayList<>();

    private AppDetailEvaluateAdapter appDetailEvaluateAdapter = null;
    private ArrayList<SoftwareInfo> mApps = new ArrayList<>();
    private LinearLayout llEvaluate;
    private RelativeLayout rlRecommend;
    private DownRecordInfo record;
    private SoftwareInfo softwareInfo;
    private ManagerReceiver managerReceiver;
    private SimpleDraweeView simpleDraweeViewBg;
    private Subscription subscription;
    private EvaluateEntity.EvaluateContent evaluateContent;
    private View mClickView = null;

    private View mEvalusteHint;
    private boolean isShowHint;
    private View mContentView;
    private String totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        context = this;
        isShowHint = PrefNormalUtils.getBoolean(PrefNormalUtils.EVALUATE_HINT, true);
        managerReceiver = new ManagerReceiver(this);
        managerReceiver.register();

        evaluateLayoutManager = new LinearLayoutManager(this);
        evaluateLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDefaultPullToRefreshAdapter = new DefaultPullToRefreshAdapter(this, mApps);
        mDefaultPullToRefreshAdapter.setIsShowDownload(true);
        downloadPath = (DownloadPath) getIntent().getSerializableExtra("downloadPath");
        Uri uri = getIntent().getData();
        if (uri != null) {
            packageName = uri.getQueryParameter("packageName");
            isBackMain = uri.getBooleanQueryParameter("isBackMain", false);
        } else {
            isBackMain = getIntent().getBooleanExtra("isBackMain", false);
            packageName = getIntent().getStringExtra("packageName");
        }
        appDetailEvaluateAdapter = new AppDetailEvaluateAdapter(comments, false);
        appDetailEvaluateAdapter.setOnItemClickListener(this);
        initView();
        initEvent();
        initDate();
    }

    private void recordBrowse() {
        Gson gson = new Gson();
        List<String> records;
        String recentBrowse = PrefNormalUtils.getString(this, PrefNormalUtils.RECENT_BROWSE_APP, null);
        if (TextUtils.isEmpty(recentBrowse)) {
            records = new ArrayList<>();
            records.add(packageName);
        } else {
            records = gson.fromJson(recentBrowse, new TypeToken<ArrayList<String>>() {
            }.getType());
            if (recentBrowse.contains(packageName)) {
                records.remove(packageName);
                records.add(packageName);
            } else {
                records.add(packageName);
                int size = records.size();
                if (size > 10) {
                    records.remove(0);
                }
            }
        }
        PrefNormalUtils.putString(this, PrefNormalUtils.RECENT_BROWSE_APP, gson.toJson(records));

    }

    private void setDraweeHierarchy(SimpleDraweeView mSimpleDraweeView) {
        GenericDraweeHierarchy hierarchy;
        //后续查证是不是每个simpleDraweeView 都有一个默认的Hierarchy
        if (mSimpleDraweeView.hasHierarchy()) {
            hierarchy = mSimpleDraweeView.getHierarchy();
        } else {
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(VpnStoreApplication.app.getResources());
            hierarchy = builder.build();
            mSimpleDraweeView.setHierarchy(hierarchy);
        }
        hierarchy.setPlaceholderImage(R.drawable.background);
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);

    }

    private void setBlurBitmap(SimpleDraweeView mSimpleDraweeView, Uri uri) {
        MyBasePostprocessor myBasePostprocessor = new MyBasePostprocessor();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(myBasePostprocessor)
                .build();

        PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(mSimpleDraweeView.getController())
                        // other setters as you need
                        .build();
        mSimpleDraweeView.setController(controller);
    }


    private void updateView() {
        if (appDetailMessageView.getPresenter() != null) {
            appDetailMessageView.getPresenter().destroy();
            appDetailMessageView.setPresenter(null);
        }
        initDate();
    }

    @Override
    protected void onDestroy() {
        managerReceiver.unregister();
        downloadPath = null;
        if (appDetailMessageView.getPresenter() != null) {
            appDetailMessageView.getPresenter().destroy();
        }
        unSubscribeEvent();
        super.onDestroy();
    }

    private void initView() {
        addLoadAndErrorView((ViewGroup) findViewById(R.id.frlt_app_detail));
        mContentView = this.findViewById(R.id.ll_app_detail_content);
        mEvalusteHint = this.findViewById(R.id.tv_evaluate_hint);
        appContentView = (HorizontalScrollPageView) this.findViewById(R.id.hsv_app_detail_content);
        appContentView.setFocusable(false);
        appContentView.setVisibility(View.INVISIBLE);
        appDetailMessageView = (AppDetailMessageView) this.findViewById(R.id.message_view);
        rvEvaluate = (RecyclerView) findViewById(R.id.rv_app_detail_evaluate);
        rvEvaluate.setFocusable(false);
        rvEvaluate.setLayoutManager(evaluateLayoutManager);
        rvEvaluate.setHasFixedSize(true);
        rvEvaluate.setAdapter(appDetailEvaluateAdapter);
        simpleDraweeViewBg = (SimpleDraweeView) findViewById(R.id.simpleDraweeView_bg);
        setDraweeHierarchy(simpleDraweeViewBg);
        rlRecommend = (RelativeLayout) this.findViewById(R.id.rl_app_detail_recommend);
        llEvaluate = (LinearLayout) this.findViewById(R.id.ll_app_detail_evaluate);

        tvSynopsis = (TextView) this.findViewById(R.id.tv_app_detail_more_synopsis);
        tvSynopsis.setOnClickListener(this);
        tvMoreEvaluate = (TextView) this.findViewById(R.id.tv_app_detail_more_evaluate);
        tvMyEvaluate = (TextView) this.findViewById(R.id.tv_app_detail_my_evaluate);
        tvMyEvaluate.setOnClickListener(this);
        tvMoreEvaluate.setOnClickListener(this);

        horizontalLayout = (HorizontalLayout) this.findViewById(R.id.vl_app_detail_other_download);
        horizontalLayout.setOnBorderListener(this);

    }

    private void initDate() {
        recordBrowse();
        rlRecommend.setVisibility(View.GONE);
        llEvaluate.setVisibility(View.GONE);
        mContentView.setVisibility(View.INVISIBLE);
        record = null;
        SQLiteDatabase mSQLiteDatabase = VpnStoreApplication.app.getSQLDatabase();
        VpnStoreApplication.recordList = BasicDataInfo.getAllRecord(mSQLiteDatabase);
        for (DownRecordInfo info : VpnStoreApplication.recordList) {
            if (info.getPackageName().equals(packageName)) {
                record = info;
            }
        }
        DisplayConfig.init(this);
        appDetailMessageView.initDate(downloadPath, record);
        getEvaluateContent(true);
        querySoftwareInfo();
    }

    public void querySoftwareInfo() {
        showLoading();
        ModeLevelAms.querySoftwareInfo(this, packageName, new ModeUserErrorCode<SoftwareInfo>() {
            @Override
            public void onJsonSuccess(SoftwareInfo response) {
                if (response != null) {
                    softwareInfo = response;
                    BasicDataInfo.updateAppInfo(VpnStoreApplication.app.getSQLDatabase(),
                            response.getPackageName(), response.getFirstTypeName(), response.getChildTypeName());
                    mContentView.setVisibility(View.VISIBLE);
                    appDetailMessageView.setMessage(response);
                    String previews = response.getPreviews();
                    if (!TextUtils.isEmpty(previews)) {
                        final String[] list = response.getPreviews().split(",");
                        setBlurBitmap(simpleDraweeViewBg, Uri.parse(list[0]));
                    }
                    hideLoading();
                    appContentView.setVisibility(View.VISIBLE);
                } else {
                    showRequestNoData("应用已下架，去看看别的应用吧~");
                }
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                if (code == 0) {
                    showRequestNoData("应用已下架，去看看别的应用吧~");
                } else {
                    showError(code);
                }
            }
        });
    }


    private void queryRecommendSoftwores() {

        ModeLevelAms.queryRecommendSoftwares(this, packageName, new ModeUserErrorCode<Softwares>() {
            @Override
            public void onJsonSuccess(Softwares softwares) {

                if (softwares != null) {
                    List<SoftwareInfo> softwareInfos = softwares.getSoftwares();
                    if (ContainerUtil.isEmptyOrNull(softwareInfos)) {
                        rlRecommend.setVisibility(View.GONE);
                    } else {
                        int[][] positionArray = Constant.position_3x2;
                        rlRecommend.setVisibility(View.VISIBLE);
                        mApps.clear();
                        horizontalLayout.clearItems();
                        horizontalLayout.setSize(ScreenUtil.dip2px(AppDetailActivity.this, 666),
                                ScreenUtil.getScreenHeight(AppDetailActivity.this) * 640 / 947, 3, 2);
                        int size = softwareInfos.size() > 6 ? 6 : softwareInfos.size();
                        mApps.addAll(softwareInfos);
                        for (int i = 0; i < size; i++) {
                            View view = mDefaultPullToRefreshAdapter.getView(i, null, horizontalLayout);
                            view.setTag(softwareInfos.get(i));
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SoftwareInfo softwareInfo = (SoftwareInfo) v.getTag();
                                    packageName = softwareInfo.getPackageName();
                                    downloadPath = (DownloadPath) getIntent().getSerializableExtra("downloadPath");
                                    if (downloadPath != null) {
                                        downloadPath.setMenuId("-3");
                                    }
                                    appContentView.smoothScrollTo(0, 0, 200);
                                    updateView();
                                }
                            });
                            horizontalLayout.addItemView(view, 0, positionArray[i][0], positionArray[i][1], positionArray[i][2],
                                    positionArray[i][3], 0, size);
                            View.OnFocusChangeListener oldOnFocusChangeListener = view.getOnFocusChangeListener();
                            view.setOnFocusChangeListener(new NewOnFocusChangeListener(AppDetailActivity.this, oldOnFocusChangeListener));
                        }

                    }
                } else {
                    rlRecommend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                rlRecommend.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onItemClick(View view, EvaluateEntity.EvaluateContent evaluateContent, int position) {
        this.evaluateContent = evaluateContent;
        mClickView = view;
        setEvaluateHintVisible();
    }


    private void setEvaluateHintVisible() {
        if (isShowHint) {
            isShowHint = false;
            mEvalusteHint.setVisibility(View.GONE);
            PrefNormalUtils.putBoolean(PrefNormalUtils.EVALUATE_HINT, false);
        }
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
            oldOnFocusChangeListener.onFocusChange(view, hasFocus);
            MarqueeTextView tvName = (MarqueeTextView) view.findViewById(R.id.tv_app_card_name);
            if (tvName != null) {
                tvName.setMarquee(hasFocus);
            }
        }
    }

    //获取评价内容
    private void getEvaluateContent(final boolean isFirst) {
        Map<String, String> map = new ArrayMap<>();
        map.put("packageName", packageName);
        ModelevelEvaluate.getEvaluateList(this, map, new ModeUserErrorCode<EvaluateEntity>() {
            @Override
            public void onJsonSuccess(EvaluateEntity response) {
//                if (isFirst) {
//                    queryRecommendSoftwores();
//                }
                if (mEvaluateListDialog != null) {
                    mEvaluateListDialog.setRequesting(false);
                }
                llEvaluate.setVisibility(View.VISIBLE);
                if (response.getComments().size() > 3) {
                    tvMoreEvaluate.setVisibility(View.VISIBLE);
                    setLayoutParams(tvMyEvaluate, true);
                } else {
                    tvMoreEvaluate.setVisibility(View.GONE);
                    setLayoutParams(tvMyEvaluate, false);
                }
                if (!ContainerUtil.isEmpty(response.getComments()) && isShowHint) {
                    mEvalusteHint.setVisibility(View.VISIBLE);
                }
                totalCount = response.getTotalCount();
                comments.clear();
                comments.addAll(response.getComments());
                appDetailEvaluateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                if (isFirst) {
                    llEvaluate.setVisibility(View.GONE);
//                    queryRecommendSoftwores();
                }
            }
        });


    }


    private void setLayoutParams(TextView view, boolean isMore) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (isMore) {
            layoutParams.width = ScreenUtil.dip2px(this, 133);
        } else {
            layoutParams.width = ScreenUtil.dip2px(this, 286);
        }
        view.setLayoutParams(layoutParams);
    }


    private EvaluateListDialog mEvaluateListDialog;
    private AppDetailSynopsisDialog mSynopsisDialog;
    private final int SHOW_EVALUATE = 1;
    private final int SHOW_SYNOPSIS = 2;

    private void setTypeDialog(int showType) {
        if (showType == SHOW_EVALUATE) {
            if (mEvaluateListDialog == null) {
                mEvaluateListDialog = new EvaluateListDialog(this);
            }
            mEvaluateListDialog.show();
            mEvaluateListDialog.setData(comments, this);
            mEvaluateListDialog.setRequestMore(packageName, totalCount);
        } else if (showType == SHOW_SYNOPSIS) {
            if (mSynopsisDialog == null) {
                mSynopsisDialog = new AppDetailSynopsisDialog(this);
            }
            mSynopsisDialog.show();
            mSynopsisDialog.setData(softwareInfo);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            tvSynopsis.setFocusable(true);
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                if ((getCurrentFocus() != null) &&
                        ((getCurrentFocus() == tvMyEvaluate) || (getCurrentFocus().getParent() == rvEvaluate))) {
                    appContentView.smoothScrollTo(0, 0, 500);
                }
                if ((getCurrentFocus() != null) && getCurrentFocus().getParent() == appDetailMessageView.getPictureView()) {
                    tvSynopsis.setFocusable(false);
                }
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (getCurrentFocus() != null && getCurrentFocus() == rvEvaluate.getChildAt(rvEvaluate.getChildCount() - 1)) {
                    tvMyEvaluate.requestFocus();
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (getCurrentFocus() != null && getCurrentFocus() == tvMyEvaluate) {
                    rvEvaluate.getChildAt(rvEvaluate.getChildCount() - 1).requestFocus();
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (getCurrentFocus() != null && getCurrentFocus().getParent() == appDetailMessageView.getPictureView()) {
                    View view = getCurrentFocus().focusSearch(View.FOCUS_RIGHT);
                    if (view != null) {
                        int[] position = new int[2];
                        view.getLocationInWindow(position);
                        int dx = appContentView.getChildAt(0).getMeasuredWidth() - position[0] - ScreenUtil.dip2px(this, 213);
                        appContentView.smoothScrollTo(dx, 0, 500);
                    }
                }
            }
        }


        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        switch (viewId) {
            case R.id.tv_app_detail_my_evaluate:
                setEvaluateHintVisible();
            case R.id.tv_app_detail_more_evaluate:
                setEvaluateHintVisible();
                setTypeDialog(SHOW_EVALUATE);
                break;
            case R.id.tv_app_detail_more_synopsis:
                setTypeDialog(SHOW_SYNOPSIS);
                break;
        }

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
        if (llEvaluate.getVisibility() == View.VISIBLE) {
            if (tvMoreEvaluate.getVisibility() == View.VISIBLE) {
                tvMoreEvaluate.requestFocus();
            } else {
                tvMyEvaluate.requestFocus();
            }
            return true;
        }
        appContentView.smoothScrollTo(0, 0, 500);
        return false;
    }

    @Override
    public boolean onKeyRightEnd(int page) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isBackMain) {
            Intent intent = new Intent(AppDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onReTryClicked() {
        appContentView.setVisibility(View.INVISIBLE);
        querySoftwareInfo();
    }

    private void initEvent() {
        //取消点击下载后焦点移动到相关应用功能
//        subscription = RxBus.getDefault()
//                .toObservable(DownRecordInfo.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<DownRecordInfo>() {
//                    @Override
//                    public void call(DownRecordInfo downRecordInfo) {
//                        int state = downRecordInfo.getState();
//                        String packageName = downRecordInfo.getPackageName();
//                        if (state == DownloadManager.CREATE_TASK && packageName.equals(softwareInfo.getPackageName())) {
//                            if (horizontalLayout.getItemCount() > 0) {
//                                horizontalLayout.getItemView(0).requestFocus();
//                                int dx;
//                                if (comments.size() == 0) {
//                                    dx = (int) (appContentView.getChildAt(0).getMeasuredWidth() -
//                                            ((ViewGroup) horizontalLayout.getParent()).getX() -
//                                            ScreenUtil.dip2px(context, 213));
//                                } else {
//                                    dx = (int) (appContentView.getChildAt(0).getMeasuredWidth() -
//                                            llEvaluate.getX() -
//                                            ScreenUtil.dip2px(context, 213));
//                                }
//                                appContentView.smoothScrollTo(dx, 0, 500);
//                            }
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        LoggerUtil.e("throwable", throwable.getMessage());
//                    }
//                });
    }

    private void unSubscribeEvent() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }
}
