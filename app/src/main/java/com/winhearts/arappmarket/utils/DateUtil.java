package com.winhearts.arappmarket.utils;

import android.text.format.DateUtils;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期相关的工具类
 * Created by suxq on 2017/3/16.
 */

public class DateUtil {

    private DateUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static final String DEFAULE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HHMM_FORMAT = "HH:mm";
    public static final String MMDD_FORMAT = "MM月dd日";
    public static final String YYMMDD_FORMAT = "yyyy年MM月dd日";

    /**
     * 返回String格式的日期
     * 时间展示规则
     * 当天： 时：分
     * 当年： X月X日
     * 其他年份： X年X月X日
     * @param time
     * @return
     */
    public static String millis2String(long time) {
        if (DateUtils.isToday(time)) {
            return millis2String(time, HHMM_FORMAT);
        } else if (isThisYear(time)) {
            return millis2String(time, MMDD_FORMAT);
        } else {
            return millis2String(time, YYMMDD_FORMAT);
        }
    }

    public static String millis2String(long time, String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(time));
    }

    public static boolean isThisYear(long time) {
        Time date = new Time();
        date.set(time);
        int thenYear = date.year;
        date.set(System.currentTimeMillis());
        return (thenYear == date.year);
    }

}
