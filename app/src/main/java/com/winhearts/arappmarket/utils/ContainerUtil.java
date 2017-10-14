package com.winhearts.arappmarket.utils;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合工具
 * Created by lmh on 2015/11/19.
 */
public class ContainerUtil {

    static public boolean isEmptyOrNull(List<?> list) {
        return list == null || list.isEmpty();

    }

    static public boolean isValid(List<?> list) {
        return !isEmptyOrNull(list);
    }

    public static boolean isEmpty(String value) {
        if (TextUtils.isEmpty(value)) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Set set) {
        if (set == null || set.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Integer value) {
        if (value == null) {
            return true;
        }
        return false;
    }
}
