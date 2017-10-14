package com.winhearts.arappmarket.utils.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.view.MarqueeTextView;
import com.winhearts.arappmarket.view.ScoreView;

import java.util.List;

/**
 * 默认的到底加载view的适配器
 * @author wangmn
 */
@SuppressLint("NewApi")
public class DefaultPullToRefreshAdapter extends PullToRefreshAdapter {
    private List<SoftwareInfo> mList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int totalCount;
    private boolean isShowDownload = false;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public DefaultPullToRefreshAdapter(Context context, List<SoftwareInfo> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mList = list;
    }

    public int getCount() {
        return mList.size();

    }

    public void setIsShowDownload(boolean isShowDownload) {
        this.isShowDownload = isShowDownload;
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final SoftwareInfo info = mList.get(position);
        HolderView holder;
        if (convertView == null) {
            holder = new HolderView();
            convertView = mInflater.inflate(R.layout.adapter_app_card, null);
            holder.llContent = (RelativeLayout) convertView.findViewById(R.id.ll_content);
            holder.ivApp = (SimpleDraweeView) convertView.findViewById(R.id.rv_app_card_icon);
            holder.ivAppTag = (SimpleDraweeView) convertView.findViewById(R.id.rv_app_card_tag);
            GenericDraweeHierarchy hierarchy = holder.ivApp.getHierarchy();
            hierarchy.setPlaceholderImage(R.drawable.background_app_icon);
            hierarchy.setFailureImage(mContext.getResources().getDrawable(R.drawable.appicon_error));
            holder.ivApp.setHierarchy(hierarchy);
            holder.tvApp = (MarqueeTextView) convertView.findViewById(R.id.tv_app_card_name);
            holder.svStar = (ScoreView) convertView
                    .findViewById(R.id.sv_app_card_star);
            holder.tvDownload = (TextView) convertView.findViewById(R.id.tv_app_card_download);
            // 调整
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        if (info.getIcon() != null) {
            holder.ivApp.setImageURI(Uri.parse(info.getIcon()));
        } else {
            Drawable drawable;
            try {
                drawable = AppManager.getAppIcon(mContext,
                        info.getPackageName());
                if (drawable != null) {
                    holder.ivApp.setBackground(drawable);
                }
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        holder.tvApp.setText(info.getName());
        String star = info.getStar();
        float score;
        if (TextUtils.isEmpty(star)) {
            score = 5;
        } else {
            score = Float.valueOf(star);
        }
        try {
            holder.svStar.setScore(score, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isShowDownload) {
            holder.tvDownload.setVisibility(View.VISIBLE);
            String download = info.getDownload().replaceAll("(?<=\\d)(?=(?:\\d{3})+$)", ",");
            String src = "下载量 : " + download;
            holder.tvDownload.setText(src);
        } else {
            holder.tvDownload.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(info.getTag())) {
            setImgTag(holder.ivAppTag, info.getTag());
        }
        convertView.setClickable(false);
        convertView.setFocusable(false);
        convertView.setFocusableInTouchMode(false);
        return convertView;
    }

    private void setImgTag(SimpleDraweeView ivAppTag, String tag) {
        if (tag.equals("最新")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_newest);
        } else if (tag.equals("最热")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_hottest);
        } else if (tag.equals("活动")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_activity);
        } else if (tag.equals("更新")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_update);
        } else if (tag.equals("首发")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_starting);
        } else if (tag.equals("专题")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_special);
        } else if (tag.equals("推荐")) {
            ivAppTag.setBackgroundResource(R.drawable.app_main_recommend);
        }
    }

    class HolderView {
        RelativeLayout llContent;
        SimpleDraweeView ivApp;
        SimpleDraweeView ivAppTag;
        MarqueeTextView tvApp;
        ScoreView svStar;
        TextView tvDownload;
    }
}
