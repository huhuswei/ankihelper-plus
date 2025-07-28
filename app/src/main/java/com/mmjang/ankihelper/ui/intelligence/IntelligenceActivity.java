package com.mmjang.ankihelper.ui.intelligence;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.intelligence.mathpix.MathpixSettingActivity;
import com.mmjang.ankihelper.ui.intelligence.tess.ApiResponse;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.DialogUtil;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.intelligence
 * @ClassName: IntelligenceActivity
 * @Description: java类作用描述
 * @Author: 唐朝
 * @CreateDate: 2022/7/30 10:27 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/30 10:27 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class IntelligenceActivity extends AppCompatActivity {
    Settings settings;
    TextView textViewTesseractOcrSettingDialog;
    TextView textViewMlKitOcrSettingDialog;
    TextView textviewMathpixActivity;
    SwitchCompat switchAutomaticRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(Settings.getInstance(this).getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }else{
//            setTheme(R.style.AppTheme);
//        }
        settings = Settings.getInstance(this);
        ColorThemeUtils.initColorTheme(IntelligenceActivity.this);
        super.onCreate(savedInstanceState);
        ActivityUtil.checkAndStartAnkiDroid(this);
        setContentView(R.layout.activity_intelligence_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //设置初始值
        textViewTesseractOcrSettingDialog = (TextView) findViewById(R.id.btn_set_ocrsetting);
        textViewMlKitOcrSettingDialog = (TextView) findViewById(R.id.btn_set_ocrsetting_mlkit);
        textviewMathpixActivity = (TextView) findViewById(R.id.btn_set_mathpix_ocr);
        switchAutomaticRecognition = (SwitchCompat) findViewById(R.id.switch_automatic_recognition);
        switchAutomaticRecognition.setChecked(
                settings.get(Settings.OCR_AUTOMATIC_RECOGNITION, false)
        );

        textViewTesseractOcrSettingDialog.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        DialogUtil.showTesseractSettingDialog(IntelligenceActivity.this);
                        ApiResponse apiResponse = ApiResponse.parseJsonFromAssets(IntelligenceActivity.this, "tess.json");
                        if(apiResponse != null) {
                            DialogUtil.tessDatadownloadDialog(IntelligenceActivity.this, apiResponse).show();
                        }
                    }
                }
        );

        textViewMlKitOcrSettingDialog.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtil.showMlKitOcrSettingDialog(IntelligenceActivity.this);
                    }
                }
        );

        textviewMathpixActivity.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(IntelligenceActivity.this, MathpixSettingActivity.class);
                        startActivity(intent);
                    }
                }
        );

        switchAutomaticRecognition.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    settings.put(Settings.OCR_AUTOMATIC_RECOGNITION, isChecked);
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtil.checkAndStartAnkiDroid(this);
    }
}
