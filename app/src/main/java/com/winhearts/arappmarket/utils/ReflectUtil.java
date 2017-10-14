package com.winhearts.arappmarket.utils;

import android.content.Context;

import com.winhearts.arappmarket.activity.SettingActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 */
public class ReflectUtil {

    /*
     *  根据某个对象的名称和方法去执行该方法
     *
     */
    public static void execute(Context context, String methodName) {

        SettingActivity settingActivity = (SettingActivity) context;
        Class clazz = settingActivity.getClass();
//        Class clazz = object.getClass();
        Method m1;
        try {
            m1 = clazz.getDeclaredMethod(methodName);
            m1.invoke(context);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
