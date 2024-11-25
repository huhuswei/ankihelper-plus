package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class BingOxford implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "必应牛津英汉双解";
    private static final String DICT_INTRO = "数据来自 Bing 在线词典";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_SENSE,
            Constant.DICT_FIELD_DEFINITION
    };

    private static final String wordUrl = "https://cn.bing.com/dict/search?q=";

    public BingOxford(Context context) {
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
        try {
            Request request = new Request.Builder().url(wordUrl + key).build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);
            ArrayList<Definition> defList = new ArrayList<>();
            String word = VocabCom.getSingleQueryResult(doc, ".hd_div h1", false);
            String yinbiao = VocabCom.getSingleQueryResult(doc, "div.hd_p1_1", false).replaceAll("[\\[\\]]", "/");

            String audioFileName = Utils.getSpecificFileName(word);

            // div.in_tip.b_fpage
            // div.hd_if
            String def = VocabCom.getSingleQueryResult(doc, "div.in_tip.b_fpage", false);
            def = (!def.equals("")?def+"<br/>":"") + VocabCom.getSingleQueryResult(doc, "div.hd_if", false);
            if(!VocabCom.getSingleQueryResult(doc, "span.pos.web", false).equals(""))
                def = doc.select("span.def.b_regtxt").last().text().trim() + "<br/>" + def;
            if(!def.equals("")) {
                String sense = RegexUtil.colorizeSense("web.");
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, key, yinbiao, sense, def, Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, key, yinbiao, sense, def, "");
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                defMap.put(Constant.DICT_FIELD_KEYWORD, key);
                defMap.put(Constant.DICT_FIELD_PHONETICS, "");
                defMap.put(Constant.DICT_FIELD_SENSE, sense);
                defMap.put(Constant.DICT_FIELD_DEFINITION, def);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                String html = "<b>" + key +"</b><br/></b>" + (!sense.equals("")?sense+"<br/>":"") + def;
                Definition.ResInformation resInfor = new Definition.ResInformation(
                        "", audioFileName, Constant.MP3_SUFFIX
                );
                defList.add(new Definition(defMap, html, resInfor));
            }

//          权威英汉双解
            Elements posList = doc.select("div.li_pos");
            for(Element pos : posList){
                String sense = RegexUtil.colorizeSense(VocabCom.getSingleQueryResult(pos, "div.pos", false).trim()) + " " +
                        VocabCom.getSingleQueryResult(pos, "span.gra.b_regtxt", false);
                Elements defs = pos.select("div.def_pa");
                for(Element ele : defs){
                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                    String definition = StringUtil.stripHtml(ele.html());
//                    String complex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, sense, definition, Constant.AUDIO_INDICATOR_MP3);
//                    String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, sense, definition, "");
                    defMap.put(Constant.DICT_FIELD_KEYWORD, word);
                    defMap.put(Constant.DICT_FIELD_PHONETICS, yinbiao);
                    defMap.put(Constant.DICT_FIELD_SENSE, sense);
                    defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                    String html = "<b>" + word +"</b> " + (!yinbiao.equals("")?yinbiao+"<br/>":"") + (!sense.equals("")?sense+"<br/>":"") + definition;
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            "", audioFileName, Constant.MP3_SUFFIX
                    );
                    defList.add(new Definition(defMap, html, resInfor));
                }
            }

            // 英汉、英英
            //tr.def_row.df_div1
            //  div.pos.pos1
            //  .gl_none span.p1-1.b_regtxt | .de_li1.de_li3 .df_cr_w
            posList = doc.select("tr.def_row.df_div1");
            for(Element pos : posList){
                String sense = RegexUtil.colorizeSense(VocabCom.getSingleQueryResult(pos, "div.pos.pos1", false));
                Elements defs = pos.select(".gl_none span.p1-1.b_regtxt");
                if(defs.isEmpty())
                    defs = pos.select(".de_li1.de_li3 .df_cr_w");
                for(Element ele : defs) {
                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                    String definition = StringUtil.stripHtml(ele.html());
//                    String complex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, sense, definition, Constant.AUDIO_INDICATOR_MP3);
//                    String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, sense, definition, "");
                    defMap.put(Constant.DICT_FIELD_KEYWORD, word);
                    defMap.put(Constant.DICT_FIELD_PHONETICS, yinbiao);
                    defMap.put(Constant.DICT_FIELD_SENSE, sense);
                    defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                    String html = "<b>" + word +"</b> " + (!yinbiao.equals("") ? yinbiao + "<br/>" : "") + (!sense.equals("")?sense+"<br/>":"") + definition;
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            "", audioFileName, Constant.MP3_SUFFIX
                    );
                    defList.add(new Definition(defMap, html, resInfor));
                }
            }

            // 搭配、同义词、反义词
            // div #colid | div #antoid | div #synoid
            //  div.b_dictHighlight
            //  div.col_fl
            HashMap<String, String> thesauruses = new HashMap<>();
            thesauruses.put("div #colid .df_div2", "搭配");
            thesauruses.put("div #antoid .df_div2", "反义词");
            thesauruses.put("div #synoid .df_div2", "同义词");
            for(String selector : thesauruses.keySet()) {
                Elements defs = doc.select(selector);
                for(Element ele : defs) {
                    String sense = RegexUtil.colorizeSense(VocabCom.getSingleQueryResult(ele, "div.b_dictHighlight", false)) + " " +
                            thesauruses.get(selector);
                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                    String definition = VocabCom.getSingleQueryResult(ele, "div.col_fl", false);
//                    String complex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, sense, definition, Constant.AUDIO_INDICATOR_MP3);
//                    String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, sense, definition, "");
                    defMap.put(Constant.DICT_FIELD_KEYWORD, word);
                    defMap.put(Constant.DICT_FIELD_PHONETICS, yinbiao);
                    defMap.put(Constant.DICT_FIELD_SENSE, sense);
                    defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                    String html = "<b>" + word +"</b> " + (!yinbiao.equals("") ? yinbiao + "<br/>" : "") + (!sense.equals("")?sense+"<br/>":"") + definition;
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            "", audioFileName, Constant.MP3_SUFFIX
                    );
                    defList.add(new Definition(defMap, html, resInfor));
                }
            }

//            // 网络
//            String source = VocabCom.getSingleQueryResult(doc, "span.pos.web", false);
//            if(!source.equals("")) {
//                source = RegexUtil.colorizeSense(source);
//                String qDef = doc.select("span.def.b_regtxt").last().text().trim();
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, source, qDef, Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, source, qDef, "");
//                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
//                defMap.put(Constant.DICT_FIELD_WORD, word);
//                defMap.put(Constant.DICT_FIELD_PHONETICS, yinbiao);
//                defMap.put(Constant.DICT_FILED_SENSE, source);
//                defMap.put(Constant.DICT_FIELD_DEFINITION, qDef);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
//                String html = "<b>" + word +"</b> " + (!yinbiao.equals("")?yinbiao+"<br/>":"") + "<b>" + source + "<br/>" + "</b>" + qDef;
//                Definition.ResInformation resInfor = new Definition.ResInformation(
//                        "", audioFileName, Constant.MP3_SUFFIX
//                );
//                defList.add(new Definition(defMap, html, resInfor));
//            }

//            //div.df_hm_w1
//            //  p1-1 b_regtxt
//            //  sen_con b_regtxt
//            posList = doc.select("div.df_hm_w1");
//            for(Element pos : posList){
//                String sense = "";
//                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
//                    String definition = VocabCom.getSingleQueryResult(pos, "div.p1-1.b_regtxt", false) + " " +
//                            VocabCom.getSingleQueryResult(pos, "div.sen_con.b_regtxt", false);
//                    String complex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, definition, Constant.AUDIO_INDICATOR_MP3);
//                    String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, word, yinbiao, definition, "");
//                    defMap.put(Constant.DICT_FIELD_WORD, word);
//                    defMap.put(Constant.DICT_FIELD_PHONETICS, yinbiao);
//                    defMap.put(Constant.DICT_FILED_SENSE, sense);
//                    defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
//                    String html = (!yinbiao.equals("")?yinbiao+"<br/>":"") + "<b>" + "</b>" + definition;
//                    Definition.ResInformation resInfor = new Definition.ResInformation(
//                            "", audioFileName, Constant.MP3_SUFFIX
//                    );
//                    defList.add(new Definition(defMap, html, resInfor));
//            }

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

