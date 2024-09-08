package com.mmjang.ankihelper.ui.plan;

import com.mmjang.ankihelper.util.Constant;

public class ComplexElement {
    private static String SEPERATOR = "\t";
    public String getElementNormal() {
        return elementNormal;
    }

    public void setElementNormal(String elementNormal) {
        this.elementNormal = elementNormal;
    }

    public String getElement() {
        return elementNormal + SEPERATOR + elementAppending;
    }
    public String getElementAppending() {
        return elementAppending;
    }

    public void setElementAppending(String elementAppending) {
        this.elementAppending = elementAppending;
    }

    String elementNormal, elementAppending;

    public ComplexElement() {
        elementNormal = Constant.DICT_FIELD_EMPTY;
        elementAppending = Constant.DICT_FIELD_EMPTY;
    }

    public ComplexElement(String normal, String append) {
        elementNormal = normal.equals("") ? Constant.DICT_FIELD_EMPTY : normal;
        elementAppending = append.equals("") ? Constant.DICT_FIELD_EMPTY : append;
    }

    public ComplexElement(String str) {
        String[] eleArr = str.split(SEPERATOR);
        if(eleArr.length == 2) {
            elementNormal = eleArr[0];
            elementAppending = eleArr[1];
        } else {
            if(str.equals("")) {
                str = Constant.DICT_FIELD_EMPTY;
            }
            elementNormal = str;
            elementAppending = str;
        }
    }




}
