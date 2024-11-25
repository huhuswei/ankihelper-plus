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
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class Mnemonic implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    //private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "Mnemonic助记词典";
    private static final String DICT_INTRO = "全英文助记，慎入。";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_SENSE,
            Constant.DICT_FIELD_DEFINITION
    };
    private static final String wordUrl = "http://www.mnemonicdictionary.com/?word=%s";
    //private static final String mp3Url = "https://audio.vocab.com/1.0/us/";

    private Context mContext;
    public int getLanguageType() {
        return lt;
    }

    public Mnemonic(Context context){
        this.mContext = context;
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
//            Document doc = Jsoup.connect(wordUrl + key)
//                    .userAgent("Mozilla")
//                    .timeout(5000)
//                    .get();
//            List<Definition> definitionList = new ArrayList<>();
            List<Definition> definitionList = new ArrayList<>();
            if(key.trim().equals(""))
                return new ArrayList<Definition>();

            Request request = new Request.Builder().url(String.format(wordUrl, key, 1))
                    .addHeader("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);

            for(Element memo : doc.select(".media-body>div:nth-child(2)")){
//                String audioIndicator = "<font color='#227D51' >" + AUDIO_TAG + "</font>";
//                String complex = FieldUtil.formatComplexTplWord(DICT_NAME, key, "", memo.text(), Constant.AUDIO_INDICATOR_MP3);
//                String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, key, "", memo.text(), "");

                String[] sections = memo.toString().split("<u>Definition</u>\\s*?<br>");
                for(String sect : sections) {
                    if (StringUtil.stripHtml(sect).trim().equals(""))
                        continue;

                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                    String sense = RegexUtil.colorizeSense(sect.replaceFirst("\\((\\w+)\\)([\\w\\W]+)", "$1"));
                    String def = sect.replaceFirst("\\((\\w+)\\)([\\w\\W]+)", "$2").replaceAll("\\n", "");
                    defMap.put(Constant.DICT_FIELD_KEYWORD, key);
                    defMap.put(Constant.DICT_FIELD_SENSE, sense);
                    defMap.put(Constant.DICT_FIELD_DEFINITION, def);
                    String audioFileName = Utils.getSpecificFileName(key);
                    String html = "<b>" + key +"</b><br/>" + (!sense.equals("")?sense+"<br/>":"") + def;
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            "", audioFileName, Constant.MP3_SUFFIX
                    );
                    definitionList.add(new Definition(defMap, html, resInfor));
                }
            }
            return definitionList;

        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

    private static String getSingleQueryResult(Document soup, String query, boolean toString){
        Elements re = soup.select(query);
        if(!re.isEmpty()){
            if(toString){
                return re.get(0).toString();
            }else {
                return re.get(0).text();
            }
        }else{
            return "";
        }
    }

    private static String getSingleQueryResult(Element soup, String query, boolean toString){
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

