package com.winhearts.arappmarket.download.manage;

/**
 * Description: 枚举状态
 * public static final int ALL = -1;
 * public static final int UN_DOWN_LOAD = 0;
 * public static final int LOADING = 1;
 * public static final int PAUSE = 2;
 * public static final int COMPLETE_DOWN_LOAD = 3;
 * public static final int COMPLETE_INSTALL = 4;
 * public static final int DOWN_LOAD_FAIL = 5;
 * public static final int WAIT = 6;
 * public static final int CREATE_TASK_FAIL = 7;
 * public static final int CREATE_TASK = 8;
 * public static final int CANCEL_TASK = 9;
 * Created by lmh on 2016/3/30.
 */
public enum ManageDownloadStatus {
    //    调用构造函数来构造枚举项

    /**
     * 最初状态
     */
    INITIAL(-1, "initial status"),

    /**
     * 0未下载 -> 1下载
     */
    DOWNLOAD_NONE(0, "download not"),
    /**
     * 1正在下载 -> 2暂停
     */
    DOWNLOAD_LOADING(1, "download loading"),
    /**
     * 2暂停 -> 继续
     */
    DOWNLOAD_PAUSE(2, "download pause"),
    /**
     * 3下载完成 -> 安装 (自动安装，该状态先不用)
     */
    DOWNLOAD_COMPLETE(3, "download completed"),
    /**
     * 5 下载失败
     */
    DOWNLOAD_FAIL(5, "download fail"),
    /**
     * 4 安装完成 -> 打开 | 卸载
     */
    INSTALL_COMPLETE(4, "install completed"),
    /**
     * 6 等待下载
     */
    TASK_WAIT(6, "wait "),
    /**
     * 7 创建任务失败
     */
    TASK_CREATE_FAIL(7, "task create error"),
    /**
     * 8 创建中
     */
    TASK_CREATE(8, "task create"),
    /**
     * 9 任务取消
     */
    TASK_CANCEL(9, "task cancel"),

    /**
     * 未安装状态，
     * 纯识别状态未入数据库
     */
    INSTALL_NONE(101, "no install"),

    /**
     * 请求url 状态
     * 纯识别状态未入数据库
     */
    REQUEST_URL(102, "request download url"),
    /**
     * 请求下载url 失败
     * 纯识别状态未入数据库
     */
    REQUEST_URL_FAIL(103, "request download url fail"),
    /**
     * 请求url成功
     * 纯识别状态未入数据库
     */
    REQUEST_URL_OK(104, "request download url completed");

    public String description;
    public int value = 0;

    ManageDownloadStatus(int value, String description) {    //    必须是private的，否则编译错误
        this.value = value;
        this.description = description;
    }

    public static ManageDownloadStatus from(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case -1:
                return INITIAL;
            case 0:
                return DOWNLOAD_NONE;
            case 1:
                return DOWNLOAD_LOADING;
            case 2:
                return DOWNLOAD_PAUSE;
            case 3:
                return DOWNLOAD_COMPLETE;
            case 4:
                return INSTALL_COMPLETE;
            case 5:
                return DOWNLOAD_FAIL;
            case 6:
                return TASK_WAIT;
            case 7:
                return TASK_CREATE_FAIL;
            case 8:
                return TASK_CREATE;
            case 9:
                return TASK_CANCEL;
            default:
                return null;
        }
    }

    public int value() {
        return this.value;
    }
}
