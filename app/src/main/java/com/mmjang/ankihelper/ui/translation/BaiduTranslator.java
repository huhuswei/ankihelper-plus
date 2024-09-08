package com.mmjang.ankihelper.ui.translation;

import android.util.Log;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.com.baidu.translate.demo.RandomAPIKeyGenerator;
import com.mmjang.ankihelper.util.com.baidu.translate.demo.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaiduTranslator implements Translator{
    private static Translator translator;
    private static final String APP_ID = "20160220000012831";
    private static final String SECURITY_KEY = "ISSPx0K_ZyrUN9IAOKel";
    private static TransApi api;

    public static Translator getInstance() {
        if(translator == null)
            translator = new BaiduTranslator();
        return  translator;
    }

    @Override
    public String translate(String query) {
        try {
            String result;
            if (RegexUtil.isChineseSentence(query)) {
                return translator.translate(query, "zh", "en");
            } else {
                return translator.translate(query, "auto", "zh");
            }
        } catch (Exception e) {
            Log.e("translate", "failed");
            return "";
        }
    }

    @Override
    public String translate(String query, String from, String to){
        //remove line break
        //query = query.replaceAll("\n","");
       // if(api == null) {
            Settings settings = Settings.getInstance(MyApplication.getContext());
            if(settings.getUserBaidufanyiAppId().isEmpty()) {
                String[] appAndKey = RandomAPIKeyGenerator.next();
                api = new TransApi(appAndKey[0], appAndKey[1]);
            }else{
                String id = settings.getUserBaidufanyiAppId();
                String key = settings.getUserBaidufanyiAppKey();
                api = new TransApi(id, key);
        //   }
        }
        String jsonStr = "";
        try {
            jsonStr = api.getTransResult(query, from , to);
            JSONObject json = new JSONObject(jsonStr);
            JSONArray resultArray = json.getJSONArray("trans_result");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < resultArray.length() - 1; i++) {
                sb.append(resultArray.getJSONObject(i).getString("dst"));
                sb.append("\n");
            }
            if (resultArray.length() > 0) {
                sb.append(resultArray.getJSONObject(resultArray.length() - 1).getString("dst"));
            }
            Trace.i("resultjson", sb.toString());
            return sb.toString();
        } catch (JSONException e) {
            //Toast.makeText(MyApplication.getContext(), e.getMessage() + jsonStr, Toast.LENGTH_LONG).show();
            return "error\n" + e.getMessage() + "\n" + jsonStr;
        } catch (NullPointerException npe) {
            return "";
        }
    }

    @Override
    public String name() {
        return "百度";
    }

    @Override
    public String getZh() {
        return "zh";
    }

    @Override
    public String getAuto() {
        return "auto";
    }

    @Override
    public String getEn() {
        return "en";
    }

    @Override
    public String getRus() {
        return "ru";
    }

    @Override
    public String getFra() {
        return "fra";
    }

    @Override
    public String getDeu() {
        return "de";
    }

    @Override
    public String getSpa() {
        return "spa";
    }

    @Override
    public String getJpn() {
        return "jp";
    }

    @Override
    public String getKor() {
        return "kor";
    }

    @Override
    public String getTha() {
        return "th";
    }


    public static void main(String[] args) {
//        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
//        String query = "高度600米";
//        System.out.println(api.getTransResult(query, "auto", "cn"));
        System.out.println(translator.translate("i am a big fat guy", "auto", "zh"));
    }
}
