package com.mmjang.ankihelper.util;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.widget.BigBangLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by chenxiangjie on 2017/7/26.
 */

public class FieldUtil {


    public static String getSelectedText(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        List<BigBangLayout.Item> selectedItems = getSelectedItems(lines);

        boolean isNonSpaceSymbol = false;
        boolean isIndoEuropean = false;
        for (int i = 0; i < selectedItems.size(); i++) {
            BigBangLayout.Item item = selectedItems.get(i);
            if (item.isSelected()) {
                if(!isNonSpaceSymbol &&
                        (RegexUtil.isEnglish(item.getText().toString()) ||
                        RegexUtil.isRussian(item.getText().toString()) ||
                        RegexUtil.isNumber(item.getText().toString()) ||
//                        RegexUtil.isThai(item.getText().toString()) ||
                        RegexUtil.isSpecialWord(item.getText().toString()))) {
                    if(isIndoEuropean) {
                        sb.append(" ");
                    }
                    isIndoEuropean = true;
                } else {
                    isIndoEuropean = false;
                }
                sb.append(item.getText());

                isNonSpaceSymbol = RegexUtil.isSymbol(item.getText().toString());
            }
        }
        return sb.toString().trim();
    }

    private static List<BigBangLayout.Item> getSelectedItems(List<BigBangLayout.Line> lines) {
        List<BigBangLayout.Item> selectedItems = new ArrayList<>();
        for (BigBangLayout.Line line : lines) {
            for (BigBangLayout.Item item : line.getItems()) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
        }
        return selectedItems;
    }

    public static String getNormalSentence(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            BigBangLayout.Line line = lines.get(lineIndex);
            List<BigBangLayout.Item> items = line.getItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                BigBangLayout.Item item = items.get(itemIndex);
                if(item.getText().equals("\n")){
                    sb.append("<br/>");
                    continue;
                }
                if (item.isSelected()) {
                    //sb.append("<b>");
                    sb.append(item.getText());
                    //sb.append("</b>");
                } else {
                    sb.append(item.getText());
                }
            }
        }
        return sb.toString().trim();
    }


    public static String getBoldSentence(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            BigBangLayout.Line line = lines.get(lineIndex);
            List<BigBangLayout.Item> items = line.getItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                BigBangLayout.Item item = items.get(itemIndex);
                if (item.getText().equals("\n")) {
                    sb.append("<br/>");
                    continue;
                }
                if (item.isSelected() && !item.isSpaceSymbol()) {
                    sb.append("<b>");
                    sb.append(item.getText());
                    sb.append("</b>");
                } else {
                    sb.append(item.getText());
                }
            }
        }

        if (Settings.getInstance(MyApplication.getContext()).get(Settings.POPUP_SWITCH_MERGE_BOLD_TAGS, false))
            return sb.toString().replaceAll("</b>(\\s?)<b>", "$1").trim();
        else
            return sb.toString().trim();
    }


//    public static String getBoldSentence(List<BigBangLayout.Line> lines) {
//        StringBuilder sb = new StringBuilder();
//        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
//            BigBangLayout.Line line = lines.get(lineIndex);
//            List<BigBangLayout.Item> items = line.getItems();
//            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
//                BigBangLayout.Item item = items.get(itemIndex);
//                if (item.getText().equals("\n")) {
//                    sb.append("<br/>");
//                    continue;
//                }
//                if (item.isSelected()) {
//                    sb.append("<b>");
//                    sb.append(item.getText());
//                    sb.append("</b>");
//                } else {
//                    sb.append(item.getText());
//                }
//            }
//        }
//        return sb.toString().trim();
//    }

    public static String getBlankSentence(List<BigBangLayout.Line> lines, boolean multiCardMode) {
        StringBuilder sb = new StringBuilder();

        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            BigBangLayout.Line line = lines.get(lineIndex);
            List<BigBangLayout.Item> items = line.getItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                BigBangLayout.Item item = items.get(itemIndex);
                if(item.getText().equals("\n")) {
                    sb.append("<br/>");
                    continue;
                }
                if (item.isSelected()) {
                    sb.append("{{c1::" + item.getText() + "}}");
                } else {
                    sb.append(item.getText());
                }
            }
        }

        String result = sb.toString().replace("}}{{c1::","").trim(); //combine adjacent cloze
//        String result = sb.toString().replaceAll("\\}\\}\\{\\{c\\d+?::", "").trim();
        if(result.length() < 4){
            return result;
        }
        if (multiCardMode) {
//            sb = new StringBuilder(result);
//            StringBuilder sbnew = new StringBuilder(result);
//            int close_count = 1;
//            for (int i = 3; i < sb.length(); i++) {
//                String sub = sb.substring(i - 3, i);
//                if (sub.equals("{{c")) {
//                    sbnew.replace(i, i + 1, Integer.toString(close_count));
//                    close_count++;
//                }
//            }
//            return sbnew.toString();
            HashMap<String, String> sameTagMap = new HashMap<>();
            int count = 1;
            Pattern p = Pattern.compile("\\{\\{c1::[\\w\\W]+?\\}\\}", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(result);
            StringBuffer sbuffer = new StringBuffer();
            for (int i = 0; m.find(); i++) {
                String oldTag = result.substring(m.start(), m.end());
                if(!sameTagMap.containsKey(oldTag)) {
                    sameTagMap.put(oldTag, "{{c"+Integer.toString(count++)+oldTag.substring(4, oldTag.length()));
                }
                m.appendReplacement(sbuffer, sameTagMap.get(oldTag));
            }
            return m.appendTail(sbuffer).toString();
        } else {
            return result;
        }
    }

//    public static String formatComplexTplWord(String dictName, String word, String phonetic, String definition, String MEDIA_INDICATOR) {
//        return formatComplexTplWord(dictName, word, phonetic, "", definition, MEDIA_INDICATOR);
//    }

//    public static String formatComplexTplWord(String dictName, String word, String phonetic, String sense, String definition, String MEDIA_INDICATOR) {
//        String complex = String.format(
//                Constant.TPL_COMPLEX_DICT_WORD_TAG,
//                dictName,
//                MEDIA_INDICATOR,
//                word,
//                phonetic,
//                sense,
//                StringUtil.appendTagAll(StringUtil.stripHtml(definition), word, "<b>", "</b>").
//                        trim().replaceAll("[\\n\\r]{2,}", "<br/>"));
//        return complex;
//    }
//
//    public static String formatComplexTplSentence(String title, String word, String sentence, String translation, String MEDIA_INDICATOR) {
//        String complex = String.format(
//                Constant.TPL_COMPLEX_DICT_SENTENCE_TAG,
//                title,
//                MEDIA_INDICATOR,
//                StringUtil.appendTagAll(StringUtil.stripHtml(sentence), word, "<b>", "</b>").
//                        trim().replaceAll("[\\n\\r]{2,}", "<br/>"),
//                StringUtil.stripHtml(translation));
//
//        return complex;
//    }
//
//    public static String formatComplexTplCloze(String dictName, String MEDIA_INDICATOR) {
//        String complex = String.format(
//                Constant.TPL_COMPLEX_DICT_CLOZE_TAG,
//                dictName,
//                MEDIA_INDICATOR);
//        return complex;
//    }
}
