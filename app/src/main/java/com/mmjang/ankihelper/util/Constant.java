package com.mmjang.ankihelper.util;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.widget.button.MLLabel;

/**
 * Created by liao on 2017/4/27.
 */

public class Constant {

    public static String[] getSharedExportElements() {
        return SHARED_EXPORT_ELEMENTS;
    }

    public static final String[] TANGO_DEFAULT_FIELDS = new String[]{
            "body",
            Constant.DICT_FIELD_SENSE,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION,
    };
//    //
    public static final String DICT_FIELD_EMPTY = "空";                                                   //0
    public static final String DICT_FIELD_SENTENCE_PICKED = "摘取例句";                                    //1
    public static final String DICT_FIELD_SENTENCE_PICKED_BOLD = "摘取例句（加粗）";                        //2
    public static final String DICT_FIELD_SENTENCE_PICKED_CLOZE_DELETION = "摘取例句（挖空）";              //3
    public static final String DICT_FIELD_SENTENCE_PICKED_CLOZE_DELETION_C1 = "摘取例句（挖空，全c1模式）";  //4
    public static final String DICT_FIELD_NOTE = "笔记";                                                  //5
    public static final String DICT_FIELD_URL = "URL";                                                   //6
    public static final String DICT_FIELD_FULL_DEFINITIONS = "所有释义";                                   //7
    public static final String DICT_FIELD_TRANSLATION = "句子翻译";                                        //8
    public static final String DICT_FIELD_KEY_DAV_ONLINE_LINK = "🔊|🎞🌐🔗";                               //9
//    public static final String DICT_FIELD_KEY_DAV_ONLINE_AUDIOTAG = "🔊|🎞🌐▶️[Audio]";                    //10
    public static final String DICT_FIELD_KEY_DAV_ONLINE_VIDEOTAG = "🔊|🎞🌐▶️[Video]";                    //10
    public static final String DICT_FIELD_KEY_DAV_ONLINE_SOUNDTAG = "🔊|🎞🌐▶️[Sound]";                    //11
    public static final String DICT_FIELD_KEY_DAV_OFFLINE_LINK = "🔊|🎞💾🔗";                              //12
//    public static final String DICT_FIELD_KEY_DAV_OFFLINE_AUDIOTAG = "🔊|🎞💾▶️[Audio]";                   //13
    public static final String DICT_FIELD_KEY_DAV_OFFLINE_VIDEOTAG = "🔊|🎞💾▶️[Video]";                   //13
    public static final String DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG = "🔊|🎞💾▶️[Sound](🍏Remarks)";        //14
    public static final String DICT_FIELD_KEY_TTS_SOUNDTAG = "🔊▶️[tts]";        //14

    public static final String DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_LINK = "摘取例句🔊🌐🔗";               //15
//    public static final String DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_AUDIOTAG = "摘取例句🔊🌐▶️[Audio]";    //16
    public static final String DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_SOUNDTAG = "摘取例句🔊🌐▶️[Sound]";    //16
    public static final String DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK = "摘取例句🔊💾🔗";              //17
//    public static final String DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_AUDIOTAG = "摘取例句🔊💾▶️[Audio]";   //18
    public static final String DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG = "摘取例句🔊💾▶️[Sound]";   //18
    public static final String DICT_FIELD_SENTENCE_PICKED_TTS_SOUNDTAG = "摘取例句🔊▶️[tts]";        //14

    // deprecated
//    public static final String DEPRECATED_DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG = "🔊|🎞💾▶️[Sound](🍏Remarks)";        //14
    //

    public static final String MDX_ADD_TAG ="+";
    public static final String DICT_FIELD_KEYWORD = "关键字";
    public static final String DICT_FIELD_PHONETICS = "音标";
    public static final String DICT_FIELD_DEFINITION = "释义";
    public static final String DICT_FIELD_SENSE = "词性";
    public static final String DICT_FIELD_CHINESE_SENTENCE = "例句中文";
    public static final String DICT_FIELD_SENTENCE = "例句";
    public static final String DICT_FIELD_ORIGIN = "origin";
    public static final String DICT_FIELD_IMG = "🖼💾[img]";

    public static final String DICT_FIELD_CLOZE_CONTENT = "挖空内容";
    public static final String DICT_FIELD_CLOZE_TRANSLATION = "翻译挖空内容";
    public static final String DICT_FIELD_JS = "js";
    public static final String DICT_FIELD_CSS = "css";
//    public static final String DICT_FIELD_COMPLEX_ONLINE = "复合项（在线音频）";
//    public static final String DICT_FIELD_COMPLEX_OFFLINE = "复合项（本地音频）";
//    public static final String DICT_FIELD_COMPLEX_MUTE = "复合项（无音频）";


    private static final String[] SHARED_EXPORT_ELEMENTS = new String[]{
            DICT_FIELD_EMPTY,// = "空";                                                   //0
            DICT_FIELD_SENTENCE_PICKED,// = "摘取例句";                                    //1
            DICT_FIELD_SENTENCE_PICKED_BOLD,// = "摘取例句（加粗）";                        //2
            DICT_FIELD_SENTENCE_PICKED_CLOZE_DELETION,// = "摘取例句（挖空）";              //3
            DICT_FIELD_SENTENCE_PICKED_CLOZE_DELETION_C1,// = "摘取例句（挖空，全c1模式）";  //4
            DICT_FIELD_NOTE,// = "笔记";                                                  //5
            DICT_FIELD_URL,// = "URL";                                                   //6
            DICT_FIELD_FULL_DEFINITIONS,// = "所有释义";                                   //7
            DICT_FIELD_TRANSLATION,// = "句子翻译";                                        //8
            DICT_FIELD_KEY_TTS_SOUNDTAG,
            DICT_FIELD_KEY_DAV_ONLINE_LINK,// = "🔊|🎞🌐🔗";                               //9
            DICT_FIELD_KEY_DAV_OFFLINE_LINK,// = "🔊|🎞💾🔗";                              //x12
//            DICT_FIELD_KEY_DAV_ONLINE_AUDIOTAG,// = "🔊|🎞🌐▶️[Audio]",
//            DICT_FIELD_KEY_DAV_ONLINE_VIDEOTAG,// = "🔊|🎞🌐▶️[Video]";                    //x10
            DICT_FIELD_KEY_DAV_ONLINE_SOUNDTAG,// = "🔊|🎞🌐▶️[Sound]";                    //11
//            DICT_FIELD_KEY_DAV_OFFLINE_AUDIOTAG,// = "🔊|🎞💾▶️[Audio]";
//            DICT_FIELD_KEY_DAV_OFFLINE_VIDEOTAG,// = "🔊|🎞💾▶️[Video]";                   //13
            DICT_FIELD_KEY_DAV_OFFLINE_SOUNDTAG,// = "🔊|🎞💾▶️[Sound](🍏Remarks)";        //14
            DICT_FIELD_SENTENCE_PICKED_TTS_SOUNDTAG,
            DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_LINK,// = "摘取例句🔊🌐🔗";               //15
            DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_LINK,// = "摘取例句🔊💾🔗";              //x17
//            DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_AUDIOTAG,// = "摘取例句🔊🌐▶️[Audio]";
            DICT_FIELD_SENTENCE_PICKED_DAV_ONLINE_SOUNDTAG,// = "摘取例句🔊🌐▶️[Sound]";    //16
//            DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_AUDIOTAG,// = "摘取例句🔊💾▶️[Audio]";   //18
            DICT_FIELD_SENTENCE_PICKED_DAV_OFFLINE_SOUNDTAG,// = "摘取例句🔊💾▶️[Sound]";   //18
//            DICT_FIELD_COMPLEX_ONLINE,
//            DICT_FIELD_COMPLEX_OFFLINE,
//            DICT_FIELD_COMPLEX_MUTE
    };

    public static final String TAB = "\t";
    public static final String JOINT = ", ";
    public static String[] getTangoDefaultFields() { return TANGO_DEFAULT_FIELDS; }

    public static final String[] MLKIT_TEXT_RECONGNITION_LANGS = new String[] {
            "汉语（Chinese）",
            "日本語（Japanese）",
            "한국의（朝鲜语）",
            "Latin（拉丁语系）",
            "संस्कृतम्（梵文）"

    };

    public static String getNameByTraineddataName(String lang) {
        switch (lang) {
            case "chi_sim": return "中文（简体）";
            case "chi_tra": return "中文（繁體）";
            case "ita": return "Italiano";
            case "rus": return "Русский";
            case "prk": return "조선말";
            case "kor": return "한국어";
            case "jpn": return "日本語";
            case "eng": return "English";
            case "fra": return "Français";
            case "deu": return "Deutsch";
            case "spa": return "Español";
            case "tha": return "ไทย";
            default:
                return lang;
        }
    }

    public enum NoteMode {
//            NEW_MODE("新建 New"),
//            REPLACE_MODE("覆盖 Replace"),
//            APPEND_MODE("追加 Append");
        NEW_MODE(R.string.note_mode_create),
        REPLACE_MODE(R.string.note_mode_replace),
        APPEND_MODE(R.string.note_mode_append);
        private int nameResId;
        NoteMode(int nameResId) {
            this.nameResId = nameResId;
        }

        public int getNameResId() {
            return nameResId;
        }
    }


    public enum Mode {
        NORMAL_MODE("普通模式"), LATEX_MODE("Latex模式");

        private String name;
        Mode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum DarkMode {
        MODE_NIGHT_FOLLOW_SYSTEM(R.string.dark_mode_system),
        MODE_NIGHT_NO(R.string.dark_mode_light),
        MODE_NIGHT_YES(R.string.dark_mode_dark);

        private int nameId;
        DarkMode(int nameId) {
            this.nameId = nameId;
        }

        public int getNameId() {
            return nameId;
        }
    }

    public enum ThemeColor {
        THEME_COLOR_GREEN(R.string.color_theme_green),
        THEME_COLOR_BLUE(R.string.color_theme_blue),
        THEME_COLOR_PINK(R.string.color_theme_pink);

        private int nameId;
        ThemeColor(int nameId) {
            this.nameId = nameId;
        }

        public int getNameId() {
            return nameId;
        }
    }

    public enum StyleBaseTheme{
        AppTheme, Transparent, BigBangTheme, ScreenCaptureTheme, CustomDicTheme, AndroidFilePickerTheme;
    }

    //HtmlTagArr
    public static MLLabel[] htmlTagArr = new MLLabel[] {
            new MLLabel("br", "<br/>", ""),
            new MLLabel("p", "<p>", "</p>"),
            new MLLabel("b", "<b>", "</b>"),
            new MLLabel("Red", "<font color=\"red\">", "</font>"),
            new MLLabel("Gold", "<font color=\"gold\">", "</font>"),
            new MLLabel("Green", "<font color=\"green\">", "</font>"),
            new MLLabel("i", "<i>", "</i>"),
            new MLLabel("h1", "<h1>", "</h1>"),
            new MLLabel("h2", "<h2>", "</h2>"),
            new MLLabel("h3", "<h3>", "</h3>"),
            new MLLabel("a",  "<a href=\"\">","</a>")
    };

    public static MLLabel[] latexTagArr = new MLLabel[] {
            new MLLabel("\\(...\\)", "\\(", "\\)"),
            new MLLabel("\\[...\\]", "\\[", "\\]"),
            new MLLabel("()", "(", ")"),
            new MLLabel("{}", "{", "}"),
            new MLLabel("\\", "\\",""),
            new MLLabel("+", "+", ""),
            new MLLabel("!", "!", ""),
            new MLLabel("^", "^", ""),
            new MLLabel("x^2", "x^", ""),
            new MLLabel("2^x", "", "^x")
    };

    public static final String INTENT_ANKIHELPER_TARGET_WORD = "com.mmjang.ankihelper.target_word";
    public static final String INTENT_ANKIHELPER_TARGET_URL = "com.mmjang.ankihelper.url";
    public static final String INTENT_ANKIHELPER_NOTE_ID = "com.mmjang.ankihelper.note_id";
    public static final String INTENT_ANKIHELPER_UPDATE_ACTION = "com.mmjang.ankihelper.note_update_action";//replace;append;
    public static final String COLORDICT_INTENT_ACTION_SEARCH = "colordict.intent.action.SEARCH";
    public static final String MDICT_INTENT_ACTION_SEARCH = "mdict.intent.action.SEARCH";
    public static final String INTENT_ANKIHELPER_BASE64 = "com.mmjang.ankihelper.base64";
    public static final String INTENT_ANKIHELPER_PLAN_NAME = "com.mmjang.ankihelper.plan_name";
    public static final String INTENT_ANKIHELPER_FBREADER_BOOKMARK_ID = "com.mmjang.ankihelper.fbreader.bookmark.id";
    public static final String ANKI_PACKAGE_NAME = "com.ichi2.anki";
    public static final String LATEX_EDITOR_NAME = "com.viclab.ocr";
    public static final String ANKIHELPER_PACKAGE_NAME = "com.mmjang.ankihelper";
    public static final String FBREADER_URL_TMPL = "<a href=\"intent:#Intent;action=android.fbreader.action.VIEW;category=android.intent.category.DEFAULT;type=text/plain;component=org.geometerplus.zlibrary.ui.android/org.geometerplus.android.fbreader.FBReader;S.fbreader.bookmarkid.from.external=%s;end;\">查看原文</a>";
    static final public String INTENT_ANKIHELPER_NOTE = "com.mmjang.ankihelper.note";
    static final public String ASSIST_SERVICE_INFO_ID = "com.mmjang.ankihelper/.ui.floating.assist.AssistService";
    static final public String QUICKSTARTTILE_SERVICE_ID = "com.mmjang.ankihelper.ui.floating.QuickStartTileService";

//    public static final int VIBRATE_DURATION = 10;
//
//    public static final float FLOAT_ACTION_BUTTON_ALPHA = 0.3f;

    static final public int FLOATING_HOVERING_DEFAULT_TIME_MS = 300;
    static final public String REG_USERNAME = "REG_USERNAME";
    static final public String REG_ACTIVATIONCODE = "REG_ACTIVATIONCODE";
    public static final String EXTERNAL_STORAGE_DIRECTORY = "ankihelper";
    public static final String EXTERNAL_STORAGE_CONTENT_SUBDIRECTORY = "content";
    public static final String EXTERNAL_STORAGE_TESSERACT_SUBDIRECTORY = "tesseract";
    public static final String EXTERNAL_STORAGE_TESSDATA_SUBDIRECTORY = "tessdata";

    public static final String EXTERNAL_STORAGE_FORMS_SUBDIRECTORY = "forms";
    public static final String MDX_FORMS_LATEST_VERSION = "20240203.txt";
    public static final String TRAINEDDATA_SUFFIX = ".traineddata";
    public static final String LEFT_BOLD_SUBSTITUDE = "☾";
    public static final String RIGHT_BOLD_SUBSTITUDE = "☽";

    public static final String INTENT_ANKIHELPER_CONTENT_INDEX = "com.mmjang.ankihelper.content_index";
    public static final String INTENT_ANKIHELPER_MDICT_ORDER = "com.mmjang.ankihelper.mdict_order";
    public static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";

    public static final String UA_FIREFOX_MACOS = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:56.0) " +
            "Gecko/20100101 Firefox/56.0";
//    public static final String IMAGE_SUB_DIRECTORY = "ankihelper_image";
//    public static final String AUDIO_SUB_DIRECTORY = "ankihelper_audio";

//    public static final String AUDIO_MEDIA_DIRECTORY = Environment.getExternalStorageDirectory()
//            + "/AnkiDroid/collection.media/"; // /AnkiDroid/collection.media/ankihelper_image/";

//    public static String ANKIDROID_MEDIA_DIRECTORY =
//            Environment.getExternalStorageDirectory() +
//                    "/AnkiDroid/collection.media/"; // /AnkiDroid/collection.media/ankihelper_audio/";

//    public static final String MDICT_DIRECTORY = Environment.getExternalStorageDirectory()
//            + "/eudb_en/en/";

    public static final String ANKIDROID = "AnkiDroid";
    public static final String ANKIDROID_MEDIA_DIRECTORY = "collection.media";
    public static final String USE_CLIPBOARD_CONTENT_FLAG = "use_clipboard_content_flag";
    public static final String FLOATING_USE_CLIPBOARD_CONTENT_FLAG = "floating_use_clipboard_content_flag";
    public static final String USE_FX_SERVICE_CB_FLAG = "use_fx_service_cb_flag";
    public static final String FLOATING_GET_CONTENT = "floating_get_content";
//    public static final String FINISH_POPUP_WINDOW_FLAG = "finish_popup_window_flag";

    public static final String MP3_SUFFIX = ".mp3";
    public static final String MP4_SUFFIX = ".mp4";

    public static final String PNG_SUFFIX = ".png";
    public static final long DEFAULT_MAX_SIZE = 512 * 1024 * 1024;
//    public static final String TPL_HTML_VIDEO_TAG = "<video id=\"video\" width=\"100%%\" src=\"%s\" controlList=\"nodownload\" controls=\"controls\"></video>";
//    public static final String TPL_HTML_AUDIO_TAG = "<audio id=\"audio\" src=\"%s\" controlList=\"nodownload\" controls=\"controls\"></audio>";
//    public static final String TPL_HTML_NOTE_TAG = "<div class=\"note\">%s</div>";
//    public static final String _TPL_HTML_NOTE_TAG_LOCATION_ = "_tpl_html_note_tag_location_";
//    public static final String _TPL_HTML_MEDIA_TAG_LOCATION_ = "_tpl_html_media_tag_location_";
    public static final String TPL_HTML_IMG_TAG = "<img src=\"%s\" />";
//    public static final String TPL_COMPLEX_TAG =
//            "<div class=\"complex\">" +
//                    "<div class=\"source\"><font color = \"#ba400d\">%s</font> %s</div>%s%s" +
//                    "</div>";

//    public static final String TPL_COMPLEX_DICT_WORD_TAG =
//            "<div class=\"complex\">" +
//                    "<div class=\"source\"><font color = \"#ba400d\">%s</font> %s</div>" +
//            "<div class=\"word_or_phrase_content\">"+
//            "<div class=\"word_or_phrase\">%s</div>" +
//            "<div class=\"phonetics\">%s</div>" +
//            "<div class=\"sense\">%s</div>" +
//            "<div class=\"defintion\">%s</div></div>" +
//                    _TPL_HTML_NOTE_TAG_LOCATION_ + _TPL_HTML_MEDIA_TAG_LOCATION_ + "</div>";
//    public static final String TPL_COMPLEX_DICT_SENTENCE_TAG =
//            "<div class=\"complex\">" +
//            "<div class=\"source\"><font color = \"#ba400d\">%s</font> %s</div>" +
//            "<div class=\"sentence_content\"><div class=\"original_text\">%s</div>" +
//            "<div class=\"translation\">%s</div></div>" +
//                    _TPL_HTML_NOTE_TAG_LOCATION_ + _TPL_HTML_MEDIA_TAG_LOCATION_ + "</div>";
//    public static final String TPL_COMPLEX_DICT_CLOZE_TAG =
//            "<div class=\"complex\">" +
//                    "<div class=\"source\"><font color = \"#ba400d\">%s</font> %s</div>" +
//                    _TPL_HTML_NOTE_TAG_LOCATION_ + _TPL_HTML_MEDIA_TAG_LOCATION_ + "</div>";
//
//    public static final String AUDIO_INDICATOR_MP3 = "<span class=\"indicator\"><font color=\"#227D51\">mp3</font></span>";
//    public static final String VIDEO_INDICATOR_MP4 = "<span class=\"indicator\"><font color=\"#227D51\">mp4</font></span>";

    public static final int REQUEST_MEDIA_PROJECTION = 1;

//    public final static String[] SUPPORTEDLANGUAGES = {"zho-CHN", "zho-HKG", "zho-TWN", "jpn-JPN", "kor-KOR", "ara-EGY", "ara-SAU", "bul-BGR", "cat-ESP", "ces-CZE", "cym-GBR", "dan-DNK", "deu-AUT", "deu-CHE", "deu-DEU", "ell-GRC", "eng-AUS", "eng-CAN", "eng-GBR", "eng-HKG", "eng-IRL", "eng-IND", "eng-NZL", "eng-PHL", "eng-SGP", "eng-USA", "eng-ZAF", "spa-ARG", "spa-COL", "spa-ESP", "spa-MEX", "spa-USA", "est-EST", "fin-FIN", "fra-BEL", "fra-CAN", "fra-CHE", "fra-FRA", "gle-IRL", "guj-IND", "heb-ISR", "hin-IND", "hrv-HRV", "hun-HUN", "ind-IDN", "ita-ITA", "lit-LTU", "lav-LVA", "mar-IND", "msa-MYS", "mlt-MLT", "nob-NOR", "nld-BEL", "nld-NLD", "pol-POL", "por-BRA", "por-PRT", "ron-ROU", "rus-RUS", "slk-SVK", "slv-SVN", "swe-SWE", "swa-KEN", "tam-IND", "tel-IND", "tha-THA", "tur-TUR", "ukr-UKR", "urd-PAK", "vie-VNM"};

/*
 * 多国多语言
 */
//    public final static String[] SUPPORTEDLANGUAGES = {"zho-CHN", "zho-HKG", "zho-TWN", "jpn-JPN", "kor-KOR", "ara-EGY", "ara-SAU", "bul-BGR", "cat-ESP", "ces-CZE", "cym-GBR", "dan-DNK", "deu-AUT", "deu-CHE", "deu-DEU", "ell-GRC", "eng-AUS", "eng-CAN", "eng-HKG", "eng-IRL", "eng-IND", "eng-NZL", "eng-PHL", "eng-SGP", "eng-USA", "eng-ZAF", "spa-ARG", "spa-COL", "spa-ESP", "spa-MEX", "spa-USA", "est-EST", "fin-FIN", "fra-BEL", "fra-CAN", "fra-CHE", "fra-FRA", "gle-IRL", "guj-IND", "heb-ISR", "hin-IND", "hrv-HRV", "hun-HUN", "ind-IDN", "ita-ITA", "lit-LTU", "lav-LVA", "mar-IND", "msa-MYS", "mlt-MLT", "nob-NOR", "nld-BEL", "nld-NLD", "pol-POL", "por-BRA", "por-PRT", "ron-ROU", "rus-RUS", "slk-SVK", "slv-SVN", "swe-SWE", "swa-KEN", "tam-IND", "tel-IND", "tha-THA", "tur-TUR", "ukr-UKR", "urd-PAK", "vie-VNM"};
//    public final static String[] SUPPORTEDLANGUAGES = {"zho-CHN", "zho-HKG", "zho-TWN", "jpn-JPN", "kor-KOR", "ara-EGY", "ara-SAU", "bul-BGR", "cat-ESP", "ces-CZE", "cym-GBR", "dan-DNK", "deu-AUT", "deu-CHE", "deu-DEU", "ell-GRC", "eng-AUS", "eng-CAN", "eng-GB", "eng-HKG", "eng-IRL", "eng-IND", "eng-NZL", "eng-PHL", "eng-SGP", "eng-USA", "eng-ZAF", "spa-ARG", "spa-COL", "spa-ESP", "spa-MEX", "spa-USA", "est-EST", "fin-FIN", "fra-BEL", "fra-CAN", "fra-CHE", "fra-FRA", "gle-IRL", "guj-IND", "heb-ISR", "hin-IND", "hrv-HRV", "hun-HUN", "ind-IDN", "ita-ITA", "lit-LTU", "lav-LVA", "mar-IND", "msa-MYS", "mlt-MLT", "nob-NOR", "nld-BEL", "nld-NLD", "pol-POL", "por-BRA", "por-PRT", "ron-ROU", "rus-RUS", "slk-SVK", "slv-SVN", "swe-SWE", "swa-KEN", "tam-IND", "tel-IND", "tha-THA", "tur-TUR", "ukr-UKR", "urd-PAK", "vie-VNM"};


//    public final static String[] SUPPORTEDLANGUAGES = {"zho-cn", "zho-hk", "zho-tw", "jpn-jp", "kor-kr", "ara-eg", "ara-sa", "bul-bg", "cat-es", "ces-cz", "cym-gb", "dan-dk", "deu-at", "deu-ch", "deu-de", "ell-gr", "eng-au", "eng-ca", "eng-gb", "eng-hk", "eng-ie", "eng-in", "eng-nz", "eng-ph", "eng-sg", "eng-us", "eng-za", "spa-ar", "spa-co", "spa-es", "spa-mx", "spa-us", "est-ee", "fin-fi", "fra-be", "fra-ca", "fra-ch", "fra-fr", "gle-ie", "guj-in", "heb-il", "hin-in", "hrv-hr", "hun-hu", "ind-id", "ita-it", "lit-lt", "lav-lv", "mar-in", "msa-my", "mlt-mt", "nob-no", "nld-be", "nld-nl", "pol-pl", "por-br", "por-pt", "ron-ro", "rus-ru", "slk-sk", "slv-si", "swe-se", "swa-ke", "tam-in", "tel-in", "tha-th", "tur-tr", "ukr-ua", "urd-pk", "vie-vn"};

    public static String INTENT_ANKIHELPER_ACTION = "intent_ankihelper_action";

    //mathpix
    public static String MATHPIX_BASE_URL = "https://api.mathpix.com/v3/text";
//    public static String URL_SCHEME="xxx";
//    public static String URL_HOST="xxx";
//    public static String URL_PATH="xxx";
//    public static String URL_QUERY_INDEX="xxx";
//    public static String URL_QUERY_ANSWER="xxx";

    public static String CACHE_TEMP_HTML = "temp.html";
}
