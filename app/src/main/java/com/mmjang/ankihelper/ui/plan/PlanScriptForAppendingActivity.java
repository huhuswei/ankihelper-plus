package com.mmjang.ankihelper.ui.plan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.widget.NoteEditText;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.StorageUtils;
import com.mmjang.ankihelper.util.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PlanScriptForAppendingActivity extends AppCompatActivity {

    private NoteEditText etHTML, etJS, etCSS;
    private static String SEPERATOR = "__@@@__";
    private final String CARD_SCRIPT_FILE = "card_script_for_appending.html";
    private final String CODING = "UTF-8";
    private final int NUMBER_OF_SCRIPT_STRING = 3;
    private boolean isEditable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(Settings.getInstance(this).getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }
        ColorThemeUtils.initColorTheme(PlanScriptForAppendingActivity.this);
        super.onCreate(savedInstanceState);
        ActivityUtil.checkStateForAnkiDroid(this);
        setContentView(R.layout.activity_plan_script_content_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            setViewMember();
        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtil.checkStateForAnkiDroid(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_plan_script_content_editor_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_edit_script_content:
                switchEditingState();
                break;
            case R.id.menu_item_save_script_content:
                if (save()) {
                    finish();
                }
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }

    private void setViewMember() {
        try {
            InputStream ips;
            String appPath = StorageUtils.getAnkihelperDirectory().getPath();
            File file = new File(appPath, CARD_SCRIPT_FILE);
            if(file.exists())
                ips = new FileInputStream(file.getPath());
            else
                ips = getApplicationContext().getResources().getAssets().open(CARD_SCRIPT_FILE);
            byte[] data = new byte[ips.available()];
            ips.read(data);
            ips.close();
            String scriptContent = new String(data, CODING);
            String[] contentArr = scriptContent.split(SEPERATOR);
            if(contentArr.length != NUMBER_OF_SCRIPT_STRING)
                contentArr = new String[]{"","",""};
            // 实例化控件
            etHTML = (NoteEditText) findViewById(R.id.et_card_html);
            etHTML.setText(contentArr[0].trim());
            etJS = (NoteEditText) findViewById(R.id.et_card_javascript);
            etJS.setText(contentArr[1].trim());
            etCSS = (NoteEditText) findViewById(R.id.et_card_css);
            etCSS.setText(contentArr[2].trim());
            switchEditingState();

            etHTML.setOnLongClickListener(v -> {
                copy(v);
                return true;
            });
            etJS.setOnLongClickListener(v -> {
                copy(v);
                return true;
            });
            etCSS.setOnLongClickListener(v -> {
                copy(v);
                return true;
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copy(View v) {
        try {
            String text = ((NoteEditText) v).getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("script content", text);
            clipboard.setPrimaryClip(clip);
            ToastUtil.show("复制成功");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("复制失败");
        }
    }

    private boolean save() {
        try {
            String appPath = StorageUtils.getAnkihelperDirectory().getPath();
            OutputStream ops = new FileOutputStream(appPath + File.separator + CARD_SCRIPT_FILE);
            String text = etHTML.getText().toString() +
                    "\n" + SEPERATOR + "\n" +
                    etJS.getText().toString() +
                    "\n" + SEPERATOR + "\n" +
                    etCSS.getText().toString();
            ops.write(text.getBytes());
            ops.flush();
            ops.close();
            ToastUtil.show("已保存");
            return true;
        } catch (Exception e) {
            ToastUtil.show("保存失败");
            return false;
        }
    }

    private void switchEditingState() {
        isEditable = !isEditable;
        etHTML.setFocusableInTouchMode(isEditable);
        etHTML.setCursorVisible(isEditable);
        etJS.setFocusableInTouchMode(isEditable);
        etJS.setCursorVisible(isEditable);
        etCSS.setFocusableInTouchMode(isEditable);
        etCSS.setCursorVisible(isEditable);
    }

}
