package com.mmjang.ankihelper.ui.intelligence.tess;

import com.google.gson.annotations.SerializedName;
import com.mmjang.ankihelper.util.DownloadFileInfo;

public class TesseractDataInfo implements DownloadFileInfo {
    @SerializedName("language_name")
    private String name;
    @SerializedName("package_name")
    private String packageName;
    @SerializedName("download_package")
    private String downloadPackage;


    // Getters
    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDownloadPackage() {
        return downloadPackage;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDownloadPackage(String downloadPackage) {
        this.downloadPackage = downloadPackage;
    }
}
