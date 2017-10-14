package com.winhearts.arappmarket.utils;

import android.app.Activity;
import android.os.Environment;
import android.util.DisplayMetrics;

/**
 * Description:配置信息
 * 
 * @author kzy
 * @created 2014-3-24 
 */

public class DisplayConfig {
    public final static String ACTION_CONTROL_MESSAGE = "download_control";     
    public final static String ACTION_ADD_MESSAGE = "add_control";
	public static final String ACTION_START_SERVICE = "start_download_service";
    public final static String ACTION_RESULT_MESSAGE = "result_control";
    public final static String ACTION_UPDATE_DB = "update_db";  
    public final static String ACTION_UPDATE_RECORD = "update_record";  
    public final static String ACTION_UPDATE_MYAPP = "update_myapp";
	public static final String APK_SAVE_DIR = Environment.getExternalStorageDirectory() + "/EasyAppDownload";


	// 关于手机屏幕的一些属性
	public static int WIDTH = 0; // 屏幕宽度
	public static int HEIGHT = 0; // 屏幕高度
	public static float DENSITY = 0; // 屏幕密度

	public static void init(Activity activity) {

		if (DENSITY == 0 || WIDTH == 0 || HEIGHT == 0) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			WIDTH = displayMetrics.widthPixels;
			HEIGHT = displayMetrics.heightPixels;
			DENSITY = displayMetrics.density;
		}
	}
}
