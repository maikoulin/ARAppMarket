package com.winhearts.arappmarket.logic;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.winhearts.arappmarket.R;
import com.google.gson.Gson;
import com.winhearts.arappmarket.utils.adapter.RecommendAppAdapter;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

/**
 * 推荐应用逻辑处理
 * Created by lmh on 2016/11/17.
 */

public class RecommendAppLogic {
    private RecyclerView mRecyclerView;
    private RecommendAppAdapter mRecommendAppAdapter;
    private Context mContext;

    public View getRecommendAppLogic(Context context, final View.OnFocusChangeListener onFocusChangeListener, final int screen) {
        mContext = context;
        String recommendApp = PrefNormalUtils.getString(context, PrefNormalUtils.RECOMMEND_APP, null);
        if (!TextUtils.isEmpty(recommendApp)) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View recommendView = layoutInflater.inflate(R.layout.widget_recomment_app, null);
            mRecyclerView = (RecyclerView) recommendView.findViewById(R.id.rcv_recommend_app);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
            mRecyclerView.setFocusable(false);
            mRecyclerView.setClipToPadding(false);
            mRecyclerView.setClipChildren(false);
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                super.getItemOffsets(outRect, view, parent, state);
                    outRect.top = ScreenUtil.dip2px(mContext, 10);
                    outRect.right = 0;
                    outRect.left = 0;
                }
            });
            Softwares softwares = new Gson().fromJson(recommendApp, Softwares.class);
            mRecommendAppAdapter = new RecommendAppAdapter(mContext, softwares.getSoftwares());
            mRecommendAppAdapter.setOnFocusChangeListener(onFocusChangeListener);
            mRecommendAppAdapter.setPageIndex(screen);
            mRecyclerView.setAdapter(mRecommendAppAdapter);
            return recommendView;
        } else {
            return null;
        }

    }


}
