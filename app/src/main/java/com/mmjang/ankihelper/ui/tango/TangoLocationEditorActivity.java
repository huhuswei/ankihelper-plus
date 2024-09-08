package com.mmjang.ankihelper.ui.tango;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TangoLocationEditorActivity extends AppCompatActivity {

    private String dictNameToEdit;
    private OutputLocatorPOJO locatorForEdit;

    //views
    private EditText dictNameEditText;
    private Spinner languageNameSpinner;
    private RecyclerView fieldsSpinnersContainer;
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
        ColorThemeUtils.initColorTheme(TangoLocationEditorActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tango_location_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            setViewMember();
            handleIntent();
            populateLanguageName();

            // 初始化数据
            initData();
            adapter = new FieldMapListAdapter(TangoLocationEditorActivity.this, totalFieldsMapItemList);
            fieldsSpinnersContainer.setLayoutManager(new LinearLayoutManager(this));
            fieldsSpinnersContainer.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_locator_editor_menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save_locator_edit:
                if (savePlan()) {
                    finish();
                }
                break;
            case R.id.menu_item_add_fields_locator_edit:
                addFields();
                break;
            case R.id.menu_item_remove_fields_locator_edit:
                removeFields();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }

    private void setViewMember() {
        // 实例化控件
        dictNameEditText = (EditText) findViewById(R.id.text_edit_dict_name);
        languageNameSpinner = (Spinner) findViewById(R.id.language_name_spinner);
        fieldsSpinnersContainer = (RecyclerView) findViewById(R.id.recycler_view_selector_fields_map);
    }

    /**
     * 初始化数据源
     */
    private void initData() {
        fieldsMapItemLList = new ArrayList<>();
        totalFieldsMapItemList = new ArrayList<>();

        if (locatorForEdit != null) {
            String[] fields = locatorForEdit.getFieldArray();
            ArrayList<Map<String, String>> fieldsMapList = locatorForEdit.getFieldsMapList();
            for (Map<String, String> fieldsMap : fieldsMapList) {
                fieldsMapItemLList.add(new ArrayList<>());
                for (String fld : fields) {
                    if (fieldsMap.containsKey(fld)) {
                        FieldsMapItem item = new FieldsMapItem(fld, fieldsMap.get(fld));
                        totalFieldsMapItemList.add(item);
                        fieldsMapItemLList.get(fieldsMapItemLList.size() - 1).add(item);
                    }
                }
            }
        } else {
            String[] fields = Constant.getTangoDefaultFields();
            fieldsMapItemLList.add(new ArrayList<>());
            for (String fld : fields) {
                FieldsMapItem item = new FieldsMapItem(fld, "");
                totalFieldsMapItemList.add(item);
                fieldsMapItemLList.get(fieldsMapItemLList.size() - 1).add(item);
            }
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();
            if (action != null && action.equals(Intent.ACTION_SEND)) {
                dictNameToEdit = intent.getStringExtra(Intent.EXTRA_TEXT);
                OutputLocatorPOJO re = OutputLocatorPOJO.getOutputlocatorMap().get(dictNameToEdit);
                if (re != null) {
                    locatorForEdit = re;
                    dictNameEditText.setText(dictNameToEdit);
                    //dictNameEditText.setEnabled(false);
                }
            }
        }
    }

    private void populateLanguageName() {
        Trace.i("languageName", Arrays.toString(DictLanguageType.getLanguageNameList()));
        Trace.i("locatorForEdit", String.valueOf(locatorForEdit == null));
        ArrayAdapter<String> languageNameSpinnerAdapter = new ArrayAdapter<>(
                this, R.layout.support_simple_spinner_dropdown_item, DictLanguageType.getLanguageNameList());
        languageNameSpinner.setAdapter(languageNameSpinnerAdapter);

        if (locatorForEdit != null) {
                languageNameSpinner.setSelection(locatorForEdit.getLangIndex());
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

    private boolean savePlan() {
        String dictName = dictNameEditText.getText().toString().trim();
        if (dictName.isEmpty()) {
            Toast.makeText(this, R.string.str_locator_dict_name_should_not_be_blank, Toast.LENGTH_SHORT).show();
            return false;
        }
        //DataSupport.findAll()
        OutputLocatorPOJO locator;
        if (locatorForEdit != null) {
            if (!dictName.equals(dictNameToEdit)) {
                //if name conflicts, toast.
                OutputLocatorPOJO rel = OutputLocatorPOJO.getOutputlocatorMap().get(dictName);
                if (rel != null) {
                    Toast.makeText(this, R.string.locator_dict_name_already_exists, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            locator = locatorForEdit;
        } else {
            //if name conflicts, toast.
            OutputLocatorPOJO rel = OutputLocatorPOJO.getOutputlocatorMap().get(dictName);
            if (rel != null) {
                Toast.makeText(this, R.string.locator_dict_name_already_exists, Toast.LENGTH_SHORT).show();
                return false;
            }
            locator = new OutputLocatorPOJO();
        }
        //new OutputPlan();
        locator.setDictName(dictName);
        locator.removeAllFieldsMapStrList();
        locator.setLangIndex(languageNameSpinner.getSelectedItemPosition());

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        boolean allFieldsAreEmpty = true;
        int index = 0;
        for(int pos = 0; pos < fieldsMapItemLList.size(); pos++) {
            for (FieldsMapItem item : fieldsMapItemLList.get(pos)) {
//                View view = fieldsSpinnersContainer.getLayoutManager().getChildAt(index);
//                FieldMapListAdapter.ViewHolder holder = (FieldMapListAdapter.ViewHolder) fieldsSpinnersContainer.getChildViewHolder(view);
                String k = item.getField();
                String v = item.getSelector();
                if (!v.equals("")) {
                    allFieldsAreEmpty = false;
                } else {
                    v = " ";
                }
                map.put(k, v);
                index++;
            }

            if (allFieldsAreEmpty) {
                Toast.makeText(this, R.string.save_selector_error_all_blank, Toast.LENGTH_SHORT).show();
                OutputLocatorPOJO.removeOutputlocatorMap(dictName);
                return false;
            }
            locator.setFieldsMapStrList(pos, map);
        }
//        if(dictNameToEdit != null){
//            OutputLocatorPOJO.update(locator, dictName);
//        }else{
//            OutputLocatorPOJO.append(locator);
//        }
        locator.save();

        return true;
    }

    private boolean addFields() {
        String[] fields = Constant.getTangoDefaultFields();
        fieldsMapItemLList.add(new ArrayList<>());
        for (String fld : fields) {
            FieldsMapItem item = new FieldsMapItem(fld, "");
            totalFieldsMapItemList.add(item);
            adapter.notifyDataSetChanged();
            fieldsMapItemLList.get(fieldsMapItemLList.size() - 1).add(item);
        }
        Trace.i("fieldsMapItemLList size", String.valueOf(fieldsMapItemLList.size()));
        return true;
    }

    private boolean removeFields() {
        int size = Constant.getTangoDefaultFields().length;
        if(fieldsMapItemLList.size()>1) {
            fieldsMapItemLList.remove(fieldsMapItemLList.size() - 1);
            while (size>0) {
                totalFieldsMapItemList.remove(totalFieldsMapItemList.size() - 1);
                adapter.notifyDataSetChanged();
                size--;
                Trace.i("totalFieldsMapItem size", String.valueOf(totalFieldsMapItemList.size()));
            }
            Trace.i("fieldsMapItemLList size", String.valueOf(fieldsMapItemLList.size()));
            return true;
        } else {
            return false;
        }
    }

}
