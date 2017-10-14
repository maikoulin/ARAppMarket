package com.winhearts.arappmarket.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.utils.common.ManagerUtil;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 软件处理工具
 * Created by lmh on 2016/3/8.
 */
public class SoftwareUtil {

    public static SoftwareInfo getShowSoftware(Context context, List<SoftwareInfo> softwareInfos) {
        if (!ContainerUtil.isEmptyOrNull(softwareInfos)) {
            int size = softwareInfos.size();
            for (int i = 0; i < size; i++) {
                if (context.getPackageManager().getLaunchIntentForPackage(softwareInfos.get(i).getPackageName()) == null) {
                    return softwareInfos.get(i);
                } else if (i == size - 1) {
                    return softwareInfos.get(0);
                }

            }
        }
        return null;
    }

    public static List<Map.Entry<String, Long>> updateUsedRecord(Context context) {
        String usedRecord = PrefNormalUtils.getString(context, PrefNormalUtils.APP_USED_RECORD, null);
        long timeMillis = System.currentTimeMillis();
        Map<String, Long> appInfos = AppManager.getUserAppPkg(context);
        List<String> recentPkg = ManagerUtil.getRecentTasks(context);
        if (!TextUtils.isEmpty(usedRecord)) {
            HashMap<String, Long> map = new Gson().fromJson(usedRecord, new TypeToken<HashMap<String, Long>>() {
            }.getType());
            Set<String> packageNames = appInfos.keySet();
            for (String packageName : packageNames) {
                //写入老数据
                if (map.containsKey(packageName)) {
                    appInfos.put(packageName, map.get(packageName));
                }
                //更新现在的运行的应用记录
                if (recentPkg.contains(packageName)) {
                    appInfos.put(packageName, timeMillis);
                }
            }
        }
        PrefNormalUtils.putString(context, PrefNormalUtils.APP_USED_RECORD, new Gson().toJson(appInfos));
        List<Map.Entry<String, Long>> list_Data = new ArrayList<>(appInfos.entrySet());
        Collections.sort(list_Data, new Comparator<Map.Entry<String, Long>>() {

            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                if (o1.getValue() >= o2.getValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return list_Data;
    }

}
