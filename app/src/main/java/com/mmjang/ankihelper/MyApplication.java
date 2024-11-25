package com.mmjang.ankihelper;

//import android.app.Activity;
//import androidx.appcompat.app.AppCompatActivity;
import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.ui.floating.IUserService;
import com.mmjang.ankihelper.ui.floating.UserService;
import com.mmjang.ankihelper.util.DarkModeUtils;
//import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;

import okhttp3.OkHttpClient;

/**
 * Created by liao on 2017/4/27.
 */

public class MyApplication extends MultiDexApplication {
    private static Context context;
    private static Application application;
    private static AnkiDroidHelper mAnkiDroid;
    private static OkHttpClient okHttpClient;
    private static MyApplication instance;
    private static UserService shizukuService;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;
        LitePal.initialize(context);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        DarkModeUtils.init(this);
//        CrashReport.initCrashReport(getApplicationContext(), "398dc6145b", false);
        AndroidThreeTen.init(this);

        /**
         * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
         * by below code, so please do not worry about performance.
         * @see FileDownloader#init(Context)
         */
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15_000) // set connection timeout.
                        .readTimeout(15_000) // set read timeout.
                ))
                .commit();
    }

    public static Context getContext() {
        return context;
    }

    public static Application getApplication(){
        return application;
    }

    public static AnkiDroidHelper getAnkiDroid() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(getApplication());
        }
        return mAnkiDroid;
    }

//    private static void getAnkiDroidPermission(AppCompatActivity activity) {
//
//    }

    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    public static UserService getShizukuService() {
        if(shizukuService == null) {
            shizukuService = new UserService();
        }
        return shizukuService;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static void initRunningAndroid() {

    }

}
