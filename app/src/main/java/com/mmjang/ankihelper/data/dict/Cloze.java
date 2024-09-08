package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.floating.screenshot.CaptureResultActivity;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.FileUtils;
import com.mmjang.ankihelper.util.StorageUtils;
import com.mmjang.ankihelper.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by liao on 2017/5/6.
 */

public class Cloze implements IDictionary {

    private int lt = DictLanguageType.ALL;
    private static final String DICT_NAME = "挖空笔记";
    private static final String[] EXP_ELE_LIST = new String[]{
            Constant.DICT_FIELD_CLOZE_CONTENT,
            Constant.DICT_FIELD_IMG
    };
    Context mContext;
    Settings settings;
    public Cloze(Context context) {
        mContext = context;
        settings = Settings.getInstance(mContext);
    }
    private String mAudioUrl = "";
    public String getAudioUrl() {
        return mAudioUrl;
    }
    public boolean setAudioUrl(String audioUrl) {
        try {
            mAudioUrl = audioUrl;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private void emptyAudioUrl() { mAudioUrl = ""; }
    public boolean isExistAudioUrl() {
        return false;
    }


    public int getLanguageType() {
        return lt;
    }

    public String getDictionaryName() {
        return "制作填空";
    }

    public String getIntroduction() {
        return "无释义，用于快速制作填空";
    }

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    public List<Definition> wordLookup(String key) {
        ArrayList<Definition> result = new ArrayList<>();
        LinkedHashMap<String, String> defMap = new LinkedHashMap<>();

        String fileName = Utils.getSpecificFileName(key);
        String audioName = fileName;
        String imageName = fileName + ".png";
        String imageTag = "";
        String imageUri = "";
        if (!settings.get(Settings.SCREENSHOT_NAME, "").equals("")) {
            File file = new File(StorageUtils.getIndividualCacheDirectory(mContext), settings.get(Settings.SCREENSHOT_NAME, ""));
            if (file.exists() && file.isFile()) {
                imageUri = file.getAbsolutePath();
                imageTag = String.format(Constant.TPL_HTML_IMG_TAG, imageName);
            }
        }
//        String complex = FieldUtil.formatComplexTplCloze(DICT_NAME, Constant.AUDIO_INDICATOR_MP3);
//        String muteComplex = FieldUtil.formatComplexTplCloze(DICT_NAME, "");

        defMap.put(Constant.DICT_FIELD_CLOZE_CONTENT, key);
        defMap.put(Constant.DICT_FIELD_IMG, imageTag);
//        defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//        defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//        defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
        Definition.ResInformation resInfor = new Definition.ResInformation(
                imageUri, imageName, "", audioName, Constant.MP3_SUFFIX
        );
        result.add(new Definition(defMap, "制作填空卡片（单击播放挖空内容）", resInfor));
        return result;
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

}
