package com.winhearts.arappmarket.modellevel;

/**
 * 请求路径常量定义
 */
public class ModeUrl {

    /*************
     * 账号系统
     ******************/
    // 注册
    public static final String ACCOUNT_SUBMIT = "/arms/is/account/registerByPhone";

    // 获取验证码
    public static final String ACCOUNT_GET_SUBMIT_SECURITY_CODE = "/arms/is/account/getVerificationCode";

    // 注销
    public static final String ACCOUNT_EXIT = "/arms/is/account/logout";

    // 手机登陆
    public static final String ACCOUNT_LOGIN = "/arms/is/account/login";

    // 第三方登陆
    public static final String ACCOUNT_LOGIN_THIRD = "/arms/is/account/thirdPartyLogin";
    // 验证登陆状态
    public static final String ACCOUNT_CHECK_LOGIN_STATUS = "/arms/is/account/checkLogin";

    // 获取用户信息
    public static final String ACCOUNT_GET_USER_INFO = "/arms/is/user/getUserInfo";

    // 更新用户信息
    public static final String ACCOUNT_UPDATE_USER_INFO = "/arms/is/user/updateUserInfo";

    public static final String ACCOUNT_REGISTER_BY_THIRD_PARTY = "/arms/is/account/registerByThirdParty";

    //2.52	多方式联合登录
    public static final String ACCOUNT_THIRD_PARTY_LOGINS = "/arms/is/account/thirdPartyLogins";
    // 2.51	绑定手机
    public static final String BIND_PHONE = "/arms/is/account/bindPhone";

    /**************
     * 安装与卸载
     ***************/
    //预安装列表
    public static final String INSTALL_PRE = "/arms/is/software/queryPreInstallList";
    //安装包是否可以卸载
    public static final String INSTALL_CHECKABLE_UNINSTALL = "/arms/is/software/checkIfUninstall";
    //批量获取下载的url
    public static final String INSTALL_URLS = "/arms/is/software/downloads";
    //更新
    public static final String INSTALL_UPDATE = "/arms/is/software/checkUpdates";
    //查询软件
    public static final String QUERY_SOFTWARES = "/arms/is/software/querySoftwares";
    //搜索推荐
    public static final String QUERY_SEARCH_RECOMMENT = "/arms/is/software/querySearchRecomment";
    //查询所有软件包名
    public static final String QUERY_ALL_PACKAGES = "/arms/is/software/queryAllPackages";

    //获取软件下载路径
    public static final String QUERY_DOWNLOAD = "/arms/is/software/download";
    //检查是否更新
    public static final String CHECK_VERSION = "/vms/is/version/checkVersion";


    //获取专题详情
    public static final String TOPIC_DETAIL = "/arms/is/topic/detail";
    //获取应用详情
    public static final String QUERY_SOFTWATE_INFO = "/arms/is/software/querySoftwareInfo";

    //通过多个软件类型获取软件列表
    public static final String QUERY_SOFTWATES_BY_MULTI_TYPE = "/arms/is/software/querySoftwaresByMultiType";

    //获取专题列表
    public static final String QUERY_TOPIC_LIST = "/arms/is/topic/list";

    //2.16	获取软件相关应用推荐
    public static final String QUERY_RECOMMEND_SOFTWATES = "/arms/is/software/queryRecommendSoftwares";


    //2.50	获取所有带状态的包名
    public static final String QUERY_All_PACKAGES_STATUS = "/arms/is/software/queryAllPackagesAndStatus";

    /**
     * 获取屏幕布局
     */
    public static final String QUERY_LAYOUT = "/arms/is/layout/queryLayout";

    public static final String QUERY_SOFTWARES_BY_TYPE = "/arms/is/software/querySoftwaresByType";

    public static final String QUERY_SOFTWAREList = "/arms/is/topic/softwareList";

    /**
     * 获取配置文件
     */
    public static final String GET_CONFIG_LIST = "/vms/is/config/getConfigList";
    /***************
     * 行为统计
     ***************/
    //点击模块记录
    public static final String UPLOAD_CLICK = "/arms/is/layout/uploadClickRecord";

    //下载路径
    public static final String UPLOAD_DOWNLOAD_PATH = "/arms/is/layout/uploadDownloadRecord";

    //上报成功执行数据
    static final String UPLOAD_OPERATE_DATA = "/arms/is/software/uploadOperateData";

    //上报软件使用情况
    public static final String UPLOAD_DATA = "/arms/is/statistics/uploadData";

    //上报功能使用记录
    public static final String UPLOAD_FUNC_USED = "/arms/is/statistics/uploadFuncUsed";

    //2.47	应用列表上报
    public static final String UPLOAD_APP_LIST = "/arms/is/statistics/uploadAppList";

    //上报应用卸载
    public static final String UPLOAD_UNINSTALL_App = "/arms/is/statistics/uploadUninstallApp";


    /***************
     * 广告
     ***************/
    //获取服务器广告列表
    public static final String AD_QUERY = "/arms/is/advert/list";
    /***************
     * 评论
     ***************/
    //上报评论接口
    public static final String PRESENT_EVALUATE = "/arms/is/software/comment";
    //获取评论列表
    public static final String GET_EVALUATE = "/arms/is/software/listComments";
    //评论跳转二维码生成
    public static final String ZXING_EVALUATE = "/arms/is/software/toComment";
    //3.8 获取评论回复列表
    public static final String GET_REPLY_LIST = "/arms/is/comment/getReplyList";
    //回复二维码地址
    public static final String ZXING_REPLY = "/arms/is/comment/toReply";
    //评论回复
    public static final String COMMENT_REPLY = "/arms/is/comment/commentReply";

    /**
     * 日志上报
     */
    public static final String UPLOAD_LOG = "/vms/is/log/upload";

    //2.52	获取定制推荐
    public static final String RECOMMEND_APP = "/arms/is/software/queryCustomizedRecommend";

    //3.8 获取用户新消息（回复的消息）
    public static final String GET_NEW_REPLY = "/arms/is/comment/getNewReply";

}
