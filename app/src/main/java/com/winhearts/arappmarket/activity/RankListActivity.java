package com.winhearts.arappmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.drawee.view.SimpleDraweeView;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.constant.CommonHierarchy;
import com.winhearts.arappmarket.constant.Constant;
import com.winhearts.arappmarket.model.DownloadPath;
import com.winhearts.arappmarket.model.RankListInfo;
import com.winhearts.arappmarket.model.SoftwareType;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.view.CommonTitle;
import com.winhearts.arappmarket.view.RankElementsView;
import com.winhearts.arappmarket.view.HorizontalLayout;

import java.util.List;

/**
 * 排行榜列表
 * Created by lmh on 2016/4/25.
 */
public class RankListActivity extends BaseActivity {
    private DownloadPath downloadPath;
    private SimpleDraweeView sdbg;
    private CommonTitle commonTitle;
    private HorizontalLayout horizontalLayout;
    private RankListInfo rankListInfo;
    private String titleName;
    private int PAGE_SIZE;
    private boolean requestFocus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_list);
        Intent intent = this.getIntent();
        downloadPath = (DownloadPath) intent.getSerializableExtra("downloadPath");
        rankListInfo = (RankListInfo) intent.getSerializableExtra("rankListInfo");
//        LogDebugUtil.e("rankListInfo", rankListInfo.toString());
        titleName = intent.getStringExtra("titleName");
        PAGE_SIZE = 4;
        initView();
        setDate();

    }

    private void initView() {
        sdbg = (SimpleDraweeView) this.findViewById(R.id.sd_rank_list_bg);
        commonTitle = (CommonTitle) this.findViewById(R.id.common_title_rank_list_name);
        horizontalLayout = (HorizontalLayout) this.findViewById(R.id.vl_rank_list);
        horizontalLayout.setIsAutoScrollPage(false);
        horizontalLayout.setitemDistanceEnd(60);
        int viewWidth = ScreenUtil.getScreenWidth(this) * 16 / 18;
        int viewHeight = ScreenUtil.getScreenHeight(this) * 8 / 13;
        horizontalLayout.setSize(viewWidth, viewHeight, 1, 4);
        commonTitle.setTitle(titleName);
        CommonHierarchy.setBgImage(sdbg);

    }

    private void setDate() {
        if (rankListInfo != null) {
            List<SoftwareType> ranks = rankListInfo.getRanks();
            int size = ranks.size();
            for (int j = 0; j < size; j++) {
                RankElementsView view = new RankElementsView(this).bindData(ranks.get(j), Constant.rankTitle[j % 4], Constant.rankTitleDown[j % 4]);
                view.setHorizontalLayout(horizontalLayout);
                view.setDownloadPath(downloadPath);
                view.setTitleName(ranks.get(j).getName());
                horizontalLayout.addSimpleItemView(view, j, 0, j + 1, 1, ScreenUtil.dip2px(RankListActivity.this, 10));

            }
            if (requestFocus) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        RankElementsView view = (RankElementsView) horizontalLayout.getChildAt(0);
                        view.focusMoveToTop();
                        requestFocus = false;
                    }

                }, 10);

            }

        }


    }

}
