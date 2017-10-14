package com.winhearts.arappmarket.utils;

import android.text.TextUtils;

import com.winhearts.arappmarket.activity.VpnStoreApplication;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;

/**
 * 对于log的记录的配置，本地存储的log 和需要http上传的log两种
 */
public class MyLogConfigurator {
    private Level rootLevel = Level.DEBUG;
    private String filePattern = "%d - [%p::%c::%C] - %m%n";
    private String logCatPattern = "%m%n";
    private String fileName = "android-log4j.log";
    private int maxBackupSize = 5;
    private long maxFileSize = 524288L;
    private boolean immediateFlush = true;
    private boolean useLogCatAppender = true;
    private boolean useFileAppender = true;

    private Priority httpThreshold = Priority.WARN;

    private Priority localThreshold = Priority.DEBUG;

    boolean httpLog = true;

    boolean localLog = true;


    public MyLogConfigurator() {
    }

    public MyLogConfigurator(String fileName) {
        setFileName(fileName);
    }

    public MyLogConfigurator(String fileName, Level rootLevel) {
        this(fileName);
        setRootLevel(rootLevel);
    }

    public MyLogConfigurator(String fileName, Level rootLevel,
                             String filePattern) {
        this(fileName);
        setRootLevel(rootLevel);
        setFilePattern(filePattern);
    }

    public MyLogConfigurator(String fileName, int maxBackupSize,
                             long maxFileSize, String filePattern, Level rootLevel) {
        this(fileName, rootLevel, filePattern);
        setMaxBackupSize(maxBackupSize);
        setMaxFileSize(maxFileSize);
    }

    public void configure(String loggerName) {
        Logger root = Logger.getLogger(loggerName);

        if (isUseFileAppender()) {
            configureFileAppender(loggerName);
        }

        if (isUseLogCatAppender()) {
            configureLogCatAppender(loggerName);
        }
        root.setAdditivity(false);
        root.setLevel(getRootLevel());
    }

    public void setLevel(String loggerName, Level level) {
        Logger.getLogger(loggerName).setLevel(level);
    }

    private void configureFileAppender(String loggerName) {
        Logger root = Logger.getLogger(loggerName);

        Layout fileLayout = new PatternLayout(getFilePattern());
        if (httpLog) {

            RollingFileAppender rollingFileAppender;
            try {
                rollingFileAppender = new RollingFileAppender(fileLayout,
                        getFileName());
            } catch (IOException e) {
                throw new RuntimeException("Exception configuring log system", e);
            }
            rollingFileAppender.setEncoding("UTF-8");

            rollingFileAppender.setMaxBackupIndex(0);
            rollingFileAppender.setMaximumFileSize(512 * 1024);//http上传的log设小点512k，本地的大就可以了
            rollingFileAppender.setImmediateFlush(isImmediateFlush());
            String logLevel = Pref.getString(Pref.LOG_LEVEl, VpnStoreApplication.getApp(), "2").trim();
            if (TextUtils.isEmpty(logLevel)) {
                httpThreshold = Priority.WARN;
            } else {
                if (logLevel.equals("1")) {
                    httpThreshold = Priority.INFO;
                } else if (logLevel.equals("2")) {
                    httpThreshold = Priority.WARN;
                } else if (logLevel.equals("3")) {
                    httpThreshold = Priority.ERROR;
                } else if (logLevel.equals("4")) {
                    httpThreshold = Priority.FATAL;
                } else {
                    httpThreshold = Priority.WARN;
                }
            }
            rollingFileAppender.setThreshold(httpThreshold);
            root.addAppender(rollingFileAppender);
        }

        if (localLog) {
            RollingFileAppender localFileAppender;
            try {
                localFileAppender = new RollingFileAppender(fileLayout,
                        getFileName() + "_local");
            } catch (IOException e) {
                throw new RuntimeException("Exception configuring log system", e);
            }
            localFileAppender.setEncoding("UTF-8");
            localFileAppender.setMaxBackupIndex(getMaxBackupSize());
            localFileAppender.setMaximumFileSize(getMaxFileSize());
            localFileAppender.setImmediateFlush(isImmediateFlush());
            localFileAppender.setThreshold(localThreshold);

            root.addAppender(localFileAppender);
        }

    }

    public void addAppender(String loggerName, Appender newAppender) {
        Logger root = Logger.getLogger(loggerName);
        root.addAppender(newAppender);
    }


    private void configureLogCatAppender(String loggerName) {
        Logger root = Logger.getLogger(loggerName);
        Layout logCatLayout = new PatternLayout(getLogCatPattern());
        LogCatAppender logCatAppender = new LogCatAppender(logCatLayout);

        root.addAppender(logCatAppender);
    }

    public Level getRootLevel() {
        return this.rootLevel;
    }

    public void setRootLevel(Level level) {
        this.rootLevel = level;
    }

    public String getFilePattern() {
        return this.filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public String getLogCatPattern() {
        return this.logCatPattern;
    }

    public void setLogCatPattern(String logCatPattern) {
        this.logCatPattern = logCatPattern;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getMaxBackupSize() {
        return this.maxBackupSize;
    }

    public void setMaxBackupSize(int maxBackupSize) {
        this.maxBackupSize = maxBackupSize;
    }

    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean isImmediateFlush() {
        return this.immediateFlush;
    }

    public void setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }

    public boolean isUseFileAppender() {
        return this.useFileAppender;
    }

    public void setUseFileAppender(boolean useFileAppender) {
        this.useFileAppender = useFileAppender;
    }

    public boolean isUseLogCatAppender() {
        return this.useLogCatAppender;
    }

    public void setUseLogCatAppender(boolean useLogCatAppender) {
        this.useLogCatAppender = useLogCatAppender;
    }
}