//package com.mmjang.ankihelper.data.dict;
//
//import android.content.Context;
//import android.util.Log;
//import android.widget.ListAdapter;
//import android.widget.Toast;
//
//import com.mmjang.ankihelper.MyApplication;
//import com.mmjang.ankihelper.domain.PronounceManager;
//import com.mmjang.ankihelper.ui.popup.PopupActivity;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * Created by liao on 2017/4/28.
// */
//
//public class JiSho implements IDictionary {
//
//    private final int lt = DictionaryLanguageType.JA;
//    private static final String AUDIO_TAG = "MP3";
//    private static final String DICT_NAME = "Japenese (jisho.org)";
//    private static final String DICT_INTRO = "数据来自 jisho.org. audio项是形如 [sound:xxx.mp3] 的发音，使用之前默认模版的用户需要编辑模版并在里面加入{{audio}}";
//    private static final String[] EXP_ELE = new String[] {"word", "furigana", "JiSho audio", "definition"};
//
//    private static final String wordUrl = "https://jisho.org/search/";
//    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";
//
//    private String audioUrl = "";
//    public String getAudioUrl() {
//        return audioUrl;
//    }
//
//    public int getLanguageType() {
//        return lt;
//    }
//
//    public JiSho(Context context) {
//    }
//
//    public String getDictionaryName() {
//        return DICT_NAME;
//    }
//
//    public String getIntroduction() {
//        return DICT_INTRO;
//    }
//
//    public String[] getExportElementsList() {
//        return EXP_ELE;
//    }
//
//    public List<Definition> wordLookup(String key) {
//        try {
//            Document doc = Jsoup.connect(wordUrl + key)
//                    .userAgent("Mozilla")
//                    .timeout(5000)
//                    .get();
//            Elements entrys = doc.select("div.concept_light");
//            ArrayList<Definition> defList = new ArrayList<>();
//            if (entrys.size() > 0) {
//                for (Element ele : entrys) {
//                    String furigana = "";
//                    String word = "";
//                    String mp3_url = "";
//                    //String meaning_tag = "";
//                    //String definition = "";
//                    Elements furigana_soup = ele.select("span.furigana");
//                    if(furigana_soup.size() > 0){
//                        furigana = furigana_soup.get(0).text().trim();
//                    }
//
//                    Elements writing_soup = ele.select("span.text");
//                    if(writing_soup.size() > 0){
//                        word = writing_soup.get(0).text().trim();
//                    }
//
//                    Elements audio_soup = ele.select("audio > source");
//                    if(audio_soup.size() > 0){
//                        audioUrl = "https:" +audio_soup.get(0).attr("src");
//                        mp3_url = "[sound:" +audioUrl + "]";
//                    }
//
//                    Elements meaning_definition_tags = ele.select("div.meaning-definition");
//                    //String meaning_tag = tag.text().trim();
//                    HashMap<String, String> defMap = new HashMap<>();
//                    Elements defSoup = meaning_definition_tags.select("span.meaning-meaning");
//                    Elements supplemental_info = meaning_definition_tags.select("span.supplemental_info");
//                    String definition = "<i><font color='grey'>" + /* meaning_tag +*/ "</font></i> " + defSoup.text().trim();// + supplemental_info.get(0).text().trim();
//                    if(supplemental_info.size()>0) {
//                        definition += "<br />" + supplemental_info.text();
//                    }
//                    defMap.put(EXP_ELE[0], word);
//                    defMap.put(EXP_ELE[1], furigana);
//                    defMap.put(EXP_ELE[2], mp3_url);
//                    defMap.put(EXP_ELE[3], definition);
//                    String audioIndicator = "";
//
//                    if (!mp3_url.isEmpty()) {
//                        audioIndicator = "<font color='#227D51' >" + AUDIO_TAG + "</font>";
//                    }
//                    String export_html = "<b>" + word + "</b> <font color='grey'>" + furigana + "</font> " + audioIndicator + "<br/>" + definition;
//                    defList.add(new Definition(defMap, export_html));
//                }
//            }
//            return defList;
//        } catch (IOException ioe) {
//            //Log.d("time out", Log.getStackTraceString(ioe));
//            Toast.makeText(MyApplication.getContext(), Log.getStackTraceString(ioe), Toast.LENGTH_SHORT).show();
//            return new ArrayList<Definition>();
//        }
//
//    }
//
//    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
//        return null;
//    }
//
//
//}
//

package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

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

/**
 * Created by liao on 2017/4/28.
 */

public class JiSho implements IDictionary {

    private final int lt = DictLanguageType.JPN;
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
    private static final String DICT_NAME = "Japenese (jisho.org)";
    private static final String DICT_INTRO = "数据来自 jisho.org. audio项是形如 [sound:xxx.mp3] 的发音，使用之前默认模版的用户需要编辑模版并在里面加入{{audio}}";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION
    };

    private static final String wordUrl = "https://jisho.org/search/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    private Context mContext;

    public JiSho(Context context) {
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
        try {
            emptyAudioUrl();
            Document doc = Jsoup.connect(wordUrl + key)
                    .userAgent("Mozilla")
                    .timeout(5000)
                    .get();
            Elements entrys = doc.select("div.concept_light");
            ArrayList<Definition> defList = new ArrayList<>();
            if (entrys.size() > 0) {
                for (Element ele : entrys){
                    String furigana = "";
                    String writing = "";
                    String mp3_url = "";
                    //String meaning_tag = "";
                    //String definition = "";
                    Elements furigana_soup = ele.select("span.furigana");
                    if(furigana_soup.size() > 0){
                        furigana = furigana_soup.get(0).text().trim();
                    }

                    Elements writing_soup = ele.select("span.text");
                    if(writing_soup.size() > 0){
                        writing = writing_soup.get(0).text().trim();
                    }

                    Elements audio_soup = ele.select("audio > source");
                    String tempAudioUrl = "";   //页面有多个音频，用临时变量audioUrl保存，不能用mAudioUrl
                    if(audio_soup.size() > 0){
                        tempAudioUrl = "https:" + audio_soup.get(0).attr("src");
//                        mp3_url = "[sound:" + tempAudioUrl + "]";
                    }

                    Elements meaning_tags_soup = ele.select("div.meaning-tags");
                    for(Element tag : meaning_tags_soup){
                        String meaning_tag = tag.text().trim();
                        Element word_def_soup = tag.nextElementSibling();
                        if(word_def_soup != null){
                            for(Element defSoup : word_def_soup.select("div.meaning-definition > span.meaning-meaning")){
                                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                                String definition = "<i><font color='grey'>" + meaning_tag + "</font></i> " + defSoup.text().trim();
                                String audioFileName = Utils.getSpecificFileName(writing);
                                String audioIndicator = "";
                                if(!tempAudioUrl.isEmpty()){
                                    audioIndicator = "<font color='#227D51' >"+AUDIO_TAG + "</font>";
                                }
//                                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, writing, furigana, definition, Constant.AUDIO_INDICATOR_MP3);
//                                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, writing, furigana, definition, "");
                                defMap.put(Constant.DICT_FIELD_KEYWORD, writing);
                                defMap.put(Constant.DICT_FIELD_PHONETICS, furigana);
//                                defMap.put(EXP_ELE[2], mp3_url);
                                //defMap.put(EXP_ELE[3], meaning_tag);
                                defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                                String exportedHtml = "<b>" + writing + "</b> <font color='grey'>" + furigana + "</font> " + audioIndicator + "<br/>" + definition;
                                Definition.ResInformation resInfor = new Definition.ResInformation(
                                        tempAudioUrl, audioFileName, Constant.MP3_SUFFIX
                                );
                                defList.add(new Definition(defMap, exportedHtml, resInfor));
                            }
                        }
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


}

