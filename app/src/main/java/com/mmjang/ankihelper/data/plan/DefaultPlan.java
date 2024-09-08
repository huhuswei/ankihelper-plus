package com.mmjang.ankihelper.data.plan;

import static com.mmjang.ankihelper.util.Constant.JOINT;
import static com.mmjang.ankihelper.util.Constant.TAB;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import com.ichi2.anki.FlashCardsContract;
import com.ichi2.anki.api.AddContentApi;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.dict.DictionaryRegister;
import com.mmjang.ankihelper.data.dict.IDictionary;
import com.mmjang.ankihelper.data.dict.Mdict;
import com.mmjang.ankihelper.util.BuildConfig;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.ToastUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liao on 2017/7/23.
 */

public class DefaultPlan {
    private static final String DEFAULT_DECK_NAME = "Anki 划词助手+ 默认牌组";
    private final String CODING = "UTF-8";
    private final ContentResolver mResolver;

    AnkiDroidHelper mAnkidroid;
    Long defaultDeckId; //, defaultModelId;
    List<CardModel> modelList;
    List<String> planStrList;
    Map<String, String[]> planNamesMap;
    Context mContext;

//    long getDefaultModelId(){
//        return defaultModelId;
//    }

    long getDefaultDeckId(){
        return defaultDeckId;
    }

    public List<CardModel> getModelList() {
        return modelList;
    }

    public List<String> getPlanStrList() {
        return planStrList;
    }

    public Map<String, String[]> getPlanNamesMap() {
        return planNamesMap;
    }

    public  DefaultPlan(Context context){
        mAnkidroid = MyApplication.getAnkiDroid();
        mContext = context;
        mResolver = mContext.getContentResolver();
        modelList = new ArrayList<>();
        planStrList = new ArrayList<>();
        planNamesMap = new HashMap<>();

        init();
    }

    private void init() {
        try {
            initDefaultDeckId();
            String[] files = mContext.getAssets().list("models");
            for (String modelName : files) {
                InputStream inputStream = mContext.getAssets().open("models" + "/" + modelName + "/" + "template.html");
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                String text = new String(buffer, CODING);
                CardModel model = CardModel.buildCardModel(modelName, text);
                if(model != null) {
                    Long modelId  = mAnkidroid.findModelIdByName(model.getName(), model.getFields().length);
                    if (modelId == null) {
                        modelId = mAnkidroid.getApi().addNewCustomModel(
                                model.getName(),
                                model.getFields(),
                                model.getCards(),
                                Arrays.stream(model.getLayouts()).map(a -> a.getFront()).toArray(String[]::new),
                                Arrays.stream(model.getLayouts()).map(a -> a.getBack()).toArray(String[]::new),
                                model.getCss(),
                                null,
                                null
                        );
                    }
                    modelList.add(model);
                    //read plan
                    inputStream = mContext.getAssets().open("models" + "/" + modelName + "/" + "plan.txt");
                    size = inputStream.available();
                    buffer = new byte[size];
                    inputStream.read(buffer);
                    inputStream.close();
                    text = new String(buffer, CODING);
                    //default
                    String[] plans = getDefaultPlanNameArr(text);
                    //custom
                    plans = Utils.concatenate(plans, getCustomPlanNameArr());

                    if(plans.length > 0) {
                        planNamesMap.put(model.getName(), plans);
                        planStrList.add(text);
                    }

                    //默认model
//                    if(model.getName().equals(DEFAULT_VOCABULARY_MODEL_NAME)) {
//                        defaultModelId = modelId;
//                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int insertPlans(List<CardModel> models, List<String> planStrList, SparseBooleanArray isCheckedArr) {
        List<OutputPlanPOJO> existedPlans = ExternalDatabase.getInstance().getAllPlan();
        AddContentApi api = MyApplication.getAnkiDroid().getApi();
        int count = 0;
        int totalInsered = 0;
        String planExisted = "";
        for(int k=0; k < planStrList.size(); k++) {
            String[] lines = planStrList.get(k).split("\n");
            if (lines.length == 0) {
                Toast.makeText(mContext, "格式错误！", Toast.LENGTH_SHORT).show();
                return 0;
            }

            long deckId = getDefaultDeckId();
            long modelId = getModelId(models.get(k));
            for (int i = 0; i < lines.length; i++) {
                if (!isCheckedArr.get(i+count))
                    continue;
                if (lines[i].replace(" ", "").replace("\t", "").equals("")) {
                    continue;//blank line
                }

                String[] items = lines[i].split("\\|\\|\\|");
                if (items.length != 5) {
                    String errorMessage = lines[i];
                    errorMessage += "\n格式错误，每行项目数应为5";
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                    continue;
                }
                String planName = items[0].trim();

                //--debug
                if (!BuildConfig.isDebug && planName.contains("Wordbean"))
                    continue;
                //--!

                boolean existed = false;
                for (OutputPlanPOJO plan : existedPlans)
                    if (planName.equals(plan.getPlanName()))
                        existed = true;
                if (!existed) {
                    String dictKey = items[3].trim();
                    String fieldMapString = items[4];

                    OutputPlanPOJO outputPlan = new OutputPlanPOJO();
                    outputPlan.setPlanName(planName);
                    outputPlan.setOutputDeckId(deckId);
                    outputPlan.setOutputModelId(modelId);
                    outputPlan.setDictionaryKey(dictKey);
                    outputPlan.setFieldsMap(Utils.fieldsStr2Map(fieldMapString));
                    ExternalDatabase.getInstance().insertPlan(outputPlan);
                    totalInsered++;
                } else {
                    planExisted += planName + "\n";
                }
            }
            count += lines.length;
            //add custom plan
            List<IDictionary> list = DictionaryRegister.getCustomDictionaryObjectList();
            String[] elements = new String[]{
                    Constant.DICT_FIELD_EMPTY + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_SENTENCE_PICKED_BOLD + JOINT + Constant.DICT_FIELD_TRANSLATION + JOINT + Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_EMPTY + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_KEYWORD + JOINT + Constant.DICT_FIELD_KEY_DAV_OFFLINE_LINK + JOINT + Constant.DICT_FIELD_DEFINITION + TAB + Constant.DICT_FIELD_KEYWORD + JOINT + Constant.DICT_FIELD_KEY_DAV_OFFLINE_LINK + JOINT + Constant.DICT_FIELD_DEFINITION,
                    Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG + TAB + Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG,
                    Constant.DICT_FIELD_URL + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_EMPTY + TAB + Constant.DICT_FIELD_EMPTY,
            };

            String[] mdxElements = new String[]{
                    Constant.DICT_FIELD_EMPTY + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_SENTENCE_PICKED_BOLD + JOINT + Constant.DICT_FIELD_TRANSLATION + JOINT + Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_EMPTY + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_KEY_DAV_OFFLINE_LINK + JOINT + Constant.DICT_FIELD_KEYWORD + JOINT + Constant.DICT_FILED_SENSE + JOINT + Constant.MDX_ADD_TAG + Constant.DICT_FIELD_DEFINITION + TAB + Constant.DICT_FIELD_KEY_DAV_OFFLINE_LINK + JOINT + Constant.DICT_FIELD_KEYWORD + JOINT + Constant.DICT_FILED_SENSE + JOINT + Constant.MDX_ADD_TAG + Constant.DICT_FIELD_DEFINITION,
                    Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG + TAB + Constant.DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG,
                    Constant.DICT_FIELD_URL + TAB + Constant.DICT_FIELD_EMPTY,
                    Constant.DICT_FIELD_EMPTY + TAB + Constant.DICT_FIELD_EMPTY,
            };
            String[] FILEDS = {
//                    "Note ID",
//                    "摘取例句（加粗）",
//                    "句子翻译",
//                    "例句",
//                    "笔记",
//                    "释义",
//                    "单词",
//                    "音标",
//                    "自动发音",
//                    "手动发音",
//                    "发音链接",
//                    "来源"
                    "Note ID",
                    "摘取例句加粗",
                    "例句",
                    "释义",
                    "自动发音",
                    "手动发音",
                    "url",
                    "Remarks"
            };
            LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
            LinkedHashMap<String, String> mdxFieldMap = new LinkedHashMap<>();
            for (int j = 0; j < FILEDS.length; j++) {
                fieldMap.put(FILEDS[j], elements[j]);
                mdxFieldMap.put(FILEDS[j], mdxElements[j]);
            }
            for(int i=0; i<this.getCustomPlanNameArr().length; i++) {
                if(!isCheckedArr.get(i+count))
                    continue;

                IDictionary dict = list.get(i);
                boolean existed = false;
                for (OutputPlanPOJO plan : existedPlans)
                    if(dict.getDictionaryName().equals(plan.getPlanName()))
                        existed = true;
                if (!existed) {
                    OutputPlanPOJO defaultPlan = new OutputPlanPOJO();
                    defaultPlan.setPlanName(dict.getDictionaryName());
                    defaultPlan.setOutputDeckId(deckId);
                    defaultPlan.setOutputModelId(modelId);
                    Trace.i("Model_default_currentDeckId", Long.toString(deckId));
                    Trace.i("Model_default_currentModelId", Long.toString(modelId));
//                    defaultPlan.setOutputModelId(getModelId(models.get(k)));
//                    defaultPlan.setOutputDeckId(getDefaultDeckId());
                    defaultPlan.setDictionaryKey(dict.getDictionaryName());
                    if (dict instanceof Mdict) {
                        defaultPlan.setFieldsMap(mdxFieldMap);
                    } else {
                        defaultPlan.setFieldsMap(fieldMap);
                    }
                    ExternalDatabase.getInstance().insertPlan(defaultPlan);
                    totalInsered++;
                } else {
                    planExisted += dict.getDictionaryName() + "\n";
                }
            }

            count += getCustomPlanNameArr().length;
        }
        if(!planExisted.equals("")) {
            Toast.makeText(mContext, planExisted + mContext.getString(R.string.plan_already_exists), Toast.LENGTH_SHORT).show();
        }
        return totalInsered;
    }

    private String[] getDefaultPlanNameArr(String plansString) {
        if (plansString.isEmpty()) {
            return new String[]{};
        }
        String[] lines = plansString.split("\n");
        String[] planArr = new String[lines.length];
        if(lines.length == 0){
            Toast.makeText(mContext, "格式错误！", Toast.LENGTH_SHORT).show();
            return new String[]{};
        }

        for(int i=0; i < lines.length; i++){
            if(lines[i].replace(" ","").replace("\t", "").equals("")){
                continue;//blank line
            }
            String[] items = lines[i].split("\\|\\|\\|");
            if(items.length != 5){
                String errorMessage = String.format("第 %f 行：%f\n格式错误，每行项目数应为5", i, lines[i]);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
//                continue;
                return new String[]{};
            }
            planArr[i] = items[0].trim();
        }
        return planArr;
    }

    private String[] getCustomPlanNameArr() {
        List<IDictionary> customDictList = DictionaryRegister.getCustomDictionaryObjectList();
        return customDictList.stream().map(IDictionary::getDictionaryName).toArray(String[]::new);
    }


    public Long getModelId(CardModel model) {
        Map<Long, String> modelMap = mAnkidroid.getApi().getModelList();
        HashMap<Long, String> oldModelMap = Settings.getInstance(MyApplication.getContext()).getModelsHashMap();
        String name = model.getName();
        Long modelId = null;
        for(Long id : modelMap.keySet()) {
            if(modelMap.get(id).equals(name)) {
                if(oldModelMap.containsKey(id))
                    oldModelMap.replace(id, name);
                else
                    oldModelMap.put(id, name);
                modelId = id;
                break;
            }
        }

        if (modelId == null) {
            modelId = mAnkidroid.getApi().addNewCustomModel(
                    model.getName(),
                    model.getFields(),
                    model.getCards(),
                    Arrays.stream(model.getLayouts()).map(a -> a.getFront()).toArray(String[]::new),
                    Arrays.stream(model.getLayouts()).map(a -> a.getBack()).toArray(String[]::new),
                    model.getCss(),
                    null,
                    null
            );
            oldModelMap.put(modelId, model.getName());
        }

        Settings.getInstance(MyApplication.getContext()).setModelsHashMap(oldModelMap);

        return modelId;
    }

//    public Long getModelId(CardModel model) {
//        Long modelId  = mAnkidroid.findModelIdByName(model.getName(), model.getFields().length);
//        if (modelId == null) {
//            modelId = mAnkidroid.getApi().addNewCustomModel(
//                    model.getName(),
//                    model.getFields(),
//                    model.getCards(),
//                    Arrays.stream(model.getLayouts()).map(a -> a.getFront()).toArray(String[]::new),
//                    Arrays.stream(model.getLayouts()).map(a -> a.getBack()).toArray(String[]::new),
//                    model.getCss(),
//                    null,
//                    null
//            );
//        }
//
//        //保存model映射 id->name
//        Settings settings = Settings.getInstance(MyApplication.getContext());
//        HashMap<Long, String> modelsMap = settings.getModelsHashMap();
//        if(!StringUtils.isEmpty(model.getName()) && StringUtils.isEmpty(modelsMap.get(modelId))) {
//            modelsMap.put(modelId, model.getName());
//        }
//        settings.setModelsHashMap(modelsMap);
//
//        return modelId;
//    }

    void initDefaultDeckId() {
        Map<Long, String> deckList = mAnkidroid.getApi().getDeckList();
        for(Long id : deckList.keySet()){
            if(deckList.get(id).equals(DEFAULT_DECK_NAME)){
                defaultDeckId = id;
                return;
            }
        }
        defaultDeckId = mAnkidroid.getApi().addNewDeck(DEFAULT_DECK_NAME);
    }


    public void updateDefaultCardModel() {
        for(CardModel model : getModelList()) {
            if (model != null) {
                Long modelId = mAnkidroid.findModelIdByName(model.getName(), model.getFields().length);
                if (modelId != null) {
                    updateModel(
                            model.getName(),
                            model.getFields(),
                            model.getCards(),
                            Arrays.stream(model.getLayouts()).map(a -> a.getFront()).toArray(String[]::new),
                            Arrays.stream(model.getLayouts()).map(a -> a.getBack()).toArray(String[]::new),
                            model.getCss()
                    );
                }
//                if (model.getName().equals(DEFAULT_VOCABULARY_MODEL_NAME)) {
//                    defaultModelId = modelId;
//                }
            }
        }
        Toast.makeText(mContext, "模板更新成功！", Toast.LENGTH_SHORT).show();
    }

    public long updateModel(String name, String[] fields, String[] cards, String[] qfmt, String[] afmt, String css) {
        // Get modelId
        Long modelId = mAnkidroid.findModelIdByName(name, fields.length);
        Uri modelUri = Uri.withAppendedPath(FlashCardsContract.Model.CONTENT_URI, Long.toString(modelId));
        if (modelUri != null) {
            ContentValues values = new ContentValues();
            values.put(FlashCardsContract.Model.CSS, css);
            mResolver.update(modelUri, values, null, null);
            // Set the remaining template parameters
            Uri templatesUri = Uri.withAppendedPath(modelUri, "templates");
                for (int i = 0; i < cards.length; i++) {
                        Uri uri = Uri.withAppendedPath(templatesUri, Integer.toString(i));
                        values = new ContentValues();
                        values.put(FlashCardsContract.CardTemplate.NAME, cards[i]);
                        values.put(FlashCardsContract.CardTemplate.QUESTION_FORMAT, qfmt[i]);
                        values.put(FlashCardsContract.CardTemplate.ANSWER_FORMAT, afmt[i]);
                    try {
                        mResolver.update(uri, values, null, null);
    //                mResolver.update(uri, values, null, null);
                    }catch (Exception e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            mResolver.insert(uri, values, null);
                        } else {
                            ToastUtil.show(mContext, String.format("%s: Model is malformed.", name, cards[i]));
                            return -1;
                        }
                    }
                }
        }
        return modelId;
    }
}
