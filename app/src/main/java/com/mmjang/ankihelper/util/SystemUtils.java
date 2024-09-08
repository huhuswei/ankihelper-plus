package com.mmjang.ankihelper.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: SystemUtils
 * @Description: java类作用描述
 * @Author: 唐朝
 * @CreateDate: 2022/9/2 10:07 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/9/2 10:07 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class SystemUtils {
    private static String TAG = "SystemUtils";

    public static void getRunning3rdApp(Activity activity) {
        PackageManager localPackageManager = activity.getPackageManager();
        List localList = localPackageManager.getInstalledPackages(0);
        for (int i = 0; i < localList.size(); i++) {
            PackageInfo localPackageInfo1 = (PackageInfo) localList.get(i);
            String str1 = localPackageInfo1.packageName.split(":")[0];
            if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo1.applicationInfo.flags) == 0)
                    && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & localPackageInfo1.applicationInfo.flags) == 0)
                    && ((ApplicationInfo.FLAG_STOPPED & localPackageInfo1.applicationInfo.flags) == 0)) {

                Trace.d(TAG, "packageName =====>:" + str1);
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + str1));
                activity.startActivity(intent);
            }
        }
    }

    public static boolean isBackGround(Context context, String pacetName) {
        int i, j;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        if (apps != null) {
            j = apps.size();
            for (i = 0; i < j; i++) {
                Trace.i("processName", apps.get(i).processName + "\t" + apps.get(i).importance);
                if (apps.get(i).processName.equals(pacetName)) {
                    if (apps.get(i).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBackActivity(Context context, String pacetName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        if (apps != null && apps.size() > 0 && apps.get(0).processName.equals(pacetName)) {
            Trace.i("processName", apps.get(0).processName + "\t" + apps.get(0).importance);
            if (apps.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED ||
                    apps.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE) {
                return true;
            } else
                return false;
        }
        return false;
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10及以上版本不再支持获取IMEI和序列号等标识符
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceID = null;
            if (tm != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                deviceID = tm.getDeviceId();
            }
            if (deviceID == null || deviceID.isEmpty()) {
                deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return deviceID;
        }
    }
}
