package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by liao on 2017/4/28.
 */

public class Kuromoji implements IDictionary {

    private final int lt = DictLanguageType.JPN;
    private static final String DICT_NAME = "Kuromoji Japanese";
    private static final String DICT_INTRO = "数据来自 Kuromoji，一个日语分词器. 分解后的词是可以自动将汉字，平假名等转换成カタカナ";
    private static final String[] EXP_ELE = new String[] {
            "单词",
            "词性标注",
            "注音"
    };
//    private static final String[] EXP_ELE = new String[] {"Base form", "Part-of-Speech", "Reading"};
//
    public int getLanguageType() {
        return lt;
    }

    public Kuromoji(Context context) {

    }

    public Kuromoji(){

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

    public List<Definition> wordLookup(String word) {

        try {
            ArrayList<Definition> defList = new ArrayList<>();
            if (StringUtil.isJapanese(word)) {

                String baseForm = "";
                String partOfSpeech = "";
                String reading = "";

                Tokenizer tokenizer = new Tokenizer();
                List<Token> tokens = tokenizer.tokenize(word);
                for(Token token : tokens) {
                    baseForm = token.getBaseForm();
                    partOfSpeech = token.getPartOfSpeechLevel1();
                    reading = token.getReading();
                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
//                    String complex = FieldUtil.formatComplexTplWord(DICT_NAME, baseForm, reading, partOfSpeech, Constant.AUDIO_INDICATOR_MP3);
//                    String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, baseForm, reading, partOfSpeech, "");
                    defMap.put(EXP_ELE[0], baseForm);
                    defMap.put(EXP_ELE[1], partOfSpeech);
                    defMap.put(EXP_ELE[2], reading);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
                    String export_html = "<b>" + baseForm + " </b>" +
                            "<font color = '#ba400d'>" + partOfSpeech + "</font>" + "<br />" + reading;
                    String audioFileName = Utils.getSpecificFileName(baseForm);
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            "", audioFileName, Constant.MP3_SUFFIX
                    );
                    defList.add(new Definition(defMap, export_html, resInfor));
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
        IDictionary dic = new Kuromoji();
        //dic.wordLookup(UrlEscapers.urlFragmentEscaper().escape("娘"));
        dic.wordLookup("娘");
    }
}

