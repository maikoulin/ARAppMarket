package com.winhearts.arappmarket.modellevel;

/**
 * 请求路径常量定义
 */
public class ModeUrl {
    public static final String YUN_GAMES = "/ams/is/cloudroute/games";

    /**************
     * 支付系统
     **********************/
    public static final String ORDER_QUARY = "/ams/is/pay/queryPayInfo";
    public static final String ORDER_CREATE = "/ams/is/pay/queryChannels";
    public static final String ORDER_CHANGE = "/ams/is/pay/changePayChannel";
    public static final String ORDER_CANCEL = "/ams/is/pay/cancelOrder";
    public static final String ORDER_CHECKURL = "/ams/is/account/checkLogin";
    public static final String QUERY_CASH = "/ams/is/gzgd/queryCash";
    public static final String PAY_BY_GZGD = "/ams/is/gzgd/pay";
    //3.6.0新
    public static final String PAY_BY_GZGD_3_6 = "/ams/is/pay/gdPay";
    public static final String ORDER_CREATE_3_6 = "/ams/is/pay/createOrder";
    public static final String ORDER_CREATE_PAY_3_6 = "/ams/is/pay/createPayInfo";
    /*************
     * 账号系统
     ******************/
    // 注册
    public static final String ACCOUNT_SUBMIT = "/ams/is/account/registerByPhone";

    // 获取验证码
    public static final String ACCOUNT_GET_SUBMIT_SECURITY_CODE = "/ams/is/account/getVerificationCode";

    // 注销
    public static final String ACCOUNT_EXIT = "/ams/is/account/logout";

    // 手机登陆
    public static final String ACCOUNT_LOGIN = "/ams/is/account/login";

    // 第三方登陆
    public static final String ACCOUNT_LOGIN_THIRD = "/ams/is/account/thirdPartyLogin";
    // 验证登陆状态
    public static final String ACCOUNT_CHECK_LOGIN_STATUS = "/ams/is/account/checkLogin";

    // 获取用户信息
    public static final String ACCOUNT_GET_USER_INFO = "/ams/is/user/getUserInfo";

    // 更新用户信息
    public static final String ACCOUNT_UPDATE_USER_INFO = "/ams/is/user/updateUserInfo";

    public static final String ACCOUNT_REGISTER_BY_THIRD_PARTY = "/ams/is/account/registerByThirdParty";

    //2.52	多方式联合登录
    public static final String ACCOUNT_THIRD_PARTY_LOGINS = "/ams/is/account/thirdPartyLogins";
    // 2.51	绑定手机
    public static final String BIND_PHONE = "/ams/is/account/bindPhone";

    /**************
     * 安装与卸载
     ***************/
    //预安装列表
    public static final String INSTALL_PRE = "/ams/is/software/queryPreInstallList";
    //安装包是否可以卸载
    public static final String INSTALL_CHECKABLE_UNINSTALL = "/ams/is/software/checkIfUninstall";
    //批量获取下载的url
    public static final String INSTALL_URLS = "/ams/is/software/downloads";
    //更新
    public static final String INSTALL_UPDATE = "/ams/is/software/checkUpdates";
    //查询软件
    public static final String QUERY_SOFTWARES = "/ams/is/software/querySoftwares";
    //搜索推荐
    public static final String QUERY_SEARCH_RECOMMENT = "/ams/is/software/querySearchRecomment";
    //查询所有软件包名
    public static final String QUERY_ALL_PACKAGES = "/ams/is/software/queryAllPackages";

    //获取软件下载路径
    public static final String QUERY_DOWNLOAD = "/ams/is/software/download";
    //检查是否更新
    public static final String CHECK_VERSION = "/vms/is/version/checkVersion";


    //获取专题详情
    public static final String TOPIC_DETAIL = "/ams/is/topic/detail";
    //获取应用详情
    public static final String QUERY_SOFTWATE_INFO = "/ams/is/software/querySoftwareInfo";

    //通过多个软件类型获取软件列表
    public static final String QUERY_SOFTWATES_BY_MULTI_TYPE = "/ams/is/software/querySoftwaresByMultiType";

    //获取专题列表
    public static final String QUERY_TOPIC_LIST = "/ams/is/topic/list";

    //2.16	获取软件相关应用推荐
    public static final String QUERY_RECOMMEND_SOFTWATES = "/ams/is/software/queryRecommendSoftwares";


    //2.50	获取所有带状态的包名
    public static final String QUERY_All_PACKAGES_STATUS = "/ams/is/software/queryAllPackagesAndStatus";

    /**
     * 获取屏幕布局
     */
    public static final String QUERY_LAYOUT = "/ams/is/layout/queryLayout";

    public static final String QUERY_SOFTWARES_BY_TYPE = "/ams/is/software/querySoftwaresByType";

    public static final String QUERY_SOFTWAREList = "/ams/is/topic/softwareList";

    /**
     * 获取配置文件
     */
    public static final String GET_CONFIG_LIST = "/vms/is/config/getConfigList";
    /***************
     * 行为统计
     ***************/
    //点击模块记录
    public static final String UPLOAD_CLICK = "/ams/is/layout/uploadClickRecord";

    //下载路径
    public static final String UPLOAD_DOWNLOAD_PATH = "/ams/is/layout/uploadDownloadRecord";

    //上报成功执行数据
    static final String UPLOAD_OPERATE_DATA = "/ams/is/software/uploadOperateData";

    //上报launch点击跳转情况
    public static final String UPLOAD_LAUNCH_CLICK_DATA = "/ams/is/statistics/uploadLaunchClickData";


    //上报软件使用情况
    public static final String UPLOAD_DATA = "/ams/is/statistics/uploadData";

    //上报功能使用记录
    public static final String UPLOAD_FUNC_USED = "/ams/is/statistics/uploadFuncUsed";

    //2.47	应用列表上报
    public static final String UPLOAD_APP_LIST = "/ams/is/statistics/uploadAppList";

    //上报应用卸载
    public static final String UPLOAD_UNINSTALL_App = "/ams/is/statistics/uploadUninstallApp";


    /***************
     * 广告
     ***************/
    //获取服务器广告列表
    public static final String AD_QUERY = "/ams/is/advert/list";
    /***************
     * 评论
     ***************/
    //上报评论接口
    public static final String PRESENT_EVALUATE = "/ams/is/software/comment";
    //获取评论列表
    public static final String GET_EVALUATE = "/ams/is/software/listComments";
    //评论跳转二维码生成
    public static final String ZXING_EVALUATE = "/ams/is/software/toComment";
    //3.8 获取评论回复列表
    public static final String GET_REPLY_LIST = "/ams/is/comment/getReplyList";
    //回复二维码地址
    public static final String ZXING_REPLY = "/ams/is/comment/toReply";
    //评论回复
    public static final String COMMENT_REPLY = "/ams/is/comment/commentReply";
    /**
     * 积分
     */
    public static final String SIGN_IN = "/act/is/signIn/sign";
    public static final String HAS_SIGN_IN = "/act/is/signIn/hasSignIn";

    /**
     * 日志上报
     */
    public static final String UPLOAD_LOG = "/vms/is/log/upload";

    //2.52	获取定制推荐
    public static final String RECOMMEND_APP = "/ams/is/software/queryCustomizedRecommend";

    //3.8 获取用户新消息（回复的消息）
    public static final String GET_NEW_REPLY = "/ams/is/comment/getNewReply";

}
