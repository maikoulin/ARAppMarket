package com.winhearts.arappmarket.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.download.manage.ManagerDownloadImpl;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.utils.ScreenUtil;
import com.winhearts.arappmarket.utils.adapter.NewUserRecommendAdapter;
import com.winhearts.arappmarket.utils.common.ToastUtils;

import java.util.List;

/**
 * 新用户提示对话框
 */
public class NewUserDialog extends Dialog {


    private final Context mContext;
    private final LayoutParams lp;

    private RecommendAppGridView mRecommendAppGridView;
    private NewUserRecommendAdapter mRecommendAppAdapter;
    private List<SoftwareInfo> mSoftwareInfoList;


    public NewUserDialog(Context context, List<SoftwareInfo> softwareInfos) {
        super(context, R.style.dialog);
        // 初始
        this.mContext = context;

        setContentView(R.layout.dialog_new_user_enter);
        // 初始UI
        initView();

        // 设置window属性
        lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = ScreenUtil.getScreenWidth(context);
        lp.height = ScreenUtil.getScreenHeight(context);
        getWindow().setAttributes(lp);

        if (softwareInfos.size() <= 4) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecommendAppGridView.getLayoutParams();
            layoutParams.setMargins(0, 100, 0, 0);
            mRecommendAppGridView.setLayoutParams(layoutParams);
        }
        mRecommendAppAdapter = new NewUserRecommendAdapter(context, softwareInfos);
        mRecommendAppGridView.setAdapter(mRecommendAppAdapter);
        mSoftwareInfoList = softwareInfos;
    }


    /**
     * 根据type初始化
     */
    private void initView() {
        findViewById(R.id.bt_dialog_new_user_install).requestFocus();
        findViewById(R.id.bt_dialog_new_user_install).setOnClickListener(onClickListener);
        findViewById(R.id.bt_dialog_new_user_no_thank).setOnClickListener(onClickListener);

        mRecommendAppGridView = (RecommendAppGridView) findViewById(R.id.rcv_ner_user);


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_dialog_new_user_install:
                    if (getInstallData()) {
                        dismiss();
                    } else {
                        ToastUtils.show(mContext, "请至少选择一个应用");
                    }
                    break;
                case R.id.bt_dialog_new_user_no_thank:
                    dismiss();
                    break;
            }
        }
    };

    private boolean getInstallData() {
        boolean isHasInstall = false;
        for (int i = 0; i < mRecommendAppGridView.getChildCount(); i++) {
            View view = mRecommendAppGridView.getChildAt(i);
            int index = (int) view.getTag();
            if (view.findViewById(R.id.iv_recommend_app_select).getVisibility() == View.VISIBLE) {
                SoftwareInfo softwareInfo = mSoftwareInfoList.get(index);
                ManagerDownloadImpl managerDownload = new ManagerDownloadImpl(mContext, softwareInfo.getName(), softwareInfo.getPackageName(), softwareInfo.getIcon());
                managerDownload.startGetUrl2Down();
                isHasInstall = true;
            }
        }
        return isHasInstall;
    }


}
