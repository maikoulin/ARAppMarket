package com.winhearts.arappmarket.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.winhearts.arappmarket.R;


/**
 * 自定义gridLayout，添加边界判断，用于搜素界面
 * Created by lmh on 2016/5/3.
 */
public class CusGridView extends BaseEdgeView {
    private Context context;
    private int column = 0;
    private float itemW;
    private int itemH;
    private int[] itemHs;
    private BaseAdapter baseAdapter;
    private int curIndexH;
    //    private int curIndexV;
    private int verticalSpacing;
    private int horizontalSpacing;
    private OnFocusChangeListener onItemFocusChangeListener;
    private OnClickListener onItemClickListener;

    public CusGridView(Context context) {
        super(context);
        this.context = context;
    }

    public CusGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CusGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void init(int column) {
        this.column = column;
    }


    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    public void setItemH(int itemH) {
        this.itemH = itemH;
    }

    public void setAdapter(BaseAdapter baseAdapter) {
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        this.baseAdapter = baseAdapter;
        if (getMeasuredWidth() <= 0) {
            getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        } else {
            initView();
        }
        baseAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                removeAllViews();
                setMeasuredDimension(0, 0);
                if (getMeasuredWidth() <= 0) {
                    getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
                } else {
                    initView();
                }
            }
        });
    }

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
            initView();
        }
    };

    private void initView() {
        if (baseAdapter == null) {
            return;
        }
        itemHs = new int[column];

        itemW = (getMeasuredWidth() - (column + 1) * horizontalSpacing * 1.0f) / column;

        for (int i = 0; i < baseAdapter.getCount(); i++) {
            View view = baseAdapter.getView(i, null, null);
            view.setTag(i);
            if (onItemFocusChangeListener != null) {
                view.setOnFocusChangeListener(onItemFocusChangeListener);
            }
            if (onItemClickListener != null) {
                view.setOnClickListener(onItemClickListener);
            }
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) itemW, LayoutParams.WRAP_CONTENT);

            curIndexH = i % column;
            layoutParams.leftMargin = (int) (curIndexH * itemW) + (curIndexH + 1) * horizontalSpacing;
            layoutParams.topMargin = itemHs[curIndexH];
            if (itemH == 0) {
                view.measure((int) itemW, 0);
                itemHs[curIndexH] += view.getMeasuredHeight();
                layoutParams.height = view.getMeasuredHeight()+10;
            } else if (itemH == -1) {
                itemHs[curIndexH] += itemW;
                layoutParams.height = (int) itemW;
            } else {
                itemHs[curIndexH] += itemH;
                itemW = itemH;
                layoutParams.height = itemH;
            }

            if ((i + 1) % column == 0) {
                view.setTag(R.integer.edge_view_key_of_is_right, 1);
            }

            if (i < column) {
                view.setTag(R.integer.edge_view_key_of_is_top, 1);
            }

            if (i % column == 0) {
                view.setTag(R.integer.edge_view_key_of_is_Left, 1);
            }

            if (i >= baseAdapter.getCount() - (baseAdapter.getCount() % column)) {
                view.setTag(R.integer.edge_view_key_of_is_bottom, 1);
            }

            itemHs[curIndexH] += verticalSpacing;
            addView(view, layoutParams);
        }
    }


    public void setOnItemFoucsChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onItemFocusChangeListener = onFocusChangeListener;
    }

    public void setOnItemClickListener(OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
