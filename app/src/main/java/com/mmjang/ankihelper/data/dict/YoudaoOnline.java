package com.mmjang.ankihelper.data.dict;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.translation.TranslateBuilder;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by liao on 2017/7/30.
 */

public class YoudaoOnline {
    private static final String BASE_URL = "http://dict.youdao.com/fsearch?client=deskdict&keyfrom=chrome.extension&pos=-1&doctype=xml&dogVersion=1.0&vendor=unknown&appVer=3.1.17.4208&le=eng&q=%s";
    static public YoudaoResult getYoudaoResult(String key) throws IOException{
//            Document doc = Jsoup.connect(String.format(BASE_URL, key.trim()))
//                    .userAgent("Mozilla")
//                    .cookie("auth", "token")
//                    .timeout(2000)
//                    .parser(Parser.xmlParser())
//                    .get();
            //doc.toString();
            Request request = new Request.Builder().url(String.format(BASE_URL, key.trim())).build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml, "", Parser.xmlParser());
            String phonetic = getSingleQueryResult(doc, "phonetic-symbol");
            String returnPhrase = getSingleQueryResult(doc, "return-phrase");
            List<String> translation = new ArrayList<String>();
            for(Element e : doc.select("translation > content")){
                translation.add(e.text());
            }

            Map<String, List<String>> webTranslation = new LinkedHashMap<>();
            for(Element web : doc.select("web-translation")){
                String keyString = getSingleQueryResult(web, "key");
                List<String> values = new ArrayList<>();
                for(Element value : web.select("trans > value")){
                    String valueString = value.text().trim();
                    values.add(valueString);
                }
                webTranslation.put(keyString, values);
            }
        YoudaoResult youdaoResult = new YoudaoResult();
        youdaoResult.phonetic = phonetic;
        youdaoResult.returnPhrase = returnPhrase;
        youdaoResult.translation = translation;
        youdaoResult.webTranslation = webTranslation;
        return  youdaoResult;
    }

    private static String getSingleQueryResult(Document soup, String query){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            return re.get(0).text();
        }else{
            return "";
        }
    }

    private static String getSingleQueryResult(Element soup, String query){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            return re.get(0).text();
        }else{
            return "";
        }
    }

//    //作废
//    public static Definition getDefinitionFromYoudao(String word, String[] EXP_ELE_LIST) throws IOException {
//        YoudaoResult youdaoResult = getYoudaoResult(word);
//        String notiString = "<font color='gray'>本地词典未查到，以下是有道在线释义</font><br/>";
//        String definition = "<b>" + youdaoResult.returnPhrase + "</b><br/>";
//        for (String def : youdaoResult.translation) {
//            definition += def + "<br/>";
//        }
//
//        definition += "<font color='gray'>网络释义</font><br/>";
//        for (String key : youdaoResult.webTranslation.keySet()) {
//            String joined = "";
//            for (String value : youdaoResult.webTranslation.get(key)) {
//                joined += value + "; ";
//            }
//            definition += "<b>" + key + "</b>: " + joined + "<br/>";
//        }
//
//        LinkedHashMap<String, String> exp = new LinkedHashMap<>();
//
//        exp.put(EXP_ELE_LIST[0], youdaoResult.returnPhrase);
//        exp.put(EXP_ELE_LIST[1], youdaoResult.phonetic);
//        exp.put(EXP_ELE_LIST[2], definition);
//
//        String audioFileName = Utils.getSpecificFileName(word);
//        Definition.ResInformation resInfor = new Definition.ResInformation(
//                "", audioFileName, Constant.MP3_SUFFIX
//        );
//        return new Definition(exp, notiString + definition, resInfor);
//    }

    public static Definition getDefinition(String word, String[] DICT_EXP_ELE_LIST) throws IOException {
        YoudaoResult youdaoResult = getYoudaoResult(word);
//        final String[] EXP_ELE_LIST = new String[]{
//                Constant.DICT_FIELD_KEYWORD,
//                Constant.DICT_FIELD_PHONETICS,
//                Constant.DICT_FIELD_DEFINITION
////                "复合项"
//        };

        String notiString = "<font color='gray'>本地词典未查到，以下是有道在线释义或翻译</font><br/>";
        String definition = "<b>" + youdaoResult.returnPhrase + "</b><br/>";

        if(!youdaoResult.translation.isEmpty())
            for (String def : youdaoResult.translation) {
                definition += def + "<br/>";
            }
        else
            definition += new TranslateBuilder(
                    Settings.getInstance(MyApplication.getContext()).
                            getTranslatorCheckedIndex()).translate(youdaoResult.returnPhrase) + "<br/>";

        definition += "<font color='gray'>网络释义</font><br/>";
        for (String key : youdaoResult.webTranslation.keySet()) {
            String joined = "";
            for (String value : youdaoResult.webTranslation.get(key)) {
                joined += value + "; ";
            }
            definition += "<b>" + key + "</b>: " + joined + "<br/>";
        }

        LinkedHashMap<String, String> exp = new LinkedHashMap<>();
        exp.put(DICT_EXP_ELE_LIST[0], youdaoResult.returnPhrase);
        exp.put(DICT_EXP_ELE_LIST[1], youdaoResult.phonetic);
        exp.put(DICT_EXP_ELE_LIST[2], definition);
//            exp.put(DICT_EXP_ELE_LIST[3], getCombined(exp));
        String audioFileName = Utils.getSpecificFileName(youdaoResult.returnPhrase);
        Definition.ResInformation resInfor = new Definition.ResInformation(
                "", audioFileName, Constant.MP3_SUFFIX
        );
        return new Definition(exp, notiString + definition, resInfor);
    }

    public static String[] getDefinition(String word) throws IOException {
        YoudaoResult youdaoResult = getYoudaoResult(word);

        String definition = "<b>" + youdaoResult.returnPhrase + "</b><br/>";

        if(!youdaoResult.translation.isEmpty())
            for (String def : youdaoResult.translation) {
                definition += def + "<br/>";
            }
        else
            definition += new TranslateBuilder(
                    Settings.getInstance(MyApplication.getContext()).
                            getTranslatorCheckedIndex()).translate(youdaoResult.returnPhrase) + "<br/>";

        definition += "<font color='gray'><i>网络释义</i></font><br/>";
        for (String key : youdaoResult.webTranslation.keySet()) {
            String joined = "";
            for (String value : youdaoResult.webTranslation.get(key)) {
                joined += value + "; ";
            }
            definition += "<b>" + key + "</b>: " + joined + "<br/>";
        }

        return new String[] {word, definition};
    }
}
