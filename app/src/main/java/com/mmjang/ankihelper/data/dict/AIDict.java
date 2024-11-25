package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.widget.ListAdapter;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AIDict implements IDictionary {

    private final int lt = DictLanguageType.ALL;
    private static final String DICT_NAME = "AI Dictionary";
    private static final String DICT_INTRO = "Data powered by AI";
    private static final String[] EXP_ELE = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_DEFINITION
    };

    public AIDict(Context context) {
    }

    public String getAudioUrl() {
        return "";
    }

    public boolean setAudioUrl(String audioUrl) {
        return false;
    }

    public boolean isExistAudioUrl() {
        return false;
    }

    public int getLanguageType() {
        return lt;
    }

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public String[] getExportElementsList() {
        return EXP_ELE;
    }

    public List<Definition> wordLookup(String key) {
        List<Definition> defList = new ArrayList<>();
        String definition = "Waiting";
        String audioFileName = Utils.getSpecificFileName(key);
        LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
        defMap.put(Constant.DICT_FIELD_KEYWORD, key);
        defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
        String html = "<b>" + key + "</b><br/>" + definition;
        Definition.ResInformation resInfor = new Definition.ResInformation(
                "", audioFileName, Constant.MP3_SUFFIX
        );
        defList.add(new Definition(defMap, html, resInfor));
        return defList;
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }
}