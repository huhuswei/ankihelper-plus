package com.mmjang.ankihelper.data.dict;

import android.content.Context;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.dict.customdict.CustomDictionaryManager;
import com.mmjang.ankihelper.data.dict.mdict.MdictManager;
import com.mmjang.ankihelper.util.BuildConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liao on 2017/4/27.
 */

public class DictionaryRegister {
    //在这里注册词典类
    private static Class[] classList = new Class[]{
            Handian.class,
            Wordbean.class,
            Ode2.class,
            BingOxford.class,
            Etymonline.class,
            WebsterLearners.class,
            Collins.class,
            CollinsEnEn.class,
            DictionaryDotCom.class,
            VocabCom.class,
            Mnemonic.class,
            IdiomDict.class,
            UrbanDict.class,
//            Dub91Sentence.class,
            RenRenCiDianSentence.class,
            EudicSentence.class,
            Getyarn.class,
            Cloze.class,
            BatchClip.class,
            BingImage.class,
            Kuromoji.class,
            JiSho.class,
            HujiangJapanese.class,
            Dedict.class,
            Frdict.class,
            Esdict.class,
            Krdict.class,
//            SolrDictionary.class,
            DictTango.class,
            Mdict.class
//            EnglishDictSet.class,
//            EnglishSentenceSet.class
    };

//    private static Class<IDictionary>[] classList;
//
//    static {
//        try {
//            classList = new Class[] {
//                    Class.forName(DICT_HANDIAN_NAME),
//                    Class.forName(DICT_WORDBEAN_NAME),
//                    Class.forName(DICT_ODE2_NAME),
//                    Class.forName(DICT_BINGOXFORD_NAME),
//                    Class.forName(DICT_WEBSTERLEARNERS_NAME),
//                    Class.forName(DICT_COLLINS_NAME),
//                    Class.forName(DICT_COLLINSENEN_NAME),
//                    Class.forName(DICT_DICTIONARYDOTCOM_NAME),
//                    Class.forName(DICT_VOCABCOM_NAME),
//                    Class.forName(DICT_MNEMONIC_NAME),
//                    Class.forName(DICT_IDIOMDICT_NAME),
//                    Class.forName(DICT_URBANDICT_NAME),
//                    Class.forName(DICT_DUB91SENTENCE_NAME),
//                    Class.forName(DICT_RENRENCIDIANSENTENCE_NAME),
//                    Class.forName(DICT_EUDICSENTENCE_NAME),
//                    Class.forName(DICT_GETYARN_NAME),
//                    Class.forName(DICT_CLOZE_NAME),
//                    Class.forName(DICT_BINGIMAGE_NAME),
//                    Class.forName(DICT_KUROMOJI_NAME),
//                    Class.forName(DICT_JISHO_NAME),
//                    Class.forName(DICT_HUJIANGJAPANESE_NAME),
//                    Class.forName(DICT_DEDICT_NAME),
//                    Class.forName(DICT_FRDICT_NAME),
//                    Class.forName(DICT_ESDICT_NAME),
//                    Class.forName(DICT_DICTTANGO_NAME),
//                    Class.forName(DICT_MDICT_NAME)
//            };
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static List<IDictionary> dictList;

    public Class[] getDictionaryClassArray() {
        return classList;
    }

    public static List<IDictionary> getDictionaryObjectList() {
        //if (dictList == null) {
            dictList = new ArrayList<>();
            for (Class c : classList) {
                try {
                    //切换 debug release
                    if(!BuildConfig.isDebug && (
                            c == Wordbean.class ||
//                            c == Mdict.class ||
                            c == Handian.class ||
//                            c == Getyarn.class ||
                            c == DictTango.class
//                            c == BatchClip.class
                    ))
                        continue;
                    dictList.add(
                            (IDictionary) c.getConstructor(Context.class).newInstance(MyApplication.getContext())
                    );
                } catch (NoSuchMethodException nsme) {
                } catch (InstantiationException ie) {
                } catch (IllegalAccessException ie) {
                } catch (InvocationTargetException ite) {
                }
            }
            List<IDictionary> customDictionaries = (new CustomDictionaryManager(MyApplication.getContext(), "")).getDictionaryList();
            dictList.addAll(customDictionaries);

            List<IDictionary> mdicts = (new MdictManager(MyApplication.getContext(), "")).getDictionaryList();
            dictList.addAll(mdicts);
        //}
        return dictList;
    }

    public static List<IDictionary> getCustomDictionaryObjectList() {
        //if (dictList == null) {
        dictList = new ArrayList<>();
//        for (Class c : classList) {
//            try {
//                //切换 debug release
//                if(!BuildConfig.isDebug && (
//                        c == Wordbean.class ||
//                                c == Mdict.class ||
//                                c == Handian.class ||
////                            c == Getyarn.class ||
//                                c == DictTango.class))
//                    continue;
//                dictList.add(
//                        (IDictionary) c.getConstructor(Context.class).newInstance(MyApplication.getContext())
//                );
//            } catch (NoSuchMethodException nsme) {
//            } catch (InstantiationException ie) {
//            } catch (IllegalAccessException ie) {
//            } catch (InvocationTargetException ite) {
//            }
//        }

        List<IDictionary> customDictionaries = (new CustomDictionaryManager(MyApplication.getContext(), "")).getDictionaryList();
        dictList.addAll(customDictionaries);

        List<IDictionary> mdicts = (new MdictManager(MyApplication.getContext(), "")).getDictionaryList();
        dictList.addAll(mdicts);
        //}
        return dictList;
    }
}
