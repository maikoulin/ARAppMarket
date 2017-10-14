package com.winhearts.arappmarket.utils;


import com.winhearts.arappmarket.BuildConfig;

/**
 * log调试用
 * @author liw
 *
 */
public class LogDebugUtil {
	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	public static final int ASSERT = 7;

	public static boolean isAppDebug = BuildConfig.LOG_DEBUG;
	public static final int LEVEL = DEBUG;

	public static void v(String tag, String msg) {
		if (isAppDebug && VERBOSE >= LEVEL)
			android.util.Log.v(tag, "" + msg);
	}

	public static void d(String tag, String msg) {
		if (isAppDebug && DEBUG >= LEVEL)
			android.util.Log.d(tag, "" + msg);
	}

	public static void i(String tag, String msg) {
		if (isAppDebug && INFO >= LEVEL)
			android.util.Log.i(tag, "" + msg);
	}

	public static void w(String tag, String msg) {
		if (isAppDebug && WARN >= LEVEL)
			android.util.Log.w(tag, "" + msg);
	}

	public static void e(String tag, String msg) {
		if (isAppDebug)
			android.util.Log.e(tag, "" + msg);
	}
	
//
	public static void v(boolean isFileDebug, String tag, String msg) {
		if (isAppDebug && isFileDebug)
			android.util.Log.v(tag, "" + msg);
	}

	public static void d(boolean isFileDebug, String tag, String msg) {
		if (isAppDebug && isFileDebug)
			android.util.Log.d(tag, "" + msg);
	}

	public static void i(boolean isFileDebug, String tag, String msg) {
		if (isAppDebug && isFileDebug)
			android.util.Log.i(tag, "" + msg);
	}

	public static void w(boolean isFileDebug, String tag, String msg) {
		if (isAppDebug && isFileDebug)
			android.util.Log.w(tag, "" + msg);
	}

	public static void e(boolean isFileDebug, String tag, String msg) {
		if (isAppDebug && isFileDebug)
			android.util.Log.e(tag, "" + msg);
	}
}