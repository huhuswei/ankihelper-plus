package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by liao on 2024/10/27.
 */

public class Krdict implements IDictionary {

    private final int lt = DictLanguageType.KOR;
    public int getLanguageType() {
        return lt;
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

    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "Krdict";
    private static final String DICT_INTRO = "数据来自 krdict.korean.go.kr";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_ORIGIN,
            Constant.DICT_FIELD_SENSE,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION,
            "star"
    };

    private static final String DICT_FORMAT_URL = "https://krdict.korean.go.kr/%s/dicMarinerSearch/search?&ParaWordNo=&isSmallDic=Y&mainSearchWord=";
    private static int mediaIndex = 0;
    private static String checkKey = "";

    private Context mContext;

    public Krdict(Context context) {
        this.mContext = context;
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
            emptyAudioUrl();
            String srcLang = "chn";
            String meaningSelector = "dd.manyLang11";
            if(RegexUtil.isChinese(key) || RegexUtil.isKorean(key.charAt(0))) {
                srcLang = "chn";
            }else if(RegexUtil.isEnglish(key)) {
                srcLang = "eng";
                meaningSelector = "dd.manyLang6";
            } else if(RegexUtil.isRussian(key)) {
                srcLang = "rus";
                meaningSelector = "dd.manyLang5";
            } else if(RegexUtil.isArabic(key)) {
                srcLang = "ara";
                meaningSelector = "div.small_content2";
            } else if(StringUtil.isJapanese(key)) {
                srcLang = "jpn";
                meaningSelector = "div.small_content2";
            } else if(RegexUtil.isThai(key)) {
                srcLang = "tha";
                meaningSelector = "dd.manyLang3";
            }
            String wordUrl = String.format(DICT_FORMAT_URL, srcLang);
            Document doc = Jsoup.connect(wordUrl + key)
                    .userAgent("Mozilla")
                    .timeout(5000)
                    .get();
            Elements entrys = doc.select("div.search_result > dl");
            ArrayList<Definition> defList = new ArrayList<>();
            for(Element resultData : entrys) {
                String word, origin, sense, phonetics, star, audioFileName, tempAudioUrl;
                String[] meaningArr;
                word = resultData.select("dt span.word_type1_17").text().replaceAll("[0-9]", "");
                audioFileName = Utils.getSpecificFileName(word);
                Elements soup = resultData.select("dt span.search_sub a");
                int size = soup.size();

                if(size > 0) {
                    tempAudioUrl = soup.get(mediaIndex%size).attr("href").replaceAll("javascript:fnSoundPlay\\('([^']+\\.mp3)'\\).*", "$1");
                } else {
                    tempAudioUrl = "";
                }

                resultData.select("dt span.search_sub > a").remove();
                sense = resultData.select("dt span.word_att_type1").text();
                phonetics = resultData.select("dt span.search_sub>span.search_sub").text();
                star = "⭐".repeat(resultData.select("dt span.search_sub>span.star>i.ri-star-s-fill").size());
                resultData.select("dt a").remove();
                resultData.select("dt span.word_att_type1").remove();
                resultData.select("dt span.search_sub").remove();
                origin = resultData.select("dt span").text();
                resultData.select("dt").remove();
                meaningArr = StringUtil.stripHtml(String.join("<br/>",resultData.select(meaningSelector).eachText())).split("[0-9]+\\.");//不同语言，class名字不同

                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                defMap.put(Constant.DICT_FIELD_KEYWORD, word);
                defMap.put(Constant.DICT_FIELD_ORIGIN, origin);
                defMap.put(Constant.DICT_FIELD_SENSE, sense);
                defMap.put(Constant.DICT_FIELD_PHONETICS, phonetics);
                defMap.put("star", star);
                for(String meaning : meaningArr) {
                    if(TextUtils.isEmpty(meaning)) continue;
                    meaning = meaning.replaceAll("\\s?<br/>\\s?$", "");
                    defMap.put(Constant.DICT_FIELD_DEFINITION, meaning);
                    String audioIndicator = "";
                    if(!tempAudioUrl.isEmpty()){
                        audioIndicator = "<font color='#227D51' >"+AUDIO_TAG + "</font>" + "<sub><small> " + (size>1?mediaIndex%size+1 + "</small></sub>":"");
                    }
                    String html = "<b>" + word + "</b> "
                            + (!origin.equals("") ? origin + " " : "")
                            + (!phonetics.equals("") ? phonetics + " " : "")
                            + (!sense.equals("") ? sense + " " : "")
                            + (!star.equals("") ? star + " ": "")
                            + (!audioIndicator.equals("") ? audioIndicator: "")
                            + "<br/>"
                            + meaning;
                    Trace.i("krdict: " + html);
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            tempAudioUrl, audioFileName, Constant.MP3_SUFFIX
                    );
                    defList.add(new Definition(defMap, html, resInfor));
                }
            }
            mediaIndex++;
            return defList;
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


}

