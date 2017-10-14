package com.winhearts.arappmarket.utils;


import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 获取MAC地址
 */
public class MacUtil {
    private static String TAG = "MacUtil";

//    public static String getMacAddress() {
//        String result = "";
//        String Mac = "";
//        result = callCmd("busybox ifconfig eth0", "HWaddr");
//        //如果返回的result == null，则说明网络不可取
//        if (result == null) {
//            return "eth0 is null";
//        }
//
//        //对该行数据进行解析
//        //例如：eth0      Link encap:Ethernet  HWaddr 00:16:E8:3E:DF:67
//        if (result.length() > 0 && result.contains("HWaddr") == true) {
//            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
//            if (Mac.length() > 1) {
//                LogDebugUtil.e(TAG, Mac.trim() + "=====");
//                LogDebugUtil.e(TAG,readSysfs("/sys/class/net/eth0/address")+ "+++");
//                return Mac.trim();
//            }
//
//        }
//
//
//        return result;
//    }
//
//	  /*
//       * 通过ifconfig去获取mac地址。。。
//	   *
//	   */
//
//    public static String callCmd(String cmd, String filter) {
//        String result = "";
//        String line = "";
//        try {
//            Process proc = Runtime.getRuntime().exec(cmd);
//            InputStreamReader is = new InputStreamReader(proc.getInputStream());
//            BufferedReader br = new BufferedReader(is);
//
//            //执行命令cmd，只取结果中含有filter的这一行
//            while ((line = br.readLine()) != null && line.contains(filter) == false) {
//                //result += line;
//            }
//
//            result = line;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    public static String getMacAddress() {
        String Mac = readSysfs("/sys/class/net/eth0/address");
        if (TextUtils.isEmpty(Mac)) {
            Mac = readSysfs("/sys/class/net/wlan0/address");
        }
        if (TextUtils.isEmpty(Mac)) {
            return "Mac Address is null";
        }
        return Mac.toLowerCase();
    }

    private static String readSysfs(String path) {
        if (!new File(path).exists()) {
            return null;
        }
        String str;
        StringBuilder value = new StringBuilder();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            while ((str = br.readLine()) != null) {
                value.append(str);
            }
            fr.close();
            br.close();
            return value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}

