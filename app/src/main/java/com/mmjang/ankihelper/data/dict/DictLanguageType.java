package com.mmjang.ankihelper.data.dict;

import com.mmjang.ankihelper.util.CountryCodes;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.StringUtil;
import com.mmjang.ankihelper.util.Trace;

import java.util.HashMap;
import java.util.Locale;

//Dictionary language type
public class DictLanguageType {
    public static final int NAN = -1;
    public static final int ZHO = 1;
    public static final int RUS = 2;
    public static final int ENG = 4;
    public static final int FRA = 8;
    public static final int DEU = 16;
    public static final int SPA = 32;
    public static final int JPN = 64;
    public static final int KOR = 128;
    public static final int THA = 256;
    public static final int ARA = 512;
    public static final int ALL = 1024;
    

    public static boolean isExistLT(int type) {
        switch(type) {
            case ZHO:
            case RUS:
            case ENG:
            case FRA:
            case DEU:
            case SPA:
            case JPN:
            case KOR:
            case THA:
            case ARA:
            case ALL:
                return true;
            default:
                return false;
        }
    }

//    public static int getLangIndex(int type) {
//        switch(type) {
//            case ZHO:
//                return 0;
//            case RUS:
//                return 1;
//            case ENG:
//                return 2;
//            case FRA:
//                return 3;
//            case DEU:
//                return 4;
//            case SPA:
//                return 5;
//            case JPN:
//                return 6;
//            case KOR:
//                return 7;
//            case THA:
//                return 8;
//            default:
//                return 9;
//        }
//    }

    public static String[] getLanguageNameList() {

        String[] dictSoundNames = new String[]{
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("cn"), "汉语"),//0
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("ru"), "русский"),//1
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("gb"), "English"),//2
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("fr"), "français"),//3
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("de"), "Deutsch"),//4
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("es"), "español"),//5
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("jp"), "日本語"),//6
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("kp"), "조선말"),//7
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("th"), "ไทย"),//8
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("SA")+DictLanguageType.getFlagByCountryISO2("IQ")+DictLanguageType.getFlagByCountryISO2("EG")+DictLanguageType.getFlagByCountryISO2("AE"), "لغة عربية"),//9
            String.format("%s %s", DictLanguageType.getFlagByCountryISO2("all"), "未定")//10
        };
        return dictSoundNames;
    }

//    public static String[] getLanguageNameList() {
//
//        String[] dictSoundNames = new String[]{
//                DictLanguageType.getFlagByCountryISO2("CHN"),//0
//                DictLanguageType.getFlagByCountryISO2("RUS"),//1
//                DictLanguageType.getFlagByCountryISO2("GBR"),//2
//                DictLanguageType.getFlagByCountryISO2("FRA"),//3
//                DictLanguageType.getFlagByCountryISO2("DEU"),//4
//                DictLanguageType.getFlagByCountryISO2("ESP"),//5
//                DictLanguageType.getFlagByCountryISO2("PRK"),//6
//                DictLanguageType.getFlagByCountryISO2("JPN"),//7
//                DictLanguageType.getFlagByCountryISO2("THA"),//8
//                DictLanguageType.getFlagByCountryISO2("ALL")//9
//        };
//        return dictSoundNames;
//    }


    public static String getLanguageISO3ByLTId(int languageType) {
        HashMap<Integer, String> ltMap = new HashMap<>();
        ltMap.put(DictLanguageType.ZHO, "zho");
        ltMap.put(DictLanguageType.RUS, "rus");
        ltMap.put(DictLanguageType.ENG, "eng");
        ltMap.put(DictLanguageType.FRA, "fra");
        ltMap.put(DictLanguageType.DEU, "deu");
        ltMap.put(DictLanguageType.SPA, "spa");
        ltMap.put(DictLanguageType.JPN, "jpn");
        ltMap.put(DictLanguageType.KOR, "kor");
        ltMap.put(DictLanguageType.THA, "tha");
        ltMap.put(DictLanguageType.ARA, "ara");
        //extend languauage iso3 by diy
        ltMap.put(DictLanguageType.ALL, "ALL");
        ltMap.put(DictLanguageType.ALL|ZHO, "ALL-zho");
        ltMap.put(DictLanguageType.ALL|RUS, "ALL-rus");
        ltMap.put(DictLanguageType.ALL|ENG, "ALL-eng");
        ltMap.put(DictLanguageType.ALL|FRA, "ALL-fra");
        ltMap.put(DictLanguageType.ALL|DEU, "ALL-deu");
        ltMap.put(DictLanguageType.ALL|SPA, "ALL-spa");
        ltMap.put(DictLanguageType.ALL|JPN, "ALL-jpn");
        ltMap.put(DictLanguageType.ALL|KOR, "ALL-kor");
        ltMap.put(DictLanguageType.ALL|THA, "ALL-tha");
        ltMap.put(DictLanguageType.ALL|ARA, "ALL-ara");
        return ltMap.get(languageType);
    }

    public static int getLTIdByLangISO2(String lang) {
        HashMap<String, Integer> ltMap = new HashMap<String, Integer>();
        ltMap.put("zh", ZHO);
        ltMap.put("ru", RUS);
        ltMap.put("en", ENG);
        ltMap.put("fr", FRA);
        ltMap.put("de", DEU);
        ltMap.put("es", SPA);
        ltMap.put("ja", JPN);
        ltMap.put("kp", KOR);
        ltMap.put("kr", KOR);
        ltMap.put("th", THA);
        ltMap.put("ar", ARA);
        ltMap.put("all", ALL);
        return ltMap.containsKey((String) lang) ? ltMap.get(lang) : ltMap.get("all");
    }

    public static String getLangISO2ByLTId(final int lt) {
        switch (lt) {
            case ZHO:
                return "zh";
            case RUS:
                return "ru";
            case ENG:
                return "en";
            case FRA:
                return "fr";
            case DEU:
                return "de";
            case SPA:
                return "es";
            case JPN:
                return "ja";
            case KOR:
                return "ko";
            case THA:
                return "th";
            case ARA:
                return "ar";
            case ALL:
            default:
                return "all";
        }
    }

    public static String getNameByLocale(Locale locale) {
        String country = locale.getCountry();
        String language = locale.getLanguage();

        switch (language.toLowerCase()) {
            case "zh":
                switch (country.toUpperCase()) {
                    case CountryCodes.CHINA_HK: return "粤语";
                    default: return "普通话";
                }
            case "ru": return "русский";
            case "kp": return "조선말";
            case "ko": return "한국어";
            case "ja": return "日本語";
            case "en": return "English";
            case "fr": return "français";
            case "de": return "Deutsch";
            case "es": return "español";
            case "th": return "ไทย";
            case "ar": return "لغة عربية";
            default:
                return language;
        }
    }
    public static String getNameByLangISO2(String lang) {
        Locale locale = new Locale(lang);
        return getNameByLocale(locale);
    }



    public static String getNameByLangISO3(String lang) {
        switch (lang) {
            case "zho": return "汉语";
            case "rus": return "русский";
            case "prk": return "조선말";
            case "kor": return "한국어";
            case "jpn": return "日本語";
            case "eng": return "English";
            case "fra": return "français";
            case "deu": return "Deutsch";
            case "spa": return "español";
            case "tha": return "ไทย";
            default:
                return lang;
        }
    }
    public static int getLTIdByWord(String str) {
        str = str.trim();
        int type = ALL;
        if(str.equals(""))
            return type;

//        String[]  words = str.split(" ");
//        String word = "";
//        if(words.length>0)
//            word = words[0];
//        else
//            word = str;

        Trace.i("getLTIdByWord", str);
        //中文
        if (RegexUtil.isChinese(str.charAt(0))) {
            type = DictLanguageType.ZHO;
        }
        //俄语
        else if (RegexUtil.isRussian(str)) {
            type = DictLanguageType.RUS;
        }
        //英文
        else if (RegexUtil.isEnglish(str)) {
            type = DictLanguageType.ENG;
        }
        // 日
        else if (StringUtil.isJapanese(str)) {
            type = DictLanguageType.JPN;
        }
        // 韩
        else if (RegexUtil.isKorean(str.charAt(0))) {
            type = DictLanguageType.KOR;
        }
        // 泰
        else if (RegexUtil.isThai(str)) {
            type = DictLanguageType.THA;
        }
        // 阿拉伯
        else if (RegexUtil.isArabic(str)) {
            type = DictLanguageType.ARA;
        }

        return type;
    }

    public static String getFlagByCountryISO2(String country) {
        switch (country.toUpperCase()) {
//            case "AW" : return "🇦🇼";
//            case "AF" : return "🇦🇫";
//            case "AO" : return "🇦🇴";
//            case "AI" : return "🇦🇮";
//            case "AX" : return "🇦🇽";
//            case "AL" : return "🇦🇱";
//            case "AD" : return "🇦🇩";
            case "AE" : return "🇦🇪";
            case "AR" : return "🇦🇷";
//            case "AM" : return "🇦🇲";
//            case "AS" : return "🇦🇸";
//            case "AQ" : return "🇦🇶";
//            case "TF" : return "🇹🇫";
//            case "AG" : return "🇦🇬";
            case "AU" : return "🇦🇺";
            case "AT" : return "🇦🇹";
//            case "AZ" : return "🇦🇿";
//            case "BI" : return "🇧🇮";
            case "BE" : return "🇧🇪";
//            case "BJ" : return "🇧🇯";
//            case "BF" : return "🇧🇫";
//            case "BD" : return "🇧🇩";
//            case "BG" : return "🇧🇬";
//            case "BH" : return "🇧🇭";
//            case "BS" : return "🇧🇸";
//            case "BA" : return "🇧🇦";
//            case "BL" : return "🇧🇱";
//            case "BY" : return "🇧🇾";
//            case "BZ" : return "🇧🇿";
//            case "BM" : return "🇧🇲";
//            case "BO" : return "🇧🇴";
//            case "BR" : return "🇧🇷";
//            case "BB" : return "🇧🇧";
//            case "BN" : return "🇧🇳";
//            case "BT" : return "🇧🇹";
//            case "BV" : return "🇧🇻";
//            case "BW" : return "🇧🇼";
//            case "CF" : return "🇨🇫";
            case "CA" : return "🇨🇦";
//            case "CC" : return "🇨🇨";
            case "CH" : return "🇨🇭";
//            case "CL" : return "🇨🇱";
            case "CN" : return "🇨🇳";
//            case "CI" : return "🇨🇮";
//            case "CM" : return "🇨🇲";
//            case "CD" : return "🇨🇩";
//            case "CG" : return "🇨🇬";
//            case "CK" : return "🇨🇰";
            case "CO" : return "🇨🇴";
//            case "KM" : return "🇰🇲";
//            case "CV" : return "🇨🇻";
//            case "CR" : return "🇨🇷";
//            case "CU" : return "🇨🇺";
//            case "CX" : return "🇨🇽";
//            case "KY" : return "🇰🇾";
//            case "CY" : return "🇨🇾";
//            case "CZ" : return "🇨🇿";
            case "DE" : return "🇩🇪";
//            case "DJ" : return "🇩🇯";
//            case "DM" : return "🇩🇲";
//            case "DK" : return "🇩🇰";
//            case "DO" : return "🇩🇴";
//            case "DZ" : return "🇩🇿";
//            case "EC" : return "🇪🇨";
            case "EG" : return "🇪🇬";
//            case "ER" : return "🇪🇷";
//            case "EH" : return "🇪🇭";
            case "ES" : return "🇪🇸";
//            case "EE" : return "🇪🇪";
//            case "ET" : return "🇪🇹";
//            case "FI" : return "🇫🇮";
//            case "FJ" : return "🇫🇯";
//            case "FK" : return "🇫🇰";
            case "FR" : return "🇫🇷";
//            case "FO" : return "🇫🇴";
//            case "FM" : return "🇫🇲";
//            case "GA" : return "🇬🇦";
            case "GB" : return "🇬🇧";
//            case "GE" : return "🇬🇪";
//            case "GG" : return "🇬🇬";
//            case "GH" : return "🇬🇭";
//            case "GI" : return "🇬🇮";
//            case "GN" : return "🇬🇳";
//            case "GP" : return "🇬🇵";
//            case "GM" : return "🇬🇲";
//            case "GW" : return "🇬🇼";
//            case "GQ" : return "🇬🇶";
//            case "GR" : return "🇬🇷";
//            case "GD" : return "🇬🇩";
//            case "GL" : return "🇬🇱";
//            case "GT" : return "🇬🇹";
//            case "GF" : return "🇬🇫";
//            case "GU" : return "🇬🇺";
//            case "GY" : return "🇬🇾";
            case "HK" : return "🇭🇰";
//            case "HM" : return "🇭🇲";
//            case "HN" : return "🇭🇳";
//            case "HR" : return "🇭🇷";
//            case "HT" : return "🇭🇹";
//            case "HU" : return "🇭🇺";
//            case "ID" : return "🇮🇩";
//            case "IM" : return "🇮🇲";
            case "IN" : return "🇮🇳";
//            case "IO" : return "🇮🇴";
            case "IE" : return "🇮🇪";
//            case "IR" : return "🇮🇷";
            case "IQ" : return "🇮🇶";
//            case "IS" : return "🇮🇸";
//            case "IL" : return "🇮🇱";
//            case "IT" : return "🇮🇹";
//            case "JM" : return "🇯🇲";
//            case "JE" : return "🇯🇪";
//            case "JO" : return "🇯🇴";
            case "JP" : return "🇯🇵";
//            case "KZ" : return "🇰🇿";
//            case "KE" : return "🇰🇪";
//            case "KG" : return "🇰🇬";
//            case "KH" : return "🇰🇭";
//            case "KI" : return "🇰🇮";
//            case "KN" : return "🇰🇳";
            case "KR" : return "🇰🇷";
//            case "KW" : return "🇰🇼";
//            case "LA" : return "🇱🇦";
//            case "LB" : return "🇱🇧";
//            case "LR" : return "🇱🇷";
//            case "LY" : return "🇱🇾";
//            case "LC" : return "🇱🇨";
//            case "LI" : return "🇱🇮";
//            case "LK" : return "🇱🇰";
//            case "LS" : return "🇱🇸";
//            case "LT" : return "🇱🇹";
//            case "LU" : return "🇱🇺";
//            case "LV" : return "🇱🇻";
//            case "MO" : return "🇲🇴";
//            case "MF" : return "🇲🇫";
//            case "MA" : return "🇲🇦";
//            case "MC" : return "🇲🇨";
//            case "MD" : return "🇲🇩";
//            case "MG" : return "🇲🇬";
//            case "MV" : return "🇲🇻";
            case "MX" : return "🇲🇽";
//            case "MH" : return "🇲🇭";
//            case "MK" : return "🇲🇰";
//            case "ML" : return "🇲🇱";
//            case "MT" : return "🇲🇹";
//            case "MM" : return "🇲🇲";
//            case "ME" : return "🇲🇪";
//            case "MN" : return "🇲🇳";
//            case "MP" : return "🇲🇵";
//            case "MZ" : return "🇲🇿";
//            case "MR" : return "🇲🇷";
//            case "MS" : return "🇲🇸";
//            case "MQ" : return "🇲🇶";
//            case "MU" : return "🇲🇺";
//            case "MW" : return "🇲🇼";
//            case "MY" : return "🇲🇾";
//            case "YT" : return "🇾🇹";
//            case "NA" : return "🇳🇦";
//            case "NC" : return "🇳🇨";
//            case "NE" : return "🇳🇪";
//            case "NF" : return "🇳🇫";
//            case "NG" : return "🇳🇬";
//            case "NI" : return "🇳🇮";
//            case "NU" : return "🇳🇺";
//            case "NL" : return "🇳🇱";
//            case "NO" : return "🇳🇴";
//            case "NP" : return "🇳🇵";
//            case "NR" : return "🇳🇷";
            case "NZ" : return "🇳🇿";
//            case "OM" : return "🇴🇲";
//            case "PK" : return "🇵🇰";
//            case "PA" : return "🇵🇦";
//            case "PN" : return "🇵🇳";
//            case "PE" : return "🇵🇪";
            case "PH" : return "🇵🇭";
//            case "PW" : return "🇵🇼";
//            case "PG" : return "🇵🇬";
//            case "PL" : return "🇵🇱";
//            case "PR" : return "🇵🇷";
            case "KP" : return "🇰🇵";
//            case "PT" : return "🇵🇹";
//            case "PY" : return "🇵🇾";
//            case "PS" : return "🇵🇸";
//            case "PF" : return "🇵🇫";
//            case "QA" : return "🇶🇦";
//            case "RE" : return "🇷🇪";
//            case "RO" : return "🇷🇴";
            case "RU" : return "🇷🇺";
//            case "RW" : return "🇷🇼";
            case "SA" : return "🇸🇦";
//            case "SD" : return "🇸🇩";
//            case "SN" : return "🇸🇳";
            case "SG" : return "🇸🇬";
//            case "GS" : return "🇬🇸";
//            case "SH" : return "🇸🇭";
//            case "SJ" : return "🇸🇯";
//            case "SB" : return "🇸🇧";
//            case "SL" : return "🇸🇱";
//            case "SV" : return "🇸🇻";
//            case "SM" : return "🇸🇲";
//            case "SO" : return "🇸🇴";
//            case "PM" : return "🇵🇲";
//            case "RS" : return "🇷🇸";
//            case "SS" : return "🇸🇸";
//            case "ST" : return "🇸🇹";
//            case "SR" : return "🇸🇷";
//            case "SK" : return "🇸🇰";
//            case "SI" : return "🇸🇮";
//            case "SE" : return "🇸🇪";
//            case "SZ" : return "🇸🇿";
//            case "SC" : return "🇸🇨";
//            case "SY" : return "🇸🇾";
//            case "TC" : return "🇹🇨";
//            case "TD" : return "🇹🇩";
//            case "TG" : return "🇹🇬";
            case "TH" : return "🇹🇭";
//            case "TJ" : return "🇹🇯";
//            case "TK" : return "🇹🇰";
//            case "TM" : return "🇹🇲";
//            case "TL" : return "🇹🇱";
//            case "TO" : return "🇹🇴";
//            case "TT" : return "🇹🇹";
//            case "TN" : return "🇹🇳";
//            case "TR" : return "🇹🇷";
//            case "TV" : return "🇹🇻";
            case "TW" : return "🇨🇳";
//            case "TZ" : return "🇹🇿";
//            case "UG" : return "🇺🇬";
//            case "UA" : return "🇺🇦";
//            case "UM" : return "🇺🇲";
//            case "UY" : return "🇺🇾";
            case "US" : return "🇺🇸";
//            case "UZ" : return "🇺🇿";
//            case "VA" : return "🇻🇦";
//            case "VC" : return "🇻🇨";
//            case "VE" : return "🇻🇪";
//            case "VG" : return "🇻🇬";
//            case "VI" : return "🇻🇮";
//            case "VN" : return "🇻🇳";
//            case "VU" : return "🇻🇺";
//            case "WF" : return "🇼🇫";
//            case "WS" : return "🇼🇸";
//            case "YE" : return "🇾🇪";
            case "ZA" : return "🇿🇦";
//            case "ZM" : return "🇿🇲";
//            case "ZW" : return "🇿🇼";
            default:
                return "❓";
        }
    }

//    public static String getFlagByCountryISO3(String country) {
//        switch (country.toUpperCase()) {
////            case "ABW": return "🇦🇼";
////            case "AFG": return "🇦🇫";
////            case "AGO": return "🇦🇴";
////            case "AIA": return "🇦🇮";
////            case "ALA": return "🇦🇽";
////            case "ALB": return "🇦🇱";
////            case "AND": return "🇦🇩";
////            case "ARE": return "🇦🇪";
//            case "ARG": return "🇦🇷";
////            case "ARM": return "🇦🇲";
////            case "ASM": return "🇦🇸";
////            case "ATA": return "🇦🇶";
////            case "ATF": return "🇹🇫";
////            case "ATG": return "🇦🇬";
//            case "AUS": return "🇦🇺";
//            case "AUT": return "🇦🇹";
////            case "AZE": return "🇦🇿";
////            case "BDI": return "🇧🇮";
//            case "BEL": return "🇧🇪";
////            case "BEN": return "🇧🇯";
////            case "BFA": return "🇧🇫";
////            case "BGD": return "🇧🇩";
////            case "BGR": return "🇧🇬";
////            case "BHR": return "🇧🇭";
////            case "BHS": return "🇧🇸";
////            case "BIH": return "🇧🇦";
////            case "BLM": return "🇧🇱";
////            case "BLR": return "🇧🇾";
////            case "BLZ": return "🇧🇿";
////            case "BMU": return "🇧🇲";
////            case "BOL": return "🇧🇴";
////            case "BRA": return "🇧🇷";
////            case "BRB": return "🇧🇧";
////            case "BRN": return "🇧🇳";
////            case "BTN": return "🇧🇹";
////            case "BVT": return "🇧🇻";
////            case "BWA": return "🇧🇼";
////            case "CAF": return "🇨🇫";
//            case "CAN": return "🇨🇦";
////            case "CCK": return "🇨🇨";
//            case "CHE": return "🇨🇭";
////            case "CHL": return "🇨🇱";
//            case "CHN": return "🇨🇳";
////            case "CIV": return "🇨🇮";
////            case "CMR": return "🇨🇲";
////            case "COD": return "🇨🇩";
////            case "COG": return "🇨🇬";
////            case "COK": return "🇨🇰";
//            case "COL": return "🇨🇴";
////            case "COM": return "🇰🇲";
////            case "CPV": return "🇨🇻";
////            case "CRI": return "🇨🇷";
////            case "CUB": return "🇨🇺";
////            case "CXR": return "🇨🇽";
////            case "CYM": return "🇰🇾";
////            case "CYP": return "🇨🇾";
////            case "CZE": return "🇨🇿";
//            case "DEU": return "🇩🇪";
////            case "DJI": return "🇩🇯";
////            case "DMA": return "🇩🇲";
////            case "DNK": return "🇩🇰";
////            case "DOM": return "🇩🇴";
////            case "DZA": return "🇩🇿";
////            case "ECU": return "🇪🇨";
////            case "EGY": return "🇪🇬";
////            case "ERI": return "🇪🇷";
////            case "ESH": return "🇪🇭";
//            case "ESP": return "🇪🇸";
////            case "EST": return "🇪🇪";
////            case "ETH": return "🇪🇹";
////            case "FIN": return "🇫🇮";
////            case "FJI": return "🇫🇯";
////            case "FLK": return "🇫🇰";
//            case "FRA": return "🇫🇷";
////            case "FRO": return "🇫🇴";
////            case "FSM": return "🇫🇲";
////            case "GAB": return "🇬🇦";
//            case "GBR": return "🇬🇧";
////            case "GEO": return "🇬🇪";
////            case "GGY": return "🇬🇬";
////            case "GHA": return "🇬🇭";
////            case "GIB": return "🇬🇮";
////            case "GIN": return "🇬🇳";
////            case "GLP": return "🇬🇵";
////            case "GMB": return "🇬🇲";
////            case "GNB": return "🇬🇼";
////            case "GNQ": return "🇬🇶";
////            case "GRC": return "🇬🇷";
////            case "GRD": return "🇬🇩";
////            case "GRL": return "🇬🇱";
////            case "GTM": return "🇬🇹";
////            case "GUF": return "🇬🇫";
////            case "GUM": return "🇬🇺";
////            case "GUY": return "🇬🇾";
//            case "HKG": return "🇭🇰";
////            case "HMD": return "🇭🇲";
////            case "HND": return "🇭🇳";
////            case "HRV": return "🇭🇷";
////            case "HTI": return "🇭🇹";
////            case "HUN": return "🇭🇺";
////            case "IDN": return "🇮🇩";
////            case "IMN": return "🇮🇲";
//            case "IND": return "🇮🇳";
////            case "IOT": return "🇮🇴";
//            case "IRL": return "🇮🇪";
////            case "IRN": return "🇮🇷";
////            case "IRQ": return "🇮🇶";
////            case "ISL": return "🇮🇸";
////            case "ISR": return "🇮🇱";
////            case "ITA": return "🇮🇹";
////            case "JAM": return "🇯🇲";
////            case "JEY": return "🇯🇪";
////            case "JOR": return "🇯🇴";
//            case "JPN": return "🇯🇵";
////            case "KAZ": return "🇰🇿";
////            case "KEN": return "🇰🇪";
////            case "KGZ": return "🇰🇬";
////            case "KHM": return "🇰🇭";
////            case "KIR": return "🇰🇮";
////            case "KNA": return "🇰🇳";
//            case "KOR": return "🇰🇷";
////            case "KWT": return "🇰🇼";
////            case "LAO": return "🇱🇦";
////            case "LBN": return "🇱🇧";
////            case "LBR": return "🇱🇷";
////            case "LBY": return "🇱🇾";
////            case "LCA": return "🇱🇨";
////            case "LIE": return "🇱🇮";
////            case "LKA": return "🇱🇰";
////            case "LSO": return "🇱🇸";
////            case "LTU": return "🇱🇹";
////            case "LUX": return "🇱🇺";
////            case "LVA": return "🇱🇻";
////            case "MAC": return "🇲🇴";
////            case "MAF": return "🇲🇫";
////            case "MAR": return "🇲🇦";
////            case "MCO": return "🇲🇨";
////            case "MDA": return "🇲🇩";
////            case "MDG": return "🇲🇬";
////            case "MDV": return "🇲🇻";
//            case "MEX": return "🇲🇽";
////            case "MHL": return "🇲🇭";
////            case "MKD": return "🇲🇰";
////            case "MLI": return "🇲🇱";
////            case "MLT": return "🇲🇹";
////            case "MMR": return "🇲🇲";
////            case "MNE": return "🇲🇪";
////            case "MNG": return "🇲🇳";
////            case "MNP": return "🇲🇵";
////            case "MOZ": return "🇲🇿";
////            case "MRT": return "🇲🇷";
////            case "MSR": return "🇲🇸";
////            case "MTQ": return "🇲🇶";
////            case "MUS": return "🇲🇺";
////            case "MWI": return "🇲🇼";
////            case "MYS": return "🇲🇾";
////            case "MYT": return "🇾🇹";
////            case "NAM": return "🇳🇦";
////            case "NCL": return "🇳🇨";
////            case "NER": return "🇳🇪";
////            case "NFK": return "🇳🇫";
////            case "NGA": return "🇳🇬";
////            case "NIC": return "🇳🇮";
////            case "NIU": return "🇳🇺";
////            case "NLD": return "🇳🇱";
////            case "NOR": return "🇳🇴";
////            case "NPL": return "🇳🇵";
////            case "NRU": return "🇳🇷";
//            case "NZL": return "🇳🇿";
////            case "OMN": return "🇴🇲";
////            case "PAK": return "🇵🇰";
////            case "PAN": return "🇵🇦";
////            case "PCN": return "🇵🇳";
////            case "PER": return "🇵🇪";
//            case "PHL": return "🇵🇭";
////            case "PLW": return "🇵🇼";
////            case "PNG": return "🇵🇬";
////            case "POL": return "🇵🇱";
////            case "PRI": return "🇵🇷";
//            case "PRK": return "🇰🇵";
////            case "PRT": return "🇵🇹";
////            case "PRY": return "🇵🇾";
////            case "PSE": return "🇵🇸";
////            case "PYF": return "🇵🇫";
////            case "QAT": return "🇶🇦";
////            case "REU": return "🇷🇪";
////            case "ROU": return "🇷🇴";
//            case "RUS": return "🇷🇺";
////            case "RWA": return "🇷🇼";
////            case "SAU": return "🇸🇦";
////            case "SDN": return "🇸🇩";
////            case "SEN": return "🇸🇳";
//            case "SGP": return "🇸🇬";
////            case "SGS": return "🇬🇸";
////            case "SHN": return "🇸🇭";
////            case "SJM": return "🇸🇯";
////            case "SLB": return "🇸🇧";
////            case "SLE": return "🇸🇱";
////            case "SLV": return "🇸🇻";
////            case "SMR": return "🇸🇲";
////            case "SOM": return "🇸🇴";
////            case "SPM": return "🇵🇲";
////            case "SRB": return "🇷🇸";
////            case "SSD": return "🇸🇸";
////            case "STP": return "🇸🇹";
////            case "SUR": return "🇸🇷";
////            case "SVK": return "🇸🇰";
////            case "SVN": return "🇸🇮";
////            case "SWE": return "🇸🇪";
////            case "SWZ": return "🇸🇿";
////            case "SYC": return "🇸🇨";
////            case "SYR": return "🇸🇾";
////            case "TCA": return "🇹🇨";
////            case "TCD": return "🇹🇩";
////            case "TGO": return "🇹🇬";
//            case "THA": return "🇹🇭";
////            case "TJK": return "🇹🇯";
////            case "TKL": return "🇹🇰";
////            case "TKM": return "🇹🇲";
////            case "TLS": return "🇹🇱";
////            case "TON": return "🇹🇴";
////            case "TTO": return "🇹🇹";
////            case "TUN": return "🇹🇳";
////            case "TUR": return "🇹🇷";
////            case "TUV": return "🇹🇻";
//            case "TWN": return "🇨🇳";
////            case "TZA": return "🇹🇿";
////            case "UGA": return "🇺🇬";
////            case "UKR": return "🇺🇦";
////            case "UMI": return "🇺🇲";
////            case "URY": return "🇺🇾";
//            case "USA": return "🇺🇸";
////            case "UZB": return "🇺🇿";
////            case "VAT": return "🇻🇦";
////            case "VCT": return "🇻🇨";
////            case "VEN": return "🇻🇪";
////            case "VGB": return "🇻🇬";
////            case "VIR": return "🇻🇮";
////            case "VNM": return "🇻🇳";
////            case "VUT": return "🇻🇺";
////            case "WLF": return "🇼🇫";
////            case "WSM": return "🇼🇸";
////            case "YEM": return "🇾🇪";
//            case "ZAF": return "🇿🇦";
////            case "ZMB": return "🇿🇲";
////            case "ZWE": return "🇿🇼";
//            default:
//                return "❓";
//        }
//    }

}
