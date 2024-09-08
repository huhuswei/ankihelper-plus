package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.translation.TranslateBuilder;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.Utils;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by liao on 2017/3/15.
 */

public class Ode2 extends SQLiteAssetHelper implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    //private static final String DATABASE_NAME = "collins_v2.db";
    private static final String DATABASE_NAME = "ode2_v2.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_DICT = "dict";
    private static final String FIELD_HWD = "hwd";
    private static final String FIELD_DISPLAYED_HWD = "display_hwd";
    private static final String FIELD_PHRASE = "phrase";
    private static final String FIELD_PHONETICS = "phonetics";
    private static final String FIELD_SENSE = "sense";
    private static final String FIELD_EXT = "ext";
    private static final String FIELD_DEF_EN = "def_en";
    private static final String FIELD_DEF_CN = "def_cn";

    private static final String DICT_NAME = "ODE2";
    private static final String INTRODUCTION = "新牛津英汉双解大词典；收词量大，释义全且偏难;“复合项”指单词、音标、释义的组合";

    private SQLiteDatabase db;
    private Context mContext;


    public int getLanguageType() {
        return lt;
    }

    public Ode2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
        mContext = context;
    }
    private static String mAudioUrl = "";
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
    private void emptyAudioUrl() { mAudioUrl = ""; }
    public boolean isExistAudioUrl() {
        return false;
    }


    private static final String[] EXP_ELE_LIST = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION,
            Constant.DICT_FILED_SENSE
    };

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return INTRODUCTION;
    }

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    public List<Definition> wordLookup(String key) {
        //db = getReadableDatabase(); // according to stackoverflow, it's alright to let the database open
        key = keyCleanup(key);
        List<Definition> re = queryDefinition(key);
        Log.d("", "单词需要查找变形表");
        String[] deflectResult = getForms(key);
        for (String s : deflectResult) {
            Log.d("", "已变形单词" + s);
        }
        if (deflectResult.length == 0) {
            //
        } else {
            for (String deflectedWord : deflectResult) {
                re.addAll(queryDefinition(deflectedWord));
            }
        }

        if(re.isEmpty()){
            try{
                re.add(toDefinition(YoudaoOnline.getYoudaoResult(key)));
            }
            catch (IOException e){
                Looper.prepare();
                Toast.makeText(mContext, "本地词典未查到，有道词典在线查询失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                Looper.loop();            }
        }
        // db.close();
        return re;
    }

    /**
     * @param context this
     * @param layout  support_simple_spinner_dropdown_item
     * @return
     */
    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(context, layout,
                        null,
                        new String[]{FIELD_HWD},
                        new int[]{android.R.id.text1},
                        0
                );
        adapter.setFilterQueryProvider(
                new FilterQueryProvider() {
                    @Override
                    public Cursor runQuery(CharSequence constraint) {
                        if(constraint != null)
                            return getFilterCursor(constraint.toString());
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

    /**
     * @param q word to lookup
     * @return a array of definitions, retrun ArrayList<>() if none was found
     */
    private ArrayList<Definition> queryDefinition(String q) {
        //SQLiteDatabase db = getReadableDatabase();
        ArrayList<Definition> re = new ArrayList<>();
        if(q.isEmpty()){
            return re;
        }
        Cursor cursor = db.query(TABLE_DICT,
                new String[]{FIELD_HWD, FIELD_DISPLAYED_HWD, FIELD_PHRASE,
                        FIELD_PHONETICS, FIELD_SENSE, FIELD_EXT, FIELD_DEF_EN, FIELD_DEF_CN},
                FIELD_HWD + "=? COLLATE NOCASE", new String[]{q}, null, null, null);
        while (cursor.moveToNext()) {
            Definition def = getDefFromCursor(cursor);
            re.add(def);
        }
        return re;
    }

    private Definition getDefFromCursor(Cursor cursor) {
        LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
        String hwd = cursor.getString(0);
        // df.setDisplayedHeadWord(cursor.getString(1).trim());
        String phrase = cursor.getString(2).trim();
        String phonetics = cursor.getString(3).trim();
        String sense = RegexUtil.colorizeSense(cursor.getString(4).trim()) + cursor.getString(5).trim();
//        String ext = cursor.getString(5).trim();
        String defEn = cursor.getString(6).trim();
        String defCn = cursor.getString(7).trim();

        //如果不是词组
        if (phrase.equals("")) {
            eleMap.put(Constant.DICT_FIELD_KEYWORD, hwd);
        } else {
            eleMap.put(Constant.DICT_FIELD_KEYWORD, phrase);
        }
        String definition = defEn + "<br/>" + defCn;
//        String complex = FieldUtil.formatComplexTplWord(DICT_NAME, eleMap.get(EXP_ELE_LIST[0]), phonetics, sense, definition, Constant.AUDIO_INDICATOR_MP3);
//        String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, eleMap.get(EXP_ELE_LIST[0]), phonetics, sense, definition, "");
        eleMap.put(Constant.DICT_FIELD_PHONETICS, phonetics);
        eleMap.put(Constant.DICT_FILED_SENSE, sense);
        eleMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

        String displayHtml;
        if (phrase.equals("")) {
            StringBuilder sb = new StringBuilder();
            sb.append("<b>" + hwd + "</b> ");
//                sb.append((!phonetics.equals("")?phonetics+"<br/>":""));
            sb.append(phonetics);
            sb.append("<br/>");
            if (defEn.startsWith("■") || defEn.startsWith("●")) {
                // don't add sense
            } else {
                sb.append((!sense.equals("")?sense+"<br/>":""));
                //sb.append(" ");
                //sb.append(def.Ext);
                //sb.append("<br/>");
            }

            sb.append(defEn + " " + defCn);
            displayHtml = sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            //sb.append(def.Phonetics);
            //sb.append(" ");
            //sb.append(def.Sense);
            //sb.append(" ");
            //sb.append(def.Ext);
            sb.append("<b><i>" + phrase + "</i></b>");
            sb.append("<br/>");
            sb.append(defEn + " " + defCn);
            Log.d("phrase", sb.toString());
            displayHtml = sb.toString();
        }
        String audioFileName = Utils.getSpecificFileName(hwd);
        Definition.ResInformation resInfor = new Definition.ResInformation(
                "", audioFileName, Constant.MP3_SUFFIX
        );
        return new Definition(eleMap, displayHtml, resInfor);
    }

    private String[] getForms(String q) {
        //SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("forms", new String[]{"bases"}, "hwd=? ", new String[]{q.toLowerCase()}, null, null, null);
        String bases = "";
        while (cursor.moveToNext()) {
            bases = cursor.getString(0);
        }
        return bases.split("@@@");
    }

    private Cursor getFilterCursor(String q) {
        Log.d("databse", "getFilterCursor" + q);
        Cursor cursor = db.query("hwds", new String[]{"rowid _id", "hwd"}, "hwd LIKE ?", new String[]{q + "%"}, null, null, null);
        return cursor;
    }

    /**
     * 去除左右两边空格，标点
     *
     * @param key
     * @return
     */
    private String keyCleanup(String key) {
        return key.trim().replaceAll("[,.!?()\"'“”’？]", "").toLowerCase();
    }

    private Definition toDefinition(YoudaoResult youdaoResult){
        String notiString = "<font color='gray'>本地词典未查到，以下是有道在线释义</font><br/>";
        String definition = "<b>" + youdaoResult.returnPhrase + "</b><br/>";

        if(!youdaoResult.translation.isEmpty())
            for (String def : youdaoResult.translation) {
                definition += def + "<br/>";
            }
        else
            definition += new TranslateBuilder(
                    Settings.getInstance(MyApplication.getContext()).
                            getTranslatorCheckedIndex()).translate(youdaoResult.returnPhrase) + "<br/>";


        definition += "<font color='gray'>网络释义</font><br/>";
        for(String key : youdaoResult.webTranslation.keySet()){
            String joined = "";
            for(String value : youdaoResult.webTranslation.get(key)){
                joined += value + "; ";
            }
            definition += "<b>" + key.trim() + "</b>: " + joined.trim() + "<br/>";
        }
//        String complex = FieldUtil.formatComplexTplWord(DICT_NAME, youdaoResult.returnPhrase, youdaoResult.phonetic, definition, Constant.AUDIO_INDICATOR_MP3);
//        String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, youdaoResult.returnPhrase, youdaoResult.phonetic, definition, "");
        LinkedHashMap<String, String> exp = new LinkedHashMap<>();
        exp.put(Constant.DICT_FIELD_KEYWORD, youdaoResult.returnPhrase);
        exp.put(Constant.DICT_FIELD_PHONETICS, youdaoResult.phonetic);
        exp.put(Constant.DICT_FILED_SENSE, "");
        exp.put(Constant.DICT_FIELD_DEFINITION, definition);
//        exp.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//        exp.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//        exp.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

        String audioFileName = Utils.getSpecificFileName(youdaoResult.returnPhrase);
        Definition.ResInformation resInfor = new Definition.ResInformation(
                "", audioFileName, Constant.MP3_SUFFIX
        );
        return new Definition(exp, notiString + definition, resInfor);
    }

}
