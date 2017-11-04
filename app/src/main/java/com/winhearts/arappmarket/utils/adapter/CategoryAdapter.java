package com.winhearts.arappmarket.utils.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.AppDetailActivity;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.view.BlowUpUtil;

import java.util.List;

/**
 * Created by lmh on 2017/10/31.
 */

public class CategoryAdapter extends BaseRecyclerAdapter<SoftwareInfo> {
    private BlowUpUtil blowUpUtil;
    private Context mContext;

    public CategoryAdapter(Context mContext, List<SoftwareInfo> datas) {
        super(datas);
        this.mContext = mContext;
        blowUpUtil = new BlowUpUtil(mContext);
        blowUpUtil.setIsBringToFront(false);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.adapter_category;
    }

    @Override
    public void convert(VH holder, SoftwareInfo data, int position) {
        holder.itemView.setFocusable(true);
        holder.itemView.setClickable(true);
        SimpleDraweeView bgView = holder.getView(R.id.sd_category_item_bg);
        CommonHierarchy.setHierarchyCardPic(bgView);
        SimpleDraweeView appIcon = holder.getView(R.id.sd_category_item_icon);
        CommonHierarchy.setHierarchyAppIcon(appIcon);
        TextView appName = holder.getView(R.id.tv_category_item_name);
        TextView appDescription = holder.getView(R.id.tv_category_item_description);
        holder.itemView.setTag(data);
        bgView.setImageURI(data.getCover());
        appIcon.setImageURI(data.getIcon());
        appName.setText(data.getName());
        appDescription.setText(data.getDescription());
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    blowUpUtil.setScaleUp(v);
                } else {
                    blowUpUtil.setScaleDown(v);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftwareInfo softwareInfo = (SoftwareInfo) v.getTag();
                Intent intent = new Intent(mContext, AppDetailActivity.class);
                intent.putExtra("packageName", softwareInfo.getPackageName());
                mContext.startActivity(intent);
            }
        });
    }
}
