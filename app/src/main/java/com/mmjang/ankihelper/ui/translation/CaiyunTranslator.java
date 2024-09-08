package com.mmjang.ankihelper.ui.translation;

import android.util.Log;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.Trace;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: MicrosoftTranslator
 * @Description: java类作用描述
 * @Author: 唐朝
 * @CreateDate: 2022/8/9 1:22 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/8/9 1:22 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class CaiyunTranslator implements Translator {

    private static Translator translator;

    //2.0
    private final String USER_CAIYUN_APP_SECRET_KEY = "";
    private static final String BASE_URL = "http://api.interpreter.caiyunai.com/v1/translator";

    //3.0
//    private static final String BASE_URL = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=%s&to=%s";
//    private String key, mUrl, content;

    public static Translator getInstance() {
        if(translator == null)
            translator = new CaiyunTranslator();
        return  translator;
    }

    @Override
    public String translate(String query) {
        try {

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

    /*
     * 2.0
     */
    @Override
    public String translate(String query, String from, String to) {
        String result;
        Settings settings = Settings.getInstance(MyApplication.getContext());
        String key;
        if (settings.getUserCaiyunAppSecretKey().isEmpty()) {
            key = USER_CAIYUN_APP_SECRET_KEY;
        } else {
            key = settings.getUserCaiyunAppSecretKey();
        }
        try {
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("source", query);
            bodyJson.put("trans_type", String.format("%s2%s", from, to));
            bodyJson.put("request_id", "demo");
            bodyJson.put("detect",  true);

            String wordUrl = BASE_URL;
            MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
            Trace.i("bodyJson", bodyJson.toString());
            RequestBody body = RequestBody.create(JSON, bodyJson.toString());
            Request request = new Request.Builder()
                    .url(wordUrl)
                    .header("x-authorization", String.format("token %s", key))
                    .post(body)
                    .build();
            String resultStr = MyApplication.getOkHttpClient().newBuilder().connectTimeout(3, TimeUnit.SECONDS).build().newCall(request)
                    .execute()
                    .body()
                    .string();
            JSONObject resultJson = new JSONObject(resultStr);
            Trace.i("resultjson", resultJson.getString("target"));
            return resultJson.getString("target");

        } catch (Exception e) {
            translator = null;
            return "";
        }
    }

    @Override
    public String name() {
        return "彩云小译";
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
        return "fr";
    }

    @Override
    public String getDeu() {
        return "de";
    }

    @Override
    public String getSpa() {
        return "es";
    }

    @Override
    public String getJpn() {
        return "ja";
    }

    @Override
    public String getKor() {
        return "ko";
    }

    @Override
    public String getTha() {
        return "th";
    }

    public static void main(String[] args) {
//        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
//        String query = "高度600米";
//        System.out.println(api.getTransResult(query, "auto", "cn"));
        System.out.println(CaiyunTranslator.getInstance().translate("i am a big fat guy", "", "zh"));
    }
}
