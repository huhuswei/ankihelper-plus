package com.stardict.StarDictUtils.cn.star.dict.test;


import com.cy.stardict.StarDictUtil;

public class IdxTest {

    public static void main(String[] args) {
//        StarDictUtil dict = StarDictUtil.loadDict("D:\\work\\词典\\stardict-21cen-2.4.2\\21cen.ifo");
        StarDictUtil dict = StarDictUtil.loadDict("/Users/cy/cy/imbark/jiqingwaiyu/dict/21世纪英汉汉英双向词典完整版/21cen.ifo");

        System.out.println(dict.getDictName());
        System.out.println(dict.getAuthor());
        System.out.println("收录词数：" + dict.getWordCount());

        //模糊查询
//        List<WordIndex> wordIndexList = dict.fuzzySearch("happy");
//        for (WordIndex wordIndex :
//                wordIndexList) {
//            System.out.println("fuzzy result item:"+wordIndex.getWord());
//        }

        //查词
        String translation = dict.getWordExplanation("happy");
        System.out.println(translation);
    }

}
