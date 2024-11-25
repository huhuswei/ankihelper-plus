package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class UrbanDict implements IDictionary {

    private final int lt = DictLanguageType.ENG;
//    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "俚语词典";
    private static final String DICT_INTRO = "数据来自 urban dictionary ";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_DEFINITION,
            Constant.DICT_FIELD_SENTENCE,
//            "复合项"
    };

    private static final String wordUrl = "https://api.urbandictionary.com/v0/define?term=";
    //private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    public int getLanguageType() {
        return lt;
    }

    public UrbanDict(Context context) {

    }
    private static String mAudioUrl = "";
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
        try {
//            Document doc = Jsoup.connect(wordUrl + key)
//                    .userAgent("Mozilla")
//                    .timeout(5000)
//                    .get();
            Request request = new Request.Builder().url(wordUrl + key)
                    .header("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            //Document doc = Jsoup.parse(rawhtml);
            JSONObject ub = new JSONObject(rawhtml);
            ArrayList<Definition> defList = new ArrayList<>();
            //Elements posList = doc.select("div.def-panel");

            ///
            JSONArray defArray = ub.getJSONArray("list");
            for(int i = 0; i < defArray.length(); i ++){
                JSONObject def = defArray.getJSONObject(i);
                String word = def.getString("word").trim();
                String definition = def.getString("definition").replaceAll("[\\[\\]]", "")
                        .replaceAll("\r\n", "<br/>");
                String example = def.getString("example").replaceAll("[\\[\\]]", "")
                        .replaceAll("\r\n", "<br/>").trim();
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                //String definition = posType + " " + def;
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, word, "", definition, Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, word, "", definition, "");
                        defMap.put(Constant.DICT_FIELD_KEYWORD, word);
                defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
                defMap.put(EXP_ELE[2], example);
                String html = "<div class=lwzj><b>" + word +" </b> " + "-" + "<span class=ys>" + definition +"</span><br/><font color=#4682B4><span class=yl>•" + example + "</span> </font></div>";
//                defMap.put(EXP_ELE[3], html);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                String audioFileName = Utils.getSpecificFileName(word);
                Definition.ResInformation resInfor = new Definition.ResInformation(
                        "", audioFileName, Constant.MP3_SUFFIX
                );
                defList.add(new Definition(defMap, html, resInfor));
            }
            return defList;
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return new UrbanAutoCompleteAdapter(context, layout);
    }


}
