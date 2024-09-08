package com.mmjang.ankihelper.ui.intelligence.mathpix;

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
import com.mmjang.ankihelper.util.ColorThemeUtils;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.intelligence
 * @ClassName: IntelligenceActivity
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/7/30 10:27 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/30 10:27 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MathpixSettingActivity extends AppCompatActivity {

    private TextView textViewSelectMathpixRequestParamters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Settings settings = Settings.getInstance(MyApplication.getContext());
//        if(Settings.getInstance(this).getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }else{
//            setTheme(R.style.AppTheme);
//        }
        ColorThemeUtils.initColorTheme(MathpixSettingActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mathpix_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewSelectMathpixRequestParamters = (TextView) findViewById(R.id.tv_choose_mathipx_result);
        TextView tvMathpixOcrIntroduction = findViewById(R.id.textview_custom_mathpix_ocr);
        tvMathpixOcrIntroduction.setMovementMethod(LinkMovementMethod.getInstance());

        EditText mathpixId = findViewById(R.id.edittext_mathpix_id);
        EditText mathpixKey = findViewById(R.id.edittext_mathpix_key);

        mathpixId.setText(settings.get(Settings.OCR_MATHPIX_ID, ""));
        mathpixKey.setText(settings.get(Settings.OCR_MATHPIX_KEY, ""));

        textViewSelectMathpixRequestParamters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogUtil.selectTranslatorSettingDialog(MathpixSettingActivity.this);
            }
        });

        mathpixId.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        settings.put(Settings.OCR_MATHPIX_ID, charSequence.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );

        mathpixKey.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        settings.put(Settings.OCR_MATHPIX_KEY, charSequence.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        String s = getResources().getText(R.string.tv_select_mathpix_request_parameters).toString();
        String c = "Default";
//        String c = TranslateBuilder.getNameArr()[Settings.getInstance(getApplicationContext()).getTranslatorCheckedIndex()];
        textViewSelectMathpixRequestParamters.setText(String.format("%s:  %s", s, c));
    }
}
