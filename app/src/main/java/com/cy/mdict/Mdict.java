package com.cy.mdict;

import com.knziha.plod.dictionary.mdict;

import java.io.IOException;

public class Mdict extends mdict {

    public Mdict(String fn, String cssPathName) throws IOException {
        this(fn, cssPathName, "");
    }

    public Mdict(String fn, String cssPathName, String jsPathName) throws IOException {
        super(fn);
        this.cssPathName = cssPathName;
        this.jsPathName = jsPathName;
    }
    private String cssPathName, jsPathName;

    public String getCssPathName() {
        return cssPathName;
    }

    public Mdict setCssPathName(String cssPathName) {
        this.cssPathName = cssPathName;
        return this;
    }

    public String getJsPathName() {
        return jsPathName;
    }

    public void setJsPathName(String jsPathName) {
        this.jsPathName = jsPathName;
    }
}
