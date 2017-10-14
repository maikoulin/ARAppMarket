package com.winhearts.arappmarket.db;

import android.provider.BaseColumns;

/**
 * 数据库表列名
 *
 * @author 柯志言 2013-07-10 创建<br>
 */
class Columns {

    /**
     * 节目表单
     */
    interface FileRecordColumns extends BaseColumns {
        String TABLE_APPID = "appid";                //
        String TABLE_APPNAME = "appName";                //app名称
        String TABLE_ICONURL = "iconUrl";                //图片URL
        String TABLE_APPSIZE = "appSize";
        String TABLE_INTROIMAGE = "introImage";            //介绍图片的URL字符串
        String TABLE_ABOUT = "about";                    //APP简介
        String TABLE_UPDATEINTRO = "updateIntro";            //更新信息
        String TABLE_AUTHOR = "author";                    //开发者
        String TABLE_OSDEMAND = "osDemand";                //系统信息
        String TABLE_APPVERSION = "appVersion";                //版本信息
        String TABLE_CATEGORYNAME = "categoryName";            //app分类
        String TABLE_CATEGORYID = "categoryId";                //app分类ID
        String TABLE_PUTAWAYTIME = "putawayTime";            //上架时间
        String TABLE_DOWNLOADNUM = "downloadNum";            //下载热度
        String TABLE_SCORE = "score";                    //评分
        String TABLE_COMMENTNUM = "commentNum";                //评论数量
        String TABLE_SOURCEID = "sourceId";                //资源ID字符串

    }

    /**
     * 文件下载表单
     */
    interface SmartFileDownlogColumns extends BaseColumns {
        String TABLE_DOWNPATH = "downpath";           //文件下载路径
        String TABLE_THREADID = "threadid";           //线程ID
        String TABLE_DOWNLENGTH = "downlength";         //已经下载的数据大小
    }

    /**
     * app下载资源列表
     */
    interface AppSourceColumns extends BaseColumns {
        String TABLE_APPNAME = "appName";
        String TABLE_ICONURL = "iconUrl";
        //         final static String    TABLE_SOURCEID          = "sourceId";           //资源ID
        String TABLE_DOWNLOADURL = "downloadUrl";        //app下载URL
        String TABLE_DOWNLENGTH = "downlength";         //已经下载的数据大小
        String TABLE_FILESIZE = "fileSize";           //app（文件）大小
        String TABLE_FILENAME = "fileName";           //app文件名称
        String TABLE_STATE = "state";                //状态0：未下载；1：下载；2：暂停；3：下载完成；4：已经安装
        String TABLE_PACKAGENAME = "packageName";        //包名
        String TABLE_APPVERSION = "appversion";            //版本号
    }

    interface AppInfoColumns extends BaseColumns {
        String TABLE_PACKAGENAME = "packageName";
        String TABLE_FIRSTTYPENAME = "firstTypeName";
        String TABLE_CHILDTYPENAME = "childTypeName";

    }
}
