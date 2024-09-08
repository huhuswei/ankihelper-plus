package com.mmjang.ankihelper.ui.translation;

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
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.DialogUtil;

public class CustomTranslationActivity extends AppCompatActivity {
    private TextView textViewChooseTranslationEngine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Settings settings = Settings.getInstance(MyApplication.getContext());
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
        textViewChooseTranslationEngine = (TextView) findViewById(R.id.tv_choose_translation_engine);
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
        EditText caiyunSecretKey = findViewById(R.id.edittext_caiyunxiaoyi_key);
//        EditText microsoftAppid = findViewById(R.id.edittext_microsoftfanyi_appid);
        EditText youdaoAppid = findViewById(R.id.edittext_youdaofanyi_appid);
        EditText youdaoKey = findViewById(R.id.edittext_youdaofanyi_key);

        baiduAppid.setText(settings.getUserBaidufanyiAppId());
        baiduSecret.setText(settings.getUserBaidufanyiAppKey());
        caiyunSecretKey.setText(settings.getUserCaiyunAppSecretKey());
//        microsoftAppid.setText(settings.getUserMicrosoftAppId());
        youdaoAppid.setText(settings.get(Settings.USER_YOUDAO_APP_ID, ""));
        youdaoKey.setText(settings.get(Settings.USER_YOUDAO_APP_KEY, ""));

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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        String s = getResources().getText(R.string.tv_choose_translation_engine).toString();
        String c = TranslateBuilder.getNameArr()[Settings.getInstance(MyApplication.getContext()).getTranslatorCheckedIndex()];
        textViewChooseTranslationEngine.setText(String.format("%s:  %s", s, c));
    }
}
