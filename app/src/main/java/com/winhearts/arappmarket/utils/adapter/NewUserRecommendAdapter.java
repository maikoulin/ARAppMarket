package com.winhearts.arappmarket.utils.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.SoftwareInfo;

import java.util.List;

/**
 * 新用户推荐界面适配器
 * Created by lmh on 2016/11/9.
 */

public class NewUserRecommendAdapter extends RecyclerView.Adapter {
    private List<SoftwareInfo> mSoftwareList;
    private LayoutInflater mLayoutInflater;
//    private BlowUpUtil mBlowUpUtil;


    public NewUserRecommendAdapter(Context context, List<SoftwareInfo> softwareList) {
        mSoftwareList = softwareList;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mBlowUpUtil = new BlowUpUtil(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendAppViewHolder(mLayoutInflater.inflate(R.layout.item_recommend_app2, null));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecommendAppViewHolder recommendAppViewHolder = (RecommendAppViewHolder) holder;
        SoftwareInfo softwareInfo = mSoftwareList.get(position);
        recommendAppViewHolder.nameTv.setText(softwareInfo.getName());
        CommonHierarchy.setHierarchyCardPic(recommendAppViewHolder.iconSDV);
        recommendAppViewHolder.iconSDV.setImageURI(softwareInfo.getIcon());
    }

    @Override
    public int getItemCount() {
        if (mSoftwareList != null) {
            return mSoftwareList.size();
        }
        return 0;
    }

    private class RecommendAppViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView iconSDV;
        //        ImageView isSelectImg;
        TextView nameTv;

        RecommendAppViewHolder(View itemView) {
            super(itemView);

            iconSDV = (SimpleDraweeView) itemView.findViewById(R.id.sdv_recommend_app_icon);
            nameTv = (TextView) itemView.findViewById(R.id.tv_recommend_app_name);
            itemView.setOnClickListener(onClickListener);
        }
    }

//    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if (hasFocus) {
//                mBlowUpUtil.setScaleUp(v);
//            } else {
//                mBlowUpUtil.setScaleDown(v);
//            }
//        }
//    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View selectView = v.findViewById(R.id.iv_recommend_app_select);
            if (selectView.getVisibility() == View.VISIBLE) {
                selectView.setVisibility(View.GONE);
            } else {
                selectView.setVisibility(View.VISIBLE);
            }
        }
    };
}
