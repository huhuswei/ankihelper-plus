package com.mmjang.ankihelper.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExternalDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ankihelper.db";
    private static final int VERSION = 7;
    Context mContext;

    private static final String SQL_CREATE_HISTORY = String.format(
            "create table if not exists %s (%s integer primary key, %s integer, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
            DBContract.History.TABLE_NAME, DBContract.History.COLUMN_TIME_STAMP,
            DBContract.History.COLUMN_TYPE, DBContract.History.COLUMN_WORD,
            DBContract.History.COLUMN_SENTENCE, DBContract.History.COLUMN_DICTIONARY,
            DBContract.History.COLUMN_DEFINITION, DBContract.History.COLUMN_TRANSLATION,
            DBContract.History.COLUMN_NOTE, DBContract.History.COLUMN_TAG);

    private static final String SQL_CREATE_PLAN = String.format(
            "create table if not exists %s (%s text, %s text, %s integer, %s integer, %s text)",
            DBContract.Plan.TABLE_NAME,
            DBContract.Plan.COLUMN_PLAN_NAME,
            DBContract.Plan.COLUMN_DICTIONARY_KEY,
            DBContract.Plan.COLUMN_OUTPUT_DECK_ID,
            DBContract.Plan.COLUMN_OUTPUT_MODEL_ID,
            DBContract.Plan.COLUMN_FIELDS_MAP
    );

    private static final String SQL_CREATE_DICT_TABLE = "CREATE TABLE IF NOT EXISTS dict" +
            "(id integer, name text, lang text, " +
            "elements text, description text, tmpl text," +
            "ord integer)";
    private static final String SQL_CREATE_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS entry" +
            "(dict_id integer, headword text, entry_texts text)";

    private static final String SQL_CREATE_INDEX = "CREATE INDEX IF NOT EXISTS headword_index ON entry (headword)";
    private static final String SQL_DROP_INDEX = "DROP INDEX IF EXISTS headword_index";


    private static final String SQL_CREATE_BOOK_TABLE = String.format(
            "create table if not exists %s (%s integer, %s integer, %s text, %s text, %s text, %s text)",
                DBContract.Book.TABLE_NAME,
                DBContract.Book.COLUMN_ID,
                DBContract.Book.COLUMN_LAST_OPEN_TIME,
                DBContract.Book.COLUMN_BOOK_NAME,
                DBContract.Book.COLUMN_AUTHOR,
                DBContract.Book.COLUMN_BOOK_PATH,
                DBContract.Book.COLUMN_READ_POSITION);

    private static final String SQL_ALTER_DICT_TABLE_ORD_COLUMN = "ALTER TABLE dict ADD COLUMN ord INTEGER DEFAULT 0";

    //mdict start
    private static final String SQL_CREATE_MDICT_TABLE = "CREATE TABLE IF NOT EXISTS mdict" +
            "(id integer, name text, css text, js text, lang integer, " +
            "elements text, description text, definition_regex text, tmpl text," +
            "ord integer)";
    private static final String SQL_DROP_MDICT_TABLE = "DROP TABLE mdict";

    private static final String SQL_ALTER_MDICT_TABLE_ORD_COLUMN = "ALTER TABLE mdict ADD COLUMN ord INTEGER DEFAULT 0";
    //mdict end

    public ExternalDatabaseHelper(Context context) {
        super(new ExternalDatabaseContext(context), DB_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HISTORY);
        db.execSQL(SQL_CREATE_PLAN);
        db.execSQL(SQL_CREATE_DICT_TABLE);
        db.execSQL(SQL_CREATE_ENTRY_TABLE);
        db.execSQL(SQL_CREATE_BOOK_TABLE);
        db.execSQL(SQL_CREATE_MDICT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 6){
            db.execSQL(SQL_CREATE_BOOK_TABLE);
            db.execSQL(SQL_CREATE_INDEX);
            db.execSQL(SQL_ALTER_DICT_TABLE_ORD_COLUMN);
            db.execSQL(SQL_CREATE_MDICT_TABLE);
        }

        if((oldVersion == 2) && newVersion == 6){
            db.execSQL(SQL_CREATE_INDEX);
            db.execSQL(SQL_ALTER_DICT_TABLE_ORD_COLUMN);
            db.execSQL(SQL_CREATE_MDICT_TABLE);
        }

        if(oldVersion == 3 && newVersion == 6) {
            db.execSQL(SQL_ALTER_DICT_TABLE_ORD_COLUMN);
            db.execSQL(SQL_CREATE_MDICT_TABLE);
        }

        if(oldVersion == 4 && newVersion == 6) {
            db.execSQL(SQL_CREATE_MDICT_TABLE);
        }

        if(oldVersion == 5 && newVersion == 6) {
            db.execSQL(SQL_DROP_MDICT_TABLE);
            db.execSQL(SQL_CREATE_MDICT_TABLE);
        }

        if(oldVersion == 6 && newVersion == 7) {
            db.execSQL(SQL_DROP_MDICT_TABLE);
            db.execSQL(SQL_CREATE_MDICT_TABLE);
        }
    }

//    /**
//     * 方法：检查表中某列是否存在
//     * @param tableName 表名
//     * @param columnName 列名
//     * @return
//     */
//    private static boolean checkColumnExists(SQLiteDatabase db, String tableName, String columnName) {
//        boolean result = false;
//        Cursor cursor = null;
//
//        try {
//            cursor = db.rawQuery("select * from sqlite_master where name = ? and sql like ?"
//                    , new String[]{tableName, "%" + columnName + "%"});
//            result = null != cursor && cursor.moveToFirst();
//        } catch (Exception e) {
//            Trace.e(ExternalDatabaseHelper.class.getNameId(), "checkColumnExists..." + e.getMessage());
//        } finally {
//            if (null != cursor && !cursor.isClosed()) {
//                cursor.close();
//            }
//        }
//        return result;
//    }
//
//    //修改
//    //判断dict表是否有某个字段，没有则添加
//    private static void alterTableAddColumn(SQLiteDatabase db, String table, String column) {
//        if(!checkColumnExists(db, table,  column)) {
//            Trace.e(ExternalDatabaseHelper.class.getNameId(), "alter dict table to add ord column");
//            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " INTEGER DEFAULT 0");
//        }
//    }
}
