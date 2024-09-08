package com.mmjang.ankihelper;

import org.junit.Test;

import static org.junit.Assert.*;

import android.content.Context;
import android.widget.ListAdapter;

import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String indexHtml = "https://getyarn.io";
        String wordUrl = indexHtml + "/yarn-find?text=%s&p=%s";
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

        Document doc = Jsoup.connect(String.format(wordUrl, key, "1"))
                .userAgent("Mozilla")
                .timeout(5000)
                .get();

        Elements entrys = doc.select("div.pure-gx > div.pure-u-sm-1-2 > div > div > a.p");
        String link = indexHtml + entrys.get(0).attr("href");


        Document doc2 = Jsoup.connect(link)
                .userAgent("Mozilla")
                .timeout(5000)
                .get();

        Elements vele = doc2.select("video > source");
        String videoUrl = vele.get(0).attr("src");

        Elements txtele = doc2.select("div.w100.tac.dtc > div");
        String txt = txtele.get(0).text();
        assertEquals(0, link);


        String videoTple = "<video id=\"video\" width=\"100%\" src=\"%s\" controlList=\"nodownload\" controls></video>";
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

    private String getAudioUrlTag(String audioUrl){
        return "[sound:" + audioUrl + "]";
    }

//    private String getAudioUrl(String id) {
//        return mp3Url + id + ".mp3";
//    }

    private String getAudioTag(String audioFile) {
        return "[sound:" +  audioFile + "]";
    }

    private String getAudioFile(String headWord) {
        return headWord + "_" + "vocab" + "_" + Utils.getRandomHexString(8) + Constant.MP3_SUFFIX;
    }
    private String getDefHtml(Element def){
        String sense = def.toString().replaceAll("<h3.+?>","<div class='vocab_def'>").replace("</h3>","</div>").replaceAll("<a.+?>","<b>").replace("</a>","</b>");
        //String defString = def.child(1).text().trim();
        return sense;
    }
}