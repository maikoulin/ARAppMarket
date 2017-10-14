package com.winhearts.arappmarket.utils.cust;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.winhearts.arappmarket.model.ResInfoEntity;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;

import java.util.Map;

/**
 * 打开第三方app 工具类
 * Description:
 * Created by lmh on 2016/3/31.
 */
public class ThirdAppOpenUtil {

    private static String TAG = "ThirdAppOpenUtil";

    static public boolean openApp(Context context, ResInfoEntity.AppsBean bean) {
        if (bean == null || TextUtils.isEmpty(bean.getType())) {
            LoggerUtil.w(TAG, "bean == null || TextUtils.isEmpty(bean.getType())");
            return false;
        }
        if (bean.getType().equals("ACTION")) {
            if (TextUtils.isEmpty(bean.getAction())) {
                LoggerUtil.e(TAG, "TextUtils.isEmpty(bean.getAction())");
                return false;
            }
            Intent intent = new Intent(bean.getAction());
            if (!ManagerUtil.checkActivity3(context, intent)) {
                LoggerUtil.e(TAG, "activity is no exist");
                return false;
            }

            Map<String, String> map = bean.getParams();
            if (map != null && !map.isEmpty()) {
                for (String key : map.keySet()) {
                    intent.putExtra(key, map.get(key));
                }
            }

            if (!TextUtils.isEmpty(bean.getCategorys())) {
                intent.addCategory(bean.getCategorys());
            }
            ManagerUtil.recordOpenApps(context, bean.getPackageName());
            ModeLevelAmsUpload.updateNoOpenApp(context, bean.getPackageName(), PrefNormalUtils.ALL_NO_OPEN_APP);
            ModeLevelAmsUpload.updateNoOpenApp(context, bean.getPackageName(), PrefNormalUtils.NO_OPEN_APP);
            context.startActivity(intent);
            return true;

        } else if (!TextUtils.isEmpty(bean.getType()) && bean.getType().equals("DATA")) {
            if (!TextUtils.isEmpty(bean.getData())) {

                if (!ManagerUtil.startDataActivity(context, bean.getPackageName(), bean.getData())) {
                    return false;
                } else {
                    return true;
                }
            } else {
                LoggerUtil.w(TAG, "TextUtils.isEmpty(bean.getData())");
                return false;
            }
        }

        return true;
    }

}
