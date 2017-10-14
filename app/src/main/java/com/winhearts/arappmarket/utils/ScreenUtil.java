package com.winhearts.arappmarket.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 屏幕相关工具
 * 
 * @author huyf
 * 
 */
public class ScreenUtil {
	
	//获取屏幕密度
	static public float getDensity(Activity activity){
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;  // 屏幕宽度（像素）
//        int height = metric.heightPixels;  // 屏幕高度（像素）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）

        
        return density;
	}
	
	// dip->px
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	// px->dip
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// 获取屏幕�?
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	// 获取屏幕�?
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	// 获取屏幕�?
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(dm);

		return dm.heightPixels;
	}

	// 获取屏幕�?
	public static int getScreenWidth(Context context) {
		DisplayMetrics metric = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	// 获取计算后的宽度
	public static int getViewWidth(View view) {
		// 初始计算动画指示图片宽度
		int width = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int heigh = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(width, heigh);
		return view.getMeasuredWidth();
	}

	// 获取计算后的高度
	public static int getViewHeight(View view) {
		int width = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int heigh = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(width, heigh);
		return view.getMeasuredHeight();
	}
}
