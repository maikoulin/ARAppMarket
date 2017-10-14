package com.winhearts.arappmarket.utils.cust;

import android.text.TextUtils;

import com.winhearts.arappmarket.model.RegisterThirdEntity;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.MacUtil;
import com.winhearts.arappmarket.utils.common.ShellUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取第三方信息，主要为硬件信息
 * Created by lmh on 2016/8/18.
 */
public class GetThirdPartyInfoUtil {


    /**
     * 用命令读取系统属性ro.serialno
     *
     * @return
     */
    public static String getIPGZGD() {
        ShellUtils.CommandResult result = ShellUtils.execCommand("getprop | grep ro.serialno", false, true);
        LogDebugUtil.d("GetThirdPartyInfoUtil", "--------" + result);
        String content = result.successMsg;
        if (!TextUtils.isEmpty(content)) {
            if (content.contains("ro.serialno")) {
                String s[] = content.split(":");
                if (s != null && s.length > 1) {
                    String content2 = s[1].trim();
                    if (TextUtils.isEmpty(content2) || content2.length() < 3) {
                        return null;
                    }
                    LogDebugUtil.d("GetThirdPartyInfoUtil", "content2 = " + content2);
                    String info = content2.substring(1, content2.length() - 1);
                    LogDebugUtil.d("GetThirdPartyInfoUtil", "info = " + info);
                    return info;
                }
            }
        }
        return null;
    }

    public static List<RegisterThirdEntity> getThirdPartyInfos() {
        List<RegisterThirdEntity> registerThirdEntities = new ArrayList<RegisterThirdEntity>();
        String caNum = CaCountUtil.getCaNum();
        if (!TextUtils.isEmpty(caNum)) {
            RegisterThirdEntity registerThirdEntity = new RegisterThirdEntity();
            registerThirdEntity.setThirdPartyType("GZGD");
            registerThirdEntity.setThirdPartyId(caNum);
            registerThirdEntities.add(registerThirdEntity);
        }
        String macAddress = MacUtil.getMacAddress();
        if (!TextUtils.isEmpty(macAddress)) {
            RegisterThirdEntity registerThirdEntity = new RegisterThirdEntity();
            registerThirdEntity.setThirdPartyType("DEVICE");
            registerThirdEntity.setThirdPartyId(macAddress);
            registerThirdEntities.add(registerThirdEntity);
        }
        String ipGzgd = GetThirdPartyInfoUtil.getIPGZGD();
        if (!TextUtils.isEmpty(ipGzgd)) {
            RegisterThirdEntity registerThirdEntity = new RegisterThirdEntity();
            registerThirdEntity.setThirdPartyType("IPGZGD");
            registerThirdEntity.setThirdPartyId(ipGzgd);
            registerThirdEntities.add(registerThirdEntity);
        }
        return registerThirdEntities;
    }

}
