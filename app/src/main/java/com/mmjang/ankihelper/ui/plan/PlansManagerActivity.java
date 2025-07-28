package com.mmjang.ankihelper.ui.plan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;
import com.mmjang.ankihelper.ui.plan.helper.SimpleItemTouchHelperCallback;
import com.mmjang.ankihelper.util.ActivityUtil;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.SystemUtils;
import com.mmjang.ankihelper.util.Utils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlansManagerActivity extends AppCompatActivity {

    private List<OutputPlanPOJO> mPlanList;
    private AnkiDroidHelper mAnkiDroid;
    Settings settings;
    RecyclerView planListView;
    PlansAdapter mPlansAdapter;
    private static final String PLAN_SEP = "|||";
    private static final int ERROR_FORMAT = 1;
    public int REQUEST_HANDLE_INTENT = 1;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if(Settings.getInstance(this).getPinkThemeQ()){
//            setTheme(R.style.AppThemePink);
//        }
        ColorThemeUtils.initColorTheme(PlansManagerActivity.this);
        super.onCreate(savedInstanceState);
        settings = Settings.getInstance(MyApplication.getContext());
        ActivityUtil.checkAndStartAnkiDroid(this);
        setContentView(R.layout.activity_plans_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_plan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (MyApplication.getAnkiDroid().isAnkiDroidRunning()) {
                    Intent intent = new Intent(PlansManagerActivity.this, PlanEditorActivity.class);
                    startActivity(intent);
//                } else {
//                    DialogUtil.showStartAnkiDialog(PlansManagerActivity.this);
//                }
            }
        });
        initAnkiApi();
        initPlanList();

//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        try{
//                            MyApplication.getAnkiDroid().getApi().getDeckList();
//                        }catch (Exception e){
//                        }
//                    }
//                }
//        ).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        List<OutputPlanPOJO> newList = ExternalDatabase.getInstance().getAllPlan();
//        mPlanList.clear();
//        mPlanList.addAll(newList);
//        mPlansAdapter.notifyDataSetChanged();
        mPlansAdapter.refresh();
        ActivityUtil.checkAndStartAnkiDroid(this);
    }

    private void initPlanList() {
        mPlanList = new ArrayList<>();
        //Log.d("PlansManager:", plans.size() + "ge");
        planListView = (RecyclerView) findViewById(R.id.plan_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        planListView.setLayoutManager(llm);
        mPlansAdapter = new PlansAdapter(PlansManagerActivity.this, mPlanList);
        //planList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        planListView.setAdapter(mPlansAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mPlansAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(planListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_plans_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_plan:
                DefaultPlanDialog dialog = new DefaultPlanDialog(PlansManagerActivity.this, mPlansAdapter);
                dialog.showSelectionDialog();
//                updatePlans();
                break;
            case R.id.menu_item_refresh_models:
                updatePlans();
                break;
//            case R.id.menu_item_script_field_editor:
//                startScriptFieldEditorActivity();
//                break;
            case R.id.menu_item_export_plan:
                exportPlans();
                break;
            case R.id.menu_item_import_plan:
                importPlans();
                break;
            case R.id.menu_item_delete_all_plans:
                deleteAllPlans(item);
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }

    private void startScriptFieldEditorActivity() {
        Intent intent = new Intent(this, PlanScriptForAppendingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Intent.ACTION_SEND);
        MyApplication.getContext().startActivity(intent);
    }

    private void deleteAllPlans(MenuItem menu) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(PlansManagerActivity.this);
        dlg.setTitle(R.string.confirm_deletion).
            //.setMessage("Do you really want to whatever?")
            setIcon(android.R.drawable.ic_dialog_alert).
            setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
//                    ExternalDatabase.getInstance().deleteAllPlansDB();
//                    mPlanList.clear();
//                    mPlansAdapter.notifyDataSetChanged();
                    mPlansAdapter.removeAll();
                }
            }).setNegativeButton(android.R.string.no, null);
        dlg.show();
    }

    private void importPlans() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(clipboard.hasPrimaryClip()){
            if(clipboard.getText()!=null){
                String plansString = clipboard.getText().toString();
                processPlanString(plansString);
            }
        }else{
            Toast.makeText(this, "剪贴板为空！", Toast.LENGTH_SHORT).show();
        }
    }

    private void processPlanString(String plansString) {
        String[] lines = plansString.split("\n");
        if(lines.length == 0){
            Toast.makeText(this, "格式错误！", Toast.LENGTH_SHORT).show();
            return ;
        }

        HashMap<Long, String> modelsMap = settings.getModelsHashMap();

        for(String line : lines){
            if(line.replace(" ","").replace("\t", "").equals("")){
                continue;//blank line
            }
            String[] items = line.split("\\|\\|\\|");
            if(items.length != 5){
                String errorMessage = line;
                errorMessage += "\n格式错误，每行项目数应为5";
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                continue;
            }
            try {
                String planName = items[0].trim();
                long deckId = Long.parseLong(items[1]);
                long modelId = Long.parseLong(items[2]);
                String modelName = mAnkiDroid.getApi().getModelName(modelId);
                if(!StringUtils.isEmpty(modelName)) {
                    if(modelsMap.containsKey(modelId))
                        modelsMap.replace(modelId, modelName);
                    else
                        modelsMap.put(modelId, modelName);
                }
                String dictKey = items[3].trim();
                String fieldMapString = items[4];
                for(OutputPlanPOJO outputPlan : mPlanList){
                    if(outputPlan.getPlanName().equals(planName)){
                        planName = planName + "_copy";
                        break;
                    }
                }
                OutputPlanPOJO outputPlan = new OutputPlanPOJO();
                outputPlan.setPlanName(planName);
                outputPlan.setOutputDeckId(deckId);
                outputPlan.setOutputModelId(modelId);
                outputPlan.setDictionaryKey(dictKey);
                outputPlan.setFieldsMap(Utils.fieldsStr2Map(fieldMapString));
                ExternalDatabase.getInstance().insertPlan(outputPlan);
            }
            catch (Exception e){
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        settings.setModelsHashMap(modelsMap);

        onResume();
    }

    private void exportPlans() {
        StringBuilder sb = new StringBuilder();
        for(OutputPlanPOJO plan : mPlanList){
            sb.append(plan.getPlanName());
            sb.append(PLAN_SEP);
            sb.append(plan.getOutputDeckId());
            sb.append(PLAN_SEP);
            sb.append(plan.getOutputModelId());
            sb.append(PLAN_SEP);
            sb.append(plan.getDictionaryKey());
            sb.append(PLAN_SEP);
            sb.append(plan.getFieldsMapString());
            sb.append("\n");
        }
        String exportedString = sb.toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("plans string", exportedString);
        clipboard.setPrimaryClip(clip);
    }

    public void updatePlans() {
        Map<Long, String> modelMap = mAnkiDroid.getApi().getModelList();
        HashMap<Long, String> oldModelMap = settings.getModelsHashMap();
        for (int index=0; index < mPlanList.size(); index++) {
            String name = oldModelMap.get(mPlanList.get(index).getOutputModelId());
            for(Long id : modelMap.keySet()) {
                if(modelMap.get(id).equals(name)) {
//                    oldModelMap.remove(mPlanList.get(index).getOutputModelId());
                    if(oldModelMap.containsKey(id))
                        oldModelMap.replace(id, name);
                    else
                        oldModelMap.put(id, name);
                    mPlanList.get(index).setOutputModelId(id);
                    ExternalDatabase.getInstance().updatePlan(mPlanList.get(index), mPlanList.get(index).getPlanName());
                    break;
                }
            }
        }
        settings.setModelsHashMap(oldModelMap);
//        mPlansAdapter.refresh();
    }

    private void initAnkiApi() {
        if (mAnkiDroid == null) {
            mAnkiDroid = new AnkiDroidHelper(this);
        }
        if (!AnkiDroidHelper.isApiAvailable(MyApplication.getContext())) {
            Toast.makeText(this, R.string.api_not_available_message, Toast.LENGTH_LONG).show();
        }

        if (mAnkiDroid.shouldRequestPermission()) {
            mAnkiDroid.requestPermission(this, 0);
        }
    }


    public void startAnkiDroid() {
        if (AnkiDroidHelper.isApiAvailable(MyApplication.getContext()) && !MyApplication.getAnkiDroid().isAnkiDroidRunning()) {
            MyApplication.getAnkiDroid().startAnkiDroid();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(SystemUtils.isBackActivity(PlansManagerActivity.this, Constant.ANKIHELPER_PACKAGE_NAME)) {
                                            if(mIntent != null)
                                                startActivity(mIntent);
                                            else {
                                                Intent intent = new Intent(getApplicationContext(), PlansManagerActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                            handler.removeCallbacks(this);
                                        }
                                        else {
                                            handler.postDelayed(this, 200);
                                        }
                                    }
                                },
                    200);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(requestCode == REQUEST_HANDLE_INTENT) {
//            if (data != null && data.hasExtra(Intent.EXTRA_INDEX)) {
//                mIntent = data;
//                MyApplication.getAnkiDroid().checkAndStartAnkiDroid();
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
