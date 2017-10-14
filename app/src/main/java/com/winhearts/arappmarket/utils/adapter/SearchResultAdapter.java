package com.winhearts.arappmarket.utils.adapter;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.model.SoftwareInfo;

import java.util.List;

/**
 * 搜索结果适配器
 */
public class SearchResultAdapter extends BaseAdapter {

    private Context mContext;
    private List<SoftwareInfo> softwares;
    private int itemHeight = 0;
    private int itemWidth = 0;

    public SearchResultAdapter(Context context, List<SoftwareInfo> list, int width, int height) {
        super();
        this.mContext = context;
        this.softwares = list;
        this.itemHeight = height;
        this.itemWidth = width;
    }

    @Override
    public int getCount() {
        return softwares.size();
    }

    @Override
    public Object getItem(int position) {
        return softwares.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.search_result_item, null);
            holder = new HolderView();
            holder.ivMovie = (SimpleDraweeView) convertView
                    .findViewById(R.id.iv_movie);
            GenericDraweeHierarchy hierarchy = holder.ivMovie.getHierarchy();
            hierarchy.setPlaceholderImage(mContext.getResources().getDrawable(R.drawable.background_app_icon), ScalingUtils.ScaleType.FIT_XY);
            holder.tvMovie = (TextView) convertView.findViewById(R.id.tv_movie);
            holder.tvType = (TextView) convertView.findViewById(R.id.tv_type);
            LayoutParams ivLp = holder.ivMovie.getLayoutParams();
            ivLp.width = itemWidth;
            ivLp.height = itemHeight;

//            holder.ivMovie.setLayoutParams(ivLp);
            // 重设图片大小
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        // 影片图片

        SoftwareInfo softwareInfo = softwares.get(position);
        if (!TextUtils.isEmpty(softwareInfo.getIcon())) {
//            CommonHierarchy.showAppIcon(mContext, Uri.parse(softwareInfo.getIcon()), holder.ivMovie);
            holder.ivMovie.setImageURI(Uri.parse(softwareInfo.getIcon()));
        }
        String firstTypeCode = softwareInfo.getFirstTypeCode();
        if (firstTypeCode != null) {
            if (firstTypeCode.equals("APP")) {
                holder.tvType.setText("应用");
            } else if (firstTypeCode.equals("GAME")) {
                holder.tvType.setText("游戏");
            }
        } else {
            holder.tvType.setText("应用");
        }
        holder.tvType.setVisibility(View.INVISIBLE);
        holder.tvType.setBackgroundResource(R.drawable.teleplay_bg);

        // 名称
        holder.tvMovie.setText(softwares.get(position).getName());

        return convertView;
    }

    private class HolderView {
        SimpleDraweeView ivMovie;
        TextView tvMovie;
        TextView tvType;
    }


}
