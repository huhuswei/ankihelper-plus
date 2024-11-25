package com.mmjang.ankihelper.data.dict;

//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Utils;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by liao on 2017/3/15.
 */

public class Wordbean extends SQLiteAssetHelper implements IDictionary {
    //private static final String DATABASE_NAME = ".db";
    private static final String DATABASE_NAME = "wordbean.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_DICT = "wordbean";

    private static final String FIELD_WORD = "word";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_TYPE1 = "type1";
    private static final String FIELD_TYPE2 = "type2";
    private static final String FIELD_TYPE3 = "type3";
    private static final String FIELD_TYPE4 = "type4";
    private static final String FIELD_WORDLEVEL = "wordlevel";
    private static final String FIELD_ROOTVARIANT = "rootvariant";
    private static final String FIELD_MEANING = "meaning";
    private static final String FIELD_SOUNDMARK = "soundmark";
    private static final String FIELD_STRUCTURE = "structure";
    private static final String FIELD_EXPLAIN = "explain";
    private static final String FIELD_TRANSLATION = "translation";
    private static final String FIELD_EXAMPLE = "example";
    private static final String FIELD_EXAMPLETRANSLATION = "exampletranslation";

    private static final String DICT_NAME = "Wordbean词典";
    private final int lt = DictLanguageType.ENG;

    private SQLiteDatabase db;

    private Context mContext;

    private String mSrcWord;

    public int getLanguageType() {
        return lt;
    }

    public Wordbean(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
        mContext = context;
        mSrcWord = "";
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

    private void emptyAudioUrl() {
        mAudioUrl = "";
    }

    public boolean isExistAudioUrl() {
        return false;
    }


    private static final String[] EXP_ELE_LIST = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION,
            Constant.DICT_FIELD_SENTENCE,
            Constant.DICT_FIELD_CHINESE_SENTENCE,
//            "图片",
//            "复合项"
    };

    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return "源于app\"词根单词\"，单词结构分析一目了然，追根溯源，释义简单，举一反三，获得更完整体验，请上华为应用商店下载";
    } //。“复合项”指单词、音标、结构、释义的组合

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    public List<Definition> wordLookup(String key) {
        try {
            mSrcWord = key;
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

            // db.close();
            return re;
        } catch (Exception e) {
            return new ArrayList<>();
        }
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
                        new String[]{FIELD_WORD},
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
        if (q.isEmpty()) {
            return re;
        }
        Cursor cursor = db.query(TABLE_DICT,
                new String[]{
                        FIELD_WORD,
                        FIELD_TYPE,
                        FIELD_TYPE1,
                        FIELD_TYPE2,
                        FIELD_TYPE3,
                        FIELD_TYPE4,
                        FIELD_WORDLEVEL,
                        FIELD_ROOTVARIANT,
                        FIELD_MEANING,
                        FIELD_SOUNDMARK,
                        FIELD_STRUCTURE,
                        FIELD_EXPLAIN,
                        FIELD_TRANSLATION,
                        FIELD_EXAMPLE,
                        FIELD_EXAMPLETRANSLATION
                },
                FIELD_WORD + "=? COLLATE NOCASE", new String[]{q}, null, null, null);
        while (cursor.moveToNext()) {
            Definition def = getDefFromCursor(cursor);
            re.add(def);
        }
        return re;
    }

    private Definition getDefFromCursor(Cursor cursor) {
        LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
        String word = cursor.getString(0).trim();
        String type = cursor.getString(1).trim();
        String type1 = cursor.getString(2).trim();
        String type2 = cursor.getString(3).trim();
        String type3 = cursor.getString(4).trim();
        String type4 = cursor.getString(5).trim();
        String wordlevel = cursor.getString(6).trim();
        String rootvariant = cursor.getString(7).trim();
        String meaning = cursor.getString(8).trim();
        String soundmark = cursor.getString(9).trim();
        String structure = cursor.getString(10).trim();
        String explain = cursor.getString(11).replaceAll("\\s", "<br/>").trim();
        String translation = cursor.getString(12).trim();
        String example = cursor.getString(13).trim();
        String exampletranslation = cursor.getString(14).trim();

        int count = 0;
        String definition = String.format(
                "<div class='wordbean'> <div id='word'>%s</div><br/> " +
                        "<div id='wordlevel'> wordlevel: %s</div><br/>" +
                        "<div id='type'>%s%s%s%s%s</div><br/>" +
                        "<div id='meaning'>%s</div><br/>" +
                        "<div id='structure'>%s</div><br/>" +
                        "<div id='explain'>%s</div><br/>" +
                        "<div id='word_translation'>%s</div><br/>" +
                        "</div>",
                word,
                wordlevel,
                !type.equals("") ? type : "",
                !type1.equals("") ? "<br/>" + (new String(new char[count += 2 * type.length()]).replace("\0", "&nbsp;")) + "╰→" + type1 : "",
                !type2.equals("") ? "<br/>" + (new String(new char[count += 2 * (2 + type1.length())]).replace("\0", "&nbsp;")) + "╰→" + type2 : "",
                !type3.equals("") ? "<br/>" + (new String(new char[count += 2 * (2 + type2.length())]).replace("\0", "&nbsp;")) + "╰→" + type3 : "",
                !type4.equals("") ? "<br/>" + (new String(new char[count += 2 * (2 + type3.length())]).replace("\0", "&nbsp;")) + "╰→" + type4 : "",
                !meaning.equals("") ? meaning : "",
                !structure.equals("") ? structure : "",
                !explain.equals("") ? explain : "",
                translation);

//        String complex = FieldUtil.formatComplexTplWord(DICT_NAME, mSrcWord, soundmark, definition, Constant.AUDIO_INDICATOR_MP3);
//        String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, mSrcWord, soundmark, definition, "");
        eleMap.put(EXP_ELE_LIST[0], mSrcWord);
        eleMap.put(EXP_ELE_LIST[1], soundmark);
        eleMap.put(EXP_ELE_LIST[2], definition);
        eleMap.put(EXP_ELE_LIST[3], example);
        eleMap.put(EXP_ELE_LIST[4], exampletranslation);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//        eleMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);


        String displayHtml;
        displayHtml = String.format(
                "<b>%s</b> %s<br/>" +
                        "%s<i> wordlevel: %s</i><br/>" +
                        "%s%s%s%s%s<br/>" +
                        "%s" +
                        "%s" +
                        "%s",
                word,
                soundmark,
                translation,
                wordlevel,
                !type.equals("") ? type : "",
                !type1.equals("") ? "→" + type1 : "",
                !type2.equals("") ? "→" + type2 : "",
                !type3.equals("") ? "→" + type3 : "",
                !type4.equals("") ? "→" + type4 : "",
                !meaning.equals("") ? meaning + "<br/>" : "",
                !structure.equals("") ? structure + "<br/>" : "",
                explain);

        String audioFileName = Utils.getSpecificFileName(mSrcWord);
        Definition.ResInformation resInfor = new Definition.ResInformation(
                "", audioFileName, Constant.MP3_SUFFIX
        );
        return new Definition(eleMap, displayHtml, resInfor);
    }

    private String[] getForms(String q) {
        //SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("forms", new String[]{"bases"}, "word=? ", new String[]{q.toLowerCase()}, null, null, null);
        String bases = "";
        while (cursor.moveToNext()) {
            bases = cursor.getString(0);
        }
        return bases.split("@@@");
    }

//    private String colorizeSense(String sense) {
//        String result = sense.replaceAll("noun", "<font color=#e3412f>n.</font>");
//        result = result.replaceAll("adjective", "<font color=#f8b002>adj.</font>");
//        result = result.replaceAll("verb", "<font color=#539007>v.</font>");
//        result = result.replaceAll("adverb", "<font color=#684b9d>adv.</font>");
//        return result;
//    }

    private Cursor getFilterCursor(String q) {
        Log.d("databse", "getFilterCursor" + q);
        Cursor cursor = db.query("wordbean", new String[]{"rowid _id", "word"}, "word LIKE ?", new String[]{q + "%"}, null, null, null);
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
}
