package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
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

public class RenRenCiDianSentence implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    private static final String DICT_NAME = "人人词典原声例句";
    private static final String DICT_INTRO = "";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_CHINESE_SENTENCE,
            Constant.DICT_FIELD_SENTENCE,
            Constant.DICT_FIELD_IMG,
    };

    private static final String wordUrl = "http://www.91dict.com/words?w=";
    private static final String tplt_ui = "" +
            "<span>\uD83D\uDD0A%s</span>" +
            "<span>%s</span>" +
            "<span>%s</span>" +
            "";


    Context mContext;
    AnkiDroidHelper mAnkiDroid;

    public int getLanguageType() {
        return lt;
    }

    public RenRenCiDianSentence(Context context){
        mContext = context;
        mAnkiDroid = MyApplication.getAnkiDroid();
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
            emptyAudioUrl();
            Request request = new Request.Builder().url(wordUrl + key)
                    .addHeader("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);
            List<Definition> definitionList = new ArrayList<>();

            for(Element audioEle : doc.select("ul.slides > li")){
                LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
                String audioUrl = "";
                Elements audioElements = audioEle.select("audio");
                if(audioElements.size() > 0){
                    audioUrl = audioElements.get(0).attr("src");
                }

                String imageUrl = "";
                Elements imageElements = audioEle.select("img");
                if(imageElements.size() > 0){
                    imageUrl = imageElements.get(0).attr("src");
                }


                String fileName = Utils.getSpecificFileName(key);

                String audioName = fileName;
                String imageName = fileName + ".png";
                String imageTag = String.format(Constant.TPL_HTML_IMG_TAG, imageName);

                String channel = getSingleQueryResult(audioEle, "div.mTop", false).trim();
                String cn = getSingleQueryResult(audioEle, "div.mFoot", true)
                        .replaceAll("<em>", "<b>")
                        .replaceAll("</em>", "</b>")
                        .replaceAll(" class=\".+\"", "")
                        .replaceAll("<div>", "")
                        .replaceAll("</div>", "")
                        .replaceAll("\"", "\\\\\"")
                        .replaceAll("\\s", " ")
                        .trim();
                String en = getSingleQueryResult(audioEle, "div.mBottom", true)
                        .replaceAll("<em>", "<b>")
                        .replaceAll("</em>", "</b>")
                        .replaceAll(" class=\".+\"", "")
                        .replaceAll("<div>", "")
                        .replaceAll("</div>", "")
                        .replaceAll("\"", "\\\\\"")
                        .replaceAll("\\s", " ")
                        .trim();

                String html_ui = String.format(tplt_ui,
                        en,
                        cn,
                        "<font color=grey>" + channel + "</font>"
                );

                eleMap.put(EXP_ELE[0], key);
                eleMap.put(EXP_ELE[1], cn);
                eleMap.put(EXP_ELE[2], en);
                eleMap.put(EXP_ELE[3], imageTag);

                Definition.ResInformation resInfor = new Definition.ResInformation(
                        imageUrl, imageName, audioUrl, audioName, Constant.MP3_SUFFIX
                );
                definitionList.add(new Definition(eleMap, html_ui, resInfor));
            }

            return definitionList;
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<>();
        }
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
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
}

