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

public class HujiangJapanese implements IDictionary {

    private final int lt = DictLanguageType.JPN;
    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "沪江日语在线";
    private static final String DICT_INTRO = "数据来自沪江日语词典. “音频”项是形如 [sound:xxx.mp3] 的发音，使用之前默认模版的用户需要编辑模版并在里面加入{{audio}}";
    private static final String[] EXP_ELE = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            "注音",
            Constant.DICT_FIELD_DEFINITION
    };

    private static final String wordUrl = "https://dict.hujiang.com/jp/jc/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";
    private static final String COOKIE = "_UZT_USER_SET_106_0_DEFAULT=2%7C94203fe9fb690808b7ef29aff3834b76; HJ_UID=92482875-6c87-23dc-b654-a051128fe960; TRACKSITEMAP=3%2C11%2C22%2C63%2C; _REF=http%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3D4EF7BW5aw2AUMr14xKpyBxPDcH6Ah3LnigHy27aeIww8BuyV8j6m02hc7eUKObbxwDxtpIE4vcOUheux2UYif_%26wd%3D%26eqid%3Df6c074f90001a098000000065a3e25dd; HJ_CMATCH=1; HJ_SID=47f67f36-d438-9a1f-782b-79a0eb723d97; HJ_CST=0; HJ_SSID_3=072d9dcf-6ba0-55a0-a662-22a1d555afb2; HJ_CSST_3=0; _SREF_3=";

    public int getLanguageType() {
        return lt;
    }

    public HujiangJapanese(Context context) {

    }

    public HujiangJapanese(){

    }
    private String mAudioUrl = "";
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
//            Connection.Response res = Jsoup.connect("https://www.hjdict.com")
//                    .userAgent(DEFAULT_UA)
//                    .method(Connection.Method.GET)
//                    .execute();
//            Map<String, String> cookies = res.cookies();

//            Document doc = Jsoup.connect(wordUrl + key)
//                    .userAgent(DEFAULT_UA)
//                    .header("cookie", COOKIE)
//                    //.cookies(cookies)
//                    .timeout(5000)
//                    .get();
//
            emptyAudioUrl();
            Request request = new Request.Builder().url(wordUrl + key)
                    .addHeader("User-Agent", Constant.UA)
                    .addHeader("Cookie",COOKIE)
                    .build();
            String html = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(html);
            //String html = doc.toString();
            Elements entrys = doc.select("div.word-details-pane");
            ArrayList<Definition> defList = new ArrayList<>();
            if (entrys.size() > 0) {
                for (Element ele : entrys){
                    String word = "";
                    String reading = "";
                    String mp3_url = "";
                    String spell = "";
                    //String meaning_tag = "";
                    //String definition = "";
                    Elements furigana_soup = ele.select(".word-text");
                    if(furigana_soup.size() > 0){
                        word = furigana_soup.get(0).text().trim();
                    }

                    Elements writing_soup = ele.select("div.pronounces");
                    if(writing_soup.size() > 0){
                        reading = writing_soup.get(0).text().trim();
                        Elements spell_soup = writing_soup.select("span");
                        spell = spell_soup.get(0).text().trim().replaceAll("[\\[\\]\\-]", "");
//                        Trace.e("spell", spell);
                    }

                    String tempAudioUrl = "";   //页面有多个音频，用临时变量audioUrl保存，不能用mAudioUrl
                    Elements audio_soup = ele.select("span.word-audio");
                    if(audio_soup.size() > 0){
                        tempAudioUrl = audio_soup.get(0).attr("data-src");
                        mp3_url = "[sound:" + tempAudioUrl + "]";
                    }

                    Elements meaning_tags_soup = ele.select(".simple li");
                    for(Element tag : meaning_tags_soup){
                        String meaning = tag.text().trim();
                        LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                        String definition = meaning.trim();
                        String audioFileName = Utils.getSpecificFileName(word);

                        String audioIndicator = "";
                        if(!mp3_url.isEmpty()){
                            audioIndicator = "<font color='#227D51' >"+AUDIO_TAG + "</font>";
                        }
//                        String complex = FieldUtil.formatComplexTplWord(DICT_NAME, word, reading, definition, Constant.AUDIO_INDICATOR_MP3);
//                        String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, word, reading, definition, "");
                        defMap.put(Constant.DICT_FIELD_KEYWORD, word);
                        defMap.put(EXP_ELE[1], reading);
                        defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                        String export_html = "<b>" + word + " </b>" +
                                "<font color = '#ba400d'>" + reading + "</font> " + audioIndicator + "<br/>" + definition;
                        Definition.ResInformation resInfor = new Definition.ResInformation(
                                tempAudioUrl, audioFileName, Constant.MP3_SUFFIX, spell
                        );
                        defList.add(new Definition(defMap, export_html, resInfor));
                    }
                }
            }

            return defList;
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


    public static void main(String[] args){
        IDictionary dic = new HujiangJapanese();
        //dic.wordLookup(UrlEscapers.urlFragmentEscaper().escape("娘"));
        dic.wordLookup("娘");
    }
}

