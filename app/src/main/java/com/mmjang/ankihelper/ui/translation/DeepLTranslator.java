package com.mmjang.ankihelper.ui.translation;

import android.util.Log;

import com.deepl.api.TextResult;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.com.baidu.translate.demo.RandomAPIKeyGenerator;

import org.json.JSONObject;

import java.util.Random;
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
public class DeepLTranslator implements Translator {

    private static Translator translator;
    //2.0
//    private final String USER_DEEPL_APP_SECRET_KEY = "e68f7121-97e1-4168-85e7-a032cbe9e186";

    //3.0
//    private static final String BASE_URL = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=%s&to=%s";
//    private String key, mUrl, content;

    public static Translator getInstance() {
        if(translator == null)
            translator = new DeepLTranslator();
        return  translator;
    }

    @Override
    public String translate(String query) {
        try {

            if (RegexUtil.isChineseSentence(query)) {
                return translator.translate(query, null, getEn());
            } else {
                return translator.translate(query, null, getZh());
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
        Settings settings = Settings.getInstance(MyApplication.getContext());
        String keys = settings.getUserDeepLAppSecretKey();
        int index = 0;
        String[] keyArr = keys.split("\n");
        if (keys.isEmpty()) {
            keyArr = RandomAPIKeyGenerator.next(RandomAPIKeyGenerator.Name.DEEPL);
        } else {
            int min = 0;
            int max = keyArr.length - 1;
            index = RandomAPIKeyGenerator.randInt(min, max);
        }

        String authKey = keyArr[index];
        try {
            com.deepl.api.Translator deeplTranslator = new com.deepl.api.Translator(authKey);
            TextResult result =
                    deeplTranslator.translateText(query, null, to);
//            Trace.i("DeepL:" + query +". to:"+to);
            return result.getText();
        } catch (Exception e) {
            translator = null;
            return String.format("[\"error\": \"%s\", \"auth_key\":%s\"]", e.getMessage(), authKey);
        }
    }

    @Override
    public String name() {
        return "DeepL";
    }

    @Override
    public String getZh() {
        return "zh";
    }

    @Override
    public String getAuto() {
        return null;
    }

    @Override
    public String getEn() {
        Random rand = new Random();
        return rand.nextBoolean() ? "en-GB":"en-US";
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
        System.out.println(DeepLTranslator.getInstance().translate("i am a big fat guy", "", "zh"));
    }
}
