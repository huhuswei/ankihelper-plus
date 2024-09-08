package com.mmjang.ankihelper.ui.translation;

import android.util.Log;

import com.google.gson.Gson;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.Trace;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;

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
public class MicrosoftTranslator implements Translator {

    private static Translator translator;

    //2.0
    private final String USER_MICROSOFT_APP_ID = "DB50E2E9FBE2E92B103E696DCF4E3E512A8826FB";
    private static final String BASE_URL = "https://api.microsofttranslator.com/V2/Ajax.svc/Translate?appId=%s&oncomplete=?&text=%s&from=%s&to=%s";

    //3.0
//    private static final String BASE_URL = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=%s&to=%s";
//    private String key, mUrl, content;

    public static Translator getInstance() {
        if(translator == null)
            translator = new MicrosoftTranslator();
        return  translator;
    }

    @Override
    public String translate(String query) {
        try {

            if (RegexUtil.isChineseSentence(query)) {
                return translator.translate(query, "zh-Hans", "en");
            } else {
                return translator.translate(query, "", "zh-Hans");
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
        if (settings.getUserMicrosoftAppId().isEmpty()) {
            id = USER_MICROSOFT_APP_ID;
        } else {
            id = settings.getUserMicrosoftAppId();
        }

        try {
            String wordUrl = String.format(BASE_URL, id, URLEncoder.encode(query, "UTF-8"), from, to);
            Trace.i("url", wordUrl);
            Request request = new Request.Builder()
                    .url(wordUrl)
                    .header("User-Agent", Constant.UA)
                    .build();
            String resultJson = MyApplication.getOkHttpClient().newBuilder().connectTimeout(3, TimeUnit.SECONDS).build().newCall(request)
                    .execute()
                    .body()
                    .string();
            Trace.i("resultjson", resultJson);
            return new Gson().fromJson(resultJson, String.class);

        } catch (Exception e) {
            translator = null;
            return "";
        }
    }

    @Override
    public String name() {
        return "微软";
    }

    @Override
    public String getZh() {
        return "zh-Hans";
    }

    @Override
    public String getAuto() {
        return "";
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
        System.out.println(MicrosoftTranslator.getInstance().translate("i am a big fat guy", "", "zh-Hans"));
    }


    /*
     * 3.0
     */
    /*@Override
    public String translate(String query, String from, String to) {
        try {
            config(query, from, to);
            String response = Post();
            return prettify(response);
        } catch (Exception e) {
            e.getStackTrace();
            return "";
        }
    }

    private void config(String query, String from, String to) throws UnsupportedEncodingException {
        content = String.format("[{\"Text\": \"%s\"}]", query);
        Settings settings = Settings.getInstance(MyApplication.getContext());
        if(settings.getUserMicrosoftAppId().isEmpty()) {
            key = USER_MICROSOFT_APP_ID;
        }else{
            key = settings.getUserMicrosoftAppId();
        }
        mUrl = String.format(BASE_URL, URLEncoder.encode(query, "UTF-8"), from, to);
    }
    // This function performs a POST request.
    private String Post() throws IOException {
        // Instantiates the OkHttpClient.
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, content);
        Request request = new Request.Builder()
                .url(mUrl)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key",key)
                .addHeader("Content-type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }*/

    // This function prettifies the json response.
//    private String prettify(String json_text) {
//        JsonParser parser = new JsonParser();
//        JsonElement json = parser.parse(json_text);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        return gson.toJson(json);
//    }
}
