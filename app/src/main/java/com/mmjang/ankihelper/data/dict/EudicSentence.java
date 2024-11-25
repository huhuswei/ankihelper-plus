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

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class EudicSentence implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "欧路词典原声例句";
    private static final String DICT_INTRO = "其中输入项<原声例句>为前面项目的组合";
    private static final String[] EXP_ELE =
            new String[] {
                    Constant.DICT_FIELD_KEYWORD,
                    Constant.DICT_FIELD_CHINESE_SENTENCE,
                    Constant.DICT_FIELD_SENTENCE,
                    /*"Vocab音频",*/
                    Constant.DICT_FIELD_ORIGIN,
//                    "Card"
    };

    private static final String wordUrl = "http://dict.eudic.net/liju/en/";
    private static final String mp3Url = "https://audio.vocab.com/1.0/us/";
    private static final String dictUrl = "https://dict.eudic.net/dicts/en/";
//    private static final String tplt_card = "<div class='eudic_sentence'>" +
//            "<div class='eudic_en'>%s</div>" +
//            "<div class='eudic_cn'>%s</div>" +
//            "<div class='eudic_title'>%s</div>" +
//             "</div>";
    private static final String tplt_ui = "" +
            "<span>%s</span><br/>" +
            "<span>%s</span><br/>" +
            "<span>%s</span>" +
            "";

    static int start = 0;
    private static String mLastKey = "";
    static Document doc = null;
    static Element load_more = null;

    public int getLanguageType() {
        return lt;
    }

    public EudicSentence(Context context){

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
        return true;
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
            List<Definition> definitionList = new ArrayList<>();

            if(key.equals(""))
                return new ArrayList<Definition>();

            if(!mLastKey.equals(key)) {
                mLastKey = key;
                start = 0;
                doc = null;
            }

            Request request = new Request.Builder().url(wordUrl + key)
                    .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36")
                    //.addHeader("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            doc = Jsoup.parse(rawhtml);

            if(start == 0) {
                load_more = doc.getElementById("liju_ting_loadmore");
            }
            if(start != 0) {
                if (load_more != null) {
                    String pageStatus = doc.getElementById("page-status").attr("value");
//                    Trace.e("page_status", pageStatus);
//                    Trace.e("start", String.valueOf(start));
                    FormBody formBody = new FormBody.Builder()
                            .add("status", pageStatus)
                            .add("start", String.valueOf(start))
                            .add("type", "ting")
                            .build();
                    Request request2 = new Request.Builder()
                            .post(formBody)
                            .url("http://dict.eudic.net/dicts/LoadMoreLiju")
                            .addHeader("User-Agent", Constant.UA)
                            .build();
                    String rawhtml2 = MyApplication.getOkHttpClient().newCall(request2).execute().body().string();
                    Document doc2 = Jsoup.parse("<html><body>" + rawhtml2 + "</body></html>");
                    for (Element audioEle : doc2.select("div.lj_item")) {
                        processOneSentence(key, audioEle, definitionList);
                    }
                    load_more = doc2.getElementById("liju_ting_loadmore");
                }
            }
            if(definitionList.isEmpty()) {
                for (Element audioEle : doc.select("#TingLiju div.lj_item")) {
                    processOneSentence(key, audioEle, definitionList);
                }
                //
                if(start != 0)
                    start = 0;
            }

            if(load_more != null)
                start += 20;
            else {
                //词典的原声例句
                Request requestDict = new Request.Builder().url(dictUrl + key)
                        .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36")
                        //.addHeader("User-Agent", Constant.UA)
                        .build();
                String dictRawHtml = MyApplication.getOkHttpClient().newCall(requestDict).execute().body().string();
                Document dictDoc = Jsoup.parse(dictRawHtml);
                String status = dictDoc.getElementById("page-status").attr("value");
                FormBody formBodyDict = new FormBody.Builder()
                        .add("status", status)
                        .build();
                Request requestLj = new Request.Builder()
                        .post(formBodyDict)
                        .url("https://dict.eudic.net/Dicts/en/tab-detail/-12")
                        .addHeader("User-Agent", Constant.UA)
                        .build();
                String ljRawHtml = MyApplication.getOkHttpClient().newCall(requestLj).execute().body().string();
                Document ljDoc = Jsoup.parse("<html><body>" + ljRawHtml + "</body></html>");
                for (Element audioEle : ljDoc.select("#lj_ting > div.lj_item")) {
                    processOneSentence(key, audioEle, definitionList);
                }
                start = 0;
            }
            return definitionList;
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public void processOneSentence(String key, Element audioEle, List<Definition> definitionList) throws IOException{
        LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();

        String audioFileName = Utils.getSpecificFileName(key);
        String audioId = audioEle.attr("source");
        audioId = URLDecoder.decode(audioId, "UTF-8");
        audioId = audioId.replace("/","_");
        String audioUrl = String.format("https://fs-gateway.esdict.cn/buckets/main/store_main/sentencemp3/%s.mp3", audioId);
//                Elements a = audioEle.select("div.content > p.line a");
//                String audioUrl = "";
//                if(a.size() > 0){
//                    audioUrl = "http://api.frdic.com/api/v2/speech/speakweb?" + a.get(0).attr("data-rel");
//                }else{
//                    continue;
//                }
        //audioUrl = URLDecoder.decode(audioUrl, "UTF-8");
        //String audioTag = String.format("[sound:%s]",  fileName + Constant.MP3_SUFFIX);
        String title = getSingleQueryResult(audioEle, "div.channel > span.channel_title", false);
        String en = getSingleQueryResult(audioEle, "div.content > p.line", true);
        en = Utils.onlyBold(en.replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>"));
        String cn = getSingleQueryResult(audioEle, "div.content > p.exp", true);
        cn = Utils.onlyBold(cn.replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>"));
//        en = en.replaceAll("<a href=.*?</a>$", "")
//                .replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>")
//                .replaceAll("class=\".+\">", ">").trim();
//        en = Utils.onlyBold(en);
//        String cn = getSingleQueryResult(audioEle, "div.content > p.exp", true)
//                .replaceAll("<span class=\"key\">(.*?)</span>", "<b>$1</b>")
//                .replaceAll("class=\".+\">", ">").trim();
//        cn = Utils.onlyBold(cn);
        String html_ui = String.format("<title><font color=#ba400d>" + (start+definitionList.size()+1) +" "+ tplt_ui,
                title + " </font><font color='#227D51' >MP3</font></title>",
                en,
                cn
        );

//        String html = String.format(tplt_card,
//                en,
//                //audioTag,
//                cn,
//                "<font color=grey>" + title + "</font>"
//        );
//        String audioIndicator = "";
//        if(!audioUrl.isEmpty()){
//            audioIndicator = "<font color='#227D51' >" + AUDIO_TAG + "</font>";
//        }
//        String complex = FieldUtil.formatComplexTplSentence(title, key, en, cn, Constant.AUDIO_INDICATOR_MP3);
//        String muteComplex = FieldUtil.formatComplexTplSentence(title, key, en, cn, "");
//        String complex = String.format(
//                Constant.TPL_COMPLEX_TAG,
//                title,
//                Constant.AUDIO_INDICATOR_MP3,
//                String.format(Constant.TPL_COMPLEX_DICT_SENTENCE_TAG,
//                        StringUtil.appendTagAll(StringUtil.stripHtml(en), key, "<b>","</b>").
//                                trim().replaceAll("[\n\r]{2,}", "<br/>"),
//                        StringUtil.stripHtml(cn)),
//                "%s");
//                String.format(Constant.TPL_HTML_AUDIO_TAG, audioFileName + Constant.MP3_SUFFIX));
        eleMap.put(Constant.DICT_FIELD_KEYWORD, key);
        eleMap.put(Constant.DICT_FIELD_CHINESE_SENTENCE, cn);
        eleMap.put(Constant.DICT_FIELD_SENTENCE, en);
//        eleMap.put(EXP_ELE[3], audioTag);//test
        eleMap.put(Constant.DICT_FIELD_ORIGIN, title);
//        eleMap.put(EXP_ELE[4], html);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

        Definition.ResInformation resInfor = new Definition.ResInformation(
                audioUrl, audioFileName, Constant.MP3_SUFFIX
        );
        definitionList.add(new Definition(eleMap, html_ui, resInfor));
    }

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

