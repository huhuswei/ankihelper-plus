package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.os.Looper;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liao on 2017/4/28.
 */

public class EnglishSentenceSet implements IDictionary {

    private final int lt = DictLanguageType.ENG;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              ;
    private static final String DICT_NAME = "英语例句集合";
    private static final String DICT_INTRO = "数据来自 EGR";
    private static final String[] EXP_ELE_LIST = new String[]{
            Constant.DICT_FIELD_KEYWORD,
//            "音标",
//            "释义",
            Constant.DICT_FILED_SENTENCE,
            Constant.DICT_FILED_CHINESE_SENTENCE,
            Constant.DICT_FIELD_ORIGIN,
            Constant.DICT_FIELD_IMG
//            "复合项"
    };
    public int getLanguageType() {
        return lt;
    }

    public EnglishSentenceSet(Context context) {

    }

    public EnglishSentenceSet(){

    }
    private String mAudioUrl = "";
    public String getAudioUrl() {
        return mAudioUrl;
    }
    private void emptyAudioUrl() { mAudioUrl = ""; }
    public boolean setAudioUrl(String audioUrl) {
        try {
            mAudioUrl = audioUrl;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean isExistAudioUrl() {
        return true;
    }


    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public List<Definition> wordLookup(String word) {

        try {
            ArrayList<Definition> defList = new ArrayList<>();
            List<IDictionary> dictionaryList = DictionaryRegister.getDictionaryObjectList();
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
                    for (IDictionary dict : dictionaryList) {
                        if ((dict.getLanguageType() == DictLanguageType.ENG ||
                                dict.getLanguageType() == DictLanguageType.ALL) &&
                                (dict instanceof EudicSentence ||
                                        dict instanceof RenRenCiDianSentence ||
                                        dict instanceof Getyarn)) {
                            try {
                                //Your code goes here
                                defList.addAll(dict.wordLookup(word));
                            } catch (Exception e) {
                                String error = e.getMessage();
                            }
                        }
                    }
//                }
//            });
//            thread.start();
            return defList;
        } catch (Exception e) {
//            Log.d("time out", Log.getStackTraceString(ioe));
            Looper.prepare();
            Toast.makeText(MyApplication.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return new ArrayList<Definition>();
        }

    }

    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }


}

