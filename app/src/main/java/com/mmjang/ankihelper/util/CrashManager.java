package com.mmjang.ankihelper.util;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: CrashManager
 * @Description: 错误日志系统
 * @Author: ss
 * @CreateDate: 2022/8/15 9:01 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/8/15 9:01 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashManager implements Thread.UncaughtExceptionHandler {


    public static final String TAG = "CrashHandler";
    // CrashHandler实例
    private static volatile CrashManager instance=null;
    // 程序的Context对象
    private Application application;
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> deviceInfo = new HashMap<>();
    private String crashFilePath = "/crash/";
    private String fileName = "_crash.log";


    private CrashManager(Context contex) {
        application = (Application) contex.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CrashManager.class) {
                if (instance == null) {
                    instance = new CrashManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        /* 获取默认的UncaughtExceptionHandler，如果没有自己处理，则依然调用默认的处理逻辑*/
        if (!handleException(thread, throwable) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);//默认的处理逻辑
        }
    }
    /**
     * 处理异常事件
     */
    private boolean handleException(Thread thread, Throwable ex) {
        if (ex == null) return false;
        ex.printStackTrace();//向控制台写入异常信息
        saveCrashInfo2LocalFile(thread, ex);//将异常信息写入文件
        return true;
    }

    /**
     * 将完整的chrash信息写入本地文件
     * 如果外部储存可用则存储到外部SD卡，否则存储到内部
     */
    private void saveCrashInfo2LocalFile(Thread thread, Throwable ex) {
        boolean isFirstWrite = true;//是否是第一次建立文件
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = new File(application.getExternalFilesDir(null) + crashFilePath);
        } else {
            dir = new File(application.getFilesDir() + crashFilePath);
        }
        if (!dir.exists() && !dir.isDirectory()) dir.mkdirs();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = formatter.format(new Date());
        File file = new File(dir, time + fileName);
        if (file.exists()) {
            isFirstWrite = false;
        }
        OutputStream outStream = null;
        try {
            Log.i(TAG,"chrash信息的地址："+file.getAbsolutePath());
            outStream = new BufferedOutputStream(new FileOutputStream(file, true));
            if (isFirstWrite) {
                outStream.write(collectDeviceInfo().toString().getBytes());
            }
            outStream.write(crashInfo(thread, ex).toString().getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取设备信息
     *
     * @return 设备所有信息
     */
    private StringBuffer collectDeviceInfo() {
        PackageManager packageManager = application.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(application.getPackageName(), packageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
            String versionCode = packageInfo.versionCode + "";
            deviceInfo.put("versionName", versionName);//定义在 <manifest> tag's versionName attribute
            deviceInfo.put("versionCode", versionCode); //<manifest> tag's versionCode attribute
        }
        /**
         * 通过反射的方法获得Build的所有域
         */
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                deviceInfo.put(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {

            }
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> enty : deviceInfo.entrySet()) {
            String key = enty.getKey();
            String value = enty.getValue();
            sb.append(key + "=" + value + "\n");
        }
        return sb;
    }

    /**
     * 收集crash信息
     *
     * @return 完整的chrash信息
     */
    private StringBuffer crashInfo(Thread thread, Throwable ex) {
        StringBuffer sb = new StringBuffer();
        long timeStamp = System.currentTimeMillis();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = formatter.format(new Date());
        sb.append(time + ":").append(timeStamp).append("\n");
        sb.append("problem appears at thread:").append(thread.getName() + ",its ID:" + thread.getId() + "\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb;
    }
}