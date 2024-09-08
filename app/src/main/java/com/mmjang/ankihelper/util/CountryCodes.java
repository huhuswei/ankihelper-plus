package com.mmjang.ankihelper.util;

public class CountryCodes {
    // 中国的国家代码常量
    public static final String CHINA = "CN";
    public static final String CHINA_HK = "HK";

    // 英国的国家代码常量
    public static final String UNITED_KINGDOM = "GB";
    // 美国的国家代码常量
    public static final String UNITED_STATES = "US";
    // 俄罗斯的国家代码常量
    public static final String RUSSIA = "RU";
    // 朝鲜的国家代码常量
    public static final String NORTH_KOREA = "KP";
    // 韩国的国家代码常量
    public static final String SOUTH_KOREA = "KR";
    // 泰国的国家代码常量
    public static final String THAILAND = "TH";
    // 伊朗的国家代码常量
    public static final String IRAN = "IR";
    // 沙特阿拉伯的国家代码常量
    public static final String SAUDI_ARABIA = "SA";
    // 日本的国家代码常量
    public static final String JAPAN = "JP";
    // 法国的国家代码常量
    public static final String FRANCE = "FR";
    // 西班牙的国家代码常量
    public static final String SPAIN = "ES";
    // 德国的国家代码常量
    public static final String GERMANY = "DE";

    // 可以添加一个方法来根据国家名称获取国家代码
    public static String getCodeByName(String countryName) {
        switch (countryName.toUpperCase()) {
            case "CHINA":
                return CHINA;
            case "UNITED KINGDOM":
                return UNITED_KINGDOM;
            case "UNITED STATES":
                return UNITED_STATES;
            case "RUSSIA":
                return RUSSIA;
            case "NORTH KOREA":
                return NORTH_KOREA;
            case "SOUTH KOREA":
                return SOUTH_KOREA;
            case "THAILAND":
                return THAILAND;
            case "IRAN":
                return IRAN;
            case "SAUDI ARABIA":
                return SAUDI_ARABIA;
            case "JAPAN":
                return JAPAN;
            case "FRANCE":
                return FRANCE;
            case "SPAIN":
                return SPAIN;
            case "GERMANY":
                return GERMANY;
            default:
                throw new IllegalArgumentException("Country code not found for: " + countryName);
        }
    }
}