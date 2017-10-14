package com.winhearts.arappmarket.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.winhearts.arappmarket.utils.common.StorageUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Zheng Yuyun 主要是系统的常用函数
 * **/
public class MobileOS {

	final static String TAG = "MobileOS";
	
	/** 获取sd卡存储路径 */
	public static String getExternalStoragePath() {


		// 判断SdCard是否存在并且是可用的
		if (StorageUtils.isExternalStorageWritable()) {
			if (Environment.getExternalStorageDirectory().canWrite()) {
				return Environment.getExternalStorageDirectory().getPath();
			}
		}
		return null;
	}


	/** 获取android系统信息 */
	public static String getOSinfo() {
		return "android.os.Build.MODEL: " + Build.MODEL + ",android.os.Build.VERSION.SDK: " + Build.VERSION.SDK + ",android.os.Build.VERSION.RELEASE:" + Build.VERSION.RELEASE;
	}

	

	/** 获取系统名称 */
	public static String getOSName() {
		int sdk_int = Build.VERSION.SDK_INT;
		String ret = "UNKNOWN";
		switch (sdk_int) {

		case Build.VERSION_CODES.BASE: // 1
			ret = "BASE 1.0";
			break;
		case Build.VERSION_CODES.BASE_1_1: // 2
			ret = "BASE_1_1";
			break;
		case Build.VERSION_CODES.CUPCAKE: // 3
			ret = "CUPCAKE";
			break;
		case Build.VERSION_CODES.DONUT: // 4
			ret = "DONUT";
			break;
		case Build.VERSION_CODES.CUR_DEVELOPMENT:// 1000
			ret = "CUR_DEVELOPMENT";
			break;
		case 5: // ECLAIR Android 2.0
			ret = "ECLAIR";
			break;
		case 6: // ECLAIR_0_1 Android 2.0.1
			ret = "ECLAIR_0_1";
			break;
		case 7: // ECLAIR_MR1 android 2.1
			ret = "ECLAIR_MR1";
			break;
		case 8: // FROYO anroid 2.2
			ret = "FROYO";
			break;
		default:
			break;
		}
		return ret;
	}


	public static int getSDKInt() {
		return Build.VERSION.SDK_INT;
	}


	/*** 另外一种判断网络连接通断的方式, 用异常来代替 */
	public static boolean isNetAvailable() {
		boolean flag = false;
		try {
			ServerSocket ss = new ServerSocket(7777);
			flag = true;
			ss.close();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/** 判断网络连接通断, 包括 wifi, gprs **/
	public static boolean isNetAvailable(Context context) {
		boolean flag = false;

		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cwjManager.getActiveNetworkInfo() != null) {
			flag = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		return flag;
	}

	
	public static boolean isSDAbsence() {
		     
          File path = Environment.getExternalStorageDirectory();     
          StatFs sf = new StatFs(path.getPath());     
          //获取单个数据块的大小(Byte)     
          long blockSize = sf.getBlockSize();     
         //空闲的数据块的数量     
          long freeBlocks = sf.getAvailableBlocks();    
          //返回SD卡空闲大小     
          long size = (freeBlocks * blockSize)/1024 /1024   ;
		return size < 10;

	}

	/** 判断wifi是否存在, 可行 */
	public static boolean isWifiConnected(Context context) {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null) {
			if (info.isAvailable() && info.getTypeName().equals("WIFI")) {
				flag = true;
			}
		}
		return flag;
	}


	/***
	 * 以超级用户权限运行命令
	 * 
	 * @param command
	 *            命令
	 * @return 是否成功
	 * */
	public static boolean runRootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			//LogUtil.Log("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				// nothing
			}
		}
		return true;
	}



	public enum NetWorkEnum {
		NoConnection, Mobile, Wifi
	}

	/** 判断网络状态 */
	public static NetWorkEnum getNetWorkState(Context context) {
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null) {
			if (!info.isAvailable()) {
				return NetWorkEnum.NoConnection;
			} else {
				if (info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected()) {
					return NetWorkEnum.Wifi;
				} else if (info.getType() == ConnectivityManager.TYPE_MOBILE && info.isConnected()) {
					return NetWorkEnum.Mobile;
				}
			}
		}
		return NetWorkEnum.NoConnection;
	}

	public static void createFile(String path) {
		File f = new File(path);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static boolean fileIsExists(String str) {
		File f = new File(str);
		return f.exists();
	}

	public static void writefile(String str, String path) {
		File file;
		FileOutputStream out;
		try {
			// 创建文件
			file = new File(path);
			file.createNewFile();
			// 打开文件file的OutputStream
			out = new FileOutputStream(file);
			String infoToWrite = str;
			// 将字符串转换成byte数组写入文件
			out.write(infoToWrite.getBytes());
			// 关闭文件file的OutputStream
			out.close();
		} catch (IOException e) {
			// 将出错信息打印到Logcat
			Log.d(TAG, e.toString());
		}
	}

	public static boolean isNetWorkConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected() && info.isAvailable()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.v("error", e.toString());
		}
		return false;
	}

	public static boolean isIcsOrNewer() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
	
	public static boolean isJellyOrNewer() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

}
