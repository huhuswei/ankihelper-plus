package com.mmjang.ankihelper.ui.tango;

/**
 * Created by liao on 2017/4/28.
 */

public class FieldsMapItem {
    private String field;
    private String[] exportElements;
    private String selector;
    private int selectedFieldPos;

    public FieldsMapItem(String fld, String[] expEleList) {
        field = fld;
        exportElements = expEleList;
        selectedFieldPos = 0;
    }

    public FieldsMapItem(String fld, String selector) {
        this.field = fld;
        this.selector = selector.trim();
    }

    public FieldsMapItem(String fld, String[] expEleList, int selPos) {
        field = fld;
        exportElements = expEleList;
        selectedFieldPos = selPos;
    }

    public String[] getExportedElementNames() {
        return exportElements;
    }

    public String getField() {
        return field;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector.trim();
    }

    public int getSelectedFieldPos() {
        return selectedFieldPos;
    }

    public void setSelectedFieldPos(int selectedFieldPos) {
        this.selectedFieldPos = selectedFieldPos;
    }
}
