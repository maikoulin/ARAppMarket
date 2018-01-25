package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.model.ClientInfoForRecommendApp;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.model.PackageList;
import com.winhearts.arappmarket.model.QuerySoftwareInfoByPackageName;
import com.winhearts.arappmarket.model.QuerySoftwares;
import com.winhearts.arappmarket.model.QuerySoftwaresRecomment;
import com.winhearts.arappmarket.model.QuerySoftwareListByTopicCode;
import com.winhearts.arappmarket.model.SoftwareInfo;
import com.winhearts.arappmarket.model.Softwares;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.model.VersionNo;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据层-ams-还没开始正式使用
 *
 * @author liw
 */
public class ModeLevelAms {
    private final static String TAG = "ModeLevelAms";

    /**
     * 详情页获取应用详情
     *
     * @param mContext
     */
    public static void querySoftwareInfo(final Context mContext, String packageName, final ModeUserErrorCode<SoftwareInfo> user) {
        querySoftwareInfo(mContext, packageName, null, user);
    }

    public static void querySoftwareInfo(final Context mContext, String packageName, String tag, final ModeUserErrorCode<SoftwareInfo> user) {
        final String url = Util.getUrl(mContext, ModeUrl.QUERY_SOFTWATE_INFO);
        final QuerySoftwareInfoByPackageName mQuerySoftwareInfoByPackageName = new QuerySoftwareInfoByPackageName();
        mQuerySoftwareInfoByPackageName.setPackageName(packageName);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(mQuerySoftwareInfoByPackageName));
        Type type = new TypeToken<SoftwareInfo>() {
        }.getType();
        SubVolleyResponseHandler<SoftwareInfo> subVolleyResponseHandler = new SubVolleyResponseHandler<SoftwareInfo>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        if (TextUtils.isEmpty(tag)) {
            subVolleyResponseHandler.setRequestTag(tag);
        }
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<SoftwareInfo>() {
            @Override
            public void onDataChanged(SoftwareInfo data) {
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

    public static void querysoftwareDetailByTopicCode(final Context mContext, Object tag, Topic topic, final ModeUserErrorCode<Topic> modeUserErrorCode) {
        String url = Util.getUrl(mContext, ModeUrl.TOPIC_DETAIL);
        QuerySoftwareListByTopicCode mQuerySoftwareListByTopicCode = new QuerySoftwareListByTopicCode();
        mQuerySoftwareListByTopicCode.setCode(topic.getCode());
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(mQuerySoftwareListByTopicCode));
        Type type = new TypeToken<Topic>() {
        }.getType();
        SubVolleyResponseHandler<Topic> subVolleyResponseHandler = new SubVolleyResponseHandler<Topic>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Topic>() {
            @Override
            public void onDataChanged(Topic data) {

                if (modeUserErrorCode != null) {
                    modeUserErrorCode.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (modeUserErrorCode != null) {
                    modeUserErrorCode.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });

    }

    public static void querySoftwares(Context context, Object tag, final String keyword, final ModeUserErrorCode<Softwares> userErrorCode) {
        String url = Util.getUrl(context, ModeUrl.QUERY_SOFTWARES);
        QuerySoftwares mQuerySoftwares = new QuerySoftwares();
        mQuerySoftwares.setSearch(keyword);
        mQuerySoftwares.setPageNo("1");
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(mQuerySoftwares));
        Type type = new TypeToken<Softwares>() {
        }.getType();
        SubVolleyResponseHandler<Softwares> subVolleyResponseHandler = new SubVolleyResponseHandler<Softwares>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Softwares>() {
            @Override
            public void onDataChanged(Softwares data) {
                if (userErrorCode != null) {
                    userErrorCode.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (userErrorCode != null) {
                    userErrorCode.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void querySearchRecomment(final Context mContext, Object tag, final ModeUserErrorCode<QuerySoftwaresRecomment> userErrorCode) {
        String url = Util.getUrl(mContext, ModeUrl.QUERY_SEARCH_RECOMMENT);

        final Map<String, String> params = new HashMap<String, String>();
        Type type = new TypeToken<QuerySoftwaresRecomment>() {
        }.getType();
        SubVolleyResponseHandler<QuerySoftwaresRecomment> subVolleyResponseHandler = new SubVolleyResponseHandler<QuerySoftwaresRecomment>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<QuerySoftwaresRecomment>() {
            @Override
            public void onDataChanged(QuerySoftwaresRecomment data) {
                if (userErrorCode != null) {
                    userErrorCode.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (userErrorCode != null) {
                    userErrorCode.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void queryLayout(Context mContext, Object tag, VersionNo versionNo, final ModeUserErrorCode<Layout> modeUserErrorCode) {
        String url = Util.getUrl(mContext, ModeUrl.QUERY_LAYOUT);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(versionNo));
        Type type = new TypeToken<Layout>() {
        }.getType();
        SubVolleyResponseHandler<Layout> subVolleyResponseHandler = new SubVolleyResponseHandler<>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.setRequestTag(tag);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Layout>() {
            @Override
            public void onDataChanged(Layout data) {
                if (modeUserErrorCode != null) {
                    modeUserErrorCode.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (modeUserErrorCode != null) {
                    modeUserErrorCode.onRequestFail(errorCode, errorMessage);
                }
            }

//            @Override
//            public void onStringChanged(String src) {
////                File file = new File(Environment.getExternalStorageDirectory() + "/layout.text");
////                try {
////                    FileUtils.write(file, src);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//                LogDebugUtil.e("layoutStr", src);
//            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void queryRecommendSoftwares(final Context mContext, String packageName, final ModeUserErrorCode<Softwares> userErrorCode) {
        String url = Util.getUrl(mContext, ModeUrl.QUERY_RECOMMEND_SOFTWATES);
        Map<String, String> map = new HashMap<>();
        map.put("packageName", packageName);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<Softwares>() {
        }.getType();
        SubVolleyResponseHandler<Softwares> subVolleyResponseHandler = new SubVolleyResponseHandler<Softwares>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<Softwares>() {
            @Override
            public void onDataChanged(Softwares data) {
                if (userErrorCode != null) {
                    userErrorCode.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (userErrorCode != null) {
                    userErrorCode.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void getRecommendAppData(Context context, String recentBrowseApp, final ModeUserErrorCode<Softwares> userErrorCode) {
        String urlStr = Util.getUrl(context, ModeUrl.RECOMMEND_APP);
        Type type = new TypeToken<Softwares>() {
        }.getType();

        Type type2 = new TypeToken<ArrayList<String>>() {
        }.getType();

        ArrayList<String> packageNames = new Gson().fromJson(recentBrowseApp, type2);

        Map<String, String> params2 = RequestUtil.getRequestParam("clientInfo", new Gson().toJson(new ClientInfoForRecommendApp(packageNames)));

        SubVolleyResponseHandler<Softwares> subVolleyResponseHandler = new SubVolleyResponseHandler<Softwares>(type, context);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendAsyncPostRequest(urlStr, params2, false, new UIDataListener<Softwares>() {
            @Override
            public void onDataChanged(Softwares data) {
                if (userErrorCode != null) {
                    userErrorCode.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                if (userErrorCode != null) {
                    userErrorCode.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    public static void queryAllPackages(final Context mContext, final ModeUserErrorCode<PackageList> modeUserErrorCode) {
        final String url = Util.getUrl(mContext, ModeUrl.QUERY_All_PACKAGES_STATUS);
        String packageList = Pref.getString(Pref.PACKAGE_LIST, mContext, "");
        String version = "";
        if (!TextUtils.isEmpty(packageList)) {
            PackageList response = new Gson().fromJson(packageList, PackageList.class);
            if (response != null) {
                version = response.getVersion();
            }
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("version", version);
        final Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        Type type = new TypeToken<PackageList>() {
        }.getType();
        SubVolleyResponseHandler<PackageList> subVolleyResponseHandler = new SubVolleyResponseHandler<PackageList>(type, mContext);
        subVolleyResponseHandler.setRetrytime(2);
        subVolleyResponseHandler.sendPostRequest(url, params, false, new UIDataListener<PackageList>() {
            @Override
            public void onDataChanged(PackageList data) {
                if (data != null) {
                    Pref.saveString(Pref.PACKAGE_LIST, new Gson().toJson(data), mContext);
                }
                if (modeUserErrorCode != null) {
                    modeUserErrorCode.onJsonSuccess(data);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                LogDebugUtil.d(TAG, url + errorCode + errorMessage.getMessage());
                if (modeUserErrorCode != null) {
                    modeUserErrorCode.onRequestFail(errorCode, errorMessage);
                }
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }
}
