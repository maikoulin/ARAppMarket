package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.model.AdvertisementEntity;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by lmh on 2015/7/29.
 * 广告网络
 */
public class ModeLevelAmsAd {
    private final static String TAG = "AmsAd";

    public static void queryAd(Context context, final ModeUser<AdvertisementEntity> user) {
        final String url = Util.getUrl(context, ModeUrl.AD_QUERY);
        Map<String, String> map = new ArrayMap<String, String>();
        map.put("advertSiteCode", "global_ad");
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<AdvertisementEntity>() {
        }.getType();
        SubVolleyResponseHandler<AdvertisementEntity> subVolleyResponseHandler = new SubVolleyResponseHandler<AdvertisementEntity>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<AdvertisementEntity>() {
            @Override
            public void onDataChanged(AdvertisementEntity data) {
                if (null != user) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d(TAG,
                        url + params.toString() + "--ModeLevelAmsAd---onRequestFail-----------" + errorMessage.toString());
                if (user != null) {
                    user.onRequestFail(errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }
}
