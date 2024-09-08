package com.mmjang.ankihelper.ui.translation;

import android.util.Log;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.Trace;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: MicrosoftTranslator
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/8/9 1:22 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/8/9 1:22 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class YoudaoTranslator implements Translator {

    private static Translator translator;

    //2.0
    private final String APP_ID = "7743eee7f7e11d75";
    private final String APP_KEY = "bwPochuFLSutY4nGvyJoeUNn9GBQduzl";
    private static final String BASE_URL = "https://openapi.youdao.com/api";

    //3.0
//    private static final String BASE_URL = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=%s&to=%s";
//    private String key, mUrl, content;

    public static Translator getInstance() {
        if(translator == null)
            translator = new YoudaoTranslator();
        return  translator;
    }

    @Override
    public String translate(String query) {
        try {

            if (RegexUtil.isChineseSentence(query)) {
                return translator.translate(query, "zh-CHS", "en");
            } else {
                return translator.translate(query, "auto", "zh-CHS");
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
        String id;
        String key;
        if (settings.get(Settings.USER_YOUDAO_APP_ID, "").isEmpty()) {
            id = APP_ID;
            key = APP_KEY;

        } else {
            id = settings.get(Settings.USER_YOUDAO_APP_ID, "");
            key = settings.get(Settings.USER_YOUDAO_APP_KEY, "");
        }
        try {
            String signType = "v3";
            String salt = String.valueOf(System.currentTimeMillis());
            String curtime = String.valueOf(System.currentTimeMillis() / 1000);
            String signStr = id + truncate(query) + salt + curtime + key;
            String sign = getDigest(signStr);

            RequestBody body = new FormBody.Builder()
                    .add("q",query)
                    .add("from",from)
                    .add("to",to)
                    .add("appKey", id)
                    .add("salt",salt)
                    .add("sign",sign)
                    .add("signType",signType)
                    .add("curtime",curtime)
                    .build();

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .header("User-Agent", Constant.UA)
                    .post(body)
                    .build();
            String resultStr = MyApplication.getOkHttpClient().newBuilder().connectTimeout(3, TimeUnit.SECONDS).build().newCall(request)
                    .execute()
                    .body()
                    .string();
            Trace.i("resultjson", resultStr);
            JSONObject resultJson = new JSONObject(resultStr);
            return resultJson.getJSONArray("translation").getString(0);

        } catch (Exception e) {
            translator = null;
            return "";
        }
    }

    @Override
    public String name() {
        return "有道";
    }

    @Override
    public String getZh() {
        return "zh-CHS";
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

    /**
     * 生成加密字段
     */
    private String getDigest(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private String truncate(String query) {
        if (query == null) {
            return null;
        }
        int len = query.length();
        return len <= 20 ? query : (query.substring(0, 10) + len + query.substring(len - 10, len));
    }

    public static void main(String[] args) {
//        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
//        String query = "高度600米";
//        System.out.println(api.getTransResult(query, "auto", "cn"));
        System.out.println(YoudaoTranslator.getInstance().translate("i am a big fat guy", "", "zh-Hans"));
    }
}
