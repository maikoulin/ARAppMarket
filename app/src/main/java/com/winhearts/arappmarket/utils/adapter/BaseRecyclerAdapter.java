package com.winhearts.arappmarket.utils.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * RecyclerAdapter基类
 * Created by lmh on 2017/3/14.
 */

abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.VH> {
    private List<T> mDatas;
    onItemClickListener onItemClickListener;

    BaseRecyclerAdapter(List<T> datas) {
        this.mDatas = datas;
    }

    abstract int getLayoutId(int viewType);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return VH.get(parent, getLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        convert(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    abstract void convert(VH holder, T data, int position);

    static class VH extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;
        private View mConvertView;

        private VH(View v) {
            super(v);
            mConvertView = v;
            mViews = new SparseArray<>();
        }

        public static VH get(ViewGroup parent, int layoutId) {
            View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new VH(convertView);
        }

        public <T extends View> T getView(int id) {
            View v = mViews.get(id);
            if (v == null) {
                v = mConvertView.findViewById(id);
                mViews.put(id, v);
            }
            return (T) v;
        }

        public void setText(int id, String value) {
            TextView view = getView(id);
            view.setText(value);
        }
    }

    public void setOnItemClickListener(BaseRecyclerAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener<T> {
        void onItemClick(View view, T data, int position);
    }
}
