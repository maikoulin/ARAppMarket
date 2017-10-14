package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.model.OrderChangeResEntity;
import com.winhearts.arappmarket.model.OrderCreateEntity;
import com.winhearts.arappmarket.model.OrderCreateRevalEntity;
import com.winhearts.arappmarket.model.OrderQueryEntity;
import com.winhearts.arappmarket.model.RegisterThirdEntity;
import com.winhearts.arappmarket.utils.ContainerUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.cust.GetThirdPartyInfoUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * AMS请求：支付类
 */
public class ModeLevelAmsPaySdk {
    private final static String TAG = "pay";

    /**
     * 创建订单
     *
     * @param context
     * @param entity
     * @param user
     */
    static public void createOrder(final Context context, final OrderCreateEntity entity,
                                   final ModeUserErrorCode<OrderCreateRevalEntity> user) {
        final String url = Util.getUrl(context, ModeUrl.ORDER_CREATE_3_6);

        final String jsonString = new Gson().toJson(entity);
        final Map<String, String> params = RequestUtil.getRequestParam(jsonString);
        params.put("wsId", ConstInfo.accountWsId);
        params.put("loginToken", ConstInfo.accountTokenId);
        Type type = new TypeToken<OrderCreateRevalEntity>() {
        }.getType();
        SubVolleyResponseHandler<OrderCreateRevalEntity> subVolleyResponseHandler = new SubVolleyResponseHandler<OrderCreateRevalEntity>(type, context);
        subVolleyResponseHandler.setRetrytime(3);
        subVolleyResponseHandler.sendPostRequest(url, params, true, new UIDataListener<OrderCreateRevalEntity>() {
            @Override
            public void onDataChanged(OrderCreateRevalEntity data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (user != null) {
                    user.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });

    }

    /**
     * 查询订单
     *
     * @param context
     * @param entity
     * @param user
     */
    static public void quaryOrder(final Context context, OrderQueryEntity entity,
                                  final ModeUser<Map<String, String>> user) {
        final String url = Util.getUrl(context, ModeUrl.ORDER_QUARY);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(entity));
        params.put("wsId", ConstInfo.accountWsId);
        params.put("loginToken", ConstInfo.accountTokenId);
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        SubVolleyResponseHandler<Map<String, String>> subVolleyResponseHandler = new SubVolleyResponseHandler<Map<String, String>>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Map<String, String>>() {
            @Override
            public void onDataChanged(Map<String, String> data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
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

    /**
     * 切换订单
     */
    static public void changeOrderPayType(final Context context, Object tag, String appKey, String orderCode, String channelType,
                                          final ModeUser<OrderChangeResEntity> user) {
        final String url = Util.getUrl(context, ModeUrl.ORDER_CREATE_PAY_3_6);
        Map<String, Object> map = new ArrayMap<String, Object>();
        map.put("orderCode", orderCode);
        map.put("channelType", channelType);
        map.put("appKey", appKey);
        List<RegisterThirdEntity> registerThirdEntities = GetThirdPartyInfoUtil.getThirdPartyInfos();
        if (!ContainerUtil.isEmptyOrNull(registerThirdEntities)) {
            map.put("thirdPartyInfos", registerThirdEntities);
        }
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        params.put("wsId", ConstInfo.accountWsId);
        params.put("loginToken", ConstInfo.accountTokenId);
        LogDebugUtil.d(TAG, "-----params-----------" + params.toString());
        Type type = new TypeToken<OrderChangeResEntity>() {
        }.getType();
        SubVolleyResponseHandler<OrderChangeResEntity> subVolleyResponseHandler = new SubVolleyResponseHandler<>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<OrderChangeResEntity>() {
            @Override
            public void onDataChanged(OrderChangeResEntity data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
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

    static public void cancelOrderPayType(final Context context, String orderCode,
                                          final ModeUser<String> user) {
        final String url = Util.getUrl(context, ModeUrl.ORDER_CANCEL);
        final Map<String, String> map = new ArrayMap<String, String>();
        map.put("orderCode", orderCode);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        params.put("wsId", ConstInfo.accountWsId);
        params.put("loginToken", ConstInfo.accountTokenId);

        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                LogDebugUtil.d("payCancel",
                        url + "&" + params.toString() + "-onSuccess-" + (data == null ? "null" : data));
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d("payCancel",
                        url + params.toString() + "--onRequestFail--" + errorMessage.toString());
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

    public static void queryCash(final Context context, Object tag, String appKey, String orderCode, String channelType, final ModeUser<Map<String, String>> user) {
        final String url = Util.getUrl(context, ModeUrl.ORDER_CREATE_PAY_3_6);
        Map<String, Object> map = new ArrayMap<String, Object>();
        map.put("orderCode", orderCode);
        map.put("channelType", channelType);
        map.put("appKey", appKey);
        List<RegisterThirdEntity> registerThirdEntities = GetThirdPartyInfoUtil.getThirdPartyInfos();
        if (!ContainerUtil.isEmptyOrNull(registerThirdEntities)) {
            map.put("thirdPartyInfos", registerThirdEntities);
        }
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        params.put("wsId", ConstInfo.accountWsId);
        params.put("loginToken", ConstInfo.accountTokenId);
        LogDebugUtil.d(TAG, "-----params-----------" + params.toString());
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        SubVolleyResponseHandler<Map<String, String>> subVolleyResponseHandler = new SubVolleyResponseHandler<Map<String, String>>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, true, new UIDataListener<Map<String, String>>() {
            @Override
            public void onDataChanged(Map<String, String> data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
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

    public static void payByGzgd(final Context context, String appKey, String channelType, String orderCode, final ModeUser<String> user) {
        final String url = Util.getUrl(context, ModeUrl.PAY_BY_GZGD_3_6);
        HashMap map = new HashMap();
        map.put("appKey", appKey);
        map.put("orderCode", orderCode);
//        map.put("sm_no", CaCountUtil.getCaNum());
        map.put("channelType", channelType);
        List<RegisterThirdEntity> registerThirdEntities = GetThirdPartyInfoUtil.getThirdPartyInfos();
        if (!ContainerUtil.isEmptyOrNull(registerThirdEntities)) {
            map.put("thirdPartyInfos", registerThirdEntities);
        }
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        params.put("wsId", ConstInfo.accountWsId);
        params.put("loginToken", ConstInfo.accountTokenId);
        Type type = new TypeToken<String>() {
        }.getType();
        SubVolleyResponseHandler<String> subVolleyResponseHandler = new SubVolleyResponseHandler<String>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, true, new UIDataListener<String>() {
            @Override
            public void onDataChanged(String data) {
                if (user != null) {
                    user.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
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
