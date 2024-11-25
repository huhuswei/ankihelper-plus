package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by liao on 2017/4/28.
 */

public class Getyarn implements IDictionary {

    private final int lt = DictLanguageType.ALL;
    private static final String VIDEO_TAG = "MP4";
    private static final String DICT_NAME = "台词片段视频";
    private static final String DICT_INTRO = "数据来自 Getyarn";
    private static final String[] EXP_ELE = new String[] {
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_ORIGIN,
            Constant.DICT_FIELD_SENTENCE};

    String indexHtml = "https://getyarn.io";
    String vIndexHtml = "https://y.yarn.co";
    String wordUrl = indexHtml + "/yarn-find?text=%s&p=%s";

    public int getLanguageType() {
        return lt;
    }

    public Getyarn(Context context) {
    }

    public Getyarn(){

    }
    private static String mAudioUrl = "";
    private void emptyAudioUrl() { mAudioUrl = ""; }
    public boolean isExistAudioUrl() {
        return true;
    }
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


    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public String[] getExportElementsList() {
        return EXP_ELE;
    }

    private static String mLastKey = "";
    private static int mCurrentPage = 0;
    private static int mVideoNum = 0;
    private int mVideoNumOnPage = 20;   //取模20个视频
    private static Document doc = new Document("");
    public List<Definition> wordLookup(String key) {
        try {
            if(!mLastKey.equals(key)){
                mVideoNum = 0;
                mCurrentPage = 0;
            }

            mLastKey = key;
            List<Definition> definitionList = new ArrayList<>();

            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                doc = Jsoup.connect(String.format(wordUrl, mLastKey, mCurrentPage))
                                        .userAgent("Mozilla")
                                        .timeout(5000)
                                        .execute().parse();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).run();


            Elements mp4Entrys = doc.select("div.pure-gx > div.pure-u-1-2 > div > div > a.p");
            Elements txtEntrys = doc.select("div.pure-gx > div.pure-u-1-2 > div > div > a:nth-child(2)");
            Elements titleEntrys = doc.select("div.pure-gx > div.pure-u-1-2 > div > div > a > div > div.title.ab.fw5.px05.tal");

            int startIndex = mVideoNum;
            int endIndex = startIndex + 10;

            if(mp4Entrys.size()<endIndex) {
                endIndex = mp4Entrys.size();
                mVideoNum = 0;
                mCurrentPage = 0;
            }

            for(int i=startIndex; i < endIndex; i++) {
                String audioUrl =  vIndexHtml + mp4Entrys.get(i).attr("href").replace("yarn-clip/", "") + Constant.MP4_SUFFIX;
                String line = txtEntrys.get(i).text().trim();
                String title = titleEntrys.get(i).text().trim();

                String videoFileName = Utils.getSpecificFileName(mLastKey);
                line = StringUtil.appendTagToPharse(line, mLastKey, "<b>", "</b>");
                String audioIndicator = "";
                if(!audioUrl.isEmpty()){
                    audioIndicator = "<font color='#227D51' >" + VIDEO_TAG + "</font>";
                }
//                String complex = FieldUtil.formatComplexTplSentence(title, key, line, "", Constant.VIDEO_INDICATOR_MP4);
//                String muteComplex = FieldUtil.formatComplexTplSentence(title, key, line, "", "");
//                        String.format(Constant.TPL_HTML_VIDEO_TAG, videoFileName + Constant.MP4_SUFFIX));
                LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
                eleMap.put(EXP_ELE[0], mLastKey);
                eleMap.put(EXP_ELE[1], title);
                eleMap.put(EXP_ELE[2], line);//.replaceAll("(?i)"+mLastKey, "<b>"+mLastKey+"</b>"));
//                eleMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                eleMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                eleMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                String exportedHtml = String.format("<font color = '#ba400d'>%s</font> %s<br/>%s",
                        title , audioIndicator, line);
                Definition.ResInformation resInfor = new Definition.ResInformation(
                        audioUrl, videoFileName, Constant.MP4_SUFFIX
                );
                definitionList.add(new Definition(eleMap, exportedHtml, resInfor));
            }

            if(mVideoNum + 10 < mVideoNumOnPage) {
                mVideoNum += 10;
            } else {
                mVideoNum = 0;
                mCurrentPage++;
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


    public static void main(String[] args){
        IDictionary dic = new Getyarn();
        //dic.wordLookup(UrlEscapers.urlFragmentEscaper().escape("娘"));
        dic.wordLookup("娘");
    }
}

