package com.mmjang.ankihelper.ui.mdict;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.data.dict.mdict.MdictInformation;
import com.mmjang.ankihelper.data.dict.mdict.MdictManager;
import com.mmjang.ankihelper.ui.tango.FieldMapListAdapter;
import com.mmjang.ankihelper.ui.tango.FieldsMapItem;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Trace;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.rosuh.filepicker.config.FilePickerConfig;
import me.rosuh.filepicker.config.FilePickerManager;
import me.rosuh.filepicker.filetype.FileType;

public class MdictEditorActivity extends AppCompatActivity {

    private int order = -1;
    MdictManager mdictManager;
    //views
    private EditText dictNameEditText;
    private EditText cssNameEditText;
    private EditText jsNameEditText;
    private EditText customFieldsEditText;
    private Spinner languageNameSpinner;

    /**
     * 适配器的数据源
     */
    private List<Integer> dataList;
    private List<List<FieldsMapItem>> fieldsMapItemLList;
    private List<FieldsMapItem> totalFieldsMapItemList;
    /**
     * 适配器
     */
    private FieldMapListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(Settings.getInstance(this).getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }
//        ColorThemeUtils.initColorTheme(MdictEditorActivity.this);
        ColorThemeUtils.setColorTheme(MdictEditorActivity.this, Constant.StyleBaseTheme.CustomDicTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mdict_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mdictManager = new MdictManager(MyApplication.getContext(), "");
        try {
            setViewMember();
            handleIntent();
            populateLanguageName();

            // 初始化数据
            adapter = new FieldMapListAdapter(MdictEditorActivity.this, totalFieldsMapItemList);

        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_mdict_editor_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save_mdict_edit:
                if (saveMdictInformation()) {
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
        // 实例化控件
        dictNameEditText = (EditText) findViewById(R.id.et_mdict_name);
        cssNameEditText = (EditText) findViewById(R.id.et_css_name);
        jsNameEditText = (EditText) findViewById(R.id.et_js_name);
        customFieldsEditText = (EditText) findViewById(R.id.et_mdict_custom_fields);
        languageNameSpinner = (Spinner) findViewById(R.id.language_name_spinner);

        cssNameEditText.setFocusable(false);
        cssNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(cssNameEditText.getText().toString().equals("") ? dictNameEditText.getText().toString() : cssNameEditText.getText().toString());
                selectFile(path.getParent() + File.separator, "css");
            }
        });
        cssNameEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cssNameEditText.setText("");
                return false;
            }
        });

        jsNameEditText.setFocusable(false);
        jsNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = new File(jsNameEditText.getText().toString().equals("") ? dictNameEditText.getText().toString() : jsNameEditText.getText().toString());
                selectFile(path.getParent() + File.separator, "js");
            }
        });
        jsNameEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                jsNameEditText.setText("");
                return false;
            }
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();
            if (action != null && action.equals(Intent.ACTION_SEND)) {
                order = intent.getIntExtra(Constant.INTENT_ANKIHELPER_MDICT_ORDER, -1);
                MdictInformation mdictInfor = mdictManager.getMdictInfoByOrder(order);
                if (order != -1) {
                    Trace.i("dictName", ""+mdictInfor.getDictName());
                    dictNameEditText.setText(mdictInfor.getDictName());
                    cssNameEditText.setText(mdictInfor.getDictCss());
                    jsNameEditText.setText(mdictInfor.getDictJs());
                    customFieldsEditText.setText(mdictInfor.getCustomFields());
                    //dictNameEditText.setEnabled(false);
                }
            }
        } else
            finish();
    }

    private void populateLanguageName() {
        Trace.i("languageName", Arrays.toString(DictLanguageType.getLanguageNameList()));
        ArrayAdapter<String> languageNameSpinnerAdapter = new ArrayAdapter<>(
                this, R.layout.support_simple_spinner_dropdown_item, DictLanguageType.getLanguageNameList());
        languageNameSpinner.setAdapter(languageNameSpinnerAdapter);

        if (order != -1) {
            languageNameSpinner.setSelection(mdictManager.getMdictInfoByOrder(order).getDictLangNameIndex());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(TestActivity.this, R.string.permission_granted, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
        }
    }

    private boolean saveMdictInformation() {
        String dictName = dictNameEditText.getText().toString().trim();
        String dictCss = cssNameEditText.getText().toString().trim();
        String dictJs = jsNameEditText.getText().toString().trim();
        String fields = customFieldsEditText.getText().toString().trim();

        if (dictName.isEmpty()) {
            Toast.makeText(this, R.string.str_locator_dict_name_should_not_be_blank, Toast.LENGTH_SHORT).show();
            return false;
        }

//         只允许一个加号
        String regex = "^(?!.*\\s)(?!.*@\\s|@\\s|@{2,})(?!(.*@)*\\w+\\+\\w+(@.*)*|\\+\\+)[\\w\\s@]*[+]?[\\w\\s@]*(?<!\\s)$";
//         允许多个加号
//        String regex = "^(?!.*\\s)(?!.*@\\s|@\\s|@@)(?!(.*@)*\\w+\\+\\w+(@.*)*|\\+\\+)([\\w\\s@]*[+]?[\\w\\s@]*)+(?<!\\s)$";
        if (TextUtils.isEmpty(fields)) {
            fields = MdictManager.FAULT_CUSTOM_FIELDS;
        }
        String[] fieldArr = fields.split(MdictManager.SPLIT_TAG);
        if (!RegexUtil.isMatch(regex, fields) || StringUtil.contain(fieldArr, "")) {
            Toast.makeText(this, R.string.str_string_segment_format_error, Toast.LENGTH_LONG).show();
            return false;
        } else if (!fields.contains(Constant.MDX_ADD_TAG)) {
            Toast.makeText(this, R.string.str_error_missing_plus, Toast.LENGTH_LONG).show();
            return false;
        } else if (!StringUtil.contain(fieldArr, Constant.DICT_FIELD_KEYWORD)) {
            Toast.makeText(this, R.string.str_error_missing_keyword_fields, Toast.LENGTH_LONG).show();
            return false;
        } else if (!StringUtil.hasNoDuplicates(fieldArr)) {
            Toast.makeText(this, R.string.str_error_duplicate_fields, Toast.LENGTH_LONG).show();
            return false;
        }
        MdictInformation mdictInformation = mdictManager.getMdictInfoByOrder(order);
        mdictInformation.setDictCss(dictCss);
        mdictInformation.setDictJs(dictJs);
        mdictInformation.setDictLang((int) Math.pow(2,languageNameSpinner.getSelectedItemPosition()));
        mdictInformation.setCustomFields(fields);
        mdictManager.updateMdictInformation(mdictInformation);
        return true;
    }

    void selectFile(String path, final String suffix) {
//        FilePickerManager.INSTANCE
//                .from(this)
//                .setTheme(
//                        Settings.getInstance(MyApplication.getContext()).getPinkThemeQ() ?
//                                R.style.AndroidFilePickerThemePink : R.style.AndroidFilePickerTheme)
//                .registerFileType(Arrays.asList(new PlainTextFileType()), true)
//                .forResult(FilePickerManager.REQUEST_CODE);
        FilePickerManager.from(this).
                enableSingleChoice().
                storageType(FilePickerConfig.STORAGE_CUSTOM_ROOT_PATH).
                setCustomRootPath(path).
                setTheme(ColorThemeUtils.getColorTheme(MdictEditorActivity.this, Constant.StyleBaseTheme.AndroidFilePickerTheme)).
                registerFileType(Arrays.asList(new PlainTextFileType(suffix)), true).
                forResult(FilePickerManager.REQUEST_CODE);
    }

    class PlainTextFileType implements FileType {
        private final String suffix;

        public PlainTextFileType(final String SUFFIX) {
            this.suffix = SUFFIX;
        }
        @Override
        public int getFileIconResId() {
//            return Settings.getInstance(MyApplication.getContext()).getPinkThemeQ() ?
//                    R.drawable.ic_set_dict_pink : R.drawable.ic_set_dict;
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.icon_custom_dict, outValue, true);
            return outValue.resourceId;
        }

        @NonNull
        @Override
        public String getFileType() {
            return suffix;
        }

        @Override
        public boolean verify(@NonNull String s) {
            return s.endsWith(suffix);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FilePickerManager.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> pathList = FilePickerManager.obtainData();
            if (pathList.size() == 1) {
                String path = pathList.get(0);
                if(path.endsWith("css")) {
                    cssNameEditText.setText(path);
                } else if(path.endsWith("js")) {
                    jsNameEditText.setText(path);
                }
            }
        }
    }
}
