package com.mmjang.ankihelper.ui.share;

import android.graphics.drawable.Drawable;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.share
 * @ClassName: AppInfo
 * @Description: APP信息实体类
 * @Author: ss
 * @CreateDate: 2022/6/18 5:07 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/6/18 5:07 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class AppInfo {
    private String appName;
    private String packageName;
    private String versionName;
    private int versionCode;
    private String launchClassName;
    private Drawable appIcon;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getLaunchClassName() {
        return launchClassName;
    }

    public void setLaunchClassName(String launchClassName) {
        this.launchClassName = launchClassName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
