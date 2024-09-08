package com.stardict.org.yage.dict.star;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

/**
 * StarDict dictionary file parser
 * usage as follows:<br/>
 * <code>StarDictParser rdw=new StarDictParser();</code><br/>
 * <code>rdw.loadIdxIndexFile(f);</code><br/>
 * <code>rdw.loadContentFile(fcnt);</code><br/>
 *
 * @author beethoven99@126.com
 */
public class StarDictParser {


    public static List<Entry<String, WordPosition>> searchWord(
            Map<String, WordPosition> words, String searchKey) {
        //直接开头的结果
        List<Entry<String, WordPosition>> wordsBeginWithKey = new ArrayList<Entry<String, WordPosition>>();
        //间接开头的结果
        List<Entry<String, WordPosition>> wordsContainsKey = new ArrayList<Entry<String, WordPosition>>();

        int i = -1;
        for (Entry<String, WordPosition> en : words.entrySet()) {
            if (en.getKey() == null) {
                System.out.println("oh no null");
            }
            i = en.getKey().toLowerCase().indexOf(searchKey);
            if (i == 0) {
                wordsBeginWithKey.add(en);
            } else if (i > 0 && wordsContainsKey.size() < MAX_RESULT) {
                wordsContainsKey.add(en);
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

    /**
     * 加载内容文件
     *
     * @param dictFilePathName
     */
    public static RandomAccessFile loadContentFile(String dictFilePathName) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(dictFilePathName, "r");
            System.out.println("is file open valid: " + randomAccessFile.getFD().valid());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return randomAccessFile;
    }

    /**
     * 得到字典内容中的内容片断，即为释义
     *
     * @param start offset point
     * @param len   length to get
     * @return
     */
    public static String getWordExplanation(RandomAccessFile dictRandomAccessFile, int start, int len) {
        String res = "";
        byte[] buf = new byte[len];
        try {
            dictRandomAccessFile.seek(start);
            int ir = dictRandomAccessFile.read(buf);
            if (ir != len) {
                System.out.println("Error occurred, not enought bytes read, wanting:" + len + ",got:" + ir);
            }
            res = new String(buf, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    //全局读文件缓冲区
    private static byte[] buf = new byte[1024];

    //字符串的开始标记
    private static int smark;

    //缓冲区的处理标记
    private static int mark;

    //最大返回结果数
    public static final int MAX_RESULT = 40;

    /**
     * 加载索引文件
     *
     * @param idxPathName
     */
    public static Map<String, WordPosition> loadIdxIndexFile(String idxPathName) {
        Map<String, WordPosition> words = new HashMap<String, WordPosition>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(idxPathName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            //第一次读
            int res = fis.read(buf);
            while (res > 0) {
                mark = 0;
                smark = 0;
                parseByteArray(words, buf, 1024);
                if (mark == res) {
                    //上一个刚好完整，可能性几乎不存在，不过还要考虑
                    res = fis.read(buf);
                } else {
                    //有上一轮未处理完的，应该从mark+1开始
                    //System.out.println("写 buf from: "+(buf.length-smark)+", len:"+(smark));
                    res = fis.read(buf, buf.length - smark, smark);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return words;
    }

    /**
     * parse a block of bytes
     *
     * @param buf 使用全局的，之后可以去掉它
     * @param len cbuf最多只有这么长
     * @throws UnsupportedEncodingException
     */
    private static void parseByteArray(Map<String, WordPosition> words, byte buf[], int len) throws UnsupportedEncodingException {
        for (; mark < len; ) {
            if (buf[mark] != 0) {
                //在字符串中
                if (mark == len - 1) {
                    //字符串被截断了
                    //System.out.println("字符串被截断：mark:"+mark+", smark: "+smark+", left:"+(len-mark));
                    System.arraycopy(buf, smark, buf, 0, len - smark);
                    break;
                } else {
                    mark++;
                }
            } else {
                //等于0，说明一个单词完结
                String tword = null;
                if (mark != 0) {
                    byte[] bs = ArrayUtils.subarray(buf, smark, mark);
                    tword = new String(bs, "utf-8");
                }

                if (len - mark > 8) {
                    //如果后面至少还有8个
                    smark = mark + 9;
                    byte[] bstartpos = ArrayUtils.subarray(buf, mark + 1, mark + 5);
                    byte[] blen = ArrayUtils.subarray(buf, mark + 5, mark + 9);
                    int startpos = ByteArrayHelper.toIntAsBig(bstartpos);
                    int strlen = ByteArrayHelper.toIntAsBig(blen);
                    //System.out.println(" start: "+startpos+" , len:"+strlen);
                    //现在是一个完整的单词了
                    if (tword != null && tword.trim().length() > 0 && strlen < 10000) {
                        words.put(tword, new WordPosition(startpos, strlen));
                    }
                    mark += 8;
                } else {
                    //后面少于8个
                    //System.out.println("后面少于8个:mark="+mark+", smark: "+smark+", left:"+(len-mark));
                    //加完之后已跳过0，指向两个8字节数字
                    System.arraycopy(buf, smark, buf, 0, len - smark);
                    break;
                }
            }
        }
    }

    /**
     * customer comparator
     */
    private static Comparator<Entry<String, WordPosition>> WordComparator = new Comparator<Entry<String, WordPosition>>() {
        public int compare(Entry<String, WordPosition> ea, Entry<String, WordPosition> eb) {
            return ea.getKey().compareToIgnoreCase(eb.getKey());
        }
    };

    /**
     * 主程序，测试
     *
     * @param args
     */
    public static void main(String[] args) {
        String idx = "D:\\work\\词典\\stardict-21cen-2.4.2\\21cen.idx";
        String dictFilePathName = "D:\\work\\词典\\stardict-21cen-2.4.2\\21cen.dict";

        StarDictParser rdw = new StarDictParser();
        Map<String, WordPosition> idxWords = rdw.loadIdxIndexFile(idx);
        RandomAccessFile dictRaFile = rdw.loadContentFile(dictFilePathName);

        rdw.fuzzySearch(dictRaFile, idxWords, "happy");
    }

    public static void fuzzySearch(RandomAccessFile dictRaFile, Map<String, WordPosition> words,
                                   String word) {
        List<Entry<String, WordPosition>> res = searchWord(words, word);
        for (int i = 0; i < res.size(); i++) {
            Entry<String, WordPosition> entry = res.get(i);
            System.out.println("key:" + entry.getKey());
            System.out.println("translation:" +
                    getWordExplanation(dictRaFile, entry.getValue().getStartPos(),
                            entry.getValue().getLength()));
        }
    }
}
