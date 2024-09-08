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
 * Created by liao on 2017/4/28.
 */

public class MdictBackup implements IDictionary {

    private final int lt = DictLanguageType.ALL;
    private static final String DICT_NAME = "Mdict";
    private static final String DICT_INTRO = "";
    private String[] EXP_ELE_LIST = new String[]{
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
    private Map<String, String[][]> selectorsBydictNameMap;
    private HashMap<String, OutputLocatorPOJO> outputLocatorMap;
    Context mContext;
    String mOnlineUrl;
    String matchWordUrl;
    String mResultUrl;

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

    public MdictBackup(Context context) {
        mContext = context;
        final Settings settings = Settings.getInstance(context);
        mOnlineUrl = settings.getDictTangoOnlineUrl();
        matchWordUrl = mOnlineUrl + "/api/dict/matchWord?keyword=%s&_=%s";
        mResultUrl = mOnlineUrl + "/VIEW-DICT-CONTENT/BY-ENTRY-POSITION/%s/";

        // start
        outputLocatorMap = new HashMap<>();
        HashSet<String> fieldSet = new HashSet<>();
        OutputLocatorPOJO locator;

        fieldSet.addAll(Arrays.asList(EXP_ELE_LIST));

        String dictName = new String("牛津高阶第10版英汉双解V5_0");
        ArrayList<Map<String, String>> fieldSelectorsList = new ArrayList<>();
        String[] fields = Constant.TANGO_DEFAULT_FIELDS;
        locator = new OutputLocatorPOJO(dictName, 3, true, fields);

        HashMap<String, String> fieldSelectorMap = new HashMap<>();
        fieldSet.addAll(Arrays.asList(fields));
        fieldSelectorMap.put(fields[0], "div#entryContent");
        fieldSelectorMap.put(fields[1], "div span.pos");
        fieldSelectorMap.put(fields[2], "div.phons_br > span");
        fieldSelectorMap.put(fields[3], "div li.sense");
        fieldSelectorsList.add(fieldSelectorMap);
        locator.setFieldsMapStrList(0, fieldSelectorMap);
        locator.save();
        outputLocatorMap.put(dictName, locator);


        dictName = new String("21世纪大英汉词典");
        fieldSelectorsList = new ArrayList<>();
        fields = Constant.TANGO_DEFAULT_FIELDS;
        locator = new OutputLocatorPOJO(dictName, 3, true, fields);

        fieldSet.addAll(Arrays.asList(fields));

        fieldSelectorMap = new HashMap<>();
        fieldSelectorMap.put(fields[0], "#authDictTrans.trans-container>ul>li");
        fieldSelectorMap.put(fields[1], ".pos.wordGroup");
        fieldSelectorMap.put(fields[2], "#authDictTrans.trans-container .phonetic.wordGroup");
        fieldSelectorMap.put(fields[3], ".ol.wordGroup .wordGroup");
        fieldSelectorsList.add(fieldSelectorMap);
        locator.setFieldsMapStrList(0, fieldSelectorMap);
        locator.save();

        fieldSelectorMap = new HashMap<>();
        fieldSelectorMap.put(fields[0], "#authDictTrans.trans-container>ul>li");
        fieldSelectorMap.put(fields[1], ".pos.wordGroup");
        fieldSelectorMap.put(fields[2], "#authDictTrans.trans-container .phonetic.wordGroup");
        fieldSelectorMap.put(fields[3], ".def.wordGroup");
        fieldSelectorsList.add(fieldSelectorMap);
        locator.setFieldsMapStrList(1, fieldSelectorMap);
        locator.save();
        outputLocatorMap.put(dictName, locator);


        dictName = new String("Collins COBUILD Advanced English Dictionary Online");
        fieldSelectorsList = new ArrayList<>();
        fields = Constant.TANGO_DEFAULT_FIELDS;
        locator = new OutputLocatorPOJO(dictName, 3, true, fields);
        fieldSet.addAll(Arrays.asList(fields));

        fieldSelectorMap = new HashMap<>();
        fieldSelectorMap.put(fields[0], "div#collins_english_dictionary > div > div > div > div > div > div");
        fieldSelectorMap.put(fields[1], " ");
        fieldSelectorMap.put(fields[2], "span.mini_h2");
        fieldSelectorMap.put(fields[3], "div.hom");
        fieldSelectorsList.add(fieldSelectorMap);
        locator.setFieldsMapStrList(0, fieldSelectorMap);
        locator.save();
        outputLocatorMap.put(dictName, locator);


        dictName = new String("漢語大詞典2.0");
        fieldSelectorsList = new ArrayList<>();
        fields = Constant.TANGO_DEFAULT_FIELDS;
        locator = new OutputLocatorPOJO(dictName, 0, true, fields);
        fieldSet.addAll(Arrays.asList(fields));

        fieldSelectorMap = new HashMap<>();
        fieldSelectorMap.put(fields[0], "body > zi > zmlb");
        fieldSelectorMap.put(fields[1], " ");
        fieldSelectorMap.put(fields[2], "zmlb > yd");
        fieldSelectorMap.put(fields[3], "zmlb > zmsy > sylb");
        fieldSelectorsList.add(fieldSelectorMap);
        locator.setFieldsMapStrList(0, fieldSelectorMap);
        locator.save();

        fieldSelectorMap = new HashMap<>();
        fieldSelectorMap.put(fields[0], "body > zi > ci > cmlb");
        fieldSelectorMap.put(fields[1], " ");
        fieldSelectorMap.put(fields[2], "cmlb > cm > cty");
        fieldSelectorMap.put(fields[3], "cmlb > cm > cmsy > sylb");
        fieldSelectorsList.add(fieldSelectorMap);
        locator.setFieldsMapStrList(1, fieldSelectorMap);
        locator.save();

        fieldSelectorMap = new HashMap<>();
        fieldSelectorMap.put(fields[0], "body > zi > ci > cmlb");
        fieldSelectorMap.put(fields[1], " ");
        fieldSelectorMap.put(fields[2], "cmlb > cm > cty");
        fieldSelectorMap.put(fields[3], "body > p");
        fieldSelectorsList.add(fieldSelectorMap);
        locator.setFieldsMapStrList(2, fieldSelectorMap);
        locator.save();

        Log.i("fieldSelectorsList", fieldSelectorsList.toString());
        outputLocatorMap.put(dictName, locator);


        fieldSet.remove("body");
        EXP_ELE_LIST = fieldSet.toArray(new String[0]);

        LinkedHashSet<String> fieldsSet = new LinkedHashSet<>();
        fieldsSet.addAll(Arrays.asList(EXP_ELE_LIST));
        fieldsSet.addAll(OutputLocatorPOJO.getFieldSet());
        EXP_ELE_LIST = fieldsSet.toArray(new String[0]);

        // end
    }


    public List<Definition> wordLookup(String word) {

        try {
            ArrayList<Definition> defList = new ArrayList<>();
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
//            Log.e("jsonDicts len", String.valueOf(jsonDicts.length()));

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
//                Log.e("@jsonCP", jsonCP.toString());

                //生成获取反馈信息的url
                JSONObject resultJson = new JSONObject();
                resultJson.put("entryKeyword", word);
                resultJson.put("dictionaryId", jsonCP.get("dictionaryId"));
                resultJson.put("contentPositions", jsonCP.get("positions"));

                Object dictionaryId = jsonCP.get("dictionaryId");

                String dictionaryName = dictidNameMap.get(dictionaryId);
//                Log.e("dictionaryName", dictionaryName);

                if (outputLocatorMap.containsKey(dictionaryName)) {
//                    Log.v("contains", dictionaryName);

                    HashMap<String, String> elementMap = new HashMap<>();
                    elementMap.put(EXP_ELE_LIST[0], word);

                    String url = String.format(mResultUrl, URLEncoder.encode(resultJson.toString(), "UTF-8"));
                    //css
                    String cssName = dictidCssMap.get(dictionaryId);
                    String cssUrlStr = url + URLEncoder.encode(cssName, "UTF-8");
                    if(urlExists(cssUrlStr)) {
                        setmCssUrl(cssUrlStr);
                        elementMap.put("css", "_" + cssName);

                    }
                    else
                        setmCssUrl("");
                    //js
                    String jsName = dictidJsMap.get(dictionaryId);
                    setmJsUrl(url + URLEncoder.encode(jsName, "UTF-8"));
                    String jsUrlStr = url + URLEncoder.encode(jsName, "UTF-8");
                    if(urlExists(jsUrlStr)) {
                        setmJsUrl(jsUrlStr);
                        elementMap.put("js", "_" + jsName);
                    }
                    else
                        setmJsUrl("");
//                    Log.i("cssurl", getmCssUrl());
//                    Log.i("jsurl", getmJsUrl());

                    OutputLocatorPOJO outputLocator = outputLocatorMap.get(dictionaryName);

                    Document document = Jsoup.connect(url)
                            .userAgent("Mozilla")
                            .timeout(5000)
                            .execute().parse();

                    defList.addAll(addDefinitionByTagOnline(document, outputLocator, elementMap));
                }
            }

            return defList;

        } catch (InterruptedIOException iioe) {
            Looper.prepare();
            Toast.makeText(MyApplication.getContext(), "重新查询", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return new ArrayList<Definition>();
        } catch(JSONException je) {
            try {
                ArrayList<Definition> defList = new ArrayList<>();
                defList.add(YoudaoOnline.getDefinition(word, EXP_ELE_LIST));
                return defList;
            } catch (IOException ee) {
                Looper.prepare();
                Toast.makeText(mContext, "本地词典未查到，有道词典在线查询失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                Looper.loop();
                return new ArrayList<Definition>();
            }
        }
        catch (Exception e) {
            Log.d("dictTango", Log.getStackTraceString(e));
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

            for (Element blockEntry : blockEntrys) {
                fieldSet.remove("释义");
                String contentStr = selector.get("释义");
                Elements contentEntrys = blockEntry.select(contentStr);

                List<String> fields = new ArrayList<>(fieldSet);
                for (Element content : contentEntrys) {
                    LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
                    if (!content.text().isEmpty()) {
                        eleMap.putAll(elementMap);
                        String exportedHtml = this.getDictionaryName() + "用于生成配置\n";
                        for (String field : fields) {
                            if (selector.containsKey(field) &&
                                    !selector.get(field).trim().isEmpty() &&
                                    !selector.get(field).trim().equals("")) {
                                String data = blockEntry.select(selector.get(field)).outerHtml();
                                if (field.equals(Constant.DICT_FIELD_PHONETICS)) {
                                    if(data.isEmpty())
                                        data = document.select(selector.get(field)).outerHtml();
                                    data = data.replaceAll("\\n", " ");
                                }

                                eleMap.put(field, data.replaceAll("href=\"/", String.format("href=\"%s/", mOnlineUrl)));
                                exportedHtml += eleMap.get(field) + "<br/>";
                            }
                        }
                        eleMap.put("释义", content.outerHtml()
                                .replaceAll("href=\"/", String.format("href=\"%s/", mOnlineUrl)));
                        exportedHtml += eleMap.get("释义");

                        String audioFileName = Utils.getSpecificFileName(eleMap.get(Constant.DICT_FIELD_KEYWORD));
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
        return defList;
    }

    public boolean urlExists(String urlStr) {
        try {
        URL urlTest = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) urlTest.openConnection();
        int code = urlConn.getContentLength();
        urlConn.disconnect();
        Log.e("Content-Length", String.valueOf(code));
        if(code != 0)
            return true;
        else
            return false;
        } catch (IOException ioe) {
            return false;
        }
    }
}

