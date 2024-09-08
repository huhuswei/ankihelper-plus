package com.mmjang.ankihelper.util;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: PunctuationUtil
 * @Description: 中文全角标点符号转换为英文半角标点符号
 * @Author: ss
 * @CreateDate: 2022/9/10 4:34 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/9/10 4:34 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PunctuationUtil {
    //中文标点符号
    final static String[] chinesePunctuation = {"！", "，", "。", "；", "《", "》", "（", "）", "？",
            "｛", "｝", "“", "：", "【", "】", "”", "‘", "’"};


    //英文标点符号
    final static String[] englishPunctuation = {"!", ",",
            ".", ";", "<", ">", "(", ")", "?", "{", "}", "\"",
            ":", "{", "}", "\"", "\'", "\'"};


    /**
     * 中文标点符号转英文字标点符号
     *
     * @param str 原字符串
     * @return str 新字符串
     */
    public static final String chinesePunctuationToEnglish(String str) {
        if (str.isEmpty()) {
            return str;
        }
        //去除空格
//        str = str.replaceAll("\\s+", " ");
        for (int i = 0; i < chinesePunctuation.length; i++) {
            str = str.replaceAll(String.format("([a-zA-Z\\s]+)%s", chinesePunctuation[i]), "$1" + englishPunctuation[i]);
            str = str.replaceAll(String.format("%s([a-zA-Z\\s]+)", chinesePunctuation[i]),  englishPunctuation[i] + "$1");
        }
        return str;
    }
}
