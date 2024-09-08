package com.mmjang.ankihelper.ui.intelligence.tess;

import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.mmjang.ankihelper.ui.intelligence.Ocr;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.StorageUtils;

import java.io.File;
import java.util.function.Function;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.floating
 * @ClassName: Ocr
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/7/10 10:41 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/10 10:41 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class TessOcr implements Ocr {
    Context mContext;
    String mContent;
    Function<String, String> hookResult;
    TessBaseAPI tess;
    String dataPath;
    String lang;
    boolean success;


    public TessOcr(Context context) {
        mContext = context;
        mContent = "";
        lang = "";
        success = false;
        hookResult = s->s;
    }

    private void init() {

        if(tess != null) {
            tess.recycle();
            tess = null;
        }
        tess = new TessBaseAPI();

        dataPath = StorageUtils.getIndividualTesseractDirectory().getPath();

        File dataDir = new File(dataPath, Constant.EXTERNAL_STORAGE_TESSDATA_SUBDIRECTORY);
        if(!dataDir.exists())
            dataDir.mkdirs();
        // Initialize API for specified language (can be called multiple times during Tesseract lifetime)
        success = tess.init(dataPath, lang);
        if (success) {
            tess.readConfigFile("configs/config");
        } else {
            // Error initializing Tesseract (wrong data path or language)
            tess.recycle();
            lang = "";
        }

    }

    private void recognizeText(Bitmap image, int rotationDegree) {
        if(success) {
            tess.setImage(image);
            mContent = tess.getUTF8Text().replaceAll("([\u4e00-\u9fa5])\\x20*", "$1");
            hookResult.apply(mContent);
        } else {
            hookResult.apply("");
        }
    }

    public void setLanguageText(String lang) {
        // Given path must contain subdirectory `tessdata` where are `*.traineddata` language files
        this.lang = lang;
    }

    public String getText(Bitmap image, int rotationDegree) {
        init();
        if(success) {
            recognizeText(image, 0);
            return mContent;
        } else {
            return "";
        }
    }

    public String getText(Bitmap image) {
        return getText(image, 0);
    }

    public String getText(Bitmap image, Function<String, String> callback){
        this.hookResult = callback;
        return getText(image);
    }

    public void onDestroy() {
        if (tess != null) {
            tess.recycle();
            tess = null;
        }
    }
}
