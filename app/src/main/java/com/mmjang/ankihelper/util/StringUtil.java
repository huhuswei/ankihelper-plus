package com.mmjang.ankihelper.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by chenxiangjie on 2017/7/26.
 */

public class StringUtil {
    /**
     * 判断字符串是否为null或全为空白字符
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空白字符<br> {@code false}: 不为null且不全空白字符
     */
    public static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSpace(char c) {
        return Character.isSpaceChar(c) || Character.isWhitespace(c);
    }

    /**
     * 判断字符串是否为日文
     *
     * @param input
     * @return
     */
    public static boolean isJapanese(String input) {
        try {
            return input.getBytes("shift-jis").length >= (2 * input.length());
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }

    public static String appendTagAll(String s, String regex, String front, String behind) {
        try {
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(s);
            int len = front.length() + front.length();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; m.find(); i++) {
                String word = s.substring(m.start(), m.end());
                m.appendReplacement(sb, front + word + behind);
            }
            return m.appendTail(sb).toString();
        } catch (Exception e) {
            return s;
        }
    }

    public static String appendTagToPharse(String s, String phrase, String front, String behind) {
        String[] words = phrase.split(" ");
        String lastKeyRegex;
        if (RegexUtil.isChinese(s.charAt(0)) ||
                RegexUtil.isKorean(s.charAt(0)) ||
                RegexUtil.isThai(s) ||
                StringUtil.isJapanese(s)) {
            lastKeyRegex = "";
            for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                if (wordIndex + 1 < words.length) {
                    lastKeyRegex += words[wordIndex] + ".*";
                } else {
                    lastKeyRegex += words[wordIndex] + "";
                }
            }
        } else {
            lastKeyRegex = "\\b";
            for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {
                if (wordIndex + 1 < words.length) {
                    lastKeyRegex += words[wordIndex] + "\\b.*";
                } else {
                    lastKeyRegex += words[wordIndex] + "\\b";
                }
            }
        }

            Pattern pp = Pattern.compile(lastKeyRegex, Pattern.CASE_INSENSITIVE);
            Matcher pm = pp.matcher(s);
            StringBuffer sb = new StringBuffer();
            while (pm.find()) {
                String includedPharse = s.substring(pm.start(), pm.end());
                String includedPharseTag = includedPharse;
                for (String word : words) {
                    if(!word.equals(""))
                        includedPharseTag = appendTagAll(includedPharseTag, word, front, behind);
                }
                pm.appendReplacement(sb, includedPharseTag);
            }
            return pm.appendTail(sb).toString();
        }


//    /**过滤HTML里去除img、p、span外的所有标签
//     * @param str
//     * @return
//     * @throws PatternSyntaxException
//     */
//    public static String htmlTagFilter(String str)throws PatternSyntaxException {
//
//        String regEx = "(?!<(img|p|span|br).*?>)<.*?>";
//        Pattern p_html = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
//        Matcher m_html = p_html.matcher(str);
//        str = m_html.replaceAll("");
//
//        return str.trim(); // 返回文本字符串
//    }
//
//    public static String htmlTagFilter(String regex, String str)throws PatternSyntaxException {
//        String regexStr = "(?!<(img|p|span).*?>)<.*?>";
//
//        if(!regex.equals("") && regex != null)
//            regexStr = regex;
//
//        Pattern p_html = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
//        Matcher m_html = p_html.matcher(str);
//        str = m_html.replaceAll("");
//
//        return str.trim(); // 返回文本字符串
//    }

        public static String stripHtml (String content){
//            // <p>段落替换为换行
//            if(content.matches("<p\\s*/>"))
//                content = content.replaceAll("<p\\s*/>", "\n");
//            else
//                content = content.replaceAll("<p\\s*/?>", "\n");
//
//            if(content.matches("<span\\s*/>"))
//                content = content.replaceAll("<span\\s*/>", "\n");
//            else
//                content = content.replaceAll("<span\\s*/?>", "\n");
//
////            content = content.replaceAll("<p .*?>", "\r\n");
//            // <br><br/>替换为换行
//            content = content.replaceAll("<br\\s*/?>", "\n");
//            // 去掉其它的<>之间的东西
//            content = content.replaceAll("<.*?>", "");
//            // 还原HTML
//            //content = URLDecoder.decode(content, "UTF-8");
            String text = content.replaceAll("\\n+", " ").
                    replaceAll("<br/?>|</?p>", "\n").
                    replaceAll("<[\\w\\W].*?>", "").
                    replaceAll("\\n", "<br/>");

            return text;

        }

        public static String htmlTagFilter (String str)throws PatternSyntaxException {
            String regEx = "<.*?>";
            Pattern p_html = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(str);
            str = m_html.replaceAll("");

            return str.trim(); // 返回文本字符串
        }

    /**
     * 检查字符串数组中是否有重复的内容。
     *
     * @param array 要检查的字符串数组
     * @return 如果数组中没有重复元素，返回true；否则返回false。
     */
    public static boolean hasNoDuplicates(String[] array) {
        // 使用HashSet来检查重复元素
        Set<String> uniqueElements = new HashSet<>(Arrays.asList(array));
        return uniqueElements.size() == array.length;
    }

    /**
     * 检查字符串数组中是否包含特定的字符串。
     *
     * @param array 要检查的字符串数组
     * @param keyword 要查找的关键字
     * @return 如果数组包含关键字，返回true；否则返回false。
     */
    public static boolean contain(String[] array, String keyword) {
        return Arrays.asList(array).contains(keyword);
    }
}
