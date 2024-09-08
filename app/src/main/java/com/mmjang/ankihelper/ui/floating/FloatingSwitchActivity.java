package com.mmjang.ankihelper.ui.floating;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.floating.assist.AssistFloatWindow;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.floating
 * @ClassName:
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/5/19 9:57 PM
 * @UpdateUser: ss
 * @UpdateDate: 2023/12/2 6:57 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class FloatingSwitchActivity extends AppCompatActivity {
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window=getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.flags=WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        wl.alpha=0;//这句就是设置窗口里崆件的透明度的．０.０全透明．１.０不透明．
        window.setAttributes(wl);

        super.onCreate(savedInstanceState);

        settings = Settings.getInstance(MyApplication.getContext());
        settings.setFloatBallEnable(!settings.getFloatBallEnable());
        if (settings.getFloatBallEnable()) {
            AssistFloatWindow.Companion.getInstance().show();
        } else {
            AssistFloatWindow.Companion.getInstance().hide();
        }

        finish();
        return;
    }
}