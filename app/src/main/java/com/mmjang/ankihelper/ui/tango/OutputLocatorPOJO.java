package com.mmjang.ankihelper.ui.tango;

import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by liao on 2017/4/20.
 */
public class OutputLocatorPOJO  implements Comparable<OutputLocatorPOJO> {
    private String dictName;
    private String langName;
    private int langIndex;
    private int orderIndex;
    private ArrayList<String> fieldsMapStrList;
    private String[] fieldArray;
    private boolean checked;
    private static final LinkedHashSet<String> fieldSet = new LinkedHashSet<>();
    private static final String LOCATOR_SEP = "|||";
    private static final String LOCATOR_FIELDLIST_SEP = "~~~";
    private static final String LOCATOR_FLAG = "locator";

    private static HashMap<String, OutputLocatorPOJO> outputlocatorMap = new HashMap<>();

    public static HashMap<String, OutputLocatorPOJO> getOutputlocatorMap() {
        return outputlocatorMap;
    }

    public static LinkedHashSet<String> getFieldSet() {
        return fieldSet;
    }

    public static void removeOutputlocatorMap(String key) {
        OutputLocatorPOJO.outputlocatorMap.remove(key);
        OutputLocatorPOJO.saveOutputLocatorPOJOListToSettings(new ArrayList<OutputLocatorPOJO>(outputlocatorMap.values()));
    }

    public OutputLocatorPOJO() {
        this("词典名称");
    }

    public OutputLocatorPOJO(String dictName) {
        this(dictName, DictLanguageType.getLanguageNameList().length-1);
    }

    public OutputLocatorPOJO(String dictName, int langIndex) {
        this(dictName, langIndex, true);
    }

    public OutputLocatorPOJO(String dictName, int langIndex, boolean checked) {
        this(dictName, langIndex, checked, Constant.getTangoDefaultFields());
    }


    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public OutputLocatorPOJO(String dictName, int langIndex, int orderIndex, boolean checked, ArrayList<String> fieldsMapStrList) {
        this(dictName, langIndex, orderIndex, checked, Constant.getTangoDefaultFields(), fieldsMapStrList);
    }

    public OutputLocatorPOJO(String dictName, int langIndex, boolean checked, String[] fieldArray) {
        this(dictName, langIndex, 0, checked, fieldArray, new ArrayList<>());
    }

    public OutputLocatorPOJO(String dictName, int langIndex, int orderIndex, boolean checked, String[] fieldArray, ArrayList<String> fieldsMapStrList) {
        this.dictName = dictName;
        this.langIndex = langIndex;
        this.orderIndex = orderIndex;
        this.checked = checked;
        this.langName = DictLanguageType.getLanguageNameList()[this.langIndex];
        this.fieldArray = fieldArray;
        this.fieldsMapStrList = fieldsMapStrList;
        OutputLocatorPOJO.fieldSet.addAll(Arrays.asList(this.fieldArray));
        OutputLocatorPOJO.outputlocatorMap.put(dictName, this);
//        Log.e("new locator infor", String.format("orderIndex: %d, dictName: %s checked: %s", this.orderIndex, this.dictName, Boolean.valueOf(this.checked)));
    }

    public String getDictName() {
        return dictName;
    }


    public void setDictName(String dictName) {
        if(!this.dictName.equals("") && !this.dictName.equals(dictName)) {
            OutputLocatorPOJO.removeOutputlocatorMap(this.dictName);
        }
        this.dictName = dictName;
        OutputLocatorPOJO.outputlocatorMap.put(this.dictName, this);
    }


    public int getLangIndex() {
        return langIndex;
    }

    public void setLangIndex(int langIndex) {
        this.langIndex = langIndex;
        this.langName = DictLanguageType.getLanguageNameList()[this.langIndex];
    }

    public String[] getFieldArray() {
        return this.fieldArray;
    }

    public String getlangName() {
        return langName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean addFieldsMapStrList(Map<String, String> fieldsMap) {
//        try {
        if (this.fieldsMapStrList == null) {
            this.fieldsMapStrList = new ArrayList<>();
        }
        this.fieldsMapStrList.add(Utils.fieldsMap2Str(fieldsMap));
        return true;
//        } catch (Exception e) {
//            Log.e("setFieldsMapStrList", e.getMessage());
//            return false;
//        }
    }

    public boolean setFieldsMapStrList(int index, Map<String, String> fieldsMap) {
        if(index >=0 && index <= this.fieldsMapStrList.size()-1)
            this.fieldsMapStrList.set(index, Utils.fieldsMap2Str(fieldsMap));
        else if(index == this.fieldsMapStrList.size())
            this.fieldsMapStrList.add(Utils.fieldsMap2Str(fieldsMap));
        else {
//            Log.e("setFieldsMapStrList", "index is wrong value");
            return false;
        }
        return true;
    }

    public void removeAllFieldsMapStrList() {
        this.fieldsMapStrList.clear();
    }

    public ArrayList<Map<String, String>> getFieldsMapList() {
        ArrayList<Map<String, String>> mapList = new ArrayList<>();
        for(String mapStr : this.fieldsMapStrList) {
            mapList.add(Utils.fieldsStr2Map(mapStr));
        }
        return mapList;
    }

    public Map<String, String> getFieldsMap(int index) {
        return Utils.fieldsStr2Map(fieldsMapStrList.get(index));
    }

    public List<String> getFieldsMapStrList(){
        return fieldsMapStrList;
    }

    public boolean removeFieldsMapStrList(){
        if(fieldsMapStrList.size() > 0) {
            return removeFieldsMapStrList(fieldsMapStrList.size()-1);
        } else {
            return false;
        }
    }

    public boolean removeFieldsMapStrList(int index){
        if(index >= 0) {
            fieldsMapStrList.remove(index);
            return true;
        } else {
            return false;
        }
    }

    public void save() {
        saveOutputLocatorPOJOListToSettings(new ArrayList<OutputLocatorPOJO>(outputlocatorMap.values()));
    }

    public static void update() {
        saveOutputLocatorPOJOListToSettings(new ArrayList<OutputLocatorPOJO>(outputlocatorMap.values()));
    }
    //
    public static boolean saveOutputLocatorPOJOListToSettings(List<OutputLocatorPOJO> locatorList) {
        try {
            Settings settings = Settings.getInstance(MyApplication.getContext());
            String locatorsString = processLocatorListIntoString(locatorList);
            settings.setLocatorsString(locatorsString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String processLocatorListIntoString(List<OutputLocatorPOJO> locatorList) {
        Collections.sort(locatorList);

        StringBuilder sb = new StringBuilder();
        for (OutputLocatorPOJO locator : locatorList) {
            sb.append(LOCATOR_FLAG);
            sb.append(LOCATOR_SEP);
            sb.append(locator.getDictName());
            sb.append(LOCATOR_SEP);
            sb.append(locator.getLangIndex());
            sb.append(LOCATOR_SEP);
            sb.append(locatorList.indexOf(locator));
            sb.append(LOCATOR_SEP);
            sb.append(locator.isChecked());
            sb.append(LOCATOR_SEP);
            for(String fieldsMap : locator.getFieldsMapStrList()) {
                sb.append(fieldsMap);
                sb.append(LOCATOR_FIELDLIST_SEP);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static List<OutputLocatorPOJO> readOutputLocatorPOJOListFromSettings() {
        List<OutputLocatorPOJO> locatorList = new ArrayList<>();
        Settings settings = Settings.getInstance(MyApplication.getContext());
        String locatorsString = settings.getLocatorsString();
        if(locatorsString.trim().equals("")) {
//            Log.e("locatorsString", locatorsString);
            return locatorList;
        }
        else {
//            Log.e("locatorsString else", locatorsString);
            return processStringIntoLocatorList(locatorsString, locatorList);
        }
    }


    public static List<OutputLocatorPOJO> processStringIntoLocatorList(String locatorsString, List<OutputLocatorPOJO> locatorList) {
//        Collections.sort(locatorList);

        String[] lines = locatorsString.split("\n");
        if(lines.length == 0){
            Toast.makeText(MyApplication.getContext(), "格式错误！", Toast.LENGTH_SHORT).show();
            return null;
        }

        for(String line : lines){
            if(line.replace(" ","").replace("\t", "").equals("")){
                continue;//blank line
            }
            String[] items = line.split("\\|\\|\\|");
            if(!items[0].equals(LOCATOR_FLAG)) {
                String errorMessage = "格式错误，非定位器格式";
                Toast.makeText(MyApplication.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                return null;
            }
            if(items.length != 6){
                String errorMessage = line + items.length;
                errorMessage += "\n格式错误，每行项目数应为6";
                Toast.makeText(MyApplication.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                continue;
            }
            try {
                String dictName = items[1].trim();
                int langIndex = Integer.valueOf(items[2].trim());
                int orderIndex = locatorList.size();//Integer.valueOf(items[3].trim());
                boolean checked  = Boolean.parseBoolean(items[4].trim());
                ArrayList<String> fieldsMapStrList = new ArrayList<>(Arrays.asList(items[5].split(LOCATOR_FIELDLIST_SEP)));
                for(OutputLocatorPOJO outputLocator : locatorList) {
                    if(outputLocator.getDictName().equals(dictName)) {
                        dictName = dictName + "_copy";
                        break;
                    }
                }
                OutputLocatorPOJO outputLocator = new OutputLocatorPOJO(dictName, langIndex, orderIndex, checked, fieldsMapStrList);
                locatorList.add(outputLocator);
            }
            catch (Exception e){
                Toast.makeText(MyApplication.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        Collections.sort(locatorList);
        return locatorList;
    }

    @Override
    public int compareTo(OutputLocatorPOJO o) {
        return this.getOrderIndex() - o.getOrderIndex();
    }

//    public boolean setFieldSelectorList(ArrayList<Map<String, String>> fieldSelectorList) {
//        if(fieldSelectorList == null) {
//            return false;
//        }
//        this.fieldSelectorList = fieldSelectorList;
//        return true;
//    }
}