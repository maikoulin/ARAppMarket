package com.winhearts.arappmarket.constant;


import com.winhearts.arappmarket.R;

/**
 * 一些常量
 *
 * @author huyf
 */
public class Constant {

    public static float Total = 100.0f;
    public static float Head_H = 12.0f;
    public static float Navigation_H = 14.0f;
    public static float Content_H = 42.0f;
    public static float Movie_Name_H = 6.0f;
    public static float App_H = 19.0f;
    public static float Setting_H = 36.0f;

    public static float My_App_H = 36.0f;

    public static float Ratio_Head_H = Head_H / Total;
    public static float Ratio_Navigation_H = Navigation_H / Total;
    public static float Ratio_Content_H = Content_H / Total;
    public static float Ratio_Movie_Name_H = Movie_Name_H / Total;
    public static float Ratio_App_H = App_H / Total;
    public static float Ratio_Setting_H = Setting_H / Total;
    public static float Ratio_My_App_H = My_App_H / Total;

    /*****
     * 广告类
     ****/
    public static String RECOMMEND_PRODUCT = "home_ad"; // 精品推荐
    public static String RECOMMEND_INSTALL = "required_ad"; // 装机必备
    public static String RECOMMEND_GAME = "game_ad"; // 游戏推荐
    public static String RECOMMEND_EDU = "edu_ad"; // 教育推荐
    public static String RECOMMEND_LIFE = "life_ad"; // 生活推荐

    /****
     * 专题类
     ****/
    public static String GAME_NEWS = "game_news"; // 游戏新上架
    public static String GAME_RANGE = "game_rank"; // 游戏下载排行
    public static String EDU_NEWS = "education_news"; // 教育新上架
    public static String EDU_RANGE = "education_rank"; // 教育下载排行
    public static String LIFE_NEWS = "life_news"; // 生活新上架
    public static String LIFE_RANGE = "life_rank"; // 生活下载排行

    /**
     * 下载状态参数
     */
    public static final int CANCEL_DOWNLOAD = 2;
    public static final int DOWNLOAD_SUCCESS = 5;
    public static final int DOWNLOAD_FAIL = 6;
    public static final int DOWNLOAD_BACKGROUND = 7;
    public static final int INSTALL_CANCLE = 4;
    /**
     * 是否是手动第一次打开
     */
    public static boolean IS_FIRST_OPEN = true;


    /**
     * 主页是否有dialog弹出
     */
    public static boolean DIALOG_SHOW = false;

    public static boolean INSTALL_SHOW = false;


    /**
     * 屏幕布局参数
     */
    /**
     * {0,0,1,1} int startX, int startY, int endX, int endY 左上角右下角坐标
     */
    public static final int[][] position_3x3 = new int[][]{{0, 0, 1, 1}, {1, 0, 2, 1}, {2, 0, 3, 1}, {0, 1, 1, 2},
            {1, 1, 2, 2}, {2, 1, 3, 2}, {0, 2, 1, 3}, {1, 2, 2, 3}, {2, 2, 3, 3}};

    public static final int[][] position_3x4 = new int[][]{{0, 0, 1, 1}, {1, 0, 2, 1}, {2, 0, 3, 1}, {3, 0, 4, 1},
            {0, 1, 1, 2}, {1, 1, 2, 2}, {2, 1, 3, 2}, {3, 1, 4, 2}, {0, 2, 1, 3}, {1, 2, 2, 3},
            {2, 2, 3, 3}, {3, 2, 4, 3}};

    public static final int[][] position_2x2 = new int[][]{{0, 0, 1, 1}, {0, 1, 1, 2}, {1, 0, 2, 1}, {1, 1, 2, 2}};

    public static final int[][] position_2x6 = new int[][]{{0, 0, 1, 1}, {1, 0, 2, 1}, {2, 0, 3, 1}, {3, 0, 4, 1},
            {4, 0, 5, 1}, {5, 0, 6, 1}, {0, 1, 1, 2}, {1, 1, 2, 2}, {2, 1, 3, 2}, {3, 1, 4, 2},
            {4, 1, 5, 2}, {5, 1, 6, 2}};

    public static final int PAGE_3x3 = 9;
    public static final int PAGE_3x4 = 12;
    public static final int PAGE_2x2 = 4;
    public static final int PAGE_2x3 = 6;

    public static final int[][] position_2x3 = new int[][]{{0, 0, 1, 1}, {1, 0, 2, 1}, {2, 0, 3, 1}, {0, 1, 1, 2}, {1, 1, 2, 2}, {2, 1, 3, 2}};

    //应用详情页坐标,先排竖的
    public static final int[][] position_3x2 = new int[][]{{0, 0, 1, 1}, {0, 1, 1, 2}, {0, 2, 1, 3}, {1, 0, 2, 1},
            {1, 1, 2, 2}, {1, 2, 2, 3}};


    public static final int rankTitle[] = {R.drawable.rank_bg_title_blue, R.drawable.rank_bg_title_orange,
            R.drawable.rank_bg_title_green, R.drawable.rank_bg_title_red};
    public static final int rankTitleDown[] = {R.drawable.rank_bg_title_blue_down, R.drawable.rank_bg_title_orange_down,
            R.drawable.rank_bg_title_green_down, R.drawable.rank_bg_title_red_down};


    //账号跳转方式
    public static final int FROM_NORMAL = 0;
    public static final int FROM_PAY_GET_UID = 1;
    public static final int FROM_PAY_ORDER = 2;
    public static final String FROM_TYPE = "from_type";

    public static final int REGIESTER = 0;
    public static final int BINDING = 1;
    public static final String PHONE_TYPE = "phone_type";

    public static final String testUrl1 = "http://sw.bos.baidu.com/sw-search-sp/software/63f996bb0e13e/AliIM2016_taobao_8.60.01C.exe";
    public static final String testUrl2 = "http://sw.bos.baidu.com/sw-search-sp/software/298df34e0b4d2/QQ_8.6.18781.0_setup.exe";
    public static final String testUrl3 = "http://www.wandoujia.com/apps/com.daoxila.android/binding?source=wandoujia-web_direct_binded";
    public static final String testUrl4 = "http://sw.bos.baidu.com/sw-search-sp/software/5fef587e53e/WeChat_2.1.0.37.exe";
    public static final String testUrl5 = "http://gzgd.srs.ott.winhearts.com:10000//ott/ams/apk/com_holyblade_yqyz_game_1472545624844.apk";
    public static final String testUrl6 = "http://gzgd.srs.ott.winhearts.com:10000//ott/ams/apk/com_gitvdemo_video_1453355324844.apk";

    public static final int Vertical = 0; //occupy two vertical cells
    public static final int Horizontal = 1; //occupy two horizontal cells
    public static final int Normal = 2; //square rectangle
}
