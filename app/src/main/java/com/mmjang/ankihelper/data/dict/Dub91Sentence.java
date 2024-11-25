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

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class Dub91Sentence implements IDictionary {

    private final int lt = DictLanguageType.ENG;
//    private static final Pattern jsonPattern = Pattern.compile("window\\.__NUXT__=(.*?);</script>");
//    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "英语随心配例句";
    private static final String DICT_INTRO = "www.91dub.com";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_CHINESE_SENTENCE,
            Constant.DICT_FIELD_SENTENCE,
            Constant.DICT_FIELD_IMG
    };

    private static final String wordUrl = "https://www.91dub.com/api/sub_seek_ytb.php?keyword=%s&pageno=%s";
//    private static final String mp3Url = "https://audio.vocab.com/1.0/us/";
//    private static final String tplt_card = "<div class='eudic_sentence'>" +
//            "<div class='dub91_en'>%s</div>" +
//            "<div class='dub91_cn'>%s</div>" +
//            "<div class='dub91_title'>%s</div>" +
//            "<img class='dub91_img' src='%s'/>" +
//             "</div>";
//    private static final String tplt_ui = "" +
//            "<div>%s</div>" +
//            "<div>%s</div>" +
//            "<span>%s</span>" +
//            "";

    private static final String tplt_ui = "<font color = '#ba400d'>%s</font><br/>%s<br/>%s";

    public int getLanguageType() {
        return lt;
    }

    public Dub91Sentence(Context context){

    }
    private static String mAudioUrl = "";
    public String getAudioUrl() {
        return mAudioUrl;
    }
    private void emptyAudioUrl() { mAudioUrl = ""; }
    public boolean setAudioUrl(String audioUrl) {
        try {
            mAudioUrl = audioUrl;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
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

    private static String mLastKey = "";
    private static int mCurrentPage = 1;
    public List<Definition> wordLookup(String key) {
        try {
            emptyAudioUrl();
            List<Definition> definitionList = new ArrayList<>();

            if(key.equals(""))
                return new ArrayList<Definition>();

            if(!mLastKey.equals(key)) {
                mCurrentPage = 1;
            }

            mLastKey = key;
            Request request = new Request.Builder().url(String.format(wordUrl, key, 1))
                    //.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36")
                    .addHeader("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            JSONObject json = new JSONObject(rawhtml);
            JSONArray results = json.getJSONArray("seek_list");

            if(mCurrentPage > 1) {
                request = new Request.Builder().url(String.format(wordUrl, key, mCurrentPage))
                        //.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36")
                        .addHeader("User-Agent", Constant.UA)
                        .build();
                rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
                json = new JSONObject(rawhtml);
                JSONArray results2 = json.getJSONArray("seek_list");
                processOneSentence(key, results2, definitionList);
            }

            if(definitionList.isEmpty()) {
                mCurrentPage = 1;
                processOneSentence(key, results, definitionList);
            }
            mCurrentPage++;
            return definitionList;

        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public void processOneSentence(String key, JSONArray results, List<Definition> definitionList) throws IOException{
        try {

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                String en = item.getString("sub_orig")
                        .replaceAll("<em>", "<b>")
                        .replaceAll("</em>", "</b>").trim();
                String cn = item.getString("sub_trans")
                        .replaceAll("<em>", "<b>")
                        .replaceAll("</em>", "</b>").trim();

                String channel = item.getString("chn_nm");
                String imgUrl = item.getString("cover_img").replace("style/w3", "style/w1");

                String fileName = Utils.getSpecificFileName(key);
                String audioFileName = fileName;
                String imgName = fileName + ".png";
                String imageTag = String.format(Constant.TPL_HTML_IMG_TAG, imgName);

//                String complex = FieldUtil.formatComplexTplSentence(channel, key, en, cn, Constant.AUDIO_INDICATOR_MP3).
//                        replace(Constant._TPL_HTML_MEDIA_TAG_LOCATION_, imageTag + Constant._TPL_HTML_MEDIA_TAG_LOCATION_);
//                String muteComplex = FieldUtil.formatComplexTplSentence(channel, key, en, cn, "").
//                        replace(Constant._TPL_HTML_MEDIA_TAG_LOCATION_, imageTag + Constant._TPL_HTML_MEDIA_TAG_LOCATION_);
//                String complex = String.format(
//                        Constant.TPL_COMPLEX_TAG,
//                        channel,
//                        Constant.AUDIO_INDICATOR_MP3,
//                        String.format(Constant.TPL_COMPLEX_DICT_SENTENCE_TAG,
//                                StringUtil.appendTagAll(StringUtil.stripHtml(en), key, "<b>","</b>").
//                                        trim().replaceAll("[\n\r]{2,}", "<br/>"),
//                                StringUtil.stripHtml(cn)),
//                        imageTag+"%s");
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                defMap.put(Constant.DICT_FIELD_KEYWORD, key);
                defMap.put(EXP_ELE[1], cn);
                defMap.put(EXP_ELE[2], en);
                defMap.put(EXP_ELE[3], imageTag);
//                defMap.put(EXP_ELE[4], String.format(tplt_card,
//                        en,
//                        cn,
//                        "<font color=#808080>" + channel + "</font>",
//                        imgName
//                )
//                );
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                String html = String.format(tplt_ui,
                        channel,
                        en,
                        cn
                );
                if (i == 0) {//第一个条目显示页码
                    html = "<font color=#808080>page: " + mCurrentPage + "</font> " + html;
                }
                Definition.ResInformation resInfor = new Definition.ResInformation(
                        imgUrl, imgName, "", audioFileName, Constant.MP3_SUFFIX
                );
                definitionList.add(new Definition(defMap, html, resInfor));
            }
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
        }

    }

//    public void processOneSentence(String key, Element audioEle, List<Definition> definitionList) throws IOException{
//        LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
//
//        String audioFileName = key.trim() + "_" + Utils.getRandomHexString(8);
//        String audioId = audioEle.attr("source");
//        audioId = URLDecoder.decode(audioId, "UTF-8");
//        audioId = audioId.replace("/","_");
//        String audioUrl = String.format("https://fs-gateway.esdict.cn/store_main/sentencemp3/%s.mp3", audioId);
////                Elements a = audioEle.select("div.content > p.line a");
////                String audioUrl = "";
////                if(a.size() > 0){
////                    audioUrl = "http://api.frdic.com/api/v2/speech/speakweb?" + a.get(0).attr("data-rel");
////                }else{
////                    continue;
////                }
//        //audioUrl = URLDecoder.decode(audioUrl, "UTF-8");
//        String audioTag = String.format("[sound:%s]",  audioFileName + Constant.MP3_SUFFIX);
//        String channelTitle = getSingleQueryResult(audioEle, "div.channel > span.channel_title", false);
//        String en = getSingleQueryResult(audioEle, "div.content > p.line", true);
//        en = en.replaceAll("<a href=.*?</a>$", "");
//        en = en.replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>");
//        String cn = getSingleQueryResult(audioEle, "div.content > p.exp", true);
//        cn = cn.replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>");
//        String html = String.format(tplt_card,
//                en,
//                audioTag,
//                cn,
//                "<font color=grey>" + channelTitle + "</font>"
//        );
//
//        String html_ui = String.format(tplt_ui,
//                en,
//                cn,
//                "<font color=grey>" + channelTitle + "</font>"
//        );
//        defMap.put(EXP_ELE[0], key);
//        defMap.put(EXP_ELE[1], cn);
//        defMap.put(EXP_ELE[2], en);
//        defMap.put(EXP_ELE[3], html);
//        Definition.ResInformation resInfor = new Definition.ResInformation(
//                audioUrl, audioFileName, Constant.MP3_SUFFIX
//        );
//        definitionList.add(new Definition(defMap, html_ui, resInfor));
//    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

//    static String getSingleQueryResult(Document soup, String query, boolean toString){
//        Elements re = soup.select(query);
//        if(!re.isEmpty()){
//            if(toString){
//                return re.get(0).toString();
//            }else {
//                return re.get(0).text();
//            }
//        }else{
//            return "";
//        }
//    }

//    static String getSingleQueryResult(Element soup, String query, boolean toString){
//        Elements re = soup.select(query);
//        if(!re.isEmpty()){
//            if(toString) {
//                return re.get(0).toString();
//            }
//            else{
//                return re.get(0).text();
//            }
//        }else{
//            return "";
//        }
//    }

//    private String getMp3Url(String id){
//        return "[sound:" + mp3Url + id + ".mp3]";
//    }
//
//    private String getDefHtml(Element def){
//        String sense = def.toString().replaceAll("<h3.+?>","<div class='vocab_def'>").replace("</h3>","</div>").replaceAll("<a.+?>","<b>").replace("</a>","</b>");
//        //String defString = def.child(1).text().trim();
//        return sense;
//    }
}

