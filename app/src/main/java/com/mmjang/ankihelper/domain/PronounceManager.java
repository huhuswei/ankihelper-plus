package com.mmjang.ankihelper.domain;

import android.speech.tts.Voice;

import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.data.dict.IDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Gao on 2017/7/15.
 */

public class PronounceManager {

    //音频来源于以下词典 soundSource
    public static final int SOUND_SOURCE_YOUDAO = 1;
    public static final int SOUND_SOURCE_EUDICT = 2;
//    public static final int SOUND_SOURCE_BD = 3;
    public static final int SOUND_SOURCE_TTS = 3;
    public static final int SOUND_SOURCE_ONLINE = 4;

    public static ArrayList<SoundInformation> soundInforList = new ArrayList<>();

    private static List<Voice> ttsVoiceList = new ArrayList<>();

    public void PronounceManager() {
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    public static void initTtsVoiceNames(Set<Voice> voices) {
//        ttsVoiceList = voices.stream().filter(a->{return !a.getLocale().getCountry().equals("");}).sorted((a, b)->{return a.getName().compareTo(b.getName());}).collect(Collectors.toList());
        ttsVoiceList = voices.stream().filter(distinctByKey(a->a.getName())).sorted((a, b)->{return a.getName().compareTo(b.getName());}).collect(Collectors.toList());
    }
    public static SoundInformation getDictInformationByIndex(int index) {
        try {
            if(index == -1) {
                return soundInforList.get(soundInforList.size()-1);
            } else if(index > -1 || index < soundInforList.size()) {
                return soundInforList.get(index);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getDictInformationSize() {
        return soundInforList.size();
    }

    public static int getSoundInforIndexByList(int DICT_LANGUAGE_TYPE) {
        for(int index=0; index < soundInforList.size(); index++) {
            if(soundInforList.get(index).getDictLanguageType() == DICT_LANGUAGE_TYPE)
                return index;
        }
        return -1;
    }

    //每个词典的语音信息和状态都不一样，切换词典时，需要刷新语音列表信息
    public static String[] getAvailablePronounceLanguage(final IDictionary dict, final boolean toLoadTTS) {
        int dictLanguageType = dict.getLanguageType();
        String soundName = dict.getDictionaryName();
        boolean isExistAudioOrVideoUrl = dict.isExistAudioUrl();

        if(!soundInforList.isEmpty()) {
            soundInforList.clear();
        }
        
        if(dictLanguageType == DictLanguageType.ZHO || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //欧路 汉
                            DictLanguageType.ZHO,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("cn"), DictLanguageType.getNameByLangISO2("zh") , "o"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"cn"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 汉
//                            DictLanguageType.ZHO,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("cn"), DictLanguageType.getNameByLangISO3("zho") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"zh"}
//                    )
//            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 汉
//                            DictLanguageType.ZHO,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("cn"), DictLanguageType.getNameByLangISO3("zho") + "-b 粤"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"cte"}
//                    )
//            );
            loadTts(DictLanguageType.ZHO, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.RUS || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //有道 俄语
                            DictLanguageType.RUS,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("ru"), DictLanguageType.getNameByLangISO2("ru"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"ru"}
                    )
            );
            soundInforList.add(
                    new SoundInformation(
                            //欧路 俄语
                            DictLanguageType.RUS,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("ru"), DictLanguageType.getNameByLangISO2("ru") , "o"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"ru"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 俄语
//                            DictLanguageType.RUS,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("ru"), DictLanguageType.getNameByLangISO3("rus") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"ru"}
//                    )
//            );
            loadTts(DictLanguageType.RUS, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.KOR || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //有道 朝鲜
                            DictLanguageType.KOR,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("kp"), DictLanguageType.getNameByLangISO2("kp"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"ko"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 朝鲜
//                            DictLanguageType.KOR,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("kp"), DictLanguageType.getNameByLangISO3("prk") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"kor"}
//                    )
//            );
            loadTts(DictLanguageType.KOR, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.JPN || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //有道 日
                            DictLanguageType.JPN,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("jp"), DictLanguageType.getNameByLangISO2("ja"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"jap"}
                    )
            );
            soundInforList.add(
                    new SoundInformation(
                            //欧路 日
                            DictLanguageType.JPN,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("jp"), DictLanguageType.getNameByLangISO2("ja") , "o"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"jap"}
                    )

            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 日
//                            DictLanguageType.JPN,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("jp"), DictLanguageType.getNameByLangISO3("jpn") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"jp"}
//                    )
//            );
            loadTts(DictLanguageType.JPN, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.ENG || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //英语 有道 uk
                            DictLanguageType.ENG,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("gb"), DictLanguageType.getNameByLangISO2("en"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"en", "1"}
                    )
            );
            soundInforList.add(
                    new SoundInformation(
                            //欧路 英语 uk
                            DictLanguageType.ENG,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("gb"), DictLanguageType.getNameByLangISO2("en") , "o"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"eng", "en_uk_male"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 英语 uk
//                            DictLanguageType.ENG,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("gb"), DictLanguageType.getNameByLangISO3("eng") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"uk"}
//                    )
//            );
            soundInforList.add(
                    new SoundInformation(
                            //有道 英语 us
                            DictLanguageType.ENG,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("us"), DictLanguageType.getNameByLangISO2("en"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"en", "2"}
                    )
            );
            soundInforList.add(
                    new SoundInformation(
                            //欧路 英语 us
                            DictLanguageType.ENG,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("us"), DictLanguageType.getNameByLangISO2("en") , "o"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"eng", "en_us_female"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 英语 us
//                            DictLanguageType.ENG,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("us"), DictLanguageType.getNameByLangISO3("eng") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"en"}
//                    )
//            );
            loadTts(DictLanguageType.ENG, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.FRA || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //有道 法
                            DictLanguageType.FRA,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("fr"), DictLanguageType.getNameByLangISO2("fr"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"fr"}
                    )
            );
            soundInforList.add(
                    new SoundInformation(
                            //欧路 法
                            DictLanguageType.FRA,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("fr"), DictLanguageType.getNameByLangISO2("fr") , "o"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"fr"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 法
//                            DictLanguageType.FRA,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("fr"), DictLanguageType.getNameByLangISO3("fra") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"fra"}
//                    )
//            );
            loadTts(DictLanguageType.FRA, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.DEU || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //欧路 德
                            DictLanguageType.DEU,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("de"), DictLanguageType.getNameByLangISO2("de") , "o"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"de"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 德
//                            DictLanguageType.DEU,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("de"), DictLanguageType.getNameByLangISO3("deu") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"de"}
//                    )
//            );
            loadTts(DictLanguageType.DEU, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.SPA || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //欧路 西
                            DictLanguageType.SPA,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("es"), DictLanguageType.getNameByLangISO2("es"), "y"),
                            SOUND_SOURCE_EUDICT,
                            new String[] {"es"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 西
//                            DictLanguageType.SPA,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("es"), DictLanguageType.getNameByLangISO3("spa") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"spa"}
//                    )
//            );
            loadTts(DictLanguageType.SPA, toLoadTTS);
        }
        if(dictLanguageType == DictLanguageType.THA || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //有道 泰
                            DictLanguageType.THA,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("th"), DictLanguageType.getNameByLangISO2("th"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"th"}
                    )
            );
//            soundInforList.add(
//                    new SoundInformation(
//                            //Bd 泰
//                            DictLanguageType.THA,
//                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("th"), DictLanguageType.getNameByLangISO3("tha") + "-b"),
//                            SOUND_SOURCE_BD,
//                            new String[] {"th"}
//                    )
//            );
            loadTts(DictLanguageType.THA, toLoadTTS);
        }

        if(dictLanguageType == DictLanguageType.ARA || dictLanguageType == DictLanguageType.ALL) {
            soundInforList.add(
                    new SoundInformation(
                            //有道 阿拉伯
                            DictLanguageType.ARA,
                            String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2("sa"), DictLanguageType.getNameByLangISO2("ar"), "y"),
                            SOUND_SOURCE_YOUDAO,
                            new String[] {"ar"}
                    )
            );
            loadTts(DictLanguageType.ARA, toLoadTTS);
        }


        if(isExistAudioOrVideoUrl) {
            soundInforList.add(
                    new SoundInformation(
                            dictLanguageType,
                            soundName,
                            PronounceManager.SOUND_SOURCE_ONLINE,
                            new String[]{}
                    ));
        }

        String dictSoundNames[] = new String[soundInforList.size()];
        for(int index = 0; index < soundInforList.size(); index++) {
            dictSoundNames[index] = soundInforList.get(index).getSoundName();
        }
        return dictSoundNames;
    }


    private static void loadTts(int dictLanguageType, boolean toLoadTTS) {
        if(dictLanguageType != DictLanguageType.ALL && toLoadTTS) {
            if(ttsVoiceList.isEmpty()) return;

            for(Voice voice : ttsVoiceList) {
                Locale locale = voice.getLocale();
                String name = voice.getName().split("_")[0];
                if (locale.getLanguage().equals(DictLanguageType.getLangISO2ByLTId(dictLanguageType))) {
                    soundInforList.add(
                            new SoundInformation(
                                    //欧路 汉
                                    dictLanguageType,
                                    String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2(locale.getCountry()), DictLanguageType.getNameByLocale(locale), name),
                                    SOUND_SOURCE_TTS,
                                    new String[]{locale.getLanguage(), locale.getCountry()},
                                    voice
                            ));
                }
            }
        }
    }


//    private static void loadTts(int dictLanguageType, boolean toLoadTTS) {
//
//        if(dictLanguageType != DictLanguageType.ALL && toLoadTTS) {
//            ArrayList<String> available = new ArrayList<>(Arrays.asList(Constant.SUPPORTEDLANGUAGES));
//            for(String language : available) {
//                String[] temp = language.split("-");
////                Log.i("available language", temp[0]);
////                Log.i("CountryISO3 of Locale", DictLanguageType.getLanguageISO3ByLTId(dictLanguageType));
//
//                if (temp[0].equals(DictLanguageType.getLanguageISO3ByLTId(dictLanguageType))) {
//                    soundInforList.add(
//                            new SoundInformation(
//                                    //欧路 汉
//                                    dictLanguageType,
//                                    String.format("%s %s %s", DictLanguageType.getFlagByCountryISO2(temp[1]), DictLanguageType.getNameByLangISO3(temp[0])),
//                                    SOUND_SOURCE_TTS,
//                                    temp
//                            ));
//                }
//            }
//        }
//    }

    /*
     * 语音信息类
     * 用于保存语音信息：语言、声音名称、声源、locale字符串化
     */
    public static class SoundInformation {
        private int dictLanguageType;
        private String soundName;
        private int soundSourceType;
        private String[] langAndCountry;
        private Locale locale;
        private Voice voice;

        public String[] getLangAndCountry() {
            return langAndCountry;
        }

        public Locale getLocale() {
            return locale;
        }

        public SoundInformation(int dictLanguageType, String soundName, int soundSourceType, String[] langAndCountry) {
            this.dictLanguageType = dictLanguageType;
            this.soundName = soundName;
            this.soundSourceType = soundSourceType;
            this.langAndCountry = langAndCountry;
            this.voice = null;
        }

        public Voice getVoice() {
            return voice;
        }

        public SoundInformation(int dictLanguageType, String soundName, int soundSourceType, String[] langAndCountry, Voice voice) {
            this.dictLanguageType = dictLanguageType;
            this.soundName = soundName;
            this.soundSourceType = soundSourceType;
            this.langAndCountry = langAndCountry;
            this.voice = voice;
        }
        public int getDictLanguageType() {
            return dictLanguageType;
        }
        public void setDictLanguageType(int dictLanguageType) {
            this.dictLanguageType = dictLanguageType;
        }

        public String getSoundName() {
            return soundName;
        }

        public int getSoundSourceType() {
            return soundSourceType;
        }
        public boolean isSoundSourceType(int soundSourceType) {
            if(this.soundSourceType == soundSourceType) {
                return true;
            } else {
                return false;
            }
        }

    }
}
