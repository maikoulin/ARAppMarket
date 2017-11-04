package com.winhearts.arappmarket.modellevel;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.model.QuerySoftwareListByTopicCode;
import com.winhearts.arappmarket.model.SoftwareTypeInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.SoftwaresByMultiTypeEntity;
import com.winhearts.arappmarket.model.SoftwaresByTypeEntity;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.model.TopicList;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * AMS请求：专辑类
 */
public class ModeLevelAmsMenu {
    private static final String TAG = "ModeLevelAmsMenu";
    private static final boolean DEBUG = true;

    public static void queryTopicSoftWareList(final Context mContext, Object tag, final int offset, final int limit, final Topic mTopic,
                                              final ModeUserErrorCode<Softwares> userByTopicSoftwareList) {

        final String url = Util.getUrl(mContext, ModeUrl.QUERY_SOFTWAREList);
        QuerySoftwareListByTopicCode mQuerySoftwareListByTopicCode = new QuerySoftwareListByTopicCode();
        mQuerySoftwareListByTopicCode.setCode(mTopic.getCode());
        mQuerySoftwareListByTopicCode.setPageNo(String.valueOf(offset));
        mQuerySoftwareListByTopicCode.setPageSize(String.valueOf(limit));
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(mQuerySoftwareListByTopicCode));
        Type type = new TypeToken<Softwares>() {
        }.getType();
        SubVolleyResponseHandler<Softwares> subVolleyResponseHandler = new SubVolleyResponseHandler<Softwares>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Softwares>() {
            @Override
            public void onDataChanged(Softwares data) {

                if (userByTopicSoftwareList != null) {
                    userByTopicSoftwareList.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (userByTopicSoftwareList != null) {
                    userByTopicSoftwareList.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });

    }

    public static void querySoftWareList(Context context, Object tag, int offset, int limit, SoftwareTypeInfo info,
                                         ModeUserErrorCode<Softwares> userBySoftwares) {

        //3.6.0
        SoftwaresByMultiTypeEntity softwaresByMultiTypeEntity = new SoftwaresByMultiTypeEntity();
        softwaresByMultiTypeEntity.setStart(String.valueOf((offset - 1) * limit + 1));
        softwaresByMultiTypeEntity.setEnd(String.valueOf(offset * limit));

        if (info != null) {
            softwaresByMultiTypeEntity.setFirstTypeCodes(info.getRootTypeCode());
            softwaresByMultiTypeEntity.setChildTypeCodes(info.getSubTypeCode());
            softwaresByMultiTypeEntity.setOrderType(info.getOrderType());
            softwaresByMultiTypeEntity.setDeviceTypes(info.getHandlerType());
        }
        LogDebugUtil.d(TAG, "querySoftWareList: " + softwaresByMultiTypeEntity.toString());
        querySoftwaresByMultiType(context, tag, softwaresByMultiTypeEntity, userBySoftwares);
    }

    /**
     * 2.49	通过多个软件类型获取软件列表
     *
     * @param context
     * @param softwaresByMultiTypeEntity
     * @param user
     */
    public static void querySoftwaresByMultiType(Context context, Object tag, SoftwaresByMultiTypeEntity softwaresByMultiTypeEntity, final ModeUserErrorCode<Softwares> user) {

        final String url = Util.getUrl(context, ModeUrl.QUERY_SOFTWATES_BY_MULTI_TYPE);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(softwaresByMultiTypeEntity));
        Type type = new TypeToken<Softwares>() {
        }.getType();
        SubVolleyResponseHandler<Softwares> subVolleyResponseHandler = new SubVolleyResponseHandler<>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Softwares>() {
            @Override
            public void onDataChanged(Softwares data) {
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

    public static void querySoftwaresByType(Context context, Object tag, SoftwaresByTypeEntity softwaresByTypeEntity, final ModeUserErrorCode<Softwares> user) {

        final String url = Util.getUrl(context, ModeUrl.QUERY_SOFTWATES_BY_TYPE);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(softwaresByTypeEntity));
        Type type = new TypeToken<Softwares>() {
        }.getType();
        SubVolleyResponseHandler<Softwares> subVolleyResponseHandler = new SubVolleyResponseHandler<>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Softwares>() {
            @Override
            public void onDataChanged(Softwares data) {
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
//
//            @Override
//            public void onStringChanged(String src) {
//                LogDebugUtil.e("onStringChanged", src);
//            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    /**
     * 2.5	获取专题列表
     *
     * @param context
     * @param pageNo   页码
     * @param pageSize 每页个数
     * @param sortType 排序类型
     * @param user     返回接口
     */
    public static void queryTopicList(Context context, String pageNo, String pageSize, String sortType, final ModeUserErrorCode<TopicList> user) {

        final String url = Util.getUrl(context, ModeUrl.QUERY_TOPIC_LIST);
        Map<String, String> map = new HashMap<String, String>();
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        map.put("sortType", sortType);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<TopicList>() {
        }.getType();
        SubVolleyResponseHandler<TopicList> subVolleyResponseHandler = new SubVolleyResponseHandler<TopicList>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<TopicList>() {
            @Override
            public void onDataChanged(TopicList topicList) {
                if (user != null) {
                    user.onJsonSuccess(topicList);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d(TAG, "--------code----" + errorCode + "--e----" + errorMessage.getMessage());
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

}
