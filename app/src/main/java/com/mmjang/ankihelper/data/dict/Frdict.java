package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ListAdapter;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.ui.intelligence.mlkit.MlKitOcr;
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

public class Frdict implements IDictionary {

    private final int lt = DictLanguageType.FRA;
    private static final String DICT_NAME = "欧路法语在线";
    private static final String DICT_INTRO = "数据来自 frdic.com/mdicts/fr/";
    private static final String[] EXP_ELE = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION
    };

    private static final String wordUrl = "https://www.frdic.com/mdicts/fr/";
    private static final String autoCompleteUrl = "https://www.esdict.cn/dicts/prefix/";

    private Context mContext;
    public int getLanguageType() {
        return lt;
    }
    public Frdict(Context context) {
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

    static int counter = 0;
    public List<Definition> wordLookup(String key) {
        try {
            String headWord = "";
            String phonetics = "";
            String esCnDef = "";
            String esEnDef = "";

            Request request = new Request.Builder().url(wordUrl + key)
                    .header("User-Agent", Constant.UA)
                    .build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);
            Elements word = doc.select("h2.word > span.word");
            if (word.size() == 1) {
                headWord = word.get(0).text().trim();
            }
            Elements phonitic = doc.select("span.Phonitic");
            if (phonitic.size() == 1) {
                phonetics = phonitic.get(0).text().trim();
            }

            ArrayList<String> defSplitted = new ArrayList<>();
            counter = 0;

            Elements cnDiv = doc.select("#FCChild>span.exp");
            final ArrayList<String> c = new ArrayList<>();
            if (cnDiv.size() > 0) {
                MlKitOcr ocr = new MlKitOcr(MyApplication.getContext());
                ocr.setLanguageText("CHINESE");
                for(Element cnDef : cnDiv){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            counter++;
                            Bitmap bitmap = new Html2Bitmap.Builder(mContext, WebViewContent.html(cnDef.html())).build().getBitmap().copy(Bitmap.Config.ARGB_8888, false);
                            ocr.getText(
                                    bitmap,
                                    s-> {
                                        defSplitted.add(s);
                                        counter--;
                                        Trace.i("time", System.currentTimeMillis() + " " + counter);
                                        return s;
                                    }
                            );
                        }
                    }).start();
                }
            }

            while(counter>0)
                Thread.sleep(300);


            Elements enDiv = doc.select("#FEChild");
            if (enDiv.size() == 1) {
                esEnDef = enDiv.get(0).html().trim();
                defSplitted.add(enDiv.get(0).html().trim());
            }

            String audioFileName = Utils.getSpecificFileName(headWord);
            ArrayList<Definition> defList = new ArrayList<>();
            for(String definition : defSplitted) {
//                String stripedDefintion = StringUtil.stripHtml(definition);
//                String[] defs  = stripedDefintion.split("(\\n\\s+){3}");
                String stripedDefintion = definition;
                String[] defs  = stripedDefintion.split("(\\s+<br\\s*/?>[0-9]+\\.?)");
                Trace.i("defs", ""+defs.length + "\n" + stripedDefintion);
                for(String def : defs) {
//                    String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, phonetics, def, Constant.AUDIO_INDICATOR_MP3);
//                    String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headWord, phonetics, def, "");
                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                    defMap.put(Constant.DICT_FIELD_KEYWORD, headWord);
                    defMap.put(Constant.DICT_FIELD_PHONETICS, phonetics);
                    defMap.put(Constant.DICT_FIELD_DEFINITION, def);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                    String exportedHtml = headWord + " " + phonetics + "<br/>" + def;
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            "", audioFileName, Constant.MP3_SUFFIX
                    );
                    Definition d = new Definition(defMap, exportedHtml, resInfor);

                    defList.add(d);
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
