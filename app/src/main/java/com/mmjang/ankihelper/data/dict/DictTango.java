package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.tango.OutputLocatorPOJO;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;

/**
 * Created by ss on 2022/4/28.
 */

public class DictTango implements IDictionary {

    private final int lt = DictLanguageType.ALL;
    private static final String DICT_NAME = "DictTango";
    private static final String DICT_INTRO = "";
    private static String[] EXP_ELE_LIST = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION,
            Constant.DICT_FIELD_CSS,
            Constant.DICT_FIELD_JS
//            "英文例句",
//            "例句中文",
//            "片名",
//            "图片",
//            "复合项"
    };
    private final String ELE_BODY_STRING = Constant.getTangoDefaultFields()[0];
    Context mContext;
    String mOnlineUrl;
    String matchWordUrl;
    String mResultUrl;
    String mUrl;
    List<OutputLocatorPOJO> mLocatorList;

    public int getLanguageType() {
        return lt;
    }


    private String mAudioUrl = "";

    public String getAudioUrl() {
        return mAudioUrl;
    }

    private void emptyAudioUrl() {
        mAudioUrl = "";
    }

    public boolean setAudioUrl(String audioUrl) {
        try {
            mAudioUrl = audioUrl;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getmCssUrl() {
        return mCssUrl;
    }

    public void setmCssUrl(String mCssUrl) {
        this.mCssUrl = mCssUrl;
    }

    private String mCssUrl = "";

    public String getmJsUrl() {
        return mjsUrl;
    }

    public void setmJsUrl(String mjsUrl) {
        this.mjsUrl = mjsUrl;
    }

    private String mjsUrl = "";

    public boolean isExistAudioUrl() {
        return false;
    }

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public DictTango(Context context) {
        mContext = context;
        final Settings settings = Settings.getInstance(MyApplication.getContext());
        mOnlineUrl = settings.getDictTangoOnlineUrl();
        matchWordUrl = mOnlineUrl + "/api/dict/matchWord?keyword=%s&_=%s";
        mResultUrl = mOnlineUrl + "/VIEW-DICT-CONTENT/BY-ENTRY-POSITION/%s/";
        mLocatorList = OutputLocatorPOJO.readOutputLocatorPOJOListFromSettings();

        // start
        LinkedHashSet<String> fieldSet = new LinkedHashSet<>();
        fieldSet.addAll(Arrays.asList(EXP_ELE_LIST));
        fieldSet.addAll(OutputLocatorPOJO.getFieldSet());
        fieldSet.remove(ELE_BODY_STRING);
        EXP_ELE_LIST = fieldSet.toArray(new String[0]);

        // end
    }


    public List<Definition> wordLookup(String word) {
        try {
            ArrayList<Definition> defList = new ArrayList<>();
            List<ArrayList<Definition>> defLlist = new ArrayList(Arrays.asList(new ArrayList[mLocatorList.size()]));
            word = word.toLowerCase();

            String wordUrl = String.format(matchWordUrl, URLEncoder.encode(word, "UTF-8"), System.currentTimeMillis());
            Request request = new Request.Builder()
                    .url(wordUrl)
                    .header("User-Agent", Constant.UA)
                    .build();
            byte[] by = MyApplication.getOkHttpClient().newBuilder().connectTimeout(3, TimeUnit.SECONDS).build().newCall(request)
                    .execute()
                    .body()
                    .bytes();
            String doc = new String(by);

            JSONArray jsonCPs = new JSONObject(doc)
                    .getJSONObject("result")
                    .getJSONArray("keywordList")
                    .getJSONObject(0)
                    .getJSONArray("contentPositions");

            JSONArray jsonDicts = new JSONObject(doc)
                    .getJSONObject("result")
                    .getJSONArray("dictionaryList");

            HashMap<Integer, String> dictidCssMap = new HashMap<>();
            HashMap<Integer, String> dictidJsMap = new HashMap<>();
            HashMap<Integer, String> dictidNameMap = new HashMap<>();

            //
            while (jsonDicts.length() > 0) {
                JSONObject json = (JSONObject) jsonDicts.remove(0);
//                Log.i("jsonDicts", json.toString());
                int id = json.getInt("id");

                String dictionaryName = json.getString("dictionaryName");
                String cssName = dictionaryName + ".css";
                String jsName = dictionaryName + ".js";
                dictidCssMap.put(id, cssName);
                dictidJsMap.put(id, jsName);
                dictidNameMap.put(id, dictionaryName);
            }



            while (jsonCPs.length() > 0) {
                JSONObject jsonCP = (JSONObject) jsonCPs.remove(0);

                //生成获取反馈信息的url
                JSONObject resultJson = new JSONObject();
                resultJson.put("entryKeyword", word);
                resultJson.put("dictionaryId", jsonCP.get("dictionaryId"));
                resultJson.put("contentPositions", jsonCP.get("positions"));

                Object dictionaryId = jsonCP.get("dictionaryId");

                String dictionaryName = dictidNameMap.get(dictionaryId);

                if (OutputLocatorPOJO.getOutputlocatorMap().containsKey(dictionaryName)) {
//                    Log.v("contains", dictionaryName);

                    HashMap<String, String> elementMap = new HashMap<>();
                    elementMap.put(Constant.DICT_FIELD_KEYWORD, word);

                    mUrl = String.format(mResultUrl, URLEncoder.encode(resultJson.toString(), "UTF-8"));
                    //css
                    String cssName = dictidCssMap.get(dictionaryId);
                    String cssUrlStr = mUrl + URLEncoder.encode(cssName, "UTF-8");
                    if (urlExists(cssUrlStr)) {
                        setmCssUrl(cssUrlStr);
                        elementMap.put(Constant.DICT_FIELD_CSS, "_" + cssName);

                    } else
                        setmCssUrl("");
                    //js
                    String jsName = dictidJsMap.get(dictionaryId);
                    setmJsUrl(mUrl + URLEncoder.encode(jsName, "UTF-8"));
                    String jsUrlStr = mUrl + URLEncoder.encode(jsName, "UTF-8");
                    if (urlExists(jsUrlStr)) {
                        setmJsUrl(jsUrlStr);
                        elementMap.put(Constant.DICT_FIELD_JS, "_" + jsName);
                    } else
                        setmJsUrl("");
//                    Log.i("cssurl", getmCssUrl());
//                    Log.i("jsurl", getmJsUrl());

                    OutputLocatorPOJO outputLocator = OutputLocatorPOJO.getOutputlocatorMap().get(dictionaryName);
                    if(outputLocator.isChecked()) {
                        Document document = Jsoup.connect(mUrl)
                                .userAgent("Mozilla")
                                .timeout(5000)
                                .execute().parse();

//                    defList.addAll(addDefinitionByTagOnline(document, outputLocator, elementMap));
                        for (int pos = 0; pos < mLocatorList.size(); pos++) {
                            if (mLocatorList.get(pos).getDictName().equals(outputLocator.getDictName())) {
                                defLlist.set(pos, addDefinitionByTagOnline(document, outputLocator, elementMap));
                                break;
                            }
                        }
                    }
                }
            }
//            for(ArrayList<Definition> l : defLlist) {
//                if(l != null)
//                    defList.addAll(l);
//            }
//            Log.e("def", String.valueOf(defLlist.stream().mapToInt(a->a==null ? 0 : 1).sum()));
            if(defLlist.stream().mapToInt(a->a==null ? 0 : 1).sum() > 0) {
                defList = defLlist.stream().reduce((a, b) -> {
                    if (a == null && b == null) {
                        return null;
                    }
                    if (a != null && b == null) {
                        return a;
                    } else if (a == null && b != null) {
                        return b;
                    } else {
                        a.addAll(b);
                        return a;
                    }
                }).get();
            }
            if(defList.stream().mapToInt(a->a==null ? 0 : 1).sum() == 0) {
                Definition result = YoudaoOnline.getDefinition(word, EXP_ELE_LIST);
//                if(result == null) {
//                    result = new TranslateBuilder(Settings.getInstance(mContext).getTranslatorCheckedIndex()).getDefinition(word, EXP_ELE_LIST);
//                }
                if(result != null)
                    defList.add(result);
            }
            return defList;


        } catch (InterruptedIOException iioe) {
            Looper.prepare();
            Toast.makeText(MyApplication.getContext(), "重新查询", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return new ArrayList<Definition>();
        }
        catch(JSONException je) {
            try {
                ArrayList<Definition> defList = new ArrayList<>();
                if(defList.isEmpty()){
                    Definition result = YoudaoOnline.getDefinition(word, EXP_ELE_LIST);
//                    if(result == null) {
//                        result = new TranslateBuilder(Settings.getInstance(mContext).getTranslatorCheckedIndex()).getDefinition(word, EXP_ELE_LIST);
//                    }
                    if(result != null)
                        defList.add(result);
                }
                return defList;
            } catch (Exception e) {
                Trace.e("time out", Log.getStackTraceString(e));
                return new ArrayList<Definition>();
            }
        }
        catch (Exception e) {
            Trace.d("DictTango", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }

    }

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }


    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


    private ArrayList<Definition> addDefinitionByTagOnline(Document document, OutputLocatorPOJO outputLocator, HashMap<String, String> elementMap) {
        ArrayList<Definition> defList = new ArrayList<>();
        Set<String> fieldSet = new HashSet<String>(Arrays.asList(outputLocator.getFieldArray()));

        for (Map<String, String> selector : outputLocator.getFieldsMapList()) {
            fieldSet.remove(ELE_BODY_STRING);
            String blockStr = selector.get(ELE_BODY_STRING);

            Elements blockEntrys = document.select(blockStr);

            switch (blockEntrys.size()) {
                case(0):
                    break;
                default:
                    for (Element blockEntry : blockEntrys) {
                        fieldSet.remove(Constant.DICT_FIELD_DEFINITION);
                        String contentStr = selector.get(Constant.DICT_FIELD_DEFINITION);
                        Elements contentEntrys = blockEntry.select(contentStr);

                        List<String> fields = new ArrayList<>(fieldSet);
                        for (Element content : contentEntrys) {
                            LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
                            if (!content.text().isEmpty()) {
                                eleMap.putAll(elementMap);
                                String exportedHtml = "";
                                for (String field : fields) {
                                    if (selector.containsKey(field) &&
                                            !selector.get(field).trim().isEmpty() &&
                                            !selector.get(field).trim().equals("")) {
                                        String data = blockEntry.select(selector.get(field)).outerHtml();
                                        if (field.equals(Constant.DICT_FIELD_PHONETICS)) {
                                            if (data.isEmpty())
                                                data = document.select(selector.get(field)).outerHtml();
                                            data = data.replaceAll("\\n", " ");
                                        }
                                        eleMap.put(field, data.
                                                replaceAll("href=\"/", String.format("href=\"%s/", mOnlineUrl)));
                                        exportedHtml += eleMap.get(field) + "<br/>";
                                    }
                                }
                                eleMap.put(Constant.DICT_FIELD_DEFINITION, content.outerHtml()
                                        .replaceAll("href=\"/", String.format("href=\"%s/", mOnlineUrl))
                                        .replaceAll("src=\"", String.format("src=\"%s", mUrl))
                                        .replaceAll("_dtPlayAudio\\('", String.format("_dtPlayAudio\\('%s", mUrl)));

                                exportedHtml += Utils.stripJSAndStyle(eleMap.get(Constant.DICT_FIELD_DEFINITION));

                                String audioFileName = Utils.getSpecificFileName(eleMap.get(Constant.DICT_FIELD_KEYWORD));
//                                String complex = FieldUtil.formatComplexTplWord(
//                                        DICT_NAME,
//                                        eleMap.get(Constant.DICT_FIELD_KEYWORD),
//                                        eleMap.get(Constant.DICT_FIELD_PHONETICS),
//                                        eleMap.get(Constant.DICT_FIELD_DEFINITION),
//                                        Constant.AUDIO_INDICATOR_MP3);
//                                String muteComplex = FieldUtil.formatComplexTplWord(
//                                        DICT_NAME,
//                                        eleMap.get(Constant.DICT_FIELD_KEYWORD),
//                                        eleMap.get(Constant.DICT_FIELD_PHONETICS),
//                                        eleMap.get(Constant.DICT_FIELD_DEFINITION),
//                                        "");
//                                eleMap.put(
//                                        Constant.DICT_FIELD_COMPLEX_ONLINE,
//                                        complex);
//                                eleMap.put(
//                                        Constant.DICT_FIELD_COMPLEX_OFFLINE,
//                                        complex);
//                                eleMap.put(
//                                        Constant.DICT_FIELD_COMPLEX_MUTE,
//                                        muteComplex);
                                Definition.ResInformation resInfor = new Definition.ResInformation(
                                        "", "",
                                        "", audioFileName, Constant.MP3_SUFFIX, "",
                                        getmCssUrl(), elementMap.get(Constant.DICT_FIELD_CSS),
                                        getmJsUrl(), elementMap.get(Constant.DICT_FIELD_JS)
                                );
                                defList.add(new Definition(eleMap, exportedHtml, resInfor));
                            }
                        }
                    }
            }

        }
        return defList;
    }

    public boolean urlExists(String urlStr) {
        try {
            URL urlTest = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) urlTest.openConnection();
            int code = urlConn.getContentLength();
            urlConn.disconnect();
//        Log.e("Content-Length", String.valueOf(code));
            if(code != 0)
                return true;
            else
                return false;
        } catch (IOException ioe) {
            return false;
        }
    }
}

