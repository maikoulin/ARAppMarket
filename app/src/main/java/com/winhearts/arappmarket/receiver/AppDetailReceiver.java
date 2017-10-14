package com.winhearts.arappmarket.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.winhearts.arappmarket.activity.AppCategoryActivity;
import com.winhearts.arappmarket.activity.AppDetailActivity;
import com.winhearts.arappmarket.activity.LoadActivity;
import com.winhearts.arappmarket.activity.MainActivity;
import com.winhearts.arappmarket.activity.TopicActivity;
import com.winhearts.arappmarket.model.SoftwareType;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.modellevel.ModeLevelAmsUpload;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.common.ManagerUtil;

import java.util.ArrayList;

/**
 * 广播接收者：刚开始用于应用详情，后扩展成全部广播跳转的接收中转。
 * Created by lmh on 2015/10/22.
 */
public class AppDetailReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogDebugUtil.d("AppDetailReceiver", "-------------");
        String action = intent.getAction();
        ModeLevelAmsUpload.uploadLaunchClickData(context, intent.getStringExtra("location"), null);
        String type = intent.getStringExtra("type");
        if (action.equals("com.winhearts.APP_DETAIL_RECEIVER") && !TextUtils.isEmpty(type)) {
            switch (type) {
                case "SOFTWARE":
                    String packageName = intent.getStringExtra("packageName");
                    boolean isExist = ManagerUtil.isPackageAppExist(context, packageName);
                    if (isExist) {
                        ManagerUtil.startApk(context, packageName);
                    } else {
                        Intent appDetailIntent = new Intent(context, AppDetailActivity.class);
                        appDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        appDetailIntent.putExtra("packageName", packageName);
                        appDetailIntent.putExtra("isBackMain", intent.getBooleanExtra("isBackMain",false));
                        context.startActivity(appDetailIntent);
                    }
                    break;
                case "APPMANAGER":
                    Intent myAPPIntent = new Intent(context, MainActivity.class);
                    myAPPIntent.putExtra("isMyApp", true);
                    myAPPIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(myAPPIntent);
                    break;
                case "TOPIC":
                    Intent topicIntent = new Intent(context,
                            TopicActivity.class);
                    topicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    Topic topic = new Topic();
                    topic.setCode(intent.getStringExtra("code"));
                    topic.setImageUrl("imageUrl");
                    bundle.putSerializable("topic", topic);
                    topicIntent.putExtras(bundle);
                    topicIntent.putExtra("isBackMain", intent.getBooleanExtra("isBackMain",false));
                    context.startActivity(topicIntent);
                    break;
                case "SOFTWARE_TYPE":
                    Intent softwareIntent = new Intent(context,
                            AppCategoryActivity.class);
                    softwareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SoftwareType softwareType = new SoftwareType();
                    softwareType.setChildTypeCodes(intent.getStringExtra("subTypeCode"));
                    softwareType.setFirstTypeCodes(intent.getStringExtra("rootTypeCode"));
                    softwareType.setName(intent.getStringExtra("resName"));
                    softwareIntent.putExtra("orderType", intent.getStringExtra("orderType"));
                    softwareIntent.putExtra("isBackMain", intent.getBooleanExtra("isBackMain",false));
                    ArrayList<SoftwareType> softwareTypes = new ArrayList<SoftwareType>();
                    softwareTypes.add(softwareType);
                    softwareIntent.putExtra("softwareTypes", softwareTypes);
                    context.startActivity(softwareIntent);
                    break;
                case "NONE":
                    Intent mainIntent = new Intent(context,
                            MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mainIntent.putExtra("selectItem", intent.getStringExtra("selectItem"));
                    context.startActivity(mainIntent);
                    break;
                case "LOAD":
                    Intent loadIntent = new Intent(context,
                            LoadActivity.class);
                    loadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    loadIntent.putExtra("layoutCode", intent.getStringExtra("layoutCode"));
                    context.startActivity(loadIntent);
                    break;
            }
        }
    }
}
