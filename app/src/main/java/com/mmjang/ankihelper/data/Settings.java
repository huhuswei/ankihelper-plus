package com.mmjang.ankihelper.data;

/**
 * Created by liao on 2017/4/13.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.dict.DictLanguageType;
import com.mmjang.ankihelper.ui.floating.assist.ScreenUtil;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.ViewUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 单例，getInstance()得到实例
 */
public class Settings {

    private static Settings settings = null;

    private final static String PREFER_NAME = "settings";    //应用设置名称
    private final static String MODEL_ID = "model_id";       //应用设置项 模版id
    private final static String DECK_ID = "deck_id";         //应用设置项 牌组id
    private final static String DEFAULT_MODEL_ID = "default_model_id"; //默认模版id，如果此选项存在，则已写入配套模版
    private final static String FIELDS_MAP = "fields_map";   //字段映射
    private final static String MONITE_CLIPBOARD_Q = "show_clipboard_notification_q";   //是否监听剪切板
    private final static String AUTO_CANCEL_POPUP_Q = "auto_cancel_popup";              //点加号后是否退出
    private final static String DEFAULT_PLAN = "default_plan";
    private final static String LAST_SELECTED_PLAN = "last_selected_plan";
    private final static String DEFAULT_TAG = "default_tag";
    private final static String SET_AS_DEFAULT_TAG = "set_as_default_tag";
    private final static String LAST_PRONOUNCE_LANGUAGE = "last_pronounce_language";
    private final static String LEFT_HAND_MODE_Q = "left_hand_mode_q";
    private final static String PINK_THEME_Q = "pink_theme_q";
    private final static String OLD_DATA_MIGRATED = "old_data_migrated";
    private final static String SHOW_CONTENT_ALREADY_READ = "show_content_already_read";
    private final static String FIRST_TIME_RUNNING_READER = "first_time_running_reader";

    public final static String USER_YOUDAO_APP_ID = "user_youdao_app_id";
    public final static String USER_YOUDAO_APP_KEY = "user_youdao_app_key";

    private final static String USER_DEEPLX_APP_URL = "user_deeplx_app_url";
    private final static String USER_DEEPL_APP_SECRET_KEY = "user_deepl_app_secret_key";
    private final static String USER_CAIYUN_APP_SECRET_KEY = "user_caiyun_app_secret_key";

    private final static String USER_MICROSOFT_APP_ID = "user_microsoft_translate_app_id";

    private final static String USER_BAIDUFANYI_APP_ID = "user_baidu_fanyi_app_id";
    private final static String USER_BAIDUFANYI_APP_KEY = "user_baidu_fanyi_app_key";

    private final static String DICTTANGO_ONLINE_URL = "mdictionary_online_url";

    private final static String CURRENT_DICTIONARY_LANGUAGE_TYPE = "current_dictionary_language_type";
    private final static String CURRENT_DICTIONARY_NAME = "current_dictionary_name";

    private final static String LOCATORS_STRING = "locators_string";

    private  final static String MDICT_CHECKER = "mdict_checker_";

    private  final static String OCR_TESSERACT_TRAINEDDATA_CHECKBOX_MAP = "ocr_tess_checker_lang_traineddata";
    private final static  String OCR_MLKIT_CHECKED_INDEX = "ocr_mlkit_checked_index";
    public final static String OCR_AUTOMATIC_RECOGNITION = "ocr_automatic_recognition";
    public final static String OCR_MATHPIX_ID = "ocr_mathpix_id";
    public final static String OCR_MATHPIX_KEY = "ocr_mathpix_key";
    public final static  String OCR_MATHPIX_CHECKED_INDEX = "ocr_mathpix_checked_index";

    public final static String FORMAT_TEXT_ENABLE = "format_text_enable";
    private final static String TRANSLATOR_CHECKED_INDEX = "translator_checked_index";

    private final static String ORC_SELECTED_LANG = "orc_selected_lang";

    private final static String FLOAT_BALL_ENABLE = "float_ball_enable";

    public final static String COPY_MARKED_TEXT = "copy_marked_text";

    public final static String WEBVIEW_CONTEXT_MENU_COPY = "popup_webview_context_menu_copy";
    public final static String WEBVIEW_CONTEXT_MENU_SELECT_ALL = "popup_webview_context_menu_select_all";
    public final static String WEBVIEW_CONTEXT_MENU_RICH_TO_PLAIN = "popup_webview_context_menu_add_plain";
    public final static String WEBVIEW_CONTEXT_MENU_SHARE_TEXT = "popup_webview_context_menu_share_text";
    public final static String POPUP_SWITCH_AUTO_SEARCH = "popup_switch_auto_search";
    public final static String POPUP_SWITCH_SCROLL_BOTTOM = "popup_switch_scroll_bottom";

    public final static String POPUP_SWITCH_COPY = "popup_switch_copy";

    public final static String CAPTURE_RESULT_EDIT_MODE = "capture_result_mode";

    public final static String NOTE_MODE = "note_mode";
    public final static String DARK_MODE_INDEX = "dark_modea_index";

    public final static String THEME_COLOR_INDEX = "theme_color_index";

    private final static String CLEAR_SEARCHED_ENABLE =  "clear_searched_enable";

    private final static String POPUP_TOOLBAR_AUTOMATIC_TRANSLATION_ENABLE = "pop_toolbar_automatic_translation_enable";

    private final static String POPUP_TOOLBAR_SEARCH_ENABLE = "popup_toolbar_search_enable";

    private final static String POPUP_TOOLBAR_NOTE_ENABLE = "popup_toolbar_note_enable";

    private final static String POPUP_TOOLBAR_TAG_ENABLE = "popup_toolbar_tag_enable";

    public static final String FLOATING_BUTTON_POSITION = "floating_button_position";

    public static final String FLOATING_SNIP_SEARCH_SWITCH = "floating_snip_search_switch";
    public static final String FLOATING_SIDELINE_SIZE = "floating_sideline_size";
    public static final String FLOATING_BUTTON_PIN_POSITION_ENABLE = "floating_button_pin_position_enable";
    public static final String FlOATING_BUTTON_AUTO_SIDE = "floating_button_auto_side";
    private  final static String POPUP_TEXT_SIZE= "popup_text_size";
    private final static String FLOATING_BUTTON_ALPHA = "floating_button_alpha";
    private final static String FLOATING_BUTTON_SIZE = "floating_button_size";
    public final static String FLOATING_HOVERING_MILLISECOND = "floating_hovering_millisecond";
    private  final static String POPUP_SWITCH_SYMBOL_SELECTION = "popup_switch_symbol_selection";
    public final static String POPUP_SWITCH_MERGE_BOLD_TAGS = "popup_switch_merge_bold_tags";
    private final static String POPUP_SPINNER_DECK_ENABLE = "popup_spinner_deck_enable";

    private final static String POPUP_IGNORE_DECK_SCHEME = "popup_ignore_deck_scheme";

    private final static String LAST_MODEL_ID = "last_model_id";       //应用设置项 模版id

    private final static String FILE_PICKER_HISTORY_PATH = "file_picker_history_path";

    public final static String ACTION_CLICK = "action_click";
    public final static String ACTION_LONGCLICK = "action_longclick";
    public final static String SCREENSHOT_NAME = "screenshot_name";
    private static final String DECK_SELECTED = "deck_selected";
    private static final String PLAN_SELECTED = "plan_selected";

    private static final String MODELS_MAP = "models_map";

    private static final String ANKIDROID_MEDIA_DIRECTORY = "ankidroid_media_directory";

    public static final String ORIENTATION_PORTRAIT = "orientation_portrait";

    public static final String KEYBOARD_STATE = "keyboard_state";

    public static final String POPUP_DISPLAY_STATE = "popup_display_state";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    private Settings(Context context) {
        sp = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 获得单例
     *
     * @return
     */
    public static Settings getInstance(Context context) {
        if (settings == null) {
            settings = new Settings(context);
        }
        return settings;
    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }
    /*************/

    Long getModelId() {
        return sp.getLong(MODEL_ID, 0);
    }

    void setModelId(Long modelId) {
        editor.putLong(MODEL_ID, modelId);
        editor.commit();
    }

    /**************/

    Long getDeckId() {
        return sp.getLong(DECK_ID, 0);
    }

    void setDeckId(Long deckId) {
        editor.putLong(DECK_ID, deckId);
        editor.commit();
    }

    /**************/

    Long getDefaultModelId() {
        return sp.getLong(DEFAULT_MODEL_ID, 0);
    }

    void setDefaultModelId(Long defaultModelId) {
        editor.putLong(DEFAULT_MODEL_ID, defaultModelId);
        editor.commit();
    }

    /**************/

    String getFieldsMap() {
        return sp.getString(FIELDS_MAP, "");
    }

    void setFieldsMap(String filedsMap) {
        editor.putString(FIELDS_MAP, filedsMap);
        editor.commit();
    }

    /**************/

    public boolean getMoniteClipboardQ() {
        return sp.getBoolean(MONITE_CLIPBOARD_Q, false);
    }

    public void setMoniteClipboardQ(boolean moniteClipboardQ) {
        editor.putBoolean(MONITE_CLIPBOARD_Q, moniteClipboardQ);
        editor.commit();
    }

    /**************/

    public boolean getAutoCancelPopupQ() {
        return sp.getBoolean(AUTO_CANCEL_POPUP_Q, false);
    }

    public void setAutoCancelPopupQ(boolean autoCancelPopupQ) {
        editor.putBoolean(AUTO_CANCEL_POPUP_Q, autoCancelPopupQ);
        editor.commit();
    }

    /**************/
    public String getDefaultPlan() {
        return sp.getString(DEFAULT_PLAN, "");
    }

    public void setDefaultPlan(String defaultPlan) {
        editor.putString(DEFAULT_PLAN, defaultPlan);
        editor.commit();
    }

    /******************/

    public String getLastSelectedPlan() {
        return sp.getString(LAST_SELECTED_PLAN, "");
    }

    public void setLastSelectedPlan(String lastSelectedPlan) {
        editor.putString(LAST_SELECTED_PLAN, lastSelectedPlan);
        editor.commit();
    }

    /*****************/
    public String getDefaulTag() {
        return sp.getString(DEFAULT_TAG, "");
    }

    public void setDefaultTag(String defaultTag) {
        editor.putString(DEFAULT_TAG, defaultTag);
        editor.commit();
    }

    /****************/
    public boolean getSetAsDefaultTag() {
        return sp.getBoolean(SET_AS_DEFAULT_TAG, false);
    }

    public void setSetAsDefaultTag(boolean setAsDefaultTag) {
        editor.putBoolean(SET_AS_DEFAULT_TAG, setAsDefaultTag);
        editor.commit();
    }

    public boolean getLeftHandModeQ() {
        return sp.getBoolean(LEFT_HAND_MODE_Q, false);
    }

    public void setLeftHandModeQ(boolean leftHandModeQ) {
        editor.putBoolean(LEFT_HAND_MODE_Q, leftHandModeQ);
        editor.commit();
    }

    public boolean getPinkThemeQ() {
        return sp.getBoolean(PINK_THEME_Q, false);
    }

    public void setPinkThemeQ(boolean pinkThemeQ) {
        editor.putBoolean(PINK_THEME_Q, pinkThemeQ);
        editor.commit();
    }

    public boolean getOldDataMigrated() {
        return sp.getBoolean(OLD_DATA_MIGRATED, false);
    }

    public void setOldDataMigrated(boolean oldDataMigrated) {
        editor.putBoolean(OLD_DATA_MIGRATED, oldDataMigrated);
        editor.commit();
    }

    public boolean getShowContentAlreadyRead() {
        return sp.getBoolean(SHOW_CONTENT_ALREADY_READ, false);
    }

    public void setShowContentAlreadyRead(boolean showContentAlreadyRead) {
        editor.putBoolean(SHOW_CONTENT_ALREADY_READ, showContentAlreadyRead);
        editor.commit();
    }

    public boolean getFirstTimeRunningReader() {
        return sp.getBoolean(FIRST_TIME_RUNNING_READER, true);
    }

    public void setFirstTimeRunningReader(boolean firstTimeRunningReader) {
        editor.putBoolean(FIRST_TIME_RUNNING_READER, firstTimeRunningReader);
        editor.commit();
    }

    public int getPopupFontSize() {
        return sp.getInt(POPUP_TEXT_SIZE, 14);
    }

    public void setPopupTextSize(int size) {
        editor.putInt(POPUP_TEXT_SIZE, size);
        editor.commit();
    }

    public int getFloatingButtonAlpha() {
        return sp.getInt(FLOATING_BUTTON_ALPHA, 100);
    }

    public void setFloatingButtonAlpha(int size) {
        editor.putInt(FLOATING_BUTTON_ALPHA, size);
        editor.commit();
    }

    public int getFloatingButtonSize() {
        return sp.getInt(FLOATING_BUTTON_SIZE, ViewUtil.dp2px(60));
    }

    public void setFloatingButtonSize(int size) {
        editor.putInt(FLOATING_BUTTON_SIZE, size);
        editor.commit();
    }

    public boolean getFloatBallEnable() {
        return sp.getBoolean(FLOAT_BALL_ENABLE, false);
    }

    public void setFloatBallEnable(boolean status) {
        editor.putBoolean(FLOAT_BALL_ENABLE, status);
        editor.commit();
    }

    public boolean getClearSearchedEnable() {
        return sp.getBoolean(CLEAR_SEARCHED_ENABLE, false);
    }

    public void setClearSearchedEnable(boolean status) {
        editor.putBoolean(CLEAR_SEARCHED_ENABLE, status);
        editor.commit();
    }

    public boolean getPopupSwitchSymbolSelection() {
        return sp.getBoolean(POPUP_SWITCH_SYMBOL_SELECTION, false);
    }


    public void setPopupSwitchSymbolSelection(boolean status) {
        editor.putBoolean(POPUP_SWITCH_SYMBOL_SELECTION, status);
        editor.commit();
    }

    public boolean getPopupToolbarAutomaticTranslationEnable() {
        return sp.getBoolean(POPUP_TOOLBAR_AUTOMATIC_TRANSLATION_ENABLE, false);
    }

    public void setPopupToolbarAutomaticTranslationEnable(boolean status) {
        editor.putBoolean(POPUP_TOOLBAR_AUTOMATIC_TRANSLATION_ENABLE, status);
        editor.commit();
    }

    public boolean getPopupToolbarSearchEnable() {
        return sp.getBoolean(POPUP_TOOLBAR_SEARCH_ENABLE, true);
    }

    public void setPopupToolbarSearchEnable(boolean status) {
        editor.putBoolean(POPUP_TOOLBAR_SEARCH_ENABLE, status);
        editor.commit();
    }

    public boolean getPopupToolbarNoteEnable() {
        return sp.getBoolean(POPUP_TOOLBAR_NOTE_ENABLE, true);
    }

    public void setPopupToolbarNoteEnable(boolean status) {
        editor.putBoolean(POPUP_TOOLBAR_NOTE_ENABLE, status);
        editor.commit();
    }

    public boolean getPopupToolbarTagEnable() {
        return sp.getBoolean(POPUP_TOOLBAR_TAG_ENABLE, true);
    }

    public void setPopupToolbarTagEnable(boolean status) {
        editor.putBoolean(POPUP_TOOLBAR_TAG_ENABLE, status);
        editor.commit();
    }

    public void setFloatingButtonPosition(Point point) {
        if(point.x < 1) point.x = 1;
        if(get(ORIENTATION_PORTRAIT, true)) {
            editor.putInt(FLOATING_BUTTON_POSITION + "_p_x", point.x);
            editor.putInt(FLOATING_BUTTON_POSITION + "_p_y", point.y);
        } else {
            editor.putInt(FLOATING_BUTTON_POSITION + "_l_x", point.x);
            editor.putInt(FLOATING_BUTTON_POSITION + "_l_y", point.y);
        }
        editor.commit();
    }

    public Point getFloatingButtonPosition() {
        if(get(ORIENTATION_PORTRAIT, true)) {
            return new Point(
                    sp.getInt(FLOATING_BUTTON_POSITION + "_p_x", ScreenUtil.INSTANCE.getScreenWidthPixels(MyApplication.getContext())),
                    sp.getInt(FLOATING_BUTTON_POSITION + "_p_y", ScreenUtil.INSTANCE.getScreenHeightPixels(MyApplication.getContext()) / 2));
        } else {
            return new Point(
                    sp.getInt(FLOATING_BUTTON_POSITION + "_l_x", ScreenUtil.INSTANCE.getScreenWidthPixels(MyApplication.getContext())),
                    sp.getInt(FLOATING_BUTTON_POSITION + "_l_y", ScreenUtil.INSTANCE.getScreenHeightPixels(MyApplication.getContext()) / 2));
        }
    }

//    public void setKeyboardState(boolean state) {
//        editor.putBoolean(KEYBOARD_STATE, state);
//        editor.commit();
//    }
//
//    public boolean getKeyboardState() {
//        return sp.getBoolean(KEYBOARD_STATE, false);
//    }


    public void setSidelineSize(int[] size) {
        if (size.length == 2) {
            editor.putInt(FLOATING_SIDELINE_SIZE + "_x", size[0]);
            editor.putInt(FLOATING_SIDELINE_SIZE + "_y", size[1]);
            editor.commit();
        }

    }

    public int[] getSidelineSize() {
        int[] size =  {
                sp.getInt(FLOATING_SIDELINE_SIZE + "_x", ViewUtil.dp2px(30f)),
                sp.getInt(FLOATING_SIDELINE_SIZE + "_y", ViewUtil.dp2px(50f))
        };
        return size;
    }

    public boolean getPopupSpinnerDeckEnable() {
        return sp.getBoolean(POPUP_SPINNER_DECK_ENABLE, false);
    }

    public void setPopupSpinnerDeckEnable(boolean status) {
        editor.putBoolean(POPUP_SPINNER_DECK_ENABLE, status);
        editor.commit();
    }

    public boolean getPopupIgnoreDeckScheme() {
        return sp.getBoolean(POPUP_IGNORE_DECK_SCHEME, false);
    }

    public void setPopupIgnoreDeckScheme(boolean status) {
        editor.putBoolean(POPUP_IGNORE_DECK_SCHEME, status);
        editor.commit();
    }

    public Long getLastDeckId() {
        return sp.getLong(LAST_MODEL_ID, -1);
    }

    public void setLastDeckId(Long deckId) {
        editor.putLong(LAST_MODEL_ID, deckId);
        editor.commit();
    }

    public String getUserMicrosoftAppId() {
        return sp.getString(USER_MICROSOFT_APP_ID, "");
    }

    public void setUserMicrosoftAppId(String userMicrosoftAppId) {
        editor.putString(USER_MICROSOFT_APP_ID, userMicrosoftAppId);
        editor.commit();
    }

    public String getUserBaidufanyiAppId() {
        return sp.getString(USER_BAIDUFANYI_APP_ID, "");
    }

    public void setUserBaidufanyiAppId(String userBaidufanyiAppId) {
        editor.putString(USER_BAIDUFANYI_APP_ID, userBaidufanyiAppId);
        editor.commit();
    }

    public String getUserBaidufanyiAppKey() {
        return sp.getString(USER_BAIDUFANYI_APP_KEY, "");
    }

    public void setUserBaidufanyiAppKey(String userBaidufanyiAppKey) {
        editor.putString(USER_BAIDUFANYI_APP_KEY, userBaidufanyiAppKey);
        editor.commit();
    }

    public String getDictTangoOnlineUrl() {
        return sp.getString(DICTTANGO_ONLINE_URL, "http://127.0.0.1:1688");
    }

    public void setDictTangoOnlineUrl(String url) {
        editor.putString(DICTTANGO_ONLINE_URL, url);
        editor.commit();
    }

    public int getLastPronounceLanguage() {
        return sp.getInt(LAST_PRONOUNCE_LANGUAGE, 0);//PronounceManager.LANGUAGE_ENGLISH_UK_YOUDAO_INDEX);
    }

    public void setLastPronounceLanguage(int lastPronounceLanguageIndex) {
        editor.putInt(LAST_PRONOUNCE_LANGUAGE, lastPronounceLanguageIndex);
        editor.commit();
    }


    public void setRestorePronounceSpinnerIndex(String action, int index, int languageType, boolean isExistAudioUrl, boolean isloadedTTS) {
        String PRONOUNCE_SPINNER_INDEX_NAME =
                String.format("%s_%s_%s_%s",
                        action,
                        DictLanguageType.getLanguageISO3ByLTId(languageType),
                    String.valueOf(isExistAudioUrl),
                    String.valueOf(isloadedTTS));
        editor.putInt(PRONOUNCE_SPINNER_INDEX_NAME, index);
        editor.commit();
//        Trace.e("Settings", String.format("(%d)set %s is %d", languageType, PRONOUNCE_SPINNER_INDEX_NAME, index));
    }

    public int getRestorePronounceSpinnerIndex(String action, int languageType, boolean isExistAudioUrl, boolean isloadedTTS) {
        String PRONOUNCE_SPINNER_INDEX_NAME =
                String.format("%s_%s_%s_%s",
                        action,
                        DictLanguageType.getLanguageISO3ByLTId(languageType),
                        String.valueOf(isExistAudioUrl),
                        String.valueOf(isloadedTTS));
        int index = sp.getInt(PRONOUNCE_SPINNER_INDEX_NAME, -1);
//        Trace.e("Settings", String.format("(%d)get %d from %s", languageType, index, PRONOUNCE_SPINNER_INDEX_NAME));
        return index;
    }

    public HashMap<String, Boolean> getTangoDictChckerMap(String planName) {
        Gson gson = new Gson();
        Type type= new TypeToken<HashMap<String, Boolean>>(){}.getType();
        String jsonStr = sp.getString(MDICT_CHECKER +planName, "");
        if(jsonStr.equals("")) {
            return new HashMap<>();
        }
        return gson.fromJson(jsonStr, type);
    }

    public boolean setTangoDictCheckerMap(String planName, HashMap<String, Boolean> dictCheckerMap) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(dictCheckerMap);
        editor.putString(MDICT_CHECKER +planName, jsonStr);
        editor.commit();
        return true;
    }

    public boolean removeMdictCheckerMap(String planName) {
        editor.remove(MDICT_CHECKER +planName);
        editor.commit();
        return true;
    }

    public int getTranslatorCheckedIndex() {
        return sp.getInt(TRANSLATOR_CHECKED_INDEX, 0);
    }

    public void setTranslatorCheckedIndex(int index) {
        editor.putInt(TRANSLATOR_CHECKED_INDEX, index);
        editor.commit();
    }

    public int getMlKitOcrLangCheckedIndex() {
        return sp.getInt(OCR_MLKIT_CHECKED_INDEX, 0);
    }

    public void setMlKitOcrCheckedIndex(int index) {
        editor.putInt(OCR_MLKIT_CHECKED_INDEX, index);
        editor.commit();
    }

    public HashMap<String, Boolean> getTesseractOcrTraineddataCheckBoxMap() {
        Gson gson = new Gson();
        Type type= new TypeToken<HashMap<String, Boolean>>(){}.getType();
        String jsonStr = sp.getString(OCR_TESSERACT_TRAINEDDATA_CHECKBOX_MAP, "");
        if(jsonStr.equals("")) {
            return new HashMap<>();
        }
        return gson.fromJson(jsonStr, type);
    }

    public boolean setTesseractOcrTraineddataCheckBoxMap(HashMap<String, Boolean> dictCheckerMap) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(dictCheckerMap);
        editor.putString(OCR_TESSERACT_TRAINEDDATA_CHECKBOX_MAP, jsonStr);
        editor.commit();
        return true;
    }

    public String getOcrSelectedLang() {
        return sp.getString(ORC_SELECTED_LANG, "");
    }

    public void setOrcSelectedLang(String lang) {
        editor.putString(ORC_SELECTED_LANG, lang);
        editor.commit();
    }

    public String getLocatorsString() {
        return sp.getString(LOCATORS_STRING, "\n");
    }

    public void setLocatorsString(String locatorsString) {
        editor.putString(LOCATORS_STRING, locatorsString);
        editor.commit();
    }

    public String getFilePickerHistoryPath() { return sp.getString(FILE_PICKER_HISTORY_PATH, ""); }

    public void setFilePickerHistoryPath(String path) {
        editor.putString(FILE_PICKER_HISTORY_PATH, path);
        editor.commit();
    }

//    //checkd locator
//    public String getLocatorsCheckedString() {
//        return sp.getString(LOCATORS_CHECKED_STRING, "\n");
//    }
//
//    public void setLocatorsCheckedString(String locatorsString) {
//        editor.putString(LOCATORS_CHECKED_STRING, locatorsString);
//        editor.commit();
//    }


    boolean hasKey(String key) {
        return sp.contains(key);
    }

    public String getUserDeepLXAPIURLs() {
        return sp.getString(USER_DEEPLX_APP_URL, "");
    }

    public void setUserDeepLXAPIURLs(String urls) {
        editor.putString(USER_DEEPLX_APP_URL, urls);
        editor.commit();
    }

    public String getUserDeepLAppSecretKey() {
        return sp.getString(USER_DEEPL_APP_SECRET_KEY, "");
    }

    public void setUserDeepLAppSecretKey(String key) {
        editor.putString(USER_DEEPL_APP_SECRET_KEY, key);
        editor.commit();
    }

    public String getUserCaiyunAppSecretKey() {
        return sp.getString(USER_CAIYUN_APP_SECRET_KEY, "");
    }

    public void setUserCaiyunAppSecretKey(String key) {
        editor.putString(USER_CAIYUN_APP_SECRET_KEY, key);
        editor.commit();
    }

    public String getAnkiDroidDir() {
        String dir = sp.getString(
                ANKIDROID_MEDIA_DIRECTORY,
                Environment.getExternalStorageDirectory() +
                        File.separator +
                        Constant.ANKIDROID +
                        File.separator +
                        Constant.ANKIDROID_MEDIA_DIRECTORY +
                        File.separator);
        return dir;
    }

    public void setAnkiDroidDir(String dir) {
        editor.putString(
                ANKIDROID_MEDIA_DIRECTORY,
                dir + (dir.endsWith(File.separator) ? "" : File.separator));
        editor.commit();
    }

    public HashMap<Long, String> getModelsHashMap() {
        Gson gson = new Gson();
        Type type= new TypeToken<HashMap<Long, String>>(){}.getType();
        String jsonStr = sp.getString(MODELS_MAP, "");
        if(jsonStr.equals("")) {
            return new HashMap<>();
        }
        return gson.fromJson(jsonStr, type);
    }

    public boolean setModelsHashMap(HashMap<Long, String> modelsHashMap) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(modelsHashMap);
        editor.putString(MODELS_MAP, jsonStr);
        editor.commit();
        return true;
    }

    public LinkedHashMap<Long, Boolean> getDeckSelectedLinkedHashMap() {
        Gson gson = new Gson();
        Type type= new TypeToken<LinkedHashMap<Long, Boolean>>(){}.getType();
        String jsonStr = sp.getString(DECK_SELECTED, "");
        if(jsonStr.equals("")) {
            return new LinkedHashMap<>();
        }
        return gson.fromJson(jsonStr, type);
    }

    public boolean setDeckSelectedLinkedHashMap(HashMap<Long, Boolean> deckSelectedLinkedHashMap) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(deckSelectedLinkedHashMap);
        editor.putString(DECK_SELECTED, jsonStr);
        editor.commit();
        return true;
    }

    public LinkedHashMap<String, Boolean> getPlanSelectedLinkedHashMap() {
        Gson gson = new Gson();
        Type type= new TypeToken<LinkedHashMap<String, Boolean>>(){}.getType();
        String jsonStr = sp.getString(PLAN_SELECTED, "");
        if(jsonStr.equals("")) {
            return new LinkedHashMap<>();
        }
        return gson.fromJson(jsonStr, type);
    }

    public boolean setPlanSelectedLinkedHashMap(HashMap<String, Boolean> planSelectedLinkedHashMap) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(planSelectedLinkedHashMap);
        editor.putString(PLAN_SELECTED, jsonStr);
        editor.commit();
        return true;
    }


    public boolean put(String TAG, String value) {
        editor.putString(TAG, value);
        editor.commit();
        return true;
    }

    public boolean put(String TAG, boolean value) {
        editor.putBoolean(TAG, value);
        editor.commit();
        return true;
    }

    public boolean put(String TAG, int value) {
        editor.putInt(TAG, value);
        editor.commit();
        return true;
    }

    public String get(String TAG, String defaultValue) {
        return sp.getString(TAG, defaultValue);
    }

    public boolean get(String TAG, boolean defaultValue) {
        return sp.getBoolean(TAG, defaultValue);
    }

    public int get(String TAG, int defaultValue) {
        return sp.getInt(TAG, defaultValue);
    }

}