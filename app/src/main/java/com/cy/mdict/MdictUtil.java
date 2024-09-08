package com.cy.mdict;

import android.text.TextUtils;

import com.cy.FuzzyWord;
import com.knziha.plod.dictionary.mdictRes;
import com.knziha.rbtree.RBTree_additive;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.FileUtils;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Trace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MdictUtil {

    public static List<FuzzyWord> queryFuzzyWordWithTrans(String word, List<Mdict> mdicts) throws IOException {
        List<String> fuzzyWords=queryFuzzyWord(word,mdicts);
        List<FuzzyWord> fuzzyWordWithTrans=new ArrayList<>();
        if (fuzzyWords.isEmpty()){
            return fuzzyWordWithTrans;
        }else {
            for (String tempWord:fuzzyWords){
                FuzzyWord fuzzyWord=new FuzzyWord(tempWord,queryWordDetail(tempWord,mdicts.get(0)));
                fuzzyWordWithTrans.add(fuzzyWord);
            }
            return fuzzyWordWithTrans;
        }
    }

    public static List<String> queryFuzzyWord(String word,List<Mdict> mdicts) {
        RBTree_additive combining_search_tree = new RBTree_additive();
        for(int i=0;i<mdicts.size();i++)
        {
            mdicts.get(i).size_confined_lookUp5(word,combining_search_tree,i,30);
            combining_search_tree.flatten().size();
        }
        combining_search_tree.inOrder();//print results stored in the RBTree

        List<String> results = combining_search_tree.print();
        return results;
/*printed results looks like 【happy____@398825@0@16905@1@16906@1】...【other results】...
how to handle:
String
html_contents0 = mdxs.get(0).getRecordAt(398825),
html_contents1 = mdxs.get(1).getRecordAt(16905),
html_contents2 = mdxs.get(1).getRecordAt(16906);
...
...
*/
    }

    public static String queryWordDetail(String word,Mdict dict) throws IOException {
        ArrayList<Integer> resultList = new ArrayList<>();
        resultList.add(dict.lookUp(word, false));//true means to match strictly
        if(resultList.get(0)!=-1){
            String html_contents = "";
//            String entry_name = dict.getEntryAt(search_result);
//            String path = dict.getPath().substring(0,dict.getPath().lastIndexOf(File.separatorChar)+1);//.replace("/storage/emulated/0/", "file:///mnt/sdcard/");

            int index = resultList.get(0);
            while(dict.getEntryAt(index).contains(word)) {
                index = index + 1;
                resultList.add(index);
            }
            html_contents = dict.getRecordsAt(resultList.stream().mapToInt(Integer::valueOf).toArray());

            if (TextUtils.isEmpty(dict.getCssPathName())){
                return html_contents;
            }else {
//                return
//                        "<style type=\"text/css\">" +
//                        FileUtils.readFile(dict.getCssPathName()) +
//                        "</style>" +
//                        html_contents;

//                html_contents.replaceAll(
//                        "((href)|(src))(=\")(.*?)(\\.)((css)|(js)\")",
//                        "$1$4_" + name + "$6$7");

                if(!TextUtils.isEmpty(dict.getCssPathName())) {
                    html_contents.replaceAll(
                            "(href=\")(.+?\\.css)(\")",
                            "$1_" + dict.getCssPathName().substring(dict.getCssPathName().lastIndexOf(File.separator) + 1) + "$3");
                }

                if(!TextUtils.isEmpty(dict.getJsPathName())) {
                    html_contents.replaceAll(
                            "(src=\")(.+?\\.js)(\")",
                            "$1_" + dict.getJsPathName().substring(dict.getJsPathName().lastIndexOf(File.separator)+1) + "$3");
                }
                return html_contents;

//                html_contents = html_contents.replaceAll(
//                        "(=\")(.*?)(\\.css)(\")",
//                        "$1$2"+ URI.create(path + name + ".css") + "$4");
//                return html_contents.replaceAll(
//                        "(=\")(.*?)(\\.js)(\")",
//                        "$1$2"+ URI.create(path + name + ".js") + "$4");
            }

        }
        return "";
    }


    public static ArrayList<String> queryWordDetails(String word, Mdict dict) throws IOException {
        ArrayList<String> resultList = new ArrayList<>();
        int sourceIndex = dict.lookUp(word, false);//true means to match strictly
        int index = sourceIndex;
        if(sourceIndex!=-1){
            while(dict.getEntryAt(index).equals(word)) {
                Trace.i("index_word", index +": " + dict.getEntryAt(index));
//                resultList.add(dict.getRecordAt(index).replaceAll(
//                        "((href)|(src))(=\")(.*?)(\\.)((css)|(js)\")",
//                        "$1$4_" + name + "$6$7"));

                String htmlContent =dict.getRecordAt(index).replaceAll(
                        "(href=\")(.+?\\.css)(\")",
                        "$1_" + dict.getCssPathName().substring(dict.getCssPathName().lastIndexOf(File.separator)+1) + "$3");

                resultList.add(htmlContent.replaceAll(
                        "(src=\")(.+?\\.js)(\")",
                        "$1_" + dict.getJsPathName().substring(dict.getJsPathName().lastIndexOf(File.separator)+1) + "$3"));
                index = index + 1;
            }

//            while(RegexUtil.isEnglish(word) && word.contains(dict.getEntryAt(--sourceIndex))) {
//                Trace.i("index_word", sourceIndex +": " + dict.getEntryAt(sourceIndex));
////                resultList.add(dict.getRecordAt(index).replaceAll(
////                        "((href)|(src))(=\")(.*?)(\\.)((css)|(js)\")",
////                        "$1$4_" + name + "$6$7"));
//
//                String htmlContent =dict.getRecordAt(sourceIndex).replaceAll(
//                        "(href=\")(.+?\\.css)(\")",
//                        "$1_" + dict.getCssPathName().substring(dict.getCssPathName().lastIndexOf(File.separator)+1) + "$3");
//
//                resultList.add(htmlContent.replaceAll(
//                        "(src=\")(.+?\\.js)(\")",
//                        "$1_" + dict.getJsPathName().substring(dict.getJsPathName().lastIndexOf(File.separator)+1) + "$3"));
//            }
        }
        return resultList;
    }


    public static ArrayList<String> queryForms(String word, Mdict dict) throws IOException {
        ArrayList<String> resultList = new ArrayList<>();
        int sourceIndex = dict.lookUp(word, false);//true means to match strictly
        int index = sourceIndex;
        if(sourceIndex!=-1){
            while(dict.getEntryAt(index).equals(word)) {
                Trace.i("index_form", index +": " + dict.getEntryAt(index));

                resultList.add(StringUtil.htmlTagFilter(dict.getRecordAt(index)));
                index = index + 1;
            }

//            while(RegexUtil.isEnglish(word) && word.contains(dict.getEntryAt(--sourceIndex))) {
//                Trace.i("index_word", sourceIndex +": " + dict.getEntryAt(sourceIndex));
////                resultList.add(dict.getRecordAt(index).replaceAll(
////                        "((href)|(src))(=\")(.*?)(\\.)((css)|(js)\")",
////                        "$1$4_" + name + "$6$7"));
//
//                String htmlContent =dict.getRecordAt(sourceIndex).replaceAll(
//                        "(href=\")(.+?\\.css)(\")",
//                        "$1_" + dict.getCssPathName().substring(dict.getCssPathName().lastIndexOf(File.separator)+1) + "$3");
//
//                resultList.add(htmlContent.replaceAll(
//                        "(src=\")(.+?\\.js)(\")",
//                        "$1_" + dict.getJsPathName().substring(dict.getJsPathName().lastIndexOf(File.separator)+1) + "$3"));
//            }
        } else {
            resultList.add(word);
        }
        return resultList;
    }


/*    public static String getConfig(Mdict dict) throws IOException {
        int search_result = 1;
        String html_contents = dict.getRecordAt(search_result);
        if (!html_contents.isEmpty()) {

        }
            html_contents = "";
        if(search_result!=-1){
            return html_contents.replaceAll(
                    "((href)|(src))(=\")(.*?)(\\.)((css)|(js)\")",
                    "$1$4_" + name + "$6$7");
        }
        return "";
    }*/

    public static List<mdictRes> queryMddDetail(String word, Mdict dict) {
        return dict.getMdd();
    }
}
