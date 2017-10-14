package com.winhearts.arappmarket.utils;

import android.os.Environment;

import com.winhearts.arappmarket.utils.common.StorageUtils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * log信息配置，缓存本地
 */
public class LoggerUtil {
    public static final MyLogConfigurator logConfigurator = new MyLogConfigurator();
    public static final String LogPath = StorageUtils.isExternalStorageWritable() ? Environment
            .getExternalStorageDirectory() + File.separator + "com_wstv_AppMarket"
            + File.separator + "log" + File.separator + "chinanetcenter_appmarket.log" : StorageUtils.getFilesAuthority("chinanetcenter_appmarket.log").getPath();
    public static final String loggerName = "com.winhearts.arappmarket.activity";

    static {
        // 配置生成的日志文件路径,log4j会自动备份文件，最多5个文件
        setLog();
    }

    public static void setLog() {
        if (LogPath != null) {
            File f = new File(LogPath);
            if (!f.exists()) {
                File f1 = new File(f.getParentFile().getPath());
                LogDebugUtil.d("logTest", f1.getPath());
                f1.mkdirs();
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            logConfigurator.setFileName(LogPath);
            //logConfigurator.

            logConfigurator.setLevel(loggerName, Level.ALL);
            //%d 时间  %p 输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL
            //%m 输出代码中指定的讯息，如log(message)中的message  %n 输出一个换行
            logConfigurator.setFilePattern("%d{yyyy:MM:dd HH:mm:ss},%p, %m|%n");
            logConfigurator.setMaxBackupSize(3);
            logConfigurator.setMaxFileSize(1024 * 1024 * 2);//5m 应该是太大了先改为2M 试试
            logConfigurator.setImmediateFlush(true);

            try {
                if (f.canWrite()) {
                    logConfigurator.configure(loggerName);
                } else {
                }
            } catch (Exception e) {
            }
        }
    }

    private static final Logger log = Logger.getLogger(loggerName);

    // 输出Level.DEBUG级别日志,一般开发调试信息用
    public static void d(String tag, String message) {
        if (null != log && log.isDebugEnabled()) {
            log.debug(tag + "," + System.currentTimeMillis() + "," + message);
        }
    }

    // 输出Level.INFO级别日志
    public static void i(String tag, String message) {
        if (null != log && log.isInfoEnabled()) {
            log.info(tag + "," + System.currentTimeMillis() + "," + message);
        }
    }

    // 输出Level.WARN级别日志
    public static void w(String tag, String message) {
        if (null != log && log.isEnabledFor(Level.WARN)) {
            log.warn(tag + "," + System.currentTimeMillis() + "," + message);
        }
    }

    public static void w(String tag, Exception e) {
        if (null != log && log.isEnabledFor(Level.WARN)) {
            log.warn(tag + "," + System.currentTimeMillis() + "," + getErrStack(e));
        }
    }

    // 输出Level.ERROR级别日志,一般catch住异常后使用,使用e.printStackTrace()打印出错误信息;
    public static void e(String tag, String message) {
        if (null != log && log.isEnabledFor(Level.ERROR)) {
            log.error(tag + "," + System.currentTimeMillis() + "," + message);
        }
    }

    public static void e(String tag, Exception e) {
        if (null != log && log.isEnabledFor(Level.ERROR)) {
            log.error(tag + "," + System.currentTimeMillis() + "," + getErrStack(e));
        }
    }

    public static void e(String tag, Throwable e) {
        if (null != log && log.isEnabledFor(Level.ERROR)) {
            log.error(tag + "," + System.currentTimeMillis() + "," + getErrStack(e));
        }
    }

    // 输出Level.FATAL级别日志
    public static void f(String tag, Throwable e) {
        if (null != log && log.isEnabledFor(Level.FATAL)) {
            log.fatal(tag + "," + System.currentTimeMillis() + "," + getErrStack(e));
        }
    }

    /**
     * What a Terrible Failure: Report a condition that should never happen. The
     * error will always be logged at level ASSERT with the call stack.
     * Depending on system configuration, a report may be added to the
     * {@link android.os.DropBoxManager} and/or the process may be terminated
     * immediately with an error dialog.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     */
    public static int wtf(String tag, String msg) {
        return android.util.Log.wtf(tag, msg, null);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, String)}, with an exception to log.
     *
     * @param tag Used to identify the source of a log message.
     * @param tr  An exception to log.
     */
    public static int wtf(String tag, Throwable tr) {
        return android.util.Log.wtf(tag, tr.getMessage(), tr);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, Throwable)}, with a message as well.
     *
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr  An exception to log. May be null.
     */
    public static int wtf(String tag, String msg, Throwable tr) {
        return android.util.Log.wtf(tag, msg, tr);
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    /**
     * Low-level logging call.
     *
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of a log message. It usually
     *                 identifies the class or activity where the log call occurs.
     * @param msg      The message you would like logged.
     * @return The number of bytes written.
     */
    public static int println(int priority, String tag, String msg) {
        return android.util.Log.println(priority, tag, msg);
    }

    public static String getErrStack(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } catch (Exception e1) {
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
