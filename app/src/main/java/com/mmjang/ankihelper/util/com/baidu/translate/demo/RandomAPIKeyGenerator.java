package com.mmjang.ankihelper.util.com.baidu.translate.demo;

import com.mmjang.ankihelper.util.BuildConfig;

import java.util.Random;
public class RandomAPIKeyGenerator {

    public enum Name {
        BAIDU,
        DEEPL,
        DEEPLX
    }

    private static String[] BAIDU_APP_ID_AND_KEY_LIST = new String[] {
            "20181126000239193\nCPhOj0FHGhvt2f5x5kag",
            "20180208000121840\n6vcjzWbWq5Swqk8y_VQG",
            "20181125000239165\nXVCzhWeP3QLLzW7TXHGm",
            "20181125000239170\np4CI4cEngtYvRx12HUec",
            "20160220000012831\nISSPx0K_ZyrUN9IAOKel"   //共用
    };
    private static String[] DEEPL_APP_KEY_LIST = new String[] {
            "e68f7121-97e1-4168-85e7-a032cbe9e186",
            "7e3b2345-0330-4ca1-887c-c0cca3d2da9e"      //共用
    };
    private static String[] DEEPLX_SERVER_URL_LIST = new String[] {
//            "http://45.32.74.174:1188/translate",
            "https://dlx.bitjss.com/translate",
            "https://deeplx.keyrotate.com/translate",
            "https://deepl.aimoyu.tech/translate",
            "https://deeplx-api.xiangtatech.com/translate",
            "https://dx.mmyy.fun/translate",
            "https://d.lfh.icu/translate"   //公共使用
    };

    public static String[] next(Name name){
        String[] keyArr = new String[0];
        int min, max;
        switch (name) {
            case BAIDU:
                keyArr = BAIDU_APP_ID_AND_KEY_LIST;
                break;
            case DEEPL:
                keyArr = DEEPL_APP_KEY_LIST;
                break;
            case DEEPLX:
                keyArr = DEEPLX_SERVER_URL_LIST;
        }
        if(BuildConfig.isDebug && keyArr.length > 1) {
            min = 0;
            max = keyArr.length - 2;
        } else {
            min = keyArr.length - 1;
            max = min;
        }
//        min = 0;
//        max = keyArr.length - 1;
        int index = randInt(min, max);
        return keyArr[index].split("\n");
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
