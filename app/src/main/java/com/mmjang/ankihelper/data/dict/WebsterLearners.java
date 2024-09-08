package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.Utils;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by liao on 2017/4/28.
 */

public class WebsterLearners extends SQLiteAssetHelper implements IDictionary {

    private final int lt = DictLanguageType.ENG;
    private static final String AUDIO_TAG = "MP3";
    private static final String DICT_NAME = "韦氏学习词典";
    private static final String DICT_INTRO = "数据来自 learnersdictionary.com";
    private static final String[] EXP_ELE = new String[]{
            Constant.DICT_FIELD_KEYWORD,
            Constant.DICT_FIELD_PHONETICS,
            Constant.DICT_FIELD_DEFINITION
    };

    private static final String WORD_URL_BASE = "https://learnersdictionary.com/definition/";
    private static final String MP3_URL_BASE = "https://media.merriam-webster.com/audio/prons/en/us/mp3";

    private static final String DATABASE_NAME = "wb_headwords.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    private Context mContext;

    public int getLanguageType() {
        return lt;
    }

    public WebsterLearners(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
        mContext = context;
    }

    private String mAudioUrl = "";

    public String getAudioUrl() {
        return mAudioUrl;
    }

    private void emptyAudioUrl() {
        mAudioUrl = "";
    }

    public boolean setAudioUrl(String audioUrl) {
        try {
            mAudioUrl = audioUrl;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExistAudioUrl() {
        return true;
    }


    public String getDictionaryName() {
        return DICT_NAME;
    }

    public String getIntroduction() {
        return DICT_INTRO;
    }

    public String[] getExportElementsList() {
        return EXP_ELE;
    }

    public List<Definition> wordLookup(String key) {
        try {
            emptyAudioUrl();
            List<Definition> result = new ArrayList<>();
            if (isWordInDict(key)) {
                result.addAll(queryDefinition(key));
            } else {
                for (String form : FormsUtil.getInstance(mContext).getForms(key)) {
                    if (isWordInDict(form)) {
                        result.addAll(queryDefinition(form));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public List<Definition> queryDefinition(String key) {
        try {
            if (key.isEmpty()) {
                return new ArrayList<Definition>();
            }
//            Document doc = Jsoup.connect(WORD_URL_BASE + key)
//                    .userAgent("Mozilla")
//                    .timeout(5000)
//                    .get();
            Request request = new Request.Builder().url(WORD_URL_BASE + key).build();
            String rawhtml = MyApplication.getOkHttpClient().newCall(request).execute().body().string();
            Document doc = Jsoup.parse(rawhtml);
            List<Definition> definitionList = new ArrayList<>();

            //remove redundant nodes
            for (Element redundant : doc.select("sup, .vis_w, .def_labels, .dxs")) {
                redundant.remove();
            }

            //String mp3Url = ""; //some entry may not have mp3, then we must share it;
            for (Element entry : doc.select(".entry")) {
                String headword = getSingleQueryResult(entry, ".hw_txt", false);
                String phonetic = getSingleQueryResult(entry, ".text_prons", false);
                String labels = "<font color=grey>" + getSingleQueryResult(entry, ".labels", false) + "</font>";
                String sense = getSingleQueryResult(entry, "div.fl", false);
                Elements mp3Soup = entry.select(".fa.fa-volume-up");
                String tempAudioUrl = "";
                String audioIndicator = "";
                String audioFileName = "";
                if (mp3Soup.size() > 0) {
                    Element mp3Node = mp3Soup.get(0);
                    String dir = mp3Node.attr("data-dir");
                    String file = mp3Node.attr("data-file");
                    tempAudioUrl = MP3_URL_BASE + "/" + dir + "/" + file + Constant.MP3_SUFFIX;
                    audioIndicator = "<font color='#227D51' >" + AUDIO_TAG + "</font>";
                    audioFileName = Utils.getSpecificFileName(headword);
                }
                for (Element sense_node : entry.select(".sblock_entry > .sblock_c")) {
                    String def_text = getSBlockHtml(sense_node);
                    LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                    String definition = String.format("<span><font color='%s'><i>%s</i></font> %s</span><span>%s</span>",
                            getSenseColor(sense), sense, labels, def_text
                    );

//                    String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headword, phonetic, definition, Constant.AUDIO_INDICATOR_MP3);
//                    String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headword, phonetic, definition, "");
                    defMap.put(Constant.DICT_FIELD_KEYWORD, headword);
                    defMap.put(Constant.DICT_FIELD_PHONETICS, phonetic);
//                    defMap.put(EXP_ELE[2], getMp3Tag(tempAudioUrl));
                    defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                    defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                    String displayedHtml =
                            String.format("<span><b>%s</b> %s<br/><font color='%s'><i>%s</i></font> %s</span><span>%s</span>",
                                    headword, audioIndicator, getSenseColor(sense), sense, labels, def_text
                            );
                    Definition.ResInformation resInfor = new Definition.ResInformation(
                            tempAudioUrl, audioFileName, Constant.MP3_SUFFIX
                    );
                    definitionList.add(new Definition(defMap, displayedHtml, resInfor));
                }

                for (Element phrase : entry.select(".dros > .dro")) {
                    String phrase_headword = getSingleQueryResult(phrase, "h2.dre", false);
                    String phrase_gram = "<font color=grey>" + getSingleQueryResult(phrase, "span.gram", false) + "</font>";
                    for (Element sense_node : phrase.select(".sblock_c")) {
                        String def_text = getSBlockHtml(sense_node);
                        LinkedHashMap<String, String> defMap = new LinkedHashMap<>();
                        String definition = String.format("<span>%s<br/>%s</span>",
                                phrase_gram.trim(), def_text.trim()
                        );
//                        String complex = FieldUtil.formatComplexTplWord(DICT_NAME, headword, phonetic, definition, Constant.AUDIO_INDICATOR_MP3);
//                        String muteComplex = FieldUtil.formatComplexTplWord(DICT_NAME, headword, phonetic, definition, "");
                        defMap.put(Constant.DICT_FIELD_KEYWORD, phrase_headword);
                        defMap.put(Constant.DICT_FIELD_PHONETICS, phonetic);
//                        defMap.put(EXP_ELE[2], getMp3Tag(mAudioUrl));
                        defMap.put(Constant.DICT_FIELD_DEFINITION, definition);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_ONLINE, complex);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_OFFLINE, complex);
//                        defMap.put(Constant.DICT_FIELD_COMPLEX_MUTE, muteComplex);

                        String displayedHtml =
                                String.format("<span><font color=blue><b>%s</b></font> %s</span><span>%s</span>",
                                        phrase_headword, phrase_gram, def_text
                                );
                        Definition.ResInformation resInfor = new Definition.ResInformation(
                                tempAudioUrl, audioFileName, Constant.MP3_SUFFIX
                        );
                        definitionList.add(new Definition(defMap, displayedHtml, resInfor));
                    }
                }
            }


            return definitionList;

        } catch (Exception e) {
            Trace.e("time out", Log.getStackTraceString(e));
            return new ArrayList<Definition>();
        }
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(context, layout,
                        null,
                        new String[]{"hwd"},
                        new int[]{android.R.id.text1},
                        0
                );
        adapter.setFilterQueryProvider(
                new FilterQueryProvider() {
                    @Override
                    public Cursor runQuery(CharSequence constraint) {
                        if(constraint != null)
                            return getFilterCursor(constraint.toString());
                        return null;
                    }
                }
        );
        adapter.setCursorToStringConverter(
                new SimpleCursorAdapter.CursorToStringConverter() {
                    @Override
                    public CharSequence convertToString(Cursor cursor) {
                        return cursor.getString(1);
                    }
                }
        );

        return adapter;
    }

    private static String getSingleQueryResult(Document soup, String query, boolean toString) {
        Elements re = soup.select(query);
        if (!re.isEmpty()) {
            if (toString) {
                return re.get(0).toString();
            } else {
                return re.get(0).text().trim();
            }
        } else {
            return "";
        }
    }

    private Cursor getFilterCursor(String q) {
        Log.d("databse", "getFilterCursor" + q);
        Cursor cursor = db.query("hwds", new String[]{"rowid _id", "hwd"}, "hwd LIKE ?", new String[]{q + "%"}, null, null, null);
        return cursor;
    }

    private boolean isWordInDict(String word) {
        Cursor cursor = db.query("hwds", new String[]{"hwd"},
                "hwd=? COLLATE NOCASE", new String[]{word}, null, null, null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    private static String getSingleQueryResult(Element soup, String query, boolean toString) {
        Elements re = soup.select(query);
        if (!re.isEmpty()) {
            if (toString) {
                return re.get(0).toString();
            } else {
                return re.get(0).text().trim();
            }
        } else {
            return "";
        }
    }

    private String getMp3Tag(String file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        return "[sound:" + file + "]";
    }

    private String getDefHtml(Element def) {
        String sense = def.toString().replaceAll("<h3.+?>", "<div>").replace("</h3>", "</div>").replaceAll("<a.+?>", "<b>").replace("</a>", "</b>");
        //String defString = def.child(1).text().trim();
        return sense;
    }

    private String getSenseColor(String sense) {
        if (sense.equals("verb")) {
            return "#539007";
        }
        if (sense.equals("adjective")) {
            return "#f8b002";
        }
        if (sense.equals("adverb")) {
            return "#684b9d";
        }
        if (sense.equals("noun")) {
            return "#e3412f";
        }
        if (sense.equals("conjunction")) {
            return "#04B7C9";
        }
        return "#04B7C9";
    }

    private String getSBlockHtml(Element sense_node) {
        //String sn_block_num = "<font color=blue>" + getSingleQueryResult(sense_node, ".sn_block_num", false) + "</font>";
        String label = "";
        Elements blockSoup = sense_node.select(".sblock_labels");
        if (blockSoup.size() > 0) {
            Element sblockLabelNode = blockSoup.get(0);
            for (Element pva : sblockLabelNode.select(".pva")) {
                pva.tagName("b");
            }
            label = sblockLabelNode.html() + "<br/>";
        }
        String senseHtml = "";
        for (Element sense : sense_node.select(".sense")) {
            for (Element sgram : sense.select(".sgram_internal")) {
                sgram.tagName("font");
                sgram.attr("color", "grey");
            }

            for (Element sgram : sense.select(".ssla")) {
                sgram.tagName("i");
            }

            for (Element sgram : sense.select(".pva")) {
                sgram.tagName("b");
            }
            senseHtml += label + sense.html() + "<br/>";
        }
        int len = senseHtml.length();
        return senseHtml.substring(0, len - 5);
    }
}

