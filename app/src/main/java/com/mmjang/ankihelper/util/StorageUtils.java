package com.mmjang.ankihelper.util;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: StorageUtils
 * @Description: java类作用描述
 * @Author: 唐朝
 * @CreateDate: 2022/4/17 11:22 AM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/4/17 11:22 AM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mmjang.ankihelper.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

final public class StorageUtils {
    private static final String TAG = "StorageUtils";
    private static final String INDIVIDUAL_DIR_NAME = "video-cache";

    StorageUtils() {
    }

    public static File generateCachePath() {
        return StorageUtils.getIndividualCacheDirectory(MyApplication.getContext());
    }
    public static File getIndividualCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context, true);
        return cacheDir;
//        return new File(cacheDir, "video-cache");
    }

    public static File getAnkihelperDirectory() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Constant.EXTERNAL_STORAGE_DIRECTORY);
    }

    public static File getIndividualTesseractDirectory() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Constant.EXTERNAL_STORAGE_DIRECTORY + File.separator +
                Constant.EXTERNAL_STORAGE_TESSERACT_SUBDIRECTORY);
    }

    public static File getFormsDirectory() {
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Constant.EXTERNAL_STORAGE_DIRECTORY + File.separator +
                Constant.EXTERNAL_STORAGE_FORMS_SUBDIRECTORY);
    }
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;

        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException var5) {
            externalStorageState = "";
        }

        if (preferExternal && "mounted".equals(externalStorageState)) {
            appCacheDir = getExternalCacheDir(context);
        }

        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }

        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            Log.w(TAG, "Can't define system cache directory! '" + cacheDirPath + "%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }

        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            Log.w(TAG, "Unable to create external cache directory");
            return null;
        } else {
            return appCacheDir;
        }
    }

    public static File createCacheFile(String fileName) {
        File cacheDir = StorageUtils.getIndividualCacheDirectory(MyApplication.getContext());
        if(!cacheDir.exists()) cacheDir.mkdirs();
        return new File(cacheDir, fileName);
    }

    /**
     *  从assets目录中复制整个文件夹内容到新的路径下
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：Data(assets文件夹下文件夹名称)
     *  @param  newPath  String  复制后路径  如：data/data/（手机内部存储路径名称）
     */
    private static void copyFilesFromAssets(Context context,String oldPath,String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ////如果捕捉到错误则通知UI线程
            //MainActivity.handler.sendEmptyMessage(COPY_FALSE);
        }
    }

    /**
     * 复制assets文件夹下的文件夹到apk安装后的Ankihelper文件夹中
     * @param context
     * @param folder 要复制的assets文件夹下的文件夹或文件的名字，如assets文件夹下有个文件夹是Data，则folder的值为Data
     */
    public static void copyFileFromAssetsToAnkihelper(Context context,String folder){
        String filesDir = StorageUtils.getAnkihelperDirectory().getPath();;
        filesDir = filesDir + File.separator + folder;
        copyFilesFromAssets(context,folder,filesDir);
    }

    /**
     * 复制assets文件夹下的文件夹到apk安装后的files文件夹中
     * @param context
     * @param folder 要复制的assets文件夹下的文件夹或文件的名字，如assets文件夹下有个文件夹是Data，则folder的值为Data
     */
    void copyFileFromAssets(Context context,String folder){
        String filesDir = context.getFilesDir().getPath();
        filesDir = filesDir + "/assets/" + folder;
        copyFilesFromAssets(context,folder,filesDir);
    }
}