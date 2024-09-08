package com.mmjang.ankihelper;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.data.dict.Definition;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.util.Constant;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GetYarnTest {

    private final int lt = DictLanguageType.ENG;
    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "必应图片搜索";
    private static final String DICT_INTRO = "图片保存至 collection.media/ankihelper_image/";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            "图片"
    };

    private static final String wordUrl = "https://cn.bing.com/images/search?q=";
    private static final String mp3Url = "https://audio.vocab.com/1.0/us/";

    public GetYarnTest(Context context){
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

//
//            for(Element imgEle : doc.select(".imgpt > a")){
//                String fileName = key.trim() + "_" + Utils.getRandomHexString(8) + ".png";
//                String json = imgEle.attr("m");
//                String url = null;
//                try {
//                    url = (new JSONObject(json)).getString("turl");
//                } catch (JSONException e) {
//                    continue;
//                }
//                LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
//                eleMap.put(EXP_ELE[0], key);
//                eleMap.put(EXP_ELE[1], String.format("<img src='%s' class='ankihelper_image'/>",
//                         fileName));
//                definitionList.add(new Definition(eleMap, "", url, fileName, "", ""));
//            }
            List<Definition> definitionList = new ArrayList<>();
            return definitionList;

        } catch (Exception ioe) {
            return new ArrayList<>();
        }

    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

    static String getSingleQueryResult(Document soup, String query, boolean toString){
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

    private String getMp3Url(String id){
        return "[sound:" + mp3Url + id + ".mp3]";
    }

    private String getDefHtml(Element def){
        String sense = def.toString().replaceAll("<h3.+?>","<div class='vocab_def'>").replace("</h3>","</div>").replaceAll("<a.+?>","<b>").replace("</a>","</b>");
        //String defString = def.child(1).text().trim();
        return sense;
    }

    @Test
    public void addition_isCorrect() throws Exception {
        String key  = "hello";
        String mtestString = "";
        String mLastKey = "";
        int mCurrentPage = 1;
        if(mLastKey.equals(key)){
            mCurrentPage ++;
        }else{
            mCurrentPage = 1;
        }
        mLastKey = key;

        Request request = new Request.Builder().url(String.format(wordUrl, key, mCurrentPage))
                //.addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Mobile Safari/537.36")
                .addHeader("User-Agent", Constant.UA)
                .build();

        String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
        Document doc = Jsoup.parse(rawhtml);
        Elements entrys = doc.select("tight bg-w");
        mtestString = entrys.get(0).toString();


        assertEquals(1, mtestString);
    }
}