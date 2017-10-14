package com.winhearts.arappmarket.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.model.Element;
import com.winhearts.arappmarket.model.Layout;
import com.winhearts.arappmarket.model.MenuItem;
import com.winhearts.arappmarket.model.Screen;
import com.winhearts.arappmarket.model.SoftwareType;
import com.winhearts.arappmarket.model.SoftwareTypesResInfo;
import com.winhearts.arappmarket.model.Topic;
import com.winhearts.arappmarket.utils.LogDebugUtil;
import com.winhearts.arappmarket.utils.LoggerUtil;
import com.winhearts.arappmarket.utils.Pref;
import com.winhearts.arappmarket.utils.common.ManagerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmh on 2016/11/22.
 * 外部URL跳转中转activity
 */
public class TransitActivity extends BaseActivity {
    private final String TAG = "TransitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        String type;
        if (uri != null) {
            type = uri.getQueryParameter("type");
            if (!TextUtils.isEmpty(type)) {
                switch (type) {
                    case "SOFTWARE":
                        String packageName = uri.getQueryParameter("packageName");
                        LogDebugUtil.i(TAG, packageName);
                        boolean isExist = ManagerUtil.isPackageAppExist(this, packageName);
                        if (isExist) {
                            ManagerUtil.startApk(this, packageName);
                            finish();
                        } else {
                            Intent appDetailIntent = new Intent(this, AppDetailActivity.class);
                            appDetailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            appDetailIntent.putExtra("packageName", packageName);
                            appDetailIntent.putExtra("isBackMain", uri.getBooleanQueryParameter("isBackMain", false));
                            startActivity(appDetailIntent);
                            finish();
                        }
                        break;
                    case "APPMANAGER":
                        Intent myAPPIntent = new Intent(this, MainActivity.class);
                        myAPPIntent.putExtra("isMyApp", true);
                        myAPPIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myAPPIntent);
                        finish();
                        break;
                    case "TOPIC":
                        Intent topicIntent = new Intent(this,
                                TopicActivity.class);
                        topicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        Topic topic = new Topic();
                        topic.setCode(uri.getQueryParameter("code"));
                        topic.setImageUrl("imageUrl");
                        bundle.putSerializable("topic", topic);
                        topicIntent.putExtras(bundle);
                        topicIntent.putExtra("isBackMain", uri.getBooleanQueryParameter("isBackMain", false));
                        startActivity(topicIntent);
                        finish();
                        break;
                    case "SOFTWARE_TYPE":
                        Intent softwareIntent = new Intent(this,
                                AppCategoryActivity.class);
                        String recommendName = uri.getQueryParameter("recommendName");
                        softwareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        String layout = Pref.getString(Pref.LAYOUT_STRING, this, "");
                        if (!TextUtils.isEmpty(layout) && !TextUtils.isEmpty(recommendName)) {
                            Layout saveLayout = new Gson().fromJson(layout, Layout.class);
                            List<MenuItem> menuItems = saveLayout.getMenuInfos();
                            for (MenuItem item : menuItems) {
                                if ("SELF_DEFINE".equals(item.getMenuDataType())) {
                                    List<Screen> screens = item.getScreens();
                                    for (Screen screen : screens) {
                                        ArrayList<Element> elements = screen.getElementResInfoList();
                                        for (Element element : elements) {
                                            if (element.getResType().equals("SOFTWARE_TYPE") && element.getResName().equals(recommendName)) {
                                                SoftwareTypesResInfo softwareTypesResInfo = new Gson().fromJson(element.getResInfo(), SoftwareTypesResInfo.class);
                                                ArrayList<SoftwareType> softwareTypes = softwareTypesResInfo.getSoftwareTypes();
                                                Bundle appCategoryBundle = new Bundle();
                                                if (softwareTypes.size() > 1) {
                                                    softwareIntent.putExtra("titleName", element.getResName());
                                                }
                                                appCategoryBundle.putSerializable("softwareTypes", softwareTypes);
                                                softwareIntent.putExtra("orderType", softwareTypesResInfo.getOrderType());
                                                softwareIntent.putExtra("defaultIndex", softwareTypesResInfo.getDefaultIndex());
                                                softwareIntent.putExtras(appCategoryBundle);
                                                softwareIntent.putExtra("isBackMain", uri.getBooleanQueryParameter("isBackMain", false));
                                                startActivity(softwareIntent);
                                                finish();
                                                return;
                                            } else {
                                                showError();
                                            }
                                        }
                                    }
                                } else {
                                    showError();
                                }
                            }
                        } else {
                            showError();
                        }

                        break;
                    case "NONE":
                        Intent mainIntent = new Intent(this,
                                MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mainIntent.putExtra("selectItem", uri.getQueryParameter("selectItem"));
                        startActivity(mainIntent);
                        finish();
                        break;

                    case "ACCOUNT_MANAGER":
                        Intent accountIntent = new Intent(this,
                                AccountManagerActivity.class);
                        accountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(accountIntent);
                        finish();
                        break;

                    case "LOAD":
                        Intent loadIntent = new Intent(this,
                                LoadActivity.class);
                        loadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        loadIntent.putExtra("layoutCode", uri.getQueryParameter("layoutCode"));
                        startActivity(loadIntent);
                        finish();
                        break;
                    default:
                        showError();
                        break;
                }
            } else {
                showError();
            }
        } else {
            showError();
            LoggerUtil.w(TAG, "uri==null");
        }
    }

    private void showError() {
        setContentView(R.layout.widget_server_error_show);
        this.findViewById(R.id.server_error_content).setBackgroundResource(R.drawable.background);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Uri uri = getIntent().getData();
            boolean isBackMain = false;
            if (uri != null) {
                isBackMain = uri.getBooleanQueryParameter("isBackMain", false);
            }
            if (isBackMain) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                finish();
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
