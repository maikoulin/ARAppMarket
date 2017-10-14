package com.winhearts.arappmarket.utils.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.winhearts.arappmarket.activity.AppDetailActivity;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;
import com.winhearts.arappmarket.view.MainSelfSoftwareView;

import java.util.List;

/**
 * 推荐应用适配器
 * Created by lmh on 2016/11/9.
 */

public class RecommendAppAdapter extends RecyclerView.Adapter {
    private List<SoftwareInfo> mSoftwareList;
    private Context mContext;
    private int mPageIndex;


    public RecommendAppAdapter(Context context, List<SoftwareInfo> softwareList) {
        mSoftwareList = softwareList.subList(0, softwareList.size() > 6 ? 6 : softwareList.size());
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendAppViewHolder(new MainSelfSoftwareView(mContext, 5));
    }

    public void setPageIndex(int pageIndex) {
        this.mPageIndex = pageIndex;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams =
                new RecyclerView.LayoutParams(ScreenUtil.dip2px(mContext, 270), ScreenUtil.dip2px(mContext, 135));
        holder.itemView.setLayoutParams(layoutParams);
        ((MainSelfSoftwareView) holder.itemView).bindData(mSoftwareList.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (mSoftwareList != null) {
            return mSoftwareList.size();
        }
        return 0;
    }

    private class RecommendAppViewHolder extends RecyclerView.ViewHolder {

        RecommendAppViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setOnFocusChangeListener(onFocusChangeListener);
            itemView.setOnClickListener(onClickListener);
            itemView.setTag(R.integer.tag_record_page, mPageIndex);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            DownloadPath downloadPath = new DownloadPath();
            downloadPath.setLayoutId(PrefNormalUtils.getString(mContext, PrefNormalUtils.LAYOUT_ID, ""));
            downloadPath.setMenuId("-5");
            SoftwareInfo softwareInfo = mSoftwareList.get(index);
            String packageName = softwareInfo.getPackageName();
            Intent intent = new Intent(mContext, AppDetailActivity.class);
            downloadPath.setModulePath(packageName);
            intent.putExtra("downloadPath", downloadPath);
            intent.putExtra("packageName", packageName);
            mContext.startActivity(intent);
        }
    };


    public void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    private View.OnFocusChangeListener onFocusChangeListener;


}
