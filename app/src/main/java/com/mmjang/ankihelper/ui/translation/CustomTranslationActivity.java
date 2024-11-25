package com.mmjang.ankihelper.ui.translation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.floating.assist.AssistFloatWindow;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.DialogUtil;

public class CustomTranslationActivity extends AppCompatActivity {
    private Settings settings;
    private TextView textViewChooseTranslationEngine;
    private SharedPreferences.OnSharedPreferenceChangeListener callbackTranslatorCheckedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = Settings.getInstance(MyApplication.getContext());
//        if(Settings.getInstance(this).getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }else{
//            setTheme(R.style.AppTheme);
//        }
        ColorThemeUtils.initColorTheme(CustomTranslationActivity.this);
        super.onCreate(savedInstanceState);
        ActivityUtil.checkStateForAnkiDroid(this);
        setContentView(R.layout.activity_custom_translation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewChooseTranslationEngine = findViewById(R.id.tv_choose_translation_engine);
        TextView baiduIntroduction = findViewById(R.id.textview_custom_baidu_translation_introduction);
        baiduIntroduction.setMovementMethod(LinkMovementMethod.getInstance());
        TextView caiyunIntroduction = findViewById(R.id.textview_custom_caiyun_translation_introduction);
        caiyunIntroduction.setMovementMethod(LinkMovementMethod.getInstance());
//        TextView microsoftIntroduction = findViewById(R.id.textview_custom_microsoft_translation_introduction);
//        microsoftIntroduction.setMovementMethod(LinkMovementMethod.getInstance());
        TextView youdaoIntroduction = findViewById(R.id.textview_custom_youdao_translation_introduction);
        youdaoIntroduction.setMovementMethod(LinkMovementMethod.getInstance());

        EditText baiduAppid = findViewById(R.id.edittext_baidufanyi_appid);
        EditText baiduSecret = findViewById(R.id.edittext_baidufanyi_key);
        EditText deeplSecretKey = findViewById(R.id.edittext_deepl_key);
        EditText deeplxApiUrls = findViewById(R.id.edittext_deeplx_urls);
        EditText caiyunSecretKey = findViewById(R.id.edittext_caiyunxiaoyi_key);
//        EditText microsoftAppid = findViewById(R.id.edittext_microsoftfanyi_appid);
        EditText youdaoAppid = findViewById(R.id.edittext_youdaofanyi_appid);
        EditText youdaoKey = findViewById(R.id.edittext_youdaofanyi_key);

        baiduAppid.setText(settings.getUserBaidufanyiAppId());
        baiduSecret.setText(settings.getUserBaidufanyiAppKey());
        deeplSecretKey.setText(settings.getUserDeepLAppSecretKey());
        deeplxApiUrls.setText(settings.getUserDeepLXAPIURLs());
        caiyunSecretKey.setText(settings.getUserCaiyunAppSecretKey());
//        microsoftAppid.setText(settings.getUserMicrosoftAppId());
        youdaoAppid.setText(settings.get(Settings.USER_YOUDAO_APP_ID, ""));
        youdaoKey.setText(settings.get(Settings.USER_YOUDAO_APP_KEY, ""));

        callbackTranslatorCheckedIndex = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                showCurrentEngine();
            }
        };
        settings.getSharedPreferences().registerOnSharedPreferenceChangeListener(callbackTranslatorCheckedIndex);
        showCurrentEngine();

        textViewChooseTranslationEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.selectTranslatorSettingDialog(CustomTranslationActivity.this);
            }
        });

        youdaoAppid.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        settings.put(Settings.USER_YOUDAO_APP_ID, s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
        youdaoKey.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        settings.put(Settings.USER_YOUDAO_APP_KEY, s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        baiduAppid.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        settings.setUserBaidufanyiAppId(charSequence.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );

        baiduSecret.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        settings.setUserBaidufanyiAppKey(charSequence.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );

        deeplSecretKey.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        settings.setUserDeepLAppSecretKey(s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        deeplxApiUrls.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        settings.setUserDeepLXAPIURLs(s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        caiyunSecretKey.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        settings.setUserCaiyunAppSecretKey(s.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );
//        microsoftAppid.addTextChangedListener(
//                new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        settings.setUserMicrosoftAppId(s.toString().trim());
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                }
//        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtil.checkStateForAnkiDroid(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settings.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(callbackTranslatorCheckedIndex);
    }

    private void showCurrentEngine() {
        String s = getResources().getText(R.string.tv_choose_translation_engine).toString();
        String c = TranslateBuilder.getNameArr()[Settings.getInstance(MyApplication.getContext()).getTranslatorCheckedIndex()];
        textViewChooseTranslationEngine.setText(String.format("%s:  %s", s, c));
    }
}
