package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class VocabCom implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    public int getLanguageType() {
        return lt;
    }

    private String mAudioUrl = "";
    public String getAudioUrl() { return mAudioUrl; }
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


    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "Vocabulary.com";
    private static final String DICT_INTRO = "数据来自 Vocabulary.com";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_DEFINITION
//            "复合项"
    };

    private static final String wordUrl = "http://app.vocabulary.com/app/1.0/dictionary/search?word=";
    private static final String mp3Url = "https://audio.vocab.com/1.0/us/";

    public VocabCom(Context context){

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
            emptyAudioUrl();
            Request request = new Request.Builder().url(wordUrl + key).build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);
            String headWord = getSingleQueryResult(doc, "h1.dynamictext", false);
            String defShort = getSingleQueryResult(doc, "p.short", true).replace("<i>","<b>").replace("</i>","</b>");
            String defLong = getSingleQueryResult(doc, "p.long", true).replace("<i>","<b>").replace("</i>","</b>");
            Elements mp3Soup = doc.select("a.audio");
            String mp3Id = "";
            if(mp3Soup.size() > 0){
                mp3Id = mp3Soup.get(0).attr("data-audio");
            }
            //此页面只有一个音频，用mAudioUrl保存
            mAudioUrl = mp3Url + mp3Id + Constant.MP3_SUFFIX;

            List<Definition> definitionList = new ArrayList<>();
            if(headWord.isEmpty()){
                return definitionList;
            }
            String audioFileName = Utils.getSpecificFileName(headWord);
            if(!defShort.isEmpty()){
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                String audioIndicator = "";
                if(!mAudioUrl.isEmpty()){
                    audioIndicator = "<font color='#227D51' >" + AUDIO_TAG + "</font>";
                }
                String definition = defLong.trim() + defLong.trim();
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", definition, Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", definition, "");

                defMap.put(Constant.DICT_FIELD_KEYWORD, headWord);
                defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                defMap.put(EXP_ELE[2], "<div class='dictionary_vocab'>" +
//                        "<div class='vocab_hwd'>" + headWord + "</div>" +
//                        "<div class='vocab_def'>" + defShort + defLong + "</div>" +
//                        "</div>");
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                String exportedHtml = audioIndicator +defShort + defLong;
                Definition.ResInformation resInfor = new Definition.ResInformation(
                        mAudioUrl, audioFileName, Constant.MP3_SUFFIX
                );
                definitionList.add(new Definition(defMap, exportedHtml, resInfor));
            }

            for(Element def : doc.select("h3.definition")){
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                String defText = getDefHtml(def);
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", defText, Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", defText, "");
                defMap.put(EXP_ELE[0], headWord);
                defMap.put(EXP_ELE[1], defText);
//                defMap.put(EXP_ELE[2],
//                        "<div class='dictionary_vocab'>" +
//                                "<div class='vocab_hwd'>" + headWord + "</div>" +
//                                "<div class='vocab_def'>" + defText + "</div>" +
//                                "</div>"
//                );
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                Definition.ResInformation resInfor = new Definition.ResInformation(
                        mAudioUrl, audioFileName, Constant.MP3_SUFFIX
                );
                definitionList.add(new Definition(defMap, defText, resInfor));
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

    static String getSingleQueryResult(Document soup, String query, boolean toString){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            if(toString){
                return re.get(0).toString();
            }else {
                return re.get(0).text();
            }
        }else{
            return "";
        }
    }

    static String getSingleQueryResult(Element soup, String query, boolean toString){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            if(toString) {
                return re.get(0).toString();
            }
            else{
                return re.get(0).text();
            }
        }else{
            return "";
        }
    }

    private String getAudioUrlTag(String audioUrl){
        return "[sound:" + audioUrl + "]";
    }

    private String getAudioUrl(String id) {
        return mp3Url + id + Constant.MP3_SUFFIX;
    }

    private String getAudioTag(String audioFile) {
        return "[sound:" +  audioFile + "]";
    }

    private String getAudioFile(String headWord) {
        return headWord + "_" + "vocab" + "_" + Utils.getRandomHexString(8) + Constant.MP3_SUFFIX;
    }
    private String getDefHtml(Element def){
        String sense = def.toString().replaceAll("<h3.+?>","<div class='vocab_def'>").replace("</h3>","</div>").replaceAll("<a.+?>","<b>").replace("</a>","</b>");
        //String defString = def.child(1).text().trim();
        return sense;
    }
}

