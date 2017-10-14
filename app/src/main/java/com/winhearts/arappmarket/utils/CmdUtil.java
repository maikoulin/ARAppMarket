package com.winhearts.arappmarket.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lmh on 2016/4/7.
 * cmd命令行执行工具类
 */
public class CmdUtil {

    private static String TAG = "CmdUtil";

    public static String callCmd(String cmd) {
        StringBuilder result = new StringBuilder();
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static boolean runtimeExec(String[] msg, String filter, CmdResultListenter cmdResultListenter) {
        try {
            Process proc = Runtime.getRuntime().exec(msg);

            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), TYPE_ERROR, null, cmdResultListenter);

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), TYPE_OUTPUT, filter, cmdResultListenter);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();


            if (proc.waitFor() != 0) {
//                LoggerUtil.d(TAG, "执行\"" + cmd + "\"时返回值=" + proc.exitValue());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean runtimeExec(String cmd, String filter, CmdResultListenter cmdResultListenter) {
        try {
            Process proc = Runtime.getRuntime().exec(cmd);

            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), TYPE_ERROR, null, cmdResultListenter);

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), TYPE_OUTPUT, filter, cmdResultListenter);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();


            if (proc.waitFor() != 0) {
                LoggerUtil.d(TAG, "执行\"" + cmd + "\"时返回值=" + proc.exitValue());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final String TYPE_ERROR = "ERROR";
    public static final String TYPE_OUTPUT = "OUTPUT";

    static class StreamGobbler extends Thread {
        InputStream is;
        String type;
        String filter;
        CmdResultListenter cmdResultListenter;

        StreamGobbler(InputStream is, String type, String filter, CmdResultListenter cmdResultListenter) {
            this.is = is;
            this.type = type;
            if (filter != null) {
                this.filter = filter;
            }
            this.cmdResultListenter = cmdResultListenter;
        }

        public void run() {
            try {
                String result = null;
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (filter != null) {
                        if (line.contains(filter)) {
                            result += line;
                            result += "\n";
                        }
                    } else if (type.equals(TYPE_ERROR)) {
                        result += line;
                    }
                    LoggerUtil.d(TAG, type + ">" + line);
                }
                if (cmdResultListenter != null) {
//                    System.out.println(type + ">" + result);
                    if (type.equals(TYPE_ERROR)) {
                        if (result != null) {
                            cmdResultListenter.setResult(TYPE_ERROR + ":" + result);
                        }
                    } else {
                        cmdResultListenter.setResult(result);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }


    public interface CmdResultListenter {
        void setResult(String result);
    }
}
