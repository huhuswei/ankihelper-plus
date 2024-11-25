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
 * Created by ss on 2023/5/20.
 */

public class Etymonline implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    private static final String DICT_NAME = "在线词源词典";
    private static final String DICT_INTRO = "www.etymonline.com";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_SENSE,
            Constant.DICT_FIELD_DEFINITION,
            Constant.DICT_FIELD_IMG
    };

    private static final String baseUrl = "https://www.etymonline.com";
    private static final String wordUrl = baseUrl + "/cn/word/%s#etymonline_v_18006";

    public int getLanguageType() {
        return lt;
    }

    public Etymonline(Context context){

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

    public List<Definition> wordLookup(String key) {
        try {
            emptyAudioUrl();
            List<Definition> defList = new ArrayList<>();

            if(key.trim().equals(""))
                return new ArrayList<Definition>();

            Request request = new Request.Builder().url(String.format(wordUrl, key, 1))
                    .addHeader("User-Agent", Constant.UA_FIREFOX_MACOS)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);

            Elements wordPosList = doc.select("div.mt-4.space-y-10");
            for (Element pos : wordPosList){
                String[] head = VocabCom.getSingleQueryResult(pos, "span.scroll-m-16.text-3xl.font-bold", false).trim().split("\\(|\\)");
                if (head.length != 2){
                    head = VocabCom.getSingleQueryResult(pos, "h1.scroll-m-16.text-3xl.font-bold", false).trim().split("\\(|\\)");
                }
                if (head.length != 2){
                    head = VocabCom.getSingleQueryResult(pos, "h2.scroll-m-16.text-3xl.font-bold", false).trim().split("\\(|\\)");
                }

                String word = head[0];
                String sense = "";
                if (head.length==2) {
                    sense = RegexUtil.colorizeSense(head[1]);
                }
                String def = VocabCom.getSingleQueryResult(pos, "section > section", true).replaceAll("\\n", "").replaceAll("(href=\")","$1"+baseUrl);
//                if (def.equals("")) {
//                    def = VocabCom.getSingleQueryResult(pos, "div.relative.flex.w-full.p-3.flex-auto", true).replaceAll("\\n", "").replaceAll("(href=\")","$1"+baseUrl);
//                }
                String imageUrl = "";
                Elements imageElements = pos.select("img");
                if(imageElements.size() > 0){
                    imageUrl = imageElements.get(0).attr("src");//.split("\\?")[0];
                }
                String fileName = Utils.getSpecificFileName(word);

                String audioName = fileName;
                String imageName = fileName + ".png";
                String imageTag = imageUrl.isEmpty() ? "" : String.format(Constant.TPL_HTML_IMG_TAG, imageName);
                LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                defMap.put(Constant.DICT_FIELD_KEYWORD, word);
                defMap.put(Constant.DICT_FIELD_SENSE, sense);
                defMap.put(Constant.DICT_FIELD_DEFINITION, def);
                defMap.put(Constant.DICT_FIELD_IMG, imageTag);
                String html = "<b>" + word +"</b><br/>" + (!sense.equals("")?sense+"<br/>":"") + def;
                Definition.ResInformation resInfor = new Definition.ResInformation(
                        imageUrl, imageName, "", audioName, Constant.MP3_SUFFIX
                );
                defList.add(new Definition(defMap, html, resInfor));
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

