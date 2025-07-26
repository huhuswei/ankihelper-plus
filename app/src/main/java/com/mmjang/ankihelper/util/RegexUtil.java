package com.mmjang.ankihelper.util;

import android.text.TextUtils;

import org.nlpcn.commons.lang.jianfan.JianFan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static final String EMOJI_REX = "[\\U0001f0a0-\\U0001f0ff\\U0001f170-\\U0001f19a\\U0001f300-\\U0001faff\\u2190-\\u21FF\\u2300-\\u23FF\\u2500-\\u26FF\\u2700-\\u27BF\\u27F0-\\u27FF\\u2900-\\u297F\\u2B00-\\u2BFF]+";
    public static final String SYMBOL_REX_WITH_BLANK = "[ ,\\./:\"\\\\\\[\\]\\|`~!@#\\$%\\^&\\*\\(\\)_\\+=<\\->\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）—=]„";
//    public static final String SYMBOL_REX_WITHOUT_BLANK = "[ ,./:\"\\\\\\[\\]|~!@#$%^&*()_+\\-=<>?;'‘’“”，、；：！ ￥…（）【】《》？{}¡¿„·]+";
    public static final String SYMBOL_REX_WITHOUT_BLANK = "[,./:\"\\\\\\[\\]|`~ˌ!@#$%^&*()_+=<\\->?;'ˈ，、；：‘’“”【】《》？{}！￥…（）—„¡¿→〖〗「」・]+";
    public static final String SYMBOL_FILTER = "[,/:\"\\\\\\[\\]\\|`~!!@#\\$%\\^&\\*\\(\\)_\\+=<>\\?;'，。、；：‘’“”【】《》？\\{\\}！￥…（）=„¡¿→]";
//    public static final String SYMBOL_REX_WITHOUT_BLANK_WITH_SPACE = "[\\)\"]+";
//    public static final String SYMBOL_REX_WITHOUT_BLANK = "[/:\"\\\\\\[\\]\\|`~!! @#\\$%\\^&\\*\\(\\)_\\+=<\\->\\?;，。、；：‘’“”【】《》？\\{\\}！￥…（）—=„¡¿]";
//    public static final String SYMBOL_REX_NON_SPACE = "['‘’/]";
    public static String SYMBOL_REX = SYMBOL_REX_WITHOUT_BLANK;

//    public static void refreshSymbolSelection(){
//        boolean b = SPHelper.getBoolean(ConstantUtil.TREAT_BLANKS_AS_SYMBOL, true);
//        if (b) {
//            SYMBOL_REX = SYMBOL_REX_WITH_BLANK;
//        }else {
//            SYMBOL_REX = SYMBOL_REX_WITHOUT_BLANK;
//        }
//    }
    public static boolean isEnglish(String charaString){
//        return charaString.matches("^[a-zA-Z]*-*[a-zA-Z]*");
        return charaString.matches("^[a-zA-Z\\d]*");
    }

//    public static boolean isRussian(String charaString){
//        return charaString.matches("^[а-яA-Я]*-*[а-яA-Я]*");
//    }

    public static boolean isChinese(String charaString) {
        return charaString.matches("^[\u4E00-\u9FA5]*");
    }

    public static boolean isRussian(String charaString){
        return charaString.matches("^[\u0400-\u052f]*");
    }

    public static boolean isArabic(String charaString){
        return charaString.matches("^[\u0600-\u06ff\u0750-\u077f]*");
    }

    public static boolean isThai(String charaString){
        return charaString.matches("^[\u0e00-\u0e7f]*");
    }

    public static boolean isSpecialWord(String charaString){
        return charaString.matches("^[a-zA-ZÀ-ÿ]*");
    }

    public static boolean isKorean(char ch){
        return ch>='가' && ch<='힣';
    }

    public static boolean isPositiveInteger(String orginal) {
        return isMatch("^\\+{0,1}[1-9]\\d*", orginal);
    }

    public static boolean isNegativeInteger(String orginal) {
        return isMatch("^-[1-9]\\d*", orginal);
    }

    public static boolean isWholeNumber(String orginal) {
        return isMatch("[+-]{0,1}0", orginal) || isPositiveInteger(orginal) || isNegativeInteger(orginal);
    }

    public static boolean isPositiveDecimal(String orginal){
        return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", orginal);
    }

    public static boolean isNegativeDecimal(String orginal){
        return isMatch("^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*", orginal);
    }

    public static boolean isDecimal(String orginal){
        return isMatch("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", orginal);
    }

    public static boolean isEmoji(String orginal){
        return isMatch(EMOJI_REX, orginal);
    }

    public static boolean isEmojiChar(char codePoint) {
        if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
            return false;
        return true;
    }


    public static boolean isNumber(String orginal){
        return isWholeNumber(orginal) || isDecimal(orginal) || isNegativeDecimal(orginal) || isPositiveDecimal(orginal);
    }

    public static boolean isMatch(String regex, String orginal){
        if (orginal == null || orginal.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(orginal);
        return isNum.matches();
    }

    private static String REGEX_TEXT_AND_LINK_FRAGMENT = "^\"{0,1}([\\w\\W]+?)\"{0,1}\\s*(https{0,1}://[^\\s]+:~:text=[^\\s]+)$";
    public static boolean isScrollingToTextFragment(final String text) {
        return text.matches(REGEX_TEXT_AND_LINK_FRAGMENT);
    }

    public static String getTextOfFragment(String plain) {
        String text = plain.replaceAll(REGEX_TEXT_AND_LINK_FRAGMENT, "$1");
        return (text == null || text == "") ? "" : text;
    }
    public static String getLinkOfFragment(String plain) {
        String text = plain.replaceAll(REGEX_TEXT_AND_LINK_FRAGMENT, "$2");
        return (text == null || text == "") ? "" : text;
    }

//    // 根据Unicode编码完美的判断中文汉字和符号
//    public static boolean isChinese(char c) {
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
//                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
//                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
//            return true;
//        }
//        return false;
//    }
    /**
     * 输入的字符是否是汉字
     * @param a char
     * @return boolean
     */
    public static boolean isChinese(char a) {
        int v = (int)a;
        return (v >=0x4E00 && v <= 0x9FA5);
    }

    public static boolean isChineseSentence(String s){
        if(s.length() == 0) return false;
        int count = 0;
        for(int i = 0; i < s.length(); i ++){
            if(isChinese(s.charAt(i))){
                count ++;
            }
        }
        if((count + 0.0) / s.length() >= 0.5){
            return true;
        }else{
            return false;
        }
    }


    public static boolean isSymbol(char a){
        String s = a+"";
       return s.matches(SYMBOL_REX);
    }

    public static boolean isSymbol(String a){
        return a.matches(SYMBOL_REX);
    }

//    public static boolean isSymbolWithSpace(char a) {
//        String s = a+"";
//        return s.matches(SYMBOL_REX_WITHOUT_BLANK_WITH_SPACE);
//    }
//
//    public static boolean isSymbolWithSpace(String a) {
//        return a.matches(SYMBOL_REX_WITHOUT_BLANK_WITH_SPACE);
//    }

    public static String colorizeSense(String sense) {
//        if(true)
//            return sense;
        String start;
        String result = "";
        String separator;
        String[] senses;

        if (sense.matches("[★☆]+.*")) {
            start = sense.replaceAll("[^★☆]+", "");
            separator = ";";
            senses = sense.replaceAll("[★☆]+", "").trim().split(separator);
        } else {
            start = "";
            separator = ",";
            senses = sense.replace("&", ",").split(separator);
        }
        for (String s : senses) {
            if(!result.equals("")) result = result + separator;
            s = s.trim();
            if(s.matches("(n\\.|(n-|noun|num|abbr|card|colour|comb|contr|conv|frac|infini|ord|plu|pron|symbol).*)$")) {
                result = result + s.replaceAll("(n\\.|(n-|noun|num|abbr|card|colour|comb|contr|conv|frac|infini|ord|plu|pron|symbol).*)", "<font color=#e3412f>$1</font>");
            } else if(s.matches("(v\\.|(v|aux|imper|inter|quest|sense|sound).*)$")) {
                result = result + s.replaceAll("(v\\.|(v|aux|imper|inter|quest|sense|sound).*)", "<font color=#539007>$1</font>");
            } else if(s.matches("((adj\\.|adj|prep|det|exclam).*)$")) {
                result = result + s.replaceAll("((adj|prep|det|exclam).*)", "<font color=#f8b002>$1</font>");
            } else if(s.matches("(adv\\.|adv.*)$")) {
                result = result + s.replaceAll("(adv.*)", "<font color=#684b9d>$1</font>");
            } else if(s.matches("((aux|conj|modal|poss|predct|prefix|pron|relative|suffix).*)$")) {
                result = result + s.replaceAll("((aux|conj|modal|poss|predct|prefix|pron|relative|suffix).*)", "<font color=#485b4d>$1</font>");
            } else if(s.matches("((neg|phr).*)$")) {
                result = result + s.replaceAll("((neg|phr).*)", "<font color=#3c9566>$1</font>");
            } else if (s.matches("web\\."))
                result = result + s.replaceAll("([\\w\\W]+)", "<font color=#1781b5>$1</font>");
            else
                result = result + s.replaceAll("([\\w\\W]+)", "<font color=#806d9e>$1</font>");
        }
        return start + result.replaceAll("([\\W\\w].*?)\\.?\\s?</font>$", "$1. </font>");

//        private String colorizeSense(String sense) {
//            String result = sense.replaceAll("noun", "<font color=#e3412f>n.</font>");
//            result = result.replaceAll("n-var", "<font color=#e3412f>n-var</font>");
//            result = result.replaceAll("adjective", "<font color=#f8b002>adj.</font>");
//            result = result.replaceAll("verb", "<font color=#539007>v.</font>");
//            result = result.replaceAll("adverb", "<font color=#684b9d>adv.</font>");
//            return "<i>" + result +"</i>";
//        }
    }

    public static String getApproximateWord(String word, String content) {
        String htmlText = content;
        String wordToFind = word;

        Pattern pattern;
        if(RegexUtil.isChinese(wordToFind)) {
            pattern = Pattern.compile("<[^>]*>([^>]*?(" + JianFan.f2j(wordToFind) + "|" + JianFan.j2f(wordToFind) + ")\\d.?[^<]*?)<[^>]*>");
        } else {
            pattern = Pattern.compile("<[^>]*>([^>]*?" + wordToFind + "[^<]*?)<[^>]*>");
        }
//        Pattern pattern = Pattern.compile("<[^>]*>([-()\\w\\s]*?" + wordToFind + "[-()\\w\\s]*?)<[^>]*>");
//        Pattern pattern = Pattern.compile("([-\\w\\s].*?" + wordToFind + "[-\\w\\s].*?)");
        Matcher matcher = pattern.matcher(htmlText);

        if (matcher.find()) {
            String wordFound = matcher.group(1).replaceFirst("<[\\w\\W]*", "").trim();
            Trace.i("getApproximateWord", wordFound);
            return TextUtils.isEmpty(wordFound) ? word : wordFound;
        } else {
            return word;
        }
    }
}