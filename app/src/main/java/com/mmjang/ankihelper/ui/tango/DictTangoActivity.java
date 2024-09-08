package com.mmjang.ankihelper.ui.tango;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.tango.helper.SimpleItemTouchHelperCallback;
import com.mmjang.ankihelper.util.ColorThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class DictTangoActivity extends AppCompatActivity {
    private List<OutputLocatorPOJO> mLocatorList;
    RecyclerView locatorListView;
    TangoLocatorsAdapter mLocatorAdapter;
    private static final String LOCATOR_SEP = "|||";
    private static final String LOCATOR_FIELDLIST_SEP = "~~~";
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(Settings.getInstance(this).getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }else{
//            setTheme(R.style.AppTheme);
//        }
        ColorThemeUtils.initColorTheme(DictTangoActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tango_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView introduction = findViewById(R.id.textview_mdictionary_introduction);
        introduction.setMovementMethod(LinkMovementMethod.getInstance());
        EditText onlineUrl = findViewById(R.id.edittext_mdictionary_online_url);

        settings = Settings.getInstance(MyApplication.getContext());
        onlineUrl.setText(settings.getDictTangoOnlineUrl());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_locator);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DictTangoActivity.this, TangoLocationEditorActivity.class);
                startActivity(intent);
            }
        });
        initLocatorList();

        onlineUrl.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        settings.setDictTangoOnlineUrl(charSequence.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<OutputLocatorPOJO> newList = OutputLocatorPOJO.readOutputLocatorPOJOListFromSettings();
        mLocatorList.clear();
        mLocatorList.addAll(newList);
        mLocatorAdapter.notifyDataSetChanged();

    }

    private void initLocatorList() {
        Log.e("initLocatorList", "into");
        mLocatorList = new ArrayList<>();
        //Log.d("locatorsManager:", locators.size() + "ge");
        locatorListView = (RecyclerView) findViewById(R.id.locator_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        locatorListView.setLayoutManager(llm);
        mLocatorAdapter = new TangoLocatorsAdapter(DictTangoActivity.this, mLocatorList);
        //locatorList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        locatorListView.setAdapter(mLocatorAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mLocatorAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(locatorListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_tango_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_export_locator:
                exportlocators();
                break;
            case R.id.menu_item_import_locator:
                importLocators();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }

    private void importLocators() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(clipboard.hasPrimaryClip()){
            if(clipboard.getText()!=null){
                String locatorsString = clipboard.getText().toString();
                List<OutputLocatorPOJO> locatorList = OutputLocatorPOJO.processStringIntoLocatorList(locatorsString, mLocatorList);
                if(locatorList != null) {
                    mLocatorList = locatorList;
                    OutputLocatorPOJO.saveOutputLocatorPOJOListToSettings(locatorList);
                    onResume();
                }
            }
        }else{
            Toast.makeText(this, "剪贴板为空！", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportlocators() {
        String exportedString = OutputLocatorPOJO.processLocatorListIntoString(mLocatorList);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("locators string", exportedString);
        clipboard.setPrimaryClip(clip);
    }

}
