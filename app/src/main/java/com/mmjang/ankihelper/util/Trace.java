package com.mmjang.ankihelper.util;

import android.util.Log;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: Trace
 * @Description: 重新定义日志输出
 * @Author: ss
 * @CreateDate: 2022/5/22 10:22 AM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/5/22 10:22 AM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class Trace {
    public final static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.isTracing)
            Log.e(tag, msg, tr);
    }
    public final static void e(String tag, String msg) {
        if (BuildConfig.isTracing)
            Log.e(tag, msg);
    }

    public final static void e(String msg) {
        if (BuildConfig.isTracing)
            Log.e("", msg);
    }

    public final static void e(Throwable tr) {
        if (BuildConfig.isTracing)
            Log.e("", "", tr);
    }

    public final static void d(String tag, String msg) {
        if (BuildConfig.isTracing)
            Log.d(tag, msg);
    }

    public final static void d(String msg) {
        if (BuildConfig.isTracing)
            Log.d("", msg);
    }

    public final static void d(Throwable tr) {
        if (BuildConfig.isTracing)
            Log.d("", "", tr);
    }

    public final static void i(String tag, String msg, Throwable tr) {
        if (BuildConfig.isTracing)
            Log.i(tag, msg, tr);
    }
    public final static void i(String tag, String msg) {
        if (BuildConfig.isTracing)
            Log.i(tag, msg);
    }

    public final static void i(String msg) {
        if (BuildConfig.isTracing)
            Log.i("", msg);
    }

    public final static void i(Throwable tr) {
        if (BuildConfig.isTracing)
            Log.i("", "", tr);
    }
}
