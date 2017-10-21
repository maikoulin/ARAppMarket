package com.winhearts.arappmarket.constant;


import com.winhearts.arappmarket.BuildConfig;

/**
 * url常量定义
 */
public class ConstVersionUrl {
    /*******
     * URL
     ********/
    //vms 是配置url, ams ics 都有对应配置，这里只是取一个默认值防止没有取到
    //gradle 统一配置
    public final static String ARMS = BuildConfig.ARMS;
    public final static String VMS = BuildConfig.VMS;
    public final static String PROJECT = BuildConfig.PROJECT;
}
