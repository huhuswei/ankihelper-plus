package com.mmjang.ankihelper.util;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadItem {
    private View itemView;
    private Button deleteButton;
    private Button downloadButton;
    private ProgressBar downloadProgress;
    private TextView statusText;
    private TextView displayName;
    private String savePath;
    private String downloadUrl; // 下载链接

    private DownloadFileInfo info;

    public View getItemView() {
        return itemView;
    }

    public Button getDownloadButton() {
        return downloadButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public ProgressBar getDownloadProgress() {
        return downloadProgress;
    }

    public TextView getStatusText() {
        return statusText;
    }

    public TextView getDisplayName() {
        return displayName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public DownloadFileInfo getInfo() {
        return info;
    }

    // 构造函数
    public DownloadItem(View itemView, Button downloadButton, Button deleteButton, ProgressBar downloadProgress, TextView statusText, TextView displayName, String downloadUrl, String savePath, DownloadFileInfo info) {
        this.itemView = itemView;
        this.downloadButton = downloadButton;
        this.deleteButton = deleteButton;
        this.downloadProgress = downloadProgress;
        this.displayName = displayName;
        this.statusText = statusText;
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
        this.info = info;

    }


    // 关联视图和DownloadItem对象的方法
    public static DownloadItem associateWithView(View view, DownloadItem item) {
        if (view instanceof Button) {
            ((Button) view).setTag(item);
        } else if (view instanceof ProgressBar) {
            ((ProgressBar) view).setTag(item);
        } else if (view instanceof TextView) {
            ((TextView) view).setTag(item);
        } else if (view instanceof View) {
            view.setTag(item);
        }
        return item;
    }

    // 获取关联的视图的方法
    public View getView() {
        return (View) this.getItemView();
    }

    // 可以添加更多的方法，比如开始下载、取消下载等

}