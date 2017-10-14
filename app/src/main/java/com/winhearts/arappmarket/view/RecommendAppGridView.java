package com.winhearts.arappmarket.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.winhearts.arappmarket.utils.adapter.NewUserRecommendAdapter;
import com.winhearts.arappmarket.utils.ScreenUtil;

/**
 * 推荐应用view
 * Created by lmh on 2016/11/11.
 */

public class RecommendAppGridView extends RelativeLayout {

    private final static int GRID_COLOUM = 4;
    private Context mContext;


    public RecommendAppGridView(Context context) {
        super(context);
        mContext = context;
    }

    public RecommendAppGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public RecommendAppGridView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setAdapter(NewUserRecommendAdapter adapter) {
        removeAllViews();
        int itemW = ScreenUtil.dip2px(mContext, 170);
        int itemH = ScreenUtil.dip2px(mContext, 200);
        int flag = ScreenUtil.dip2px(mContext, 40);
        for (int i = 0; i < adapter.getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = adapter.createViewHolder(null, 0);
            adapter.bindViewHolder(viewHolder, i);
            int column = i % GRID_COLOUM;
            int row = i / GRID_COLOUM;
            int left = itemW * column;
            int top = (itemH - flag) * row;
            LayoutParams layoutParams
                    = new LayoutParams(itemW, itemH);
            layoutParams.setMargins(left, top, 0, 0);
            viewHolder.itemView.setTag(i);
            addView(viewHolder.itemView, layoutParams);
        }

    }
}
