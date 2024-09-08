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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class DictionaryDotCom implements IDictionary {

    private final int lt = DictLanguageType.ENG;

    private static final String DICT_NAME = "dictionary.com";
    private static final String DICT_INTRO = "英英词典，收词量大，释义全面\n" +
            "css class：";
    private static final String[] EXP_ELE = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION,
//            "复合项"
    };
    private static final String AUDIO_TAG = "MP3";
    private static final String wordUrl = "http://restcdn.dictionary.com/v2/word.json/";
    private static final String urlParams = "/completeFormatted?api_key=I6SnT6uSpyaarEn&audio=mp3&entry=all&part=all&hotlinks=on&platform=android&app_id=dcomAndroidFreeV7513";

    public int getLanguageType() {
        return lt;
    }
    public DictionaryDotCom(Context context) {
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
        return true;
    }


    public DictionaryDotCom(){

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
            emptyAudioUrl();
//            String doc = HttpGet.get(wordUrl + key + urlParams, null);
            Request request = new Request.Builder().url(wordUrl + key + urlParams)
                    .header("User-Agent", Constant.UA)
                    .build();
            String doc = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            JSONObject docJson = new JSONObject(doc);
            JSONArray entries = docJson.getJSONArray("entries");
            ArrayList<Definition> definitionList = new ArrayList<>();
            for(int i = 0; i < entries.length(); i ++){
                JSONObject entry = entries.getJSONObject(i);
                String headWord = entry.getString("entry");
                String prounceIPA = entry.getString("pronunciationIpa").trim();
                //此页面只有一个音频，用mAudioUrl保存
                String tempAudioUrl = entry.getString("audioUrlMp3");
                String audioUrlTag = "[sound:" + tempAudioUrl + "]";
                JSONArray dataParts = entry.getJSONArray("dataParts");
                for(int j = 0; j < dataParts.length(); j ++) {
                    JSONObject dataPartObj = dataParts.getJSONObject(j);
                    String type = dataPartObj.getString("type");
                    if (!type.equals("pos-block")) {
                        continue;
                    }
                    String pos = dataPartObj.getString("pos");
                    String content = dataPartObj.getString("content");
                    Document contentDoc = Jsoup.parse("<html><body>" + content + "</body></html>");
                    Elements smallElements = contentDoc.select("li");
                    for (int n = 0; n < smallElements.size(); n++) {
                        String defHtml = smallElements.get(n).text().replaceAll("\n", "<br/>")
                                .replaceAll(":(.+?)$", ":<font color='gray'><i>$1</i></font>");

                        LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                        String audioFileName = Utils.getSpecificFileName(headWord);
//                        String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, prounceIPA, defHtml, Constant.AUDIO_INDICATOR_MP3);
//                        String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, prounceIPA, defHtml, "");
                        defMap.put(Constant.DICT_FIELD_KEYWORD, headWord);
                        defMap.put(Constant.DICT_FIELD_PHONETICS, prounceIPA);
                        defMap.put(Constant.DICT_FIELD_DEFINITION, "<i>" + pos + "</i>" + "<br/>" + defHtml);
//                        defMap.put(EXP_ELE[3],
//                                "<div class='div_dictionary'><div class='dictionary_hwd'>" + headWord + "</div>" +
//                                        "<div class='dictionary_ipa'>" + "/" + prounceIPA + "/" + audioUrlTag + "</div>" +
//                                        "<div class='dictionary_def'><i>" + pos + "</i>" + "<br/>" + defHtml + "</div></div>"
//                        );
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                        String exportedHtml;
                        if (n == 0) {
                            String audioIndicator = "";
                            if (!tempAudioUrl.isEmpty()) {
                                mAudioUrl = tempAudioUrl;
                                audioIndicator = "<font color='#227D51' >" + AUDIO_TAG + "</font>";
                            }
                            exportedHtml = "<b>" + headWord + " </b>" +
                                    "<i>" + pos + "</i> " + audioIndicator + "<br/>" + defHtml;
                        } else {
                            exportedHtml = defHtml;
                        }
                        Definition.ResInformation resInfor = new Definition.ResInformation(
                                tempAudioUrl, audioFileName, Constant.MP3_SUFFIX
                        );
                        definitionList.add(new Definition(defMap, exportedHtml, resInfor));
                    }
                }
            }
            return definitionList;
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

    public static void main(String[] args){
        DictionaryDotCom dictionaryDotCom = new DictionaryDotCom();
        dictionaryDotCom.wordLookup("word");
    }
}