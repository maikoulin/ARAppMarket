package com.winhearts.arappmarket.modellevel;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.winhearts.arappmarket.activity.VpnStoreApplication;
import com.winhearts.arappmarket.network.SubVolleyResponseHandler;
import com.winhearts.arappmarket.network.UIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.winhearts.arappmarket.constant.ConstInfo;
import com.winhearts.arappmarket.model.NewReplyResEntity;
import com.winhearts.arappmarket.model.RelateReplyEntity;
import com.winhearts.arappmarket.utils.FileUtil;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.MacUtil;
import com.winhearts.arappmarket.utils.RequestUtil;
import com.winhearts.arappmarket.utils.Util;
import com.winhearts.arappmarket.utils.common.StorageUtils;
import com.winhearts.arappmarket.utils.cust.PrefNormalUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 用户相关新消息请求封装
 * Created by suxq on 2017/3/8.
 */

public class ModeLevelAmsNewReply {

    private static final String TAG = "ModeLevelAmsNewReply";

    /**
     * 默认填充 userId, lastTime 请求参数
     *
     * @see #getNewReplyList(Context, Map, Object)
     */
    public static void getNewReplyList(Context context, Object tag) {
        Map<String, String> map = new ArrayMap<String, String>();
        String dir = getUserCacheDir();
        String lastTime = null;
        if (!TextUtils.isEmpty(dir)) {
            String path = dir + File.separator + CACHE_LAST_TIME;
            Object obj = FileUtil.restoreObject(path);
            lastTime = obj != null ? (String) obj : null;
        }
        String userId = TextUtils.isEmpty(ConstInfo.accountWsId) ? MacUtil.getMacAddress() : ConstInfo.accountWsId;
        map.put("userId", userId);
        if (!TextUtils.isEmpty(lastTime)) {
            map.put("lastTime", lastTime);
        }
        getNewReplyList(context, map, tag);
    }

    /**
     * 当用户没有新消息时，接口返回的data为null，注意处理
     */
    public static void getNewReplyList(final Context context, Map<String, String> map, Object tag) {
        final String url = Util.getUrl(context, ModeUrl.GET_NEW_REPLY);
        Type type = new TypeToken<NewReplyResEntity>() {
        }.getType();
        Map<String, String> params = RequestUtil.getRequestParam(new Gson().toJson(map));
        SubVolleyResponseHandler<NewReplyResEntity> volleyHandler = new SubVolleyResponseHandler<NewReplyResEntity>(type, context);
        volleyHandler.setRetrytime(2);
        volleyHandler.setRequestTag(tag);
        volleyHandler.sendAsyncPostRequest(url, params, false, new UIDataListener<NewReplyResEntity>() {
            @Override
            public void onDataChanged(NewReplyResEntity data) {
                if (data != null) {
                    //获取之前还未读的消息数量
                    int lastUnreadMsgCnt = getUnreadCount();
                    //存储 lastTime, 注意不同用户 lastTime 不一样，所以需区分开
                    String lastTime = String.valueOf(data.getCurrentTime());
                    String dir = getUserCacheDir();
                    if (!TextUtils.isEmpty(dir)) {
                        String path = dir + File.separator + CACHE_LAST_TIME;
                        FileUtil.saveObject(path, lastTime);
                    }
                    //对接收到的 reply 消息数据包装一下，方便后续的使用
                    final List<RelateReplyEntity> replyList = new ArrayList<>();
                    for (NewReplyResEntity.RelatedReply rr : data.getRelatedReply()) {
                        for (NewReplyResEntity.ReplyContent rl : rr.getReplyList()) {
                            RelateReplyEntity replyEntity = new RelateReplyEntity(rl);
                            replyEntity.setCommentId(rr.getCommentId());
                            replyList.add(replyEntity);
                        }
                    }
                    //按时间最近排序
                    Collections.sort(replyList);
                    resetUnreadCount(lastUnreadMsgCnt + replyList.size());
                    saveData(replyList);
                    setIsPreview(true);
                } else {
                    setIsPreview(false);
                }
            }

            @Override
            public void onErrorHappened(int errorCode, Exception errorMessage) {
                setIsPreview(false);
                LogDebugUtil.d(TAG, "--------onRequestFail-----------------" + errorMessage.getMessage() + errorCode);
            }

            @Override
            public void onVolleyError(int errorCode, Exception errorMessage) {
                onErrorHappened(errorCode, errorMessage);
            }
        });
    }

    //定义缓存到本地的消息数据，请求时间和消息未读数的文件名，方便统一使用和更改命名
    private static final String CACHE_REPLY = "reply_data";
    private static final String CACHE_LAST_TIME = "ams_last_time";
    private static final String CACHE_UNREAD_COUNT = "news_unread_count";

    /**
     * 客户端缓存用户的消息数据（与我有关的评论消息），最多200条
     *
     * @param replyList
     */
    private static void saveData(List<RelateReplyEntity> replyList) {
        String dir = getUserCacheDir();
        if (!TextUtils.isEmpty(dir)) {
            String path = dir + File.separator + CACHE_REPLY;
            List<RelateReplyEntity> oldReplyList = restoreData();
            replyList.addAll(oldReplyList);
            FileUtil.saveObject(path, (replyList.size() > 200 ? replyList.subList(0, 200) : replyList));
            LogDebugUtil.d(TAG, "-----saveData---- " + replyList.size() + "  " + replyList.toString());
        } else {
            LogDebugUtil.e(TAG, "----缓存消息数据失败----");
        }
    }

    /**
     * 取出客户端缓存的用户的消息记录
     *
     * @return 没有历史消息时，返回size()=0的List
     */
    public static List<RelateReplyEntity> restoreData() {
        String dir = getUserCacheDir();
        List<RelateReplyEntity> replyList = new ArrayList<>();
        if (!TextUtils.isEmpty(dir)) {
            String path = dir + File.separator + CACHE_REPLY;
            Object obj = FileUtil.restoreObject(path);
            List<RelateReplyEntity> data = obj != null ? (List<RelateReplyEntity>) obj : null;
            if (data != null) {
                replyList.addAll(data);
            }
        }
        LogDebugUtil.d(TAG, "-------restoreData------" + replyList.size() + "   " + replyList.toString());
        return replyList;
    }

    /**
     * 重置消息未读数
     */
    public static void resetUnreadCount(int count) {
        String dir = getUserCacheDir();
        if (!TextUtils.isEmpty(dir)) {
            String path = dir + File.separator + CACHE_UNREAD_COUNT;
            FileUtil.saveObject(path, count);
        }
    }

    /**
     * 设置是否显示消息预览
     *
     * @param isPreview true or false
     */
    public static void setIsPreview(boolean isPreview) {
        PrefNormalUtils.putBoolean(PrefNormalUtils.IS_PREVIEW, isPreview);
    }

    //获取是否显示预览
    public static boolean getIsPreview() {
        boolean isPreView = PrefNormalUtils.getBoolean(PrefNormalUtils.IS_PREVIEW, false);
        if (isPreView) {
            PrefNormalUtils.putBoolean(PrefNormalUtils.IS_PREVIEW, false);
        }
        return isPreView;
    }

    /**
     * 获取消息未读数, 如果没有则返回0
     *
     * @return
     */
    public static int getUnreadCount() {
        String dir = getUserCacheDir();
        int count = 0;
        if (!TextUtils.isEmpty(dir)) {
            String path = dir + File.separator + CACHE_UNREAD_COUNT;
            Object obj = FileUtil.restoreObject(path);
            count = obj != null ? (int) obj : 0;
        }
        return count;
    }

    private static String getUserCacheDir() {
        File dir = StorageUtils.getUserDataDir();
        if (dir == null) {
            dir = VpnStoreApplication.getApp().getCacheDir();
            LogDebugUtil.e(TAG, "-----没有外部存储------");
        }

        if (dir != null) {
            //用网宿id或Mac命名的文件夹来标识不同账户缓存的消息记录
            String wsId = ConstInfo.accountWsId;
            if (TextUtils.isEmpty(wsId)) {
                wsId = MacUtil.getMacAddress();
            }
            //文件夹命名为 _网宿id， 防止与之前实现的以网宿id命名的文件起冲突
            String userDir = dir.getAbsolutePath() + File.separator + "_" + wsId;
            File file = new File(userDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } else {
            LogDebugUtil.e(TAG, "-----没有外部存储------");
            //消息的相关数据均缓存在外部存储，如果没有外部存储的话，暂时没有其他替代方案
            return null;
        }
    }
}
