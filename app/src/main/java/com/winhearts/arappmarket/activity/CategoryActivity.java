package com.winhearts.arappmarket.activity;

import android.content.Context;
import android.os.Bundle;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.SoftwareTypeInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.SoftwaresByTypeEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsMenu;
import com.winhearts.arappmarket.modellevel.ModeUserErrorCode;
import com.winhearts.arappmarket.utils.LogDebugUtil;

/**
 * Created by lmh on 2017/10/24.
 */

public class CategoryActivity extends BaseActivity {
    private final String TAG = "CategoryActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        MenuItem item = (MenuItem) bundle.getSerializable("message");
        LogDebugUtil.e("MenuItem", item.toString());
        getSoftwareList(item.getChild().get(0));
    }

    private void getSoftwareList(MenuItem item) {
        SoftwareTypeInfo softwareTypeInfo = item.getSoftwareTypeInfo();
        SoftwaresByTypeEntity softwaresByTypeEntity = new SoftwaresByTypeEntity();
        softwaresByTypeEntity.setPageNo("1");
        softwaresByTypeEntity.setPageSize("10");
        softwaresByTypeEntity.setFirstTypeCode(softwareTypeInfo.getRootTypeCode());
        softwaresByTypeEntity.setChildTypeCode(softwareTypeInfo.getSubTypeCode());
        softwaresByTypeEntity.setDeviceType(softwareTypeInfo.getHandlerType());
        softwaresByTypeEntity.setOrderType(softwareTypeInfo.getOrderType());


        ModeLevelAmsMenu.querySoftwaresByType(mContext, TAG, softwaresByTypeEntity, new ModeUserErrorCode<Softwares>() {
            @Override
            public void onJsonSuccess(Softwares softwares) {
                LogDebugUtil.e("softwares", softwares.toString());
            }

            @Override
            public void onRequestFail(int code, Throwable e) {
                LogDebugUtil.e("onRequestFail", e.getMessage());
            }
        });
    }
}
