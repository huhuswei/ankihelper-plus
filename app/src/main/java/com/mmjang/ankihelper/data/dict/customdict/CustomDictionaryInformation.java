package com.mmjang.ankihelper.data.dict.customdict;

/**
 * Created by liao on 2017/8/12.
 */

public class CustomDictionaryInformation {
    private int id;
    private String dictName;
    private String dictIntro;
    private String dictLang;
    private String defTpml;
    private String[] fields;
    private int order;

    public CustomDictionaryInformation(int id, String dictName, String dictIntro, String dictLang, String defTpml, String[] fields, int order){
        //this.version = version;
        this.id = id;
        this.dictName = dictName;
        this.dictIntro = dictIntro;
        this.dictLang = dictLang;
        this.defTpml = defTpml;
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

    public String getDictLang() {
        return dictLang;
    }

    public String getDefTpml() {
        return defTpml;
    }

    public String[] getFields() {
        return fields;
    }
}
