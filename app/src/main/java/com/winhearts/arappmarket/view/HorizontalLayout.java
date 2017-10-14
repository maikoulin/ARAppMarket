package com.winhearts.arappmarket.view;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Scroller;


import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.logic.RecommendAppLogic;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.common.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * 横向布局
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HorizontalLayout extends FrameLayout implements
        View.OnFocusChangeListener {

    private static final boolean IS_DEBUG = true;
    private final static String TAG = HorizontalLayout.class.getSimpleName();
    private final static String TAG2 = TAG + "Scroll";
    public Scroller mScroller;
    private Boolean isAutoScrollPage = null;//null不滑  true翻页 false翻一个

    private Context mContext;
    public static int DIVIDE_SIZE = 15;
    private List<WeakReference<View>> mViewList = new ArrayList<>();
    private int leftPadding = 0;

    private View mLeftView;//其实这个就是第一个视图firstAddView  后续删除
    private View mRightView;
    private List<View> screenFirstView = new ArrayList<View>();//记录每个屏幕的第一个item
    private int currentScreen = 0; //当前的屏幕位置
    private int screenWidth;
    private int screenHeight;
    private int rowCount;
    private int colum;
    private OnBorderListener mOnBorderListener;
    private RectF mRect;
    private int mDirection;
    private boolean isDownFocus = false; //向下移动到最后一列时，焦点是否能触发onKeyBottomDown

    private int itemWidth = -1;
    private int itemHeight = -1;

    private int[] mPadding = new int[2];
    private int pageNow;
    private int scrollPageTime = 300;
    private int scrollNextViewTime = 200;
    public View lastFocusedView; //记录最后一次的位置，如果不手动清空，是要存在的。注意和mScrollerNextView 区分
    private View mScrollerNextView;//滚动的最后位置
    private volatile boolean isFromOther = false;
    private int mVisiableWidth = 0;
    Rect mVisibleRect = new Rect();

    public BlowUpUtil blowUpUtil;
    private View lastAddView;//最早一个添加到layout的视图， 因为用了bringChildToFront后，视图树会变化
    private View firstAddView;//最后一个添加到layout的视图， 因为用了bringChildToFront后，视图树会变化

    public HorizontalLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public HorizontalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    /**
     * @param width  容器长 宽现在设置是无效的了，从新测量了下。如果要设置在父布局设置
     * @param height 容器高
     * @param row    行数
     * @param colum  列数
     */
    public void setSize(int width, int height, int row, int colum) {

        screenWidth = width;
        screenHeight = height;
        this.rowCount = row;
        this.colum = colum;

        itemWidth = width / colum;
        itemHeight = height / row;
    }

    private void init() {
        this.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        clearItems();
        setClipChildren(false);//子空间的大小可以超过父控件
        setClipToPadding(false);
        mScroller = new Scroller(getContext());
        blowUpUtil = new BlowUpUtil(mContext);
        setIsAutoScrollPage(null);
    }

    public void setDownFocus(boolean isDownFocus) {
        this.isDownFocus = isDownFocus;
    }

    /**
     * 设置获取焦点的放大图片背景
     *
     * @param drawableId 图片ID
     */
    public void setFocusDrawable(int drawableId) {
        if (blowUpUtil != null) {
            blowUpUtil.setFocusDrawable(drawableId);
        }
    }

    public void setitemDistanceEnd(int distance) {
        if (blowUpUtil != null) {
            blowUpUtil.setItemDistanceEnd(distance);
        }
    }

    public int getItemCount() {
        return mViewList.size();
    }


    public View getItemView(int index) {
        if (index >= mViewList.size()) {
            return null;
        }
        return mViewList.get(index).get();
    }

    public View addItemView(View child, int celltype, int startX, int startY,
                            int endX, int endY, int page, int count) {
        return addItemView(child, celltype, startX, startY, endX, endY,
                DIVIDE_SIZE, page, count);
    }

    public void clearItems() {
        removeAllViews();
        mViewList.clear();
        mLeftView = null;
        mRightView = null;
    }

    int mPageCount = 1;

    public void setPageCount(int pageCount) {
        this.mPageCount = pageCount;
    }

    /******/
    public View addItemView(View child, int celltype, float left, float top,
                            float right, float bottom, int padding, int page, int count) {
        return addItemView(child, celltype, left, top, right, bottom, padding, padding, page, count);
    }

    public View addSimpleItemView(View view, float left, float top, float right, float bottom, int padding) {
        return addItemView(view, 0, left, top, right, bottom, padding, 1, 10);
    }

    public View addItemView(View child, int celltype, float left, float top,
                            float right, float bottom, int xPadding, int yPadding, int page, int count) {

        pageNow = page;
        if (mLeftView == null) {
            mLeftView = child;
        }

        //需把不同布局的坐标 转化为占用位置。 还有填充不满的情况。。。。
        RectF retct = new RectF(
                left / colum,
                top / rowCount,
                right / colum,
                bottom / rowCount);
        child.setTag(R.integer.tag_record_rect, retct);
        child.setTag(R.integer.tag_record_page, 0);
        if (top == 0) {
            mRightView = child;
        }
        if (screenFirstView.size() == 0) {
            screenFirstView.add(child);
        }
        mViewList.add(new WeakReference<View>(child));
        View result = child;
        float width = (right - left) * itemWidth;
        float height = (bottom - top) * itemHeight;
        float marginLeft = (left) * itemWidth;
        float marginTop = (top) * itemHeight;
        LayoutParams flp = new LayoutParams(((int) (width - xPadding)), (int) (height - yPadding));

        child.setFocusable(true);
        child.setOnFocusChangeListener(this);
        if (left > 0) {
            mPadding[0] += xPadding;
        }
        flp.leftMargin = (int) (this.getPaddingLeft() + marginLeft + xPadding);
        flp.topMargin = (int) (this.getPaddingTop() + marginTop + yPadding);
        flp.rightMargin = getPaddingRight();
        addViewDeal(child, flp);

        return result;
    }

    public View addItemView(View child, int celltype,
                            float left, float top, float right, float bottom,
                            int xPadding, int yPadding,
                            int page,
                            int colCount, int rowCount) {

        //让pageNow等于0 旨在说明该方法不支持以前的viewPager的上下翻页
        pageNow = 0;
        if (mLeftView == null) {
            mLeftView = child;
        }
        if (screenFirstView.size() == page - 1) {
            screenFirstView.add(child);
        }
        //需把不同布局的坐标 转化为占用位置。 还有填充不满的情况。。。。
        RectF retct = new RectF(
                left / colCount,
                top / rowCount,
                right / colCount,
                bottom / rowCount);
        child.setTag(R.integer.tag_record_rect, retct);
        child.setTag(R.integer.tag_record_page, page - 1);
        if (top == 0) {
            mRightView = child;
        }

        mViewList.add(new WeakReference<View>(child));
        float width = (right - left) * (screenWidth / colCount);
        float height = (bottom - top) * (screenHeight / rowCount);
        float marginLeft = (left + colCount * (page - 1)) * (screenWidth / colCount);
        float marginTop = (top) * (screenHeight / rowCount);
        LayoutParams flp = new LayoutParams(((int) (width - xPadding)), (int) (height - yPadding));

        child.setFocusable(true);
        child.setOnFocusChangeListener(this);
        if (left > 0) {
            mPadding[0] += xPadding;
        }
        flp.leftMargin = (int) (this.getPaddingLeft() + leftPadding + marginLeft + xPadding);
        flp.topMargin = (int) (this.getPaddingTop() + marginTop + yPadding);
        flp.rightMargin = getPaddingRight() + 50;
        addViewDeal(child, flp);

        return child;
    }

    private View recommendView = null;

    public void addRecommend(int screen) {
        recommendView = new RecommendAppLogic().getRecommendAppLogic(mContext, this, screen);
        if (recommendView != null) {
            LayoutParams flp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            measure(0, 0);
            flp.leftMargin = getMeasuredWidth() - ScreenUtil.dip2px(mContext, 42);
            addViewDeal(recommendView, flp);
        }

    }

    private void addViewDeal(View child, ViewGroup.LayoutParams flp) {
        addView(child, flp);
        lastAddView = child;
        if (firstAddView == null) {
            firstAddView = child;
        }
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction,
                                                  Rect previouslyFocusedRect) {
        isFromOther = true;
        if (mRect == null) {
            return true;
        } else {
            focusNearestView(mDirection);
            mRect = null;
            return true;
        }
    }


    /**
     * @param direction 0 向上
     *                  1 向下
     */
    public void focusNearestView(int direction) {

        if (direction == 0) {

            float minDistance = 10000;
            int recordIndex = 0;
            View requestFocusView = null;
            //	Collections.sort(list, mBottomComparator);
            for (int i = 0; i < this.getChildCount(); i++) {
                View child = this.getChildAt(i);
                RectF rect = (RectF) child.getTag(R.integer.tag_record_rect);
                {
                    //rect.bottom == row
                    if (!(rect.right <= mRect.left || rect.left >= mRect.right)) {
                        if (rect.bottom == 1) {
                            LogDebugUtil.i(TAG, "index:" + i);
                            requestFocusView = child;
                            break;
                        } else {
                            if (requestFocusView == null) {
                                requestFocusView = child;
                            }
                        }
                    } else {
                        float distance = Math.min(
                                Math.abs(rect.right - mRect.left),
                                Math.abs(rect.left - mRect.right));

                        if (distance < minDistance) {
                            minDistance = distance;
                            recordIndex = i;
                        }
                    }
                }
            }
            if (requestFocusView != null) {
                if (requestFocusView.getClass() == RankElementsView.class) {
                    RankElementsView rankElementsView = (RankElementsView) requestFocusView;
                    rankElementsView.focusMoveToDown();
                } else {
                    requestFocusView.requestFocus();
                }
            } else {
                //LogDebugUtil.i(IS_DEBUG, TAG, "requestFocusView is null");
                if (recordIndex >= 0) {
                    View child = this.getChildAt(recordIndex);
                    if (child != null) {
                        child.requestFocus();
                    }
                }
            }

        } else if (direction == 1) {
            float minDistance = 10000;
            int recordIndex = 0;
            View requestFocusView = null;
            for (int i = 0; i < this.getChildCount(); i++) {
                View child = this.getChildAt(i);
                RectF rect = (RectF) child.getTag(R.integer.tag_record_rect);
                {

                    if (!(rect.right <= mRect.left || rect.left >= mRect.right)) {
                        if (rect.top == 0) {
                            requestFocusView = child;
                            break;
                        } else {
                            if (requestFocusView == null) {
                                requestFocusView = child;
                            }
                        }
                    } else {
                        float distance = Math.min(
                                Math.abs(rect.right - mRect.left),
                                Math.abs(rect.left - mRect.right));
                        if (distance < minDistance) {
                            minDistance = distance;
                            recordIndex = i;
                        }
                    }
                }
            }
            if (requestFocusView != null) {
                requestFocusView.requestFocus();
            } else {
                if (recordIndex >= 0) {
                    View child = this.getChildAt(recordIndex);
                    if (child != null) {
                        child.requestFocus();
                    }
                }
            }
        }
    }

    public void requestLastChildFocus() {
        int index = getChildCount();
        if (index >= 0) {
            View child = this.getChildAt(index - 1);
            if (child != null) {
                child.requestFocus();
            }
        }
    }

    public void requestFirstChildFocus() {
        if (getChildCount() > 0) {
            View child = firstAddView;//.getChildAt(0);
            if (child != null) {
                child.requestFocus();
            }
        }
    }


    @SuppressLint("NewApi")
    public void onFocusChange(final View v, boolean hasFocus) {
        simulateScale(v, hasFocus);
    }

    private void playScaleAnimator(View v) {
        blowUpUtil.setScaleUp(v);
    }

    private void restoreScale(View v) {
        blowUpUtil.setScaleDown(v);
    }

    public void simulateScale(View v, boolean hasFocus) {
        if (hasFocus) {
            currentScreen = (Integer) v.getTag(R.integer.tag_record_page);
            if (isAutoScrollPage == null) {
                playScaleAnimator(v);
            } else if (isAutoScrollPage) {
                scrollNextPage(v);
            } else {
                scrollNextView(v);
            }


            lastFocusedView = v;

        } else {

            restoreScale(v);
        }
    }

    private boolean scrollNextView(final View view) {

        Rect visibleRect = new Rect();
        getGlobalVisibleRect(visibleRect);

        Rect focusVisibleRect = new Rect();
        view.getGlobalVisibleRect(focusVisibleRect);

        int[] focusL = new int[2];
        view.getLocationInWindow(focusL);

        Boolean isRight = null;

        //普通的view  没有跨的
        if (focusVisibleRect.left > visibleRect.left && focusVisibleRect.right < visibleRect.right) {
            LogDebugUtil.d(IS_DEBUG, "MetroCursorView_m", "middle");

            playScaleAnimator(view);


            return false;
        }

        //view 在可视范围右边  -->>
        if ((focusL[0] + view.getWidth()) >= visibleRect.right && focusL[0] > 0) {
            //最后一个view

            int focusViewRight = focusL[0] - visibleRect.right + view.getWidth();
            int distance = focusViewRight + blowUpUtil.getDistance(view);//放大部分的修正
            playScaleAnimator(view);
            if (view != lastAddView) {
                smoothScrollXBy(distance + blowUpUtil.getItemDistanceEnd(), view);
            } else {
                smoothScrollXBy(distance, view);
            }


        }
        //view 在可视范围左边  <<--
        else if ((focusL[0]) < visibleRect.left) {
            if (getChildAt(getChildCount() - 1) == view) {
                LogDebugUtil.d(IS_DEBUG, TAG2, "getChildAt(getChildCount() - 1) == view");
                return false;
            }

            int focusPRight = focusL[0];
            int distance = visibleRect.left - focusPRight + blowUpUtil.getDistance(view);//放大部分的修正;
            LogDebugUtil.d(IS_DEBUG, TAG2, "mScroller.getCurrX()= " + mScroller.getCurrX() + "  left -> right distance = " + -1 * distance);
            playScaleAnimator(view);
            if (view != firstAddView) {
                smoothScrollXBy(-1 * distance - blowUpUtil.getItemDistanceEnd(), view);
            } else {
                smoothScrollXBy(-1 * distance, view);
            }

        } else {
            playScaleAnimator(view);

            return false;
        }

        return true;
    }

    public Boolean getIsAutoScrollPage() {
        return isAutoScrollPage;
    }

    public void setIsAutoScrollPage(Boolean isAutoScrollPage) {
        this.isAutoScrollPage = isAutoScrollPage;
    }

    /**
     * 翻页
     *
     * @param view
     * @return
     */
    private boolean scrollNextPage(final View view) {

        Rect visibleRect = new Rect();
        getGlobalVisibleRect(visibleRect);
//        int visibleWidth = visibleRect.right - visibleRect.left;
        int width = getWidth();
        int[] focusL = new int[2];
        view.getLocationInWindow(focusL);

        if (mVisibleRect.left == 0) {
            getGlobalVisibleRect(mVisibleRect);
        }

        if (mVisiableWidth == 0) {
            mVisiableWidth = visibleRect.right - visibleRect.left;
        }

        //放大部分的修正;
        int scaleDistance = blowUpUtil.getDistance(view);

        if (recommendView != null && view.getParent().getParent() == recommendView) {//recommend
            int x = width - mVisiableWidth + blowUpUtil.getDistanceRight(lastAddView);
            isFromOther = false;
            if (mScroller.getCurrX() != x) {
                smoothScrollX(x, view);
            }
            playScaleAnimator(view);
            return true;
        }
        if (recommendView != null && lastFocusedView != null && lastFocusedView.getParent().getParent() == recommendView
                && view.getParent().getParent() != recommendView) {//recommend
            int x = width - mVisiableWidth + blowUpUtil.getDistanceRight(lastAddView) - recommendView.getWidth();
            isFromOther = false;
            if (x < 0) {
                x = 0;
            }
            smoothScrollX(x, view);
            playScaleAnimator(view);
            return true;
        }

        //右边翻页(第一页不满不进入)  <<--
        if ((focusL[0] + view.getWidth()) > visibleRect.right && focusL[0] > 0 && !isFromOther) {
            isFromOther = false;

            LogDebugUtil.d(IS_DEBUG, TAG2, "<<-- view.getRight() =" + view.getRight() + " width =" + getWidth() + " visibleRect_x=" + (visibleRect.right - visibleRect.left));

            if (view == firstAddView) {
                playScaleAnimator(view);
                return false;
            }

            int x = view.getLeft();
            Object rect = view.getTag(R.integer.tag_record_rect);
            if (rect != null) { //rect == null 说明是子view
                x = view.getLeft() - scaleDistance;
            } else {
                x = view.getLeft();
            }

            if (x + mVisiableWidth > width || view == lastAddView) {//最后一页
                smoothScrollX(width - mVisiableWidth + blowUpUtil.getDistanceRight(lastAddView), view);
            } else {
                smoothScrollX(x, view);
            }

            playScaleAnimator(view);

        }
        //左边翻页  -->>
        else if ((focusL[0]) < visibleRect.left) {
            isFromOther = false;

            LogDebugUtil.d(IS_DEBUG, TAG2,
                    "-->> left view.getRight() =" + view.getRight() + " width =" + getWidth() + " visibleRect_x=" + (visibleRect.right - visibleRect.left));

            //TODO:这里要做避归快速滑动 快速滑动时mVisiableWidth 的左右值都是小值
            int x = view.getRight() - mVisiableWidth + scaleDistance;
            //第一页规避
            if (x < 0) {
                smoothScrollX(0, view);
            } else {
                smoothScrollX(x, view);
            }
            playScaleAnimator(view);

        } else {
            LogDebugUtil.d(IS_DEBUG, TAG2, " view.getRight() =" + view.getRight() + " (focusL[0]) = " + focusL[0]);
            isFromOther = false;
            playScaleAnimator(view);
            return false;
        }

        // invalidate();
        return true;
    }

    /**
     * 绝对滑动
     *
     * @param x
     * @param nextView
     */
    public void smoothScrollX(int x, View nextView) {
        int time = 0;
        if (isAutoScrollPage != null && isAutoScrollPage == true) {
            time = scrollPageTime;
        } else if (isAutoScrollPage != null && isAutoScrollPage == false) {
            time = scrollNextViewTime;
        }

        mScrollerNextView = nextView;
        //设置mScroller的滚动偏移量
        LogDebugUtil.d(IS_DEBUG, TAG2, "--mScroller.getFinalX()= " + mScroller.getFinalX() + "  x= " + x);
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), x - mScroller.getFinalX(), mScroller.getFinalY(), time);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    /**
     * 相对滑动
     *
     * @param dx
     * @param nextView
     */
    public void smoothScrollXBy(int dx, View nextView) {
        int time = 0;
        if (isAutoScrollPage != null && isAutoScrollPage == true) {
            time = scrollPageTime;
        } else if (isAutoScrollPage != null && isAutoScrollPage == false) {
            time = scrollNextViewTime;
        }

        mScrollerNextView = nextView;
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, mScroller.getFinalY(), time);
        LogDebugUtil.d(IS_DEBUG, TAG2, "--mScroller.getFinalX()= " + mScroller.getFinalX() + "  mScroller.getFinalY()= " + mScroller.getFinalY());

        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {

        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();

        }
        super.computeScroll();
    }


    public void scrollFirstPosition() {
        scrollTo(0, 0);

        if (lastFocusedView != null) {
            restoreScale(lastFocusedView);
        }
        if (mScrollerNextView != null) {
            restoreScale(mScrollerNextView);
        }

        lastFocusedView = null;
        mScrollerNextView = null;
    }

    public void clearCurrentScreen() {
        currentScreen = 0;
    }

    public void focusFirstChildView() {
        if (screenFirstView.size() > 0) {
            if (screenFirstView.size() >= currentScreen) {
                if (screenFirstView.size() == currentScreen) {
                    screenFirstView.add(((RecyclerView) recommendView.findViewById(R.id.rcv_recommend_app)).getLayoutManager().findViewByPosition(0));
                }
                screenFirstView.get(currentScreen).requestFocus();
            } else {
                screenFirstView.get(0).requestFocus();
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(expandSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int direction = 0;
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (event.hasNoModifiers()) {
                        direction = View.FOCUS_LEFT;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (event.hasNoModifiers()) {
                        direction = View.FOCUS_RIGHT;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (event.hasNoModifiers()) {
                        direction = View.FOCUS_UP;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (event.hasNoModifiers()) {
                        direction = View.FOCUS_DOWN;
                    }
                    break;
                case KeyEvent.KEYCODE_TAB:
                    if (event.hasNoModifiers()) {
                        direction = View.FOCUS_FORWARD;
                    } else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
                        direction = View.FOCUS_BACKWARD;
                    }
                    break;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                int velocity = (int) mScroller.getCurrVelocity();

                //过滤快速滚动
                if (velocity < -1000) {//相差过了1秒是不拦截
                    //LogDebugUtil.d("ScrollerState", "jump ++ " + "mScroller.isFinished() = " + mScroller.isFinished() + " getCurrVelocity = " + velocity);
                } else if (!mScroller.isFinished()) {
                    LogDebugUtil.d("ScrollerState", "jump interrupt " + "mScroller.isFinished() = " + mScroller.isFinished() + " getCurrVelocity = " + velocity);
                    return true;
                }

            }

            // 当前的view
            View currentView = findFocus();

            Class<? extends View> currentClass;
            Class<? extends View> nextClass;
            if (currentView != null && direction != 0) {
                currentClass = currentView.getClass();

                Object rect = currentView.getTag(R.integer.tag_record_rect);
                RectF recordRect = null;
                if (rect != null) {
                    recordRect = (RectF) rect;
                }
                View focusNext;
                if (direction == View.FOCUS_DOWN) {
                    focusNext = currentView.focusSearch(direction);
                    if (focusNext != null) {
                        nextClass = focusNext.getClass();
                        if (nextClass != currentClass) {
                            if (pageNow < mPageCount - 1) {
                                if (mOnBorderListener != null) {
                                    if (mOnBorderListener.onKeyBottomDown(pageNow, mPageCount, recordRect)) {
                                        return true;
                                    }

                                }
                            } else if (focusNext.getParent() != null && focusNext.getParent() == this) {
                                Utils.playKeySound(this, Utils.SOUND_ERROR_KEY);
                                focusNext.requestFocus();
                            } else if (isDownFocus) {
                                if (mOnBorderListener != null) {
                                    return mOnBorderListener.onKeyBottomDown(pageNow, mPageCount, recordRect);
                                }
                            } else {
                                Utils.playKeySound(this, Utils.SOUND_ERROR_KEY);
                                setScaleUpDown();
                            }
                        } else {
                            focusNext.requestFocus();
                        }
                        return true;
                    } else {
                        if (pageNow < mPageCount - 1) {
                            if (mOnBorderListener != null) {
                                if (currentClass == RankElementsItemView.class) {
                                    View view = (View) currentView.getParent().getParent();
                                    recordRect = (RectF) view.getTag(R.integer.tag_record_rect);
                                }

                                mOnBorderListener.onKeyBottomDown(pageNow,
                                        mPageCount, recordRect);
                            }
                        } else {
                            if (mOnBorderListener != null && mOnBorderListener.onKeyBottomDown(pageNow, mPageCount, recordRect)) {
                                Utils.playKeySound(this, Utils.SOUND_ERROR_KEY);
                                setScaleUpDown();
                            } else {
                                return super.dispatchKeyEvent(event);
                            }
                        }
                        return true;
                    }

                } else if (direction == View.FOCUS_UP) {
                    focusNext = currentView.focusSearch(direction);
                    if (focusNext != null) {
                        if (!isChild(focusNext)) {
                            if (mOnBorderListener != null) {
                                if (mOnBorderListener.onKeyTopUp(
                                        pageNow, recordRect)) {
                                    return true;
                                }
                            }
                        } else {
                            focusNext.requestFocus();
                            return true;
                        }

                    } else {
                        //recordRect != null 单焦点的时候使用
                        if (mOnBorderListener != null) {
                            if (mOnBorderListener.onKeyTopUp(pageNow, recordRect)) {
                                return true;
                            }
                        }
                    }

                } else if (direction == View.FOCUS_LEFT) {
                    focusNext = currentView.focusSearch(direction);
                    if (focusNext != null) {
                        nextClass = focusNext.getClass();
                        if (!isChild(focusNext) && mOnBorderListener != null) {
                            if (mOnBorderListener.onKeyLeftEnd(pageNow)) {
                                return true;
                            }
                        }
                        if (nextClass == RankElementsItemView.class) {
                            RankElementsView rankElementsView = (RankElementsView) focusNext.getParent().getParent();
                            rankElementsView.focusMoveToTop();
                            lastFocusedView = null;
                            return true;
                        }
                    } else {
                        if (mOnBorderListener != null && mOnBorderListener.onKeyLeftEnd(pageNow)) {
                            return true;
                        }
                    }

                } else if (direction == View.FOCUS_RIGHT) {
                    focusNext = currentView.focusSearch(direction);
                    if (focusNext != null) {
                        nextClass = focusNext.getClass();
                        if (!isChild(focusNext) && mOnBorderListener != null) {
                            if (mOnBorderListener.onKeyRightEnd(pageNow)) {
                                return true;
                            }
                        }
                        if (nextClass == RankElementsItemView.class) {
                            RankElementsView rankElementsView = (RankElementsView) focusNext.getParent().getParent();
                            rankElementsView.focusMoveToTop();
                            lastFocusedView = null;
                            return true;
                        }
                    } else {
                        if (mOnBorderListener != null && mOnBorderListener.onKeyRightEnd(pageNow)) {
                            return true;
                        }
                    }
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private boolean isChild(View nextView) {
        boolean isChild = false;
        for (ViewParent parent = nextView.getParent(); parent instanceof ViewGroup;
             parent = parent.getParent()) {
            if (parent == this) {
                isChild = true;
                break;
            }
        }
        return isChild;
    }

    public void focusMoveToLeft() {
        if (mLeftView != null) {
            if (mLeftView.getClass() == RankElementsView.class) {
                lastFocusedView = null;
                RankElementsView rankElementsView = (RankElementsView) mLeftView;
                rankElementsView.focusMoveToTop();
            } else {
                mLeftView.requestFocus();
            }
        }
        scrollTo(0, 0);
    }

    public void focusMoveToRight() {
        if (mRightView != null) {
            if (recommendView != null) {
                View view = ((RecyclerView) recommendView.findViewById(R.id.rcv_recommend_app))
                        .getLayoutManager().findViewByPosition(1);
                if (view != null) {
                    //滑动在聚焦时已处理
                    view.requestFocus();
                }
                return;
            } else {
                mRightView.requestFocus();
            }

        }

        if (isAutoScrollPage == null) {
            return;
        }

        if (mVisiableWidth == 0) {
            getLocalVisibleRect(mVisibleRect);
            mVisiableWidth = mVisibleRect.right - mVisibleRect.left;
        }
        if (mRightView != null && mRightView.getRight() < mVisibleRect.right) {
            return;
        }
        if (mRightView != null) {
            scrollTo(mRightView.getRight() - mVisiableWidth + blowUpUtil.getDistanceRight(mRightView), 0);
        }

    }

    /**
     * 焦点 focus 到最后一次的焦点位置。如果没有的话 回到首位置
     */
    public void focusLastView() {
        if (lastFocusedView != null) {
            lastFocusedView.requestFocus();
        } else if (getChildCount() > 0) {
            firstAddView.requestFocus();
        }
    }

    public void clearLastFocusedView() {
        if (lastFocusedView != null) {
            restoreScale(lastFocusedView);
        }

        mScrollerNextView = null;
        this.lastFocusedView = null;

    }

    public interface OnBorderListener {
        boolean onKeyBottomDown(int page, int pageCount, RectF rect);

        boolean onKeyTopUp(int page, RectF rect);

        boolean onKeyLeftEnd(int page);

        boolean onKeyRightEnd(int page);

    }

    public void setOnBorderListener(OnBorderListener onBorderListener) {
        this.mOnBorderListener = onBorderListener;
    }

    /**
     * @param rect
     * @param direction 0 向上
     *                  1 向下
     */
    public void requestFocusByRect(RectF rect, int direction) {
        mRect = rect;
        mDirection = direction;
    }

    /**
     * 强引用  标scaleView的对象 在二级界面中使用
     */
    static public class LayouScaleViewHolder {
        public View view;
    }

    public void setScaleDown(View view) {
        blowUpUtil.setScaleDown(view);
    }

    public void setScaleUp(View view) {
        blowUpUtil.setScaleUp(view);
    }

    public void setScaleUpDown() {
        blowUpUtil.setScaleUpDown(lastFocusedView);
    }
}
