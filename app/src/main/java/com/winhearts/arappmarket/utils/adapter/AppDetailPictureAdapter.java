package com.winhearts.arappmarket.utils.adapter;


import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.wrapper.MyItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.constant.CommonHierarchy;

import java.util.List;


/**
 * 应用详情界面里应用图片列表适配器
 * Created by lmh on 2015/10/28.
 */
public class AppDetailPictureAdapter extends RecyclerView.Adapter<AppDetailPictureAdapter.ViewHolder> {

    private List<String> picList;
    private MyItemClickListener mItemClickListener;

    public AppDetailPictureAdapter(List<String> picList) {
        this.picList = picList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SimpleDraweeView imageView;
        private MyItemClickListener mListener;

        public ViewHolder(View v, MyItemClickListener listener) {
            super(v);
            imageView = (SimpleDraweeView) v.findViewById(R.id.main_tab_icon);
            CommonHierarchy.setHierarchyCardPic(imageView);
            this.mListener = listener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(view, getLayoutPosition());
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_appdetail_pic_item, parent, false);

        return new ViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommonHierarchy.showThumb(holder.imageView.getContext(), Uri.parse(picList.get(position)), holder.imageView);
//        holder.imageView.setImageURI(Uri.parse(picList.get(position)));
    }

    @Override
    public int getItemCount() {
        return picList.size();
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
