package com.mmjang.ankihelper.data.plan;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liao on 2017/3/19.
 */

public class VocabularyCardModel {
    private final String MODEL_FILE = "vocabulary_card_model2.html";
    private final String MODEL_SPLITTER = "@@@";
    private final String CODING = "UTF-8";
    private final int NUMBER_OF_MODEL_STRING = 3;

    private String[] front = new String[1];
    private String css = "";
    private String[] back = new String[1];

    String[] QFMT = new String[1];
    String[] AFMT = new String[1];
    String[] Cards = {"cloze"};
    String CSS;
    public static final String [] FILEDS = {
            "Note ID",
            "摘取例句（加粗）",
            "句子翻译",
            "例句",
            "笔记",
            "释义",
            "单词",
            "音标",
            "自动发音",
            "手动发音",
            "发音链接",
            "来源"
    };


    VocabularyCardModel(Context ct){

        try {
            InputStream ips = ct.getResources().getAssets().open(MODEL_FILE);
            byte[] data = new byte[ips.available()];
            ips.read(data);
            String defaultModelStr = new String(data, CODING);
            String[] defaultModelSplitted = defaultModelStr.split(MODEL_SPLITTER);
            if(defaultModelSplitted.length == NUMBER_OF_MODEL_STRING) {
                front[0] = defaultModelSplitted[0];
                back[0] = defaultModelSplitted[1];
                css = defaultModelSplitted[2];
            }
            else{
                ;
            }
            QFMT[0] = front[0];
            AFMT[0] = back[0];
            CSS = css;

        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }
}

