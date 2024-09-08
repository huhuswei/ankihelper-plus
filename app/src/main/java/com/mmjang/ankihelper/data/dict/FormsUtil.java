package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.HashMap;

/**
 * Created by liao on 2017/8/13.
 */

public class FormsUtil extends SQLiteAssetHelper{
    private static final String DEFAULT_DATABASE_NAME = "forms.db";
    private static final int DATABASE_VERSIOn = 1;
    private static HashMap<Integer, FormsUtil> instanceMap = new HashMap<>();
    Context mContext;
    SQLiteDatabase db;

    protected FormsUtil(Context context){
        super(context, DEFAULT_DATABASE_NAME, null, DATABASE_VERSIOn);
        mContext = context;
        db = getReadableDatabase();
    }

    protected FormsUtil(Context context, final String databaseName) {
        super(context, databaseName, null, DATABASE_VERSIOn);
        mContext = context;
        db = getReadableDatabase();
    }
    public static FormsUtil getInstance(Context context){
        if(instanceMap.get(DictLanguageType.ENG) == null){
            instanceMap.put(DictLanguageType.ENG, new FormsUtil(context));
        }
        return instanceMap.get(DictLanguageType.ENG);
    }

    public static FormsUtil getInstance(Context context, final int languageType) {
        String databaseName;
        switch (languageType) {
            case DictLanguageType.JPN:
                databaseName = "jforms.db";
                if(instanceMap.get(DictLanguageType.JPN) == null){
                    instanceMap.put(DictLanguageType.JPN, new FormsUtil(context, databaseName));
                }
                return instanceMap.get(DictLanguageType.JPN);
            case DictLanguageType.ENG:
            default:
                databaseName = "forms.db";
                if(instanceMap.get(DictLanguageType.ENG) == null){
                    instanceMap.put(DictLanguageType.ENG, new FormsUtil(context, databaseName));
                }
                return instanceMap.get(DictLanguageType.ENG);
        }

    }

    public String[] getForms(String q) {
        //SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("forms", new String[]{"bases"}, "hwd=? ", new String[]{q.toLowerCase()}, null, null, null);
        String bases = "";
        while (cursor.moveToNext()) {
            bases = cursor.getString(0);
        }
        if(bases.isEmpty()){
            return new String[0];
        }
        return bases.split("@@@");
    }
}
