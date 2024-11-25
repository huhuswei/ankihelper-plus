package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.translation.TranslateBuilder;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ss on 2023/11/13.
 */

public class BatchClip implements IDictionary {

    private int lt = DictLanguageType.ALL;
    private static final String DICT_NAME = "批量";
    private static final String SEPARATOR = "(?<!\")([?!]+|\\.\\s|\\n)(?!\")";
    private static final String[] EXP_ELE_LIST = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_SENTENCE,
            Constant.DICT_FIELD_CHINESE_SENTENCE
    };

    public BatchClip(Context context) {
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
        return "分批制作例句";
    }

    public String getIntroduction() {
        return "对内容进行分割和翻译";
    }

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    public List<Definition> wordLookup(String key) {
        ArrayList<Definition> result = new ArrayList<>();
        for(String k : key.split(SEPARATOR)) {
            if(TextUtils.isEmpty(k.trim())) continue;
            String audioFileName = Utils.getSpecificFileName(k);
            if((key.indexOf(k)+k.length()) < key.length())
                k = k + key.charAt(key.lastIndexOf(k)+k.length());
//        String complex = FieldUtil.formatComplexTplCloze(DICT_NAME, Constant.AUDIO_INDICATOR_MP3);
//        String muteComplex = FieldUtil.formatComplexTplCloze(DICT_NAME, "");

            String translate = new TranslateBuilder(
                    Settings.getInstance(MyApplication.getContext()).
                            getTranslatorCheckedIndex()).translate(k);
            LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
            defMap.put(Constant.DICT_FIELD_KEYWORD, k);
            defMap.put(Constant.DICT_FIELD_SENTENCE, k);
            defMap.put(Constant.DICT_FIELD_CHINESE_SENTENCE, translate);
//        defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//        defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//        defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
            Definition.ResInformation resInfor = new Definition.ResInformation(
                    "", audioFileName, Constant.MP3_SUFFIX
            );
            result.add(new Definition(defMap, String.format("%s<br/>%s", k,translate), resInfor));
        }
        return result;
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

}
