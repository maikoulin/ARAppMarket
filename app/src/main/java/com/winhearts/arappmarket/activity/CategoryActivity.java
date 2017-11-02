package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.SoftwareTypeInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.SoftwaresByTypeEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.adapter.CategoryAdapter;
import com.winhearts.arappmarket.utils.common.ToastUtils;
import com.winhearts.arappmarket.view.BorderRecyclerView;
import com.winhearts.arappmarket.view.MyAppGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmh on 2017/10/24.
 */

public class CategoryActivity extends BaseActivity implements BorderRecyclerView.BorderListener {
    private final String TAG = "CategoryActivity";
    private Context mContext;
    private TextView tvCurrentLeftItem = null;
    private Runnable mRunnable;
    private Handler mMainThreadHandler;
    private List<MenuItem> menuItemList;
    private LinearLayout llLeftMenu;
    private BorderRecyclerView recyclerView;
    private GridLayoutManager mLayoutManager;
    private CategoryAdapter categoryAdatpter;
    private List<SoftwareInfo> softwareInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        MenuItem item = (MenuItem) bundle.getSerializable("message");
        categoryAdatpter = new CategoryAdapter(mContext, softwareInfos);
        initView();
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        menuItemList = item.getChild();
        if (!ContainerUtil.isEmptyOrNull(menuItemList)) {
            int size = menuItemList.size();
            for (int i = 0; i < size; i++) {
                TextView tvLestItem = createLeftMenuItem(menuItemList.get(i));
                llLeftMenu.addView(tvLestItem);
            }
        } else {
            ToastUtils.show(mContext, "没有分类数据");
            return;
        }

        getSoftwareList(item.getChild().get(0));
    }

    private void initView() {
        addLoadAndErrorView((ViewGroup) findViewById(R.id.rl_category_content_wrap));
        mLayoutManager = new MyAppGridLayoutManager(this, 3);
        llLeftMenu = (LinearLayout) this.findViewById(R.id.ll_category_left_menu);
        recyclerView = (BorderRecyclerView) this.findViewById(R.id.rv_category_content);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(categoryAdatpter);
        recyclerView.setBorderListener(this);
        recyclerView.setClipChildren(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 创建侧边栏菜单
     *
     * @param item
     * @return
     */
    private TextView createLeftMenuItem(MenuItem item) {
        TextView menuItem = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        lp.topMargin = 10;
        menuItem.setLayoutParams(lp);
        menuItem.setBackgroundResource(R.drawable.category_left_menu_selector);
        menuItem.setTextColor(getResources().getColor(R.color.white));
        menuItem.setSingleLine();
        menuItem.setFocusable(true);
        menuItem.setTag(item);
        menuItem.setText(item.getName());
        menuItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtil.dip2px(mContext, 36));
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
            recyclerView.setVisibility(View.INVISIBLE);
            if (mRunnable != null) {
                mMainThreadHandler.removeCallbacks(mRunnable);
            }
            final MenuItem menuItem = (MenuItem) v.getTag();

            mRunnable = new Runnable() {
                @Override
                public void run() {
                    VolleyQueueController.getInstance().cancelAll(TAG);
                    getSoftwareList(menuItem);
                }
            };

            mMainThreadHandler.postDelayed(mRunnable, 500);
        }
        tvCurrentLeftItem = (TextView) v;
    }


    private void getSoftwareList(MenuItem item) {
        final SoftwareTypeInfo softwareTypeInfo = item.getSoftwareTypeInfo();
        SoftwaresByTypeEntity softwaresByTypeEntity = new SoftwaresByTypeEntity();
        softwaresByTypeEntity.setPageNo("1");
        softwaresByTypeEntity.setPageSize("10");
        softwaresByTypeEntity.setFirstTypeCode(softwareTypeInfo.getRootTypeCode());
        softwaresByTypeEntity.setChildTypeCode(softwareTypeInfo.getSubTypeCode());
        softwaresByTypeEntity.setDeviceType(softwareTypeInfo.getHandlerType());
        softwaresByTypeEntity.setOrderType(softwareTypeInfo.getOrderType());


        ModeLevelAmsMenu.querySoftwaresByType(mContext, TAG, softwaresByTypeEntity, new ModeUserErrorCode<Softwares>() {
            @Override
            public void onJsonSuccess(Softwares softwares) {
                hideLoading();
                softwareInfos.clear();
                softwareInfos.addAll(softwares.getSoftwares());
                categoryAdatpter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                showOtherError(e.getMessage());
            }
        });
    }

    @Override
    public boolean onKeyBottomDown() {
        return true;
    }

    @Override
    public boolean onKeyTopUp() {
        return true;
    }

    @Override
    public boolean onKeyLeftEnd() {
        return tvCurrentLeftItem.requestFocus();
    }

    @Override
    public boolean onKeyRightEnd() {
        return true;
    }
}
