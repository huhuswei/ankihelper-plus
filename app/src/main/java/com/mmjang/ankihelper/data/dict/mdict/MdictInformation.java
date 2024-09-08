package com.mmjang.ankihelper.data.dict.mdict;

/**
 * Created by liao on 2017/8/12.
 */

public class MdictInformation {
    private String dictJs;
    private String dictCss;
    private int id;
    private String dictName;
    private String dictIntro;
    private int dictLang;
    private String defTpml;
    private String customFields;
    private String[] fields;
    private int order;

    public MdictInformation(int id, String dictName, String dictCss, String dictJs, String dictIntro, int dictLang, String defTpml, String customFields, String[] fields, int order){
        //this.version = version;
        this.id = id;
        this.dictName = dictName;
        this.dictCss = dictCss;
        this.dictJs = dictJs;
        this.dictIntro = dictIntro;
        this.dictLang = dictLang;
        this.defTpml = defTpml;
        this.customFields = customFields;
        this.fields = fields;
        this.order = order;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDictName() {
        return dictName;
    }

    public String getDictIntro() {
        return dictIntro;
    }

    public int getDictLang() {
        return dictLang;
    }

    public int getDictLangNameIndex() {
        return (int) (Math.log(dictLang) / Math.log(2));
    }
    public void setDictLang(int dictLang) {
        this.dictLang = dictLang;
    }

    public String getDefTpml() {
        return defTpml;
    }

    public String getCustomFields() {
        return customFields;
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
        this.fields = this.customFields.split(MdictManager.SPLIT_TAG);
    }

    public String[] getFields() {
        return fields;
    }

    public String getDictJs() {
        return dictJs;
    }

    public void setDictJs(String dictJs) {
        this.dictJs = dictJs;
    }

    public String getDictCss() {
        return dictCss;
    }

    public void setDictCss(String dictCss) {
        this.dictCss = dictCss;
    }
}
