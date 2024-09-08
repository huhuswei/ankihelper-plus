package com.mmjang.ankihelper.ui.plan;

import com.mmjang.ankihelper.util.Constant;

/**
 * Created by liao on 2017/4/28.
 */

public class FieldsMapItem {
    private String field;
    private String[] exportElements;
    private int selectedFieldPos, selectedFieldAppendingPos;
    private String multiSel, multiSelAppending;

    public FieldsMapItem(String fld, String[] expEleList) {
        field = fld;
        exportElements = expEleList;
        selectedFieldPos = 0;
        selectedFieldAppendingPos = 0;
        multiSel = Constant.DICT_FIELD_EMPTY;
        multiSelAppending = Constant.DICT_FIELD_EMPTY;
    }

    public FieldsMapItem(String fld, String[] expEleList, int selPos, int selAppendingPos) {
        field = fld;
        exportElements = expEleList;
        selectedFieldPos = selPos;
        selectedFieldAppendingPos = selAppendingPos;
        multiSel = Constant.DICT_FIELD_EMPTY;
        multiSelAppending = Constant.DICT_FIELD_EMPTY;
    }

    public void setMultiSel(String multiSel) {
        this.multiSel = multiSel;
    }

    public void setMultiSelAppending(String multiSelAppending) {
        this.multiSelAppending = multiSelAppending;
    }

    public String getMultiSel() {
        return multiSel;
    }

    public String getMultiSelAppending() {
        return multiSelAppending;
    }

    public FieldsMapItem(String fld, String[] expEleList, String multiSel, String multiSelAppending) {
        field = fld;
        exportElements = expEleList;
        selectedFieldPos = 0;
        selectedFieldAppendingPos = 0;
        this.multiSel = multiSel.equals("") ? Constant.DICT_FIELD_EMPTY : multiSel;
        this.multiSelAppending = multiSelAppending.equals("") ? Constant.DICT_FIELD_EMPTY : multiSelAppending;
    }

    public String[] getExportedElementNames() {
        return exportElements;
    }

    public String getField() {
        return field;
    }

    public int getSelectedFieldPos() {
        return selectedFieldPos;
    }

    public void setSelectedFieldPos(int selectedFieldPos) {
        this.selectedFieldPos = selectedFieldPos;
    }

    public int getSelectedFieldAppendingPos() {
        return selectedFieldAppendingPos;
    }

    public void setSelectedFieldAppendingPos(int selectedFieldAppendingPos) {
        this.selectedFieldAppendingPos = selectedFieldAppendingPos;
    }
}
