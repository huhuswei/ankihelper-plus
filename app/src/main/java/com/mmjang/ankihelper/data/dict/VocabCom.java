package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.RegexUtil;
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

    private static String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "Vocabulary.com";
    private static final String DICT_INTRO = "数据来自 Vocabulary.com";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_SENSE,
            Constant.DICT_FIELD_DEFINITION,
            Constant.DICT_FIELD_SENTENCE
//            "复合项"
    };

//    private static final String wordUrl = "http://app.vocabulary.com/app/1.0/dictionary/search?word=";
    private static final String baseUrl = "https://www.vocabulary.com";
    private static final String newWordUrl = baseUrl + "/dictionary";
    private static final String mp3Url = "https://audio.vocabulary.com/1.0/us/";
    private static int mediaIndex = 0;
    private static String checkKey = "";

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
        if(!checkKey.equals(key)) mediaIndex = 0;
        checkKey = key;
        try {
//            Document doc = Jsoup.connect(wordUrl + key)
//                    .userAgent("Mozilla")
//                    .timeout(5000)
//                    .get();
            emptyAudioUrl();
//            Request request = new Request.Builder().url(wordUrl + key).build();
//            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
//            Document doc = Jsoup.parse(rawhtml);

            Request request = new Request.Builder().url(newWordUrl + "/" + key).build();
            String newRawHtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document newDoc = Jsoup.parse(newRawHtml);

            String mediaSuffix = Constant.MP3_SUFFIX;

//            String headWord = getSingleQueryResult(doc, "h1.dynamictext", false);   //new selector: div.header-container h1
//            String defShort = getSingleQueryResult(doc, "p.short", true).replace("<i>","<b>").replace("</i>","</b>");
//            String defLong = getSingleQueryResult(doc, "p.long", true).replace("<i>","<b>").replace("</i>","</b>");
//            Elements mp3Soup = doc.select("a.audio");

            String headWord = getSingleQueryResult(newDoc, "div.header-container h1", false);   //new selector: div.header-container h1
            String defShort = getSingleQueryResult(newDoc, "p.short", true).replace("<i>","<b>").replace("</i>","</b>");
            String defLong = getSingleQueryResult(newDoc, "p.long", true).replace("<i>","<b>").replace("</i>","</b>");
            String defForms = getSingleQueryResult(newDoc, "p.word-forms", true).replace("<i>","<b>").replace("</i>","</b>");
            String phonetic = "";

//            Elements soup = newDoc.select("div.ipa-with-audio > a.audio");
//            /* mp3 */
//            if(soup.size() == 1) {
//                String mp3Id = "";
//                mp3Id = soup.get(0).select("a").attr("data-audio");
//                //此页面只有一个音频，用mAudioUrl保存
//                mAudioUrl = mp3Url + mp3Id + Constant.MP3_SUFFIX;
//                phonetic = soup.get(0).select("span.span-replace-h3").text();
//            } else if (soup.size() == 2) {
//                /* mp4 */
//                String mp4Url = soup.select("a audio.pron-audio").attr("src");
//                mediaSuffix = Constant.MP4_SUFFIX;
//                mAudioUrl = mp4Url;
//                AUDIO_TAG = "MP4";
//                phonetic = soup.select("span.")
//            }
            Elements soup = newDoc.select("div.video-with-label");
            int size = soup.size();
            if (size > 0) {
                mAudioUrl = soup.select("source").get(mediaIndex%size).attr("src");
                AUDIO_TAG = "MP4";
                phonetic = soup.select("span").get(mediaIndex%size).html();
                mediaSuffix = Constant.MP4_SUFFIX;
            } else {
                soup = newDoc.select("div.ipa-with-audio");
                if(soup.size() > 0) {
                    String mp3Id = soup.select("a.audio").get(0).attr("data-audio");
                    mAudioUrl = mp3Url + mp3Id + Constant.MP3_SUFFIX;
                    AUDIO_TAG = "MP3";
                    phonetic = soup.select("span.span-replace-h3").text();
                    Trace.i("vosv:" + phonetic);
                }
            }

            List<Definition> definitionList = new ArrayList<>();
            if(headWord.isEmpty()){
                return definitionList;
            }
            String audioFileName = Utils.getSpecificFileName(headWord);
            String audioIndicator = "";
            if(!defShort.isEmpty()){
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();

                if(!mAudioUrl.isEmpty()){
                    audioIndicator = "<font color='#227D51' >" + AUDIO_TAG + "</font>" + "<sub><small> " + (size>1?mediaIndex%size+1 + "</small></sub>":"");
                }
                String definition = defShort.trim() + defLong.trim() + defForms;
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", definition, Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", definition, "");

                defMap.put(Constant.DICT_FIELD_KEYWORD, headWord);
                defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
                defMap.put(Constant.DICT_FIELD_PHONETICS, phonetic);
//                defMap.put(EXP_ELE[2], "<div class='dictionary_vocab'>" +
//                        "<div class='vocab_hwd'>" + headWord + "</div>" +
//                        "<div class='vocab_def'>" + defShort + defLong + "</div>" +
//                        "</div>");
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                String exportedHtml = headWord + " "
                        + (!phonetic.equals("") ? phonetic + " " : "")
                        + audioIndicator + "<br/>"
                        + (defShort.isEmpty() ? "" : defShort + "<br/>") + (defLong.isEmpty() ? "" : defLong + "<br/>") + (defForms.isEmpty()? "" : defForms);
//                String exportedHtml = audioIndicator +defShort + defLong;
                Definition.ResInformation resInfor = new Definition.ResInformation(
                        mAudioUrl, audioFileName, mediaSuffix
                );
                definitionList.add(new Definition(defMap, exportedHtml, resInfor));
            }

            for(Element def : newDoc.select("div.word-definitions ol li")){
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                String sense = RegexUtil.colorizeSense(def.select("div.definition div.pos-icon").html());
                def.select("div.definition div.pos-icon").remove();
                String sentence = def.select("div.defContent > div.example").html().replaceAll("<(/?)strong>", "<$1b>");//getDefHtml(def);
                def.select("div.defContent > div.example").remove();
                String defText = def.outerHtml()
                        .replace("\"#0\"", "\"/dictionary/" + headWord + "#0\"")
                        .replace("href=\"", "href=\"" + baseUrl);
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", defText, Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, "", defText, "");
                defMap.put(Constant.DICT_FIELD_KEYWORD, headWord);
                defMap.put(Constant.DICT_FIELD_DEFINITION, defText);
                defMap.put(Constant.DICT_FIELD_PHONETICS, phonetic);
                defMap.put(Constant.DICT_FIELD_SENSE, sense);
                defMap.put(Constant.DICT_FIELD_SENTENCE, sentence);
//                defMap.put(EXP_ELE[2],
//                        "<div class='dictionary_vocab'>" +
//                                "<div class='vocab_hwd'>" + headWord + "</div>" +
//                                "<div class='vocab_def'>" + defText + "</div>" +
//                                "</div>"
//                );
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
//                String exportedHtml = headWord + "<br/>" + defText;
                String exportedHtml = "<b>" + headWord +"</b> "
                        + (!phonetic.equals("") ? phonetic + " " : "")
                        + audioIndicator + "<br/>"
                        + (!sense.equals("")?sense+" ":"") + "<br/>"
                        + defText
                        + (sentence.isEmpty() ? "" : ("<b>eg:</b><br/>" + sentence));

                Definition.ResInformation resInfor = new Definition.ResInformation(
                        mAudioUrl, audioFileName, mediaSuffix
                );
                definitionList.add(new Definition(defMap, exportedHtml, resInfor));
            }
            mediaIndex++;
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

