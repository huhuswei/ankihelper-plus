package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.dict.JPDeinflector.Deinflection;
import com.mmjang.ankihelper.data.dict.JPDeinflector.Deinflector;
import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryInformation;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.FormUtils;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.Utils;
import com.mmjang.ankihelper.util.WanaKanaJava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by liao on 2017/8/11.
 */

public class CustomDictionary implements IDictionary {

    private int lt;

    private Context mContext;
    private ExternalDatabase mDatabase;
    private int mDictId;
    private static WanaKanaJava mWanaKanaJava;
    public CustomDictionaryInformation mDictInformation;

    private String[] mFields;
    public CustomDictionary(Context context, ExternalDatabase db, int dictId){
        mContext = context;
        mDatabase = db;
        mDictId = dictId;
        mDictInformation = mDatabase.getDictInfo(dictId);
        mFields = Utils.concatenate(new String[]{Constant.DICT_FIELD_KEYWORD}, mDictInformation.getFields());
        lt = DictLanguageType.getLTIdByLangISO2(mDictInformation.getDictLang());

    }
    private String mAudioUrl = "";
    public String getAudioUrl() {
        return mAudioUrl;
    }
    public boolean setAudioUrl(String audioUrl) {
        try {
            mAudioUrl = audioUrl;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isExistAudioUrl() {
        return false;
    }


    public int getId(){
        return mDictId;
    }

    @Override
    public String getDictionaryName() {
        return mDictInformation.getDictName();
    }

    @Override
    public int getLanguageType() {
        return lt;
    }

    @Override
    public String getIntroduction() {
        return mDictInformation.getDictIntro();
    }

    @Override
    public String[] getExportElementsList() {
        return mFields;
    }

    @Override
    public List<Definition> wordLookup(String key) {
        key = Utils.keyCleanup(key);
        List<Definition> re = queryDefinition(key);
        try {
            switch (mDictInformation.getDictLang()) {
                case "jp":
                    // 用mdict词典查词性形态变化
                    ArrayList<String> deflectResult = FormUtils.getJForms(key);
                    if (deflectResult.size() > 0) {
                        for (String deflectedWrod : deflectResult) {
                            re.addAll(queryDefinition(deflectedWrod));
                        }
                    }
                    //db查词性形态变化，db太大，放弃
//                        String[] deflectResult = FormsUtil.getInstance(mContext, DictLanguageType.JPN).getForms(key);
//                        if (deflectResult.length > 0) {
//                            for (String deflectedWrod : deflectResult) {
//                                re.addAll(queryDefinition(deflectedWrod));
//                            }
//                        }
                    if(re.isEmpty()) {
                        if(mWanaKanaJava == null){//lazy init of wanakana
                            mWanaKanaJava = new WanaKanaJava(false);
                        }

                        if(mWanaKanaJava.isKatakana(key) || RegexUtil.isEnglish(key)){
                            key = mWanaKanaJava.toHiragana(key);
                        }
                        re.addAll(queryDefinition(key));
                        for(Deinflection df : Deinflector.deinflect(key)){
                            String base = df.getBaseForm();
                            List<Definition> defs = queryDefinition(base);
                            re.addAll(defs);
                        }
                    }
                    break;
                case "en":
                    String[] deflectResultEn = FormsUtil.getInstance(mContext).getForms(key);
                    if (deflectResultEn.length > 0) {
                        for (String deflectedWrod : deflectResultEn) {
                            re.addAll(queryDefinition(deflectedWrod));
                        }
                    }
                    break;
            }

//        if(mDictInformation.getDictLang().equals("en")) {
//            String[] deflectResult = FormsUtil.getInstance(mContext).getForms(key);
//            if (deflectResult.length >= 0) {
//                for (String deflectedWrod : deflectResult) {
//                    re.addAll(queryDefinition(deflectedWrod));
//                }
//            }
//        }
            if(re.isEmpty()) {
                re.addAll(queryDefinition(key));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return re;
    }

    @Override
    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(context, layout, null,
                        new String[] {ExternalDatabase.getHeadwordColumnName()},
                        new int[] {android.R.id.text1},
                        0
                        );
        adapter.setFilterQueryProvider(
                new FilterQueryProvider() {
                    @Override
                    public Cursor runQuery(CharSequence constraint) {
                        if(constraint != null)
                            return mDatabase.getFilterCursor(mDictId, constraint.toString());
                        return null;
                    }
                }
        );

        adapter.setCursorToStringConverter(
                new SimpleCursorAdapter.CursorToStringConverter() {
                    @Override
                    public CharSequence convertToString(Cursor cursor) {
                        return cursor.getString(1);
                    }
                }
        );

        return adapter;
    }

    private List<Definition> queryDefinition(String q){
        ArrayList<Definition> re = new ArrayList<>();
        List<String[]> results =  mDatabase.queryHeadword(mDictId, q);
        for(String[] result : results){
            String[] values = Utils.concatenate(new String[]{q}, result);
            re.add(fromResultsToDefinition(mFields, values));
        }
        return re;
    }

    private Definition fromResultsToDefinition(String[] fields, String[] values){
        LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
//        LinkedHashMap<String, String> complexMap = new LinkedHashMap<>();
        for(int i = 0; i < fields.length; i ++){
            eleMap.put(fields[i], values[i]);
//            if(i > 0)
//                complexMap.put(fields[i], result[i]);
        }
        String displayedHtml = "";
//        String complextHtml = "";
        String tmpl = mDictInformation.getDefTpml();
        if(tmpl.isEmpty()){   //no tmpl just join fields
            StringBuilder sb = new StringBuilder();
            for(String s : values){
                sb.append(s);
            }
            displayedHtml = sb.toString();
        }else{
            displayedHtml = Utils.renderTmpl(tmpl, eleMap);
        }
//        complextHtml = Utils.renderNoTmpl(complexMap);
//        String complex = FieldUtil.formatComplexTplWord(getDictionaryName(), result[0], "", complextHtml, Constant.AUDIO_INDICATOR_MP3);
//        String muteComplex = FieldUtil.formatComplexTplWord(getDictionaryName(), result[0], "", complextHtml, "");
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);
        String audioFileName = Utils.getSpecificFileName(values[0]);
        Definition.ResInformation resInfor = new Definition.ResInformation(
                "", audioFileName, Constant.MP3_SUFFIX
        );
        return new Definition(eleMap, displayedHtml, resInfor);
    }


}
