package com.winhearts.arappmarket.fragment;

import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.Screen;
import com.winhearts.arappmarket.network.VolleyQueueController;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.HorizontalLayout;
import com.winhearts.arappmarket.view.SelfScreenUtil;

import java.util.List;

/**
 * 主页fragment
 * Created by lmh on 2016/3/11.
 */
public class MainSelfFragment extends MovableFragment {
    private boolean DEBUG = false;
    private static final String TAG = "SecondLevelMenuFragment";
    private Context mContext;
    private HorizontalLayout horizontalLayout;
    private float widthScale;
    private MenuItem mParentMenu;
    private MenuItem mCurrentMenu;
    private int mScreenWidth;
    private int mScreenHeight;
    private String layoutId;
    private boolean mFlag = false; //用于是否是第一次显示并且非tab切换

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = this.getActivity();
        mScreenWidth = ScreenUtil.getScreenWidth(mContext);
        mScreenHeight = ScreenUtil.getScreenHeight(mContext);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_self, container, false);
        }
        MenuItem item = (MenuItem) this.getArguments().getSerializable("message");
        layoutId = PrefNormalUtils.getString(mContext, PrefNormalUtils.LAYOUT_ID, "");
        mParentMenu = item;
        initView();
        return rootView;
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

    }

    @Override
    public void onDestroy() {
        VolleyQueueController.getInstance().cancelAll(TAG);
        super.onDestroy();
    }

    public void initView() {
        widthScale = (float) 15 / (float) 17;
        mCurrentMenu = mParentMenu;
        initContent(mParentMenu);
    }

    /**
     * 填充界面
     *
     * @param item
     */
    public void initContent(MenuItem item) {
        final List<Screen> screens = item.getScreens();
        initSelfScreens(screens);
    }


    /**
     * @param screens 自定义界面中显示的个数
     */
    public void initSelfScreens(final List<Screen> screens) {

        if (screens == null) {
            return;
        }
        final int pageCount = 1;//screens.size();
        horizontalLayout = (HorizontalLayout) rootView.findViewById(R.id.metrolayout);
        horizontalLayout.setIsAutoScrollPage(true);
        horizontalLayout.setVisibility(View.VISIBLE);
        horizontalLayout.setLeftPadding(50);
        final Screen screen = screens.get(0);
        int viewWidth = (int) (mScreenWidth * widthScale);
        int viewHeight = mScreenHeight * 9 / 12;
        horizontalLayout.setSize(viewWidth, viewHeight, screen.getRow(), screen.getCol());
        horizontalLayout.setPageCount(pageCount);
        if (isAdded() && mContext != null) {
            SelfScreenUtil.initScreenByElements(mContext, screens, viewWidth, viewHeight, horizontalLayout, mCurrentMenu, layoutId);
        }
        horizontalLayout.setOnBorderListener(mOnBorderListener);
        horizontalLayout.requestFirstChildFocus();
    }

    //对布局上下左右进行监听
    HorizontalLayout.OnBorderListener mOnBorderListener = new HorizontalLayout.OnBorderListener() {
        @Override
        public boolean onKeyTopUp(int page, RectF rect) {
            HorizontalLayout child = horizontalLayout;
            if (child != null) {
                child.setScaleUpDown();
            }
            return true;
        }

        @Override
        public boolean onKeyBottomDown(int page, int pageCount, RectF rect) {
            return true;
        }

        @Override
        public boolean onKeyLeftEnd(int page) {
            HorizontalLayout child = horizontalLayout;
            if (child != null) {
                child.setScaleUpDown();
            }
            return true;
        }

        @Override
        public boolean onKeyRightEnd(int page) {
            HorizontalLayout child = horizontalLayout;
            if (child != null) {
                child.setScaleUpDown();
            }
            return true;
        }
    };

    @Override
    public void focusTab2Down() {
        if (horizontalLayout != null) {
            horizontalLayout.focusFirstChildView();
        }
    }

    @Override
    public void focusMoveToLeft() {
        if (mFlag) {
            mFlag = false;
            return;
        }
        if (horizontalLayout != null) {
            horizontalLayout.focusMoveToLeft();
        }
    }

    @Override
    public void clearCurrentScreen() {
        if (horizontalLayout != null) {
            horizontalLayout.clearCurrentScreen();
        }
    }

    @Override
    public void focusMoveToRight() {
        if (mFlag) {
            mFlag = false;
            return;
        }
        if (horizontalLayout != null) {
            horizontalLayout.focusMoveToRight();
        }

    }

    @Override
    public void onScrollFirstPositionNoFocus() {
        mFlag = false;
        if (horizontalLayout != null) {
            horizontalLayout.scrollFirstPosition();
        }

    }

}
