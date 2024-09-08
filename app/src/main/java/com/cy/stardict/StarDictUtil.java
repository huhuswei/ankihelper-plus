package com.cy.stardict;

import com.stardict.StarDictUtils.cn.star.dict.StarDict;
import com.stardict.StarDictUtils.cn.star.dict.WordIndex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StarDictUtil extends StarDict {

    public static StarDictUtil loadDict(String ifoPathName) {
        StarDictUtil dict = new StarDictUtil();
        dict.load(ifoPathName);
        return dict;
    }

    //最大返回结果数
    public static final int MAX_RESULT = 40;

    public List<WordIndex> fuzzySearch(String word) {
        //直接开头的结果
        List<WordIndex> wordsBeginWithKey = new ArrayList<WordIndex>();
        //间接开头的结果
        List<WordIndex> wordsContainsKey = new ArrayList<WordIndex>();

        for (int j = 0; j < getWordCount(); j++) {
            int index = getWordIndexs().get(j).getWord().indexOf(word);
            if (index == 0) {
                wordsBeginWithKey.add(getWordIndexs().get(j));
            } else if (index > 0 && wordsContainsKey.size() < MAX_RESULT) {
                wordsContainsKey.add(getWordIndexs().get(j));
            }
            if (wordsBeginWithKey.size() > MAX_RESULT) {
                break;
            }
        }

        Collections.sort(wordsBeginWithKey, WordComparator);
        Collections.sort(wordsContainsKey, WordComparator);

        if (wordsBeginWithKey.size() < MAX_RESULT) {
            int need = MAX_RESULT - wordsBeginWithKey.size();
            if (need > wordsContainsKey.size()) {
                need = wordsContainsKey.size();
            }
            wordsBeginWithKey.addAll(wordsContainsKey.subList(0, need));
        }
        return wordsBeginWithKey;
    }

    public WordIndex exactSearch(String word) {
        List<WordIndex> results=new ArrayList<>();
        for (int j = 0; j < getWordCount(); j++) {
            if (getWordIndexs().get(j).getWord().equalsIgnoreCase(word)){
                results.add(getWordIndexs().get(j));
            }
        }
        if (!results.isEmpty()){
            if (results.size()==1){
                return results.get(0);
            }
            for (WordIndex wordIndex: results) {
                if (wordIndex.equals(word)){
                    return wordIndex;
                }
            }

        }else {
            return null;
        }
        return null;
    }

    /**
     * customer comparator
     */
    private static final Comparator<WordIndex> WordComparator = new Comparator<WordIndex>() {
        public int compare(WordIndex a, WordIndex b) {
            return a.getWord().compareToIgnoreCase(b.getWord());
        }
    };

    /**
     * 得到字典内容中的内容片断，即为释义
     *
     * @return
     */
    public String getWordExplanation(String word) {
        WordIndex wordIndex=exactSearch(word);
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(getDictFile().getAbsolutePath(), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "未发现该词典的.dict文件";
        }

        String res = "";
        byte[] buf = new byte[wordIndex.getLength()];
        try {
            randomAccessFile.seek(wordIndex.getOffset());
            int ir = randomAccessFile.read(buf);
            if (ir != wordIndex.getLength()) {
                System.out.println("Error occurred, not enought bytes read, wanting:" + wordIndex.getLength() + ",got:" + ir);
            }
            res = new String(buf, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getDictName() {
        return getInfo().getBookName();
    }

    public String getAuthor() {
        return getInfo().getAuthor();
    }

    public int getWordCount() {
        return getInfo().getWordCount();
    }


}
