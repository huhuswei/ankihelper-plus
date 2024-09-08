package com.mmjang.ankihelper.util;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: ShareUtil
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/6/18 5:14 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/6/18 5:14 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.ui.popup.PopupActivity;
import com.mmjang.ankihelper.ui.share.AppInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShareUtil {
    /**
     * 查询手机内所有的应用列表
     * @param context
     * @return
     */
    public static List<AppInfo> getAllApps(Context context) {
        List<AppInfo> appList = new ArrayList<AppInfo>();
        PackageManager pm=context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (int i = 0;i< packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
            tmpInfo.setPackageName(packageInfo.packageName);
            tmpInfo.setVersionName(packageInfo.versionName);
            tmpInfo.setVersionCode(packageInfo.versionCode);
            tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
            //如果非系统应用，则添加至appList
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //排除当前应用（替换成你的应用包名即可）
                if(!packageInfo.packageName.equals("net.zy13.skhelper")) {
                    appList.add(tmpInfo);
                }
            }
        }
        return  appList;
    }
    /**
     * 保存图片到缓存里
     * @param bitmap
     * @return
     */
    public static String SaveTitmapToCache(Bitmap bitmap){
        // 默认保存在应用缓存目录里 Context.getCacheDir()
        File file=new File(MyApplication.getContext().getCacheDir(),System.currentTimeMillis()+".png");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    public static void share(Context context, String text) {
        long[] vibList = new long[1];
        vibList[0] = 10L;
        Intent intent = new Intent(context.getApplicationContext(), PopupActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Constant.INTENT_ANKIHELPER_ACTION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        context.startActivity(intent);
        try{
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


}

