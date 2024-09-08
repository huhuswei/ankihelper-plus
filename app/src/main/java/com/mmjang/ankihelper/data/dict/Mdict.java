package com.mmjang.ankihelper.data.dict;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ListAdapter;

import com.cy.mdict.MdictUtil;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.database.ExternalDatabase;
import com.mmjang.ankihelper.data.dict.JPDeinflector.Deinflection;
import com.mmjang.ankihelper.data.dict.JPDeinflector.Deinflector;
import com.mmjang.ankihelper.data.dict.mdict.MdictInformation;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.FileUtils;
import com.mmjang.ankihelper.util.FormUtils;
import com.mmjang.ankihelper.util.RegexUtil;
import com.mmjang.ankihelper.util.StorageUtils;
import com.mmjang.ankihelper.util.Utils;
import com.mmjang.ankihelper.util.WanaKanaJava;

import org.nlpcn.commons.lang.jianfan.JianFan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Created by liao on 2017/8/11.
 */

public class Mdict implements IDictionary {

    private int lt;

    private Context mContext;
    private ExternalDatabase mDatabase;
    private int mDictId;
    private static WanaKanaJava mWanaKanaJava;
    public MdictInformation mDictInformation;

    private com.cy.mdict.Mdict mdict;
//    private static String[] EXP_ELE_LIST = new String[]{
//            Constant.DICT_FIELD_KEYWORD,
//            Constant.DICT_FIELD_DEFINITION,
//            Constant.DICT_FIELD_CSS,
//            Constant.DICT_FIELD_JS
//    };
    private String[] EXP_ELE_LIST;

//    static {
//        System.loadLibrary("jni-layer");
//    }

    public String getmCssUrl() {
        return mCssUrl;
    }

    public void setmCssUrl(String mCssUrl) {
        this.mCssUrl = mCssUrl;
    }

    private String mCssUrl = "";

    public String getmJsUrl() {
        return mjsUrl;
    }

    public void setmJsUrl(String mjsUrl) {
        this.mjsUrl = mjsUrl;
    }

    private String mjsUrl = "";

    public Mdict(Context context, ExternalDatabase db, int dictId){
        mContext = context;
        mDatabase = db;
        mDictId = dictId;
        mDictInformation = mDatabase.getMdictInfoById(dictId);
        lt = mDictInformation.getDictLang();
        EXP_ELE_LIST = mDictInformation.getFields();//Arrays.stream(mDictInformation.getFields()).map(s -> s.replace("+","")).toArray(String[]::new);

    }
    private String mAudioUrl = "";
    public String getAudioUrl() {
        return mAudioUrl;
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
        return false;
    }


    public int getId(){
        return mDictId;
    }

    @Override
    public String getDictionaryName() {
        return mDictInformation.getDictName().substring(mDictInformation.getDictName().lastIndexOf(File.separatorChar)+1);
    }

    @Override
    public int getLanguageType() {
        return lt;
    }

    @Override
    public String getIntroduction() {
        return mDictInformation.getDictIntro();
    }

    @Override
    public String[] getExportElementsList() {
        return EXP_ELE_LIST;
    }

    @Override
    public List<Definition> wordLookup(String key) {
        key = Utils.keyCleanup(key);
        List<Definition> re = new ArrayList<>();
        String dictPath = mDictInformation.getDictName();
        File file = new File(dictPath);
        if (file.exists()) {
            try {
                String cssPath =  mDictInformation.getDictCss();
                String jsPath = mDictInformation.getDictJs();
                File cssFile = new File(cssPath);
                File jsFile = new File(jsPath);
                setmCssUrl(cssFile.exists() ? cssPath : "");
                setmJsUrl(jsFile.exists() ? jsPath : "");
//                MDictDictionary dictionary = MDictDictionary.loadDictionary(dictPath);
                mdict = new com.cy.mdict.Mdict(dictPath, getmCssUrl(), getmJsUrl());//cssFile.exists() ? cssPath : "";//dictPath.substring(0,dictPath.lastIndexOf(".mdx")) + ".css");

                switch (mDictInformation.getDictLang()) {
                    case DictLanguageType.JPN:
                        // 用mdict词典查词性形态变化
                        ArrayList<String> deflectResult = FormUtils.getJForms(key);
                        if (deflectResult.size() > 0) {
                            for (String deflectedWrod : deflectResult) {
                                re.addAll(queryDefinition(deflectedWrod));
                            }
                        }
                        if(re.isEmpty()) {
                            if(mWanaKanaJava == null){//lazy init of wanakana
                                mWanaKanaJava = new WanaKanaJava(false);
                            }

                            if(mWanaKanaJava.isKatakana(key) || RegexUtil.isEnglish(key)){
                                key = mWanaKanaJava.toHiragana(key);
                            }
                            re.addAll(queryDefinition(key));
                            for(Deinflection df : Deinflector.deinflect(key)){
                                String base = df.getBaseForm();
                                List<Definition> defs = queryDefinition(base);
                                re.addAll(defs);
                            }
                        }
                        //db查词性形态变化，db太大，放弃
//                        String[] deflectResult = FormsUtil.getInstance(mContext, DictLanguageType.JPN).getForms(key);
//                        if (deflectResult.length > 0) {
//                            for (String deflectedWrod : deflectResult) {
//                                re.addAll(queryDefinition(deflectedWrod));
//                            }
//                        }
                        break;
                        case DictLanguageType.ENG:
                            Set<String> deflectResultEn = new HashSet<>();
                            deflectResultEn.add(key);
                            deflectResultEn.addAll(Arrays.asList(FormsUtil.getInstance(mContext).getForms(key)));
//                            String[] deflectResultEn = FormsUtil.getInstance(mContext).getForms(key);
                            if (deflectResultEn.size() > 0) {
                                for (String deflectedWrod : deflectResultEn) {
                                    re.addAll(queryDefinition(deflectedWrod));
                                }
                            }
                            break;
                }

                if(re.isEmpty()) {
                    re.addAll(queryDefinition(key));
                }

                if(re.isEmpty() && mDictInformation.getDictLang() == DictLanguageType.ENG) {
                    re.addAll(queryDefinitionByYoudaoOnline(key));
                }
//                if(re.isEmpty() && mDictInformation.getDictLang() == DictLanguageType.ENG) {
//                    String[] deflectResult = FormsUtil.getInstance(mContext).getForms(key);
//                    if (deflectResult.length > 0) {
//                        for (String deflectedWrod : deflectResult) {
//                            re.addAll(queryDefinition(deflectedWrod));
//                        }
//                    }
//                }
//
//                //for handling japanese
//                if(re.isEmpty() && mDictInformation.getDictLang() == DictLanguageType.JPN){
//                    if(mWanaKanaJava == null){//lazy init of wanakana
//                        mWanaKanaJava = new WanaKanaJava(false);
//                    }
//
//                    if(mWanaKanaJava.isKatakana(key) || RegexUtil.isEnglish(key)){
//                        key = mWanaKanaJava.toHiragana(key);
//                    }
//                    re.addAll(queryDefinition(key));
//                    for(Deinflection df : Deinflector.deinflect(key)){
//                        String base = df.getBaseForm();
//                        List<Definition> defs = queryDefinition(base);
//                        re.addAll(defs);
//                    }
//                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (int index=0; index < re.size(); index++) {
            saveToCache(re.get(index), index);
        }
        return re;
    }

    public ListAdapter getAutoCompleteAdapter(Context context, int layout) {
        return null;
    }

    private List<Definition> queryDefinition(String q) throws IOException {
        ArrayList<Definition> re = new ArrayList<>();
        ArrayList<String> contentList = MdictUtil.queryWordDetails(q, mdict);
        for(String content : contentList) {
            re.add(fromResultsToDefinition(new String[]{
//                    RegexUtil.getApproximateWord(q, content).replaceAll(RegexUtil.SYMBOL_FILTER, ""),
                    q,
                    content})
            );
        }
        return re;
    }

    private List<Definition> queryDefinitionByYoudaoOnline(String q) throws IOException {
        ArrayList<Definition> re = new ArrayList<>();
        re.add(fromResultsToDefinition(YoudaoOnline.getDefinition(q)));
        return re;
    }

    private Definition fromResultsToDefinition(String[] result){
        String[] fields = getExportElementsList();
        LinkedHashMap<String, String> eleMap = new LinkedHashMap<>();
        for(int i = 0; i < fields.length; i ++){
            if(fields[i].equals(Constant.DICT_FIELD_KEYWORD))
                eleMap.put(Constant.DICT_FIELD_KEYWORD, result[0]);
            else if(fields[i].contains(Constant.MDX_ADD_TAG))
                eleMap.put(fields[i], result[1]);
            else
                eleMap.put(fields[i], "");
        }

        String cssName = "";
        if(!TextUtils.isEmpty(getmCssUrl())) cssName = "_" + getmCssUrl().substring(getmCssUrl().lastIndexOf(File.separator)+1);
        String jsName = "";
        if(!TextUtils.isEmpty(getmJsUrl())) jsName = "_" + getmJsUrl().substring(getmJsUrl().lastIndexOf(File.separator)+1);
//
//        eleMap.put(Constant.DICT_FIELD_CSS,  cssName);
//        eleMap.put(Constant.DICT_FIELD_JS, jsName);
        String displayedHtml =
                "<?xml version=\"1.0\" encoding=\"" + mdict.getEncoding() +"\"?>" +
                "<html><head>" +
//                (!jsName.isEmpty()?"<script type=\"text/javascript\" src=\"" + jsName + "\"></script>" : "") +
                        "<style>a:active, a:hover, a:visited{" +
                        "    -webkit-tap-highlight-color: rgba(255, 255, 255, 0);" +
                        "    -webkit-user-select: all;" +
                        "    -moz-user-focus: all;" +
                        "    -moz-user-select: all;" +
                        "}</style>"+
                "</head><body>" +
                result[1] +
                "</body></html>";

        String audioFileName = Utils.getSpecificFileName(result[0]);
        Definition.ResInformation resInfor = new Definition.ResInformation(
                "", "",
                "", audioFileName, Constant.MP3_SUFFIX,
                "",
                getmCssUrl(), cssName,
                getmJsUrl(), jsName
//                getmCssUrl(), eleMap.get(Constant.DICT_FIELD_CSS),
//                getmJsUrl(), eleMap.get(Constant.DICT_FIELD_JS)
        );
        return new Definition(eleMap, displayedHtml, resInfor);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String entryPoint(String argument1, String argument2);

    private void saveToCache(Definition def, int index) {
        File cacheDir = StorageUtils.getIndividualCacheDirectory(MyApplication.getContext());

        String tempCssFile = cacheDir.getPath() + File.separatorChar + "_" + getmCssUrl().substring(getmCssUrl().lastIndexOf(File.separator)+1);
        if(FileUtils.isFileExists(getmCssUrl()) && !FileUtils.isFileExists(tempCssFile)) {
            try{
                FileOutputStream fos = new FileOutputStream(tempCssFile);
                InputStream is = new FileInputStream(getmCssUrl());
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String tempJsFile = cacheDir.getPath() + File.separatorChar + "_" + getmJsUrl().substring(getmJsUrl().lastIndexOf(File.separator)+1);
        if(FileUtils.isFileExists(getmJsUrl()) && !FileUtils.isFileExists(tempJsFile)) {
            try{
                FileOutputStream fos = new FileOutputStream(tempJsFile);
                InputStream is = new FileInputStream(getmJsUrl());
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();
                is.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            String tempHtmlFile = cacheDir.getPath() + File.separatorChar + index + Constant.CACHE_TEMP_HTML;
            FileOutputStream fos = new FileOutputStream(tempHtmlFile);
            fos.write(def.getDisplayHtml().getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
