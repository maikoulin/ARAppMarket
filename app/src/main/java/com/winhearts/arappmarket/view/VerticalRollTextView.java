package com.winhearts.arappmarket.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.winhearts.arappmarket.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 单行显示，内容超过一行时自动循环向上滚动显示的TextView
 * Created by suxq on 2017/3/13.
 */

public class VerticalRollTextView extends TextSwitcher implements ViewSwitcher.ViewFactory{

    private Context mContext;
    private TextView mTextView;
    /**
     * mInUp,mOutUp分别构成向上翻页的进出动画
     */
    private Animation mInUp;
    private Animation mOutUp;

    /**
     * 支持自定义的xml属性，暂时未实现
     */
    private AttributeSet mTextViewAttrs;
    /**
     * 是否需要滚动显示，即内容是否超过一行
     */
    private boolean isNeedRoll = false;
    /**
     * 自动循环滚动显示内容是否开启
     */
    private boolean isAutoRolling = false;
    /**
     * 滚动间隔，默认5s
     */
    private int mRollInterval = 5000;
    /**
     * 内容总共需要分几次显示，即总共需要滚动几次
     */
    private int mTotalCount = 1;
    /**
     * 当前显示的是第几部分内容，即当前滚动到第几次
     */
    private int mCurCount = 0;
    /**
     * TextView需要分行显示的内容
     */
    private List<String> mTvContentList;
    /**
     * 自动循环滚动显示内容的任务
     */
    private Runnable mAutoRollTask;

    public VerticalRollTextView(Context context) {
        this(context, null);
    }

    public VerticalRollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTextViewAttrs = attrs;
        init();
    }

    private void init() {
        setFactory(this);
        mInUp = createAnim(0, 0 , true, true);
        mOutUp = createAnim(0, 0, false, true);
        setInAnimation(mInUp);
        setOutAnimation(mOutUp);
        mTvContentList = new ArrayList<>();
        mAutoRollTask = new Runnable() {
            @Override
            public void run() {
                if (isAutoRolling) {
                    next();
                    postDelayed(mAutoRollTask, mRollInterval);
                }
            }
        };
    }

    private Animation createAnim(float start, float end, boolean turnIn, boolean turnUp){
        final Animation rotation = new Rotate3dAnimation(start, end, turnIn, turnUp);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        return rotation;
    }

    //这里返回的TextView，就是我们看到的View
    @Override
    public View makeView() {
        mTextView = new TextView(mContext);
        //这里硬编码设置TextView属性不怎么好
        mTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        mTextView.setTextSize(18); //字体大小18px
        mTextView.setPadding(ScreenUtil.dip2px(mContext, 12), 0, ScreenUtil.dip2px(mContext, 24), 0);
        mTextView.setTextColor(Color.parseColor("#767676"));
        return mTextView;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && isNeedRoll) {
            renewAutoRoll();
        } else {
            stopAutoRoll();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoRoll();
    }

    /**
     * 重新从头开始自动循环显示
     */
    public void renewAutoRoll() {
        if (mCurCount == 0) {
            startAutoRoll();
            return;
        }
        stopAutoRoll();
        mCurCount = 0;
        post(new Runnable() {
            @Override
            public void run() {
                setText(mTvContentList.get(mCurCount % mTotalCount));
            }
        });
        startAutoRoll();
    }

    /**
     * 手动切换显示下一条内容
     */
    public void showNextContent() {
        if (isNeedRoll) {
            stopAutoRoll();
            next();
            startAutoRoll();
        }
    }

    /**
     * 设置TextView的内容，默认设置最长宽度 300 dp
     * @see #setTvText(String, int)
     */
    public void setTvText(@NonNull String string) {
        setTvText(string, 300);
    }

    /**
     * 设置TextView的内容，支持设置最长宽度，单位为dp，如果内容超过一行，则自动开启竖直循环滚动显示
     *
     * @param string 内容
     * @param tvMaxLength 最长宽度，单位为dp
     */
    public void setTvText(@NonNull String string, int tvMaxLength) {
        int maxTvLength = ScreenUtil.dip2px(mContext, tvMaxLength);
        //计算TextView内容区域的最大宽度
        int textMaxLength = maxTvLength - mTextView.getCompoundPaddingLeft() - mTextView.getCompoundPaddingRight();
        //计算实际内容的宽度
        int textLength = (int) (mTextView.getPaint().measureText(string) + 0.5f);
        if (mTvContentList == null) {
            mTvContentList = new ArrayList<>();
        }
        mTvContentList.clear();
        //判断是否需要分行显示
        if (textLength > textMaxLength) {
            mTextView.setWidth(maxTvLength);
            mTextView.setMaxLines(1);
            //需要分行显示，切割string到List里
            int star = 0;
            int length = 0;
            for (int i=0; i<string.length(); i++) {
                String curChar = String.valueOf(string.charAt(i));
                length += mTextView.getPaint().measureText(curChar);
                if (length > textMaxLength ) {
                    mTvContentList.add(string.substring(star, i));
                    star = i;
                    length = (int) mTextView.getPaint().measureText(curChar);
                }
            }
            mTvContentList.add(string.substring(star));
        } else {
            //单行显示，所以宽度取最小即可
            int tvWidth = textLength + mTextView.getCompoundPaddingLeft() + mTextView.getCompoundPaddingRight();
            mTextView.setWidth(Math.min(tvWidth, maxTvLength));
            mTvContentList.add(string);
        }
        mTotalCount = mTvContentList.size();
        mCurCount = 0;
        isNeedRoll = (mTotalCount > 1);
        setText(mTvContentList.get(mCurCount % mTotalCount));
    }

    private void startAutoRoll() {
        if (!isNeedRoll) {
            return;
        }
        if (isAutoRolling) {
            return;
        }
        isAutoRolling = true;
        postDelayed(mAutoRollTask, mRollInterval);
    }

    private void stopAutoRoll() {
        isAutoRolling = false;
        removeCallbacks(mAutoRollTask);
    }

    private void next(){
        mCurCount++;
        post(new Runnable() {
            @Override
            public void run() {
                setText(mTvContentList.get(mCurCount%mTotalCount));
            }
        });
    }

    /**
     * 动画效果类
     */
    private class Rotate3dAnimation extends Animation {
        private final float mFromDegrees;
        private final float mToDegrees;
        private float mCenterX;
        private float mCenterY;
        private final boolean mTurnIn;
        private final boolean mTurnUp;
        private Camera mCamera;

        Rotate3dAnimation(float fromDegrees, float toDegrees, boolean turnIn, boolean turnUp) {
            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
            mCenterY = getHeight();
            mCenterX = getWidth() / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float fromDegrees = mFromDegrees;
            float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

            final float centerX = mCenterX ;
            final float centerY = mCenterY ;
            final Camera camera = mCamera;
            final int derection = mTurnUp ? 1: -1;

            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime), 0.0f);
            }
            camera.rotateX(degrees);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

}
