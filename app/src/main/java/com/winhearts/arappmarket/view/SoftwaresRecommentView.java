package com.winhearts.arappmarket.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.activity.AppDetailActivity;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.QuerySoftwaresRecomment;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.util.ArrayList;

/**
 * 推荐软件界面
 * Created by lmh on 2016/5/3.
 */
public class SoftwaresRecommentView extends BaseEdgeView {

    private Context context;
    private int[] bgs = new int[]{
            R.drawable.soft_recomment_blue_select, R.drawable.soft_recomment_organ_select,
            R.drawable.soft_recomment_green_select, R.drawable.soft_recomment_red_select};
    private ArrayList<SoftwareInfo> softwareInfos;
    private int row = 0;

    public SoftwaresRecommentView(Context context) {
        super(context);
        this.context = context;
    }

    public SoftwaresRecommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SoftwaresRecommentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setData(QuerySoftwaresRecomment querySoftwaresRecomment) {
        if (querySoftwaresRecomment != null && querySoftwaresRecomment.getSoftwares() != null) {
            removeAllViews();
            softwareInfos = querySoftwaresRecomment.getSoftwares();
            getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        } else {
            LoggerUtil.d("SoftwaresRecommentView", "querySoftwaresRecomment data null");
        }
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (softwareInfos != null) {
                Integer index = (Integer) v.getTag();
                if (softwareInfos.size() > index) {

                    DownloadPath downloadPath = new DownloadPath();
                    downloadPath.setLayoutId(PrefNormalUtils.getString(context, PrefNormalUtils.LAYOUT_ID, ""));
                    downloadPath.setMenuId("-1");
                    SoftwareInfo softwareInfo = softwareInfos.get(index);
                    String packageName = softwareInfo.getPackageName();
                    Intent intent = new Intent(context, AppDetailActivity.class);
                    downloadPath.setModulePath(packageName);
                    intent.putExtra("downloadPath", downloadPath);
                    intent.putExtra("packageName", packageName);
                    context.startActivity(intent);
                }
            }

        }
    };

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);

            int viewW = getMeasuredWidth();
            int curW = 0;
            int curH = 0;
            int textPad = ScreenUtil.dip2px(context, 30);
            int cowPad = ScreenUtil.dip2px(context, 15);
            int columnPad = ScreenUtil.dip2px(context, 5);
            row = 0;
            for (int i = 0; i < softwareInfos.size(); i++) {
                TextView textView = new TextView(context);
                int random = (int) (Math.random() * 4);
                textView.setBackgroundResource(bgs[random]);
                textView.setText(softwareInfos.get(i).getName());
                textView.setOnClickListener(onClickListener);
                textView.setTag(i);
                textView.setFocusable(true);
                textView.setFocusableInTouchMode(true);
                textView.setClickable(true);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(24);
                textView.setTextColor(Color.WHITE);

                textView.setPadding(textPad, 0, textPad, 0);
                textView.measure(0, 0);
                int itemW = textView.getMeasuredWidth();
                if (curW + itemW > viewW) {
                    curW = 0;
                    row++;
                    curH += textView.getMeasuredHeight();
                    curH += columnPad;
                    textView.setTag(R.integer.edge_view_key_of_is_Left, 1);
                    getChildAt(getChildCount() - 1).setTag(R.integer.edge_view_key_of_is_right, 1);
                }
                if (row == 0) {
                    textView.setTag(R.integer.edge_view_key_of_is_top, 1);
                }
                textView.setTag(R.integer.soft_recomment_view_key_of_row, row);

                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin = curW;
                layoutParams.topMargin = curH;

                curW += itemW;
                curW += cowPad;
                addView(textView, layoutParams);
            }

            for (int i = getChildCount() - 1; i >= 0; i--) {
                View view = getChildAt(i);
                Integer rowItem = (Integer) view.getTag(R.integer.soft_recomment_view_key_of_row);
                if (i == getChildCount() - 1) {
                    view.setTag(R.integer.edge_view_key_of_is_right, 1);
                }
                if (rowItem == row) {
                    view.setTag(R.integer.edge_view_key_of_is_bottom, 1);
                } else {
                    break;
                }
            }
        }
    };
}
