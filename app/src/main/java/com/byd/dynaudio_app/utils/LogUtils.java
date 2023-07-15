package com.byd.dynaudio_app.utils;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author xiaohu.hao
 * @description: log类，推荐使用这个.
 * 能够根据堆栈信息 获取log具体所在的代码位置
 * @date :2022/5/26 9:51
 */
public class LogUtils {

    private static Logger logger;

    static {
        try {
            FileHandler fh = new FileHandler("/storage/emulated/0/Download/dynaudio_log.txt", true);
            logger = Logger.getLogger("MyApp");
            logger.addHandler(fh);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static final boolean DEBUG = true;
    // 关键词，只需要过滤该词，只会出现该项目的的log，不会有别人的log，可以修改该值.
    private static final String TAG = "2222 byd-dynaudio_app ";

    public static void d() {
        if (DEBUG) {
            logger.log(Level.INFO, TAG + getAutoJumpLogInfos()[0] + getAutoJumpLogInfos()[1] + ":" + getAutoJumpLogInfos()[2]);
//            write(TAG + getAutoJumpLogInfos()[0] + " : " + getAutoJumpLogInfos()[1] + ":" + getAutoJumpLogInfos()[2]);
        }
    }

    public static String LOG_PATH = "";


    private static final Object lock = new Object();

    /**
     * 打开日志文件并写入日志
     *
     * @param text
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private static void write(String text) {// 新建或打开日志文件 // log path : /storage/emulated/0/dynaudio_log.txt
//        Log.d(TAG, "write: path : " + LOG_PATH);

        synchronized (lock) {
            text = getCurrentTimeStr() + "   " + text;

            File dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); // /storage/dynaudio_log.txt

            File dirsFile = new File(dirPath.getAbsolutePath());
            if (!dirsFile.exists()) {
                dirsFile.mkdirs();
            }
            //Log.i("创建文件","创建文件");
            File file = new File(dirsFile.toString(), "dynaudio_log.txt");// MYLOG_PATH_SDCARD_DIR
            Log.d(TAG, "write: log path : " + file.getAbsolutePath());
            if (!file.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    file.createNewFile();
//                LogUtils.d("file "   + file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(text);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCurrentTimeStr() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            logger.log(Level.INFO, getAutoJumpLogInfos()[1] + ":" + msg + getAutoJumpLogInfos()[2]);
        }
    }

    private static String[] getAutoJumpLogInfos() {
        String[] infos = new String[]{"", "", ""};
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < 5) {
            Log.e("MyLogger", "Stack is too shallow!!!");
            return infos;
        } else {
            infos[0] = elements[4].getClassName().substring(elements[4].getClassName().lastIndexOf(".") + 1);
            infos[1] = elements[4].getMethodName() + "()";
            infos[2] = " at(" + elements[4].getClassName() + ".java:" + elements[4].getLineNumber() + ")";
            return infos;
        }
    }
}
