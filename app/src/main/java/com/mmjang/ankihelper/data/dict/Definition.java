package com.mmjang.ankihelper.data.dict;

import java.util.LinkedHashMap;

/**
 * Created by liao on 2017/4/20.
 */

public class Definition {

    private LinkedHashMap<String, String> exportElements;
    private String displayHtml;
    ResInformation resInfor;

    public Definition(LinkedHashMap<String, String> expEle, String dspHtml) {
        exportElements = expEle;
        displayHtml = dspHtml;
        this.resInfor = new ResInformation();
    }

    //将作废
    public Definition(LinkedHashMap<String, String> expEle, String dspHtml, String imageUrl, String imageName, String audioUrl, String audioName, String audioSuffix) {
        this.exportElements = expEle;
        this.displayHtml = dspHtml;
        this.resInfor = new ResInformation(imageUrl, imageName, audioUrl, audioName, audioSuffix, "","", "", "", "");
    }

    public Definition(LinkedHashMap<String, String> expEle, String dspHtml, ResInformation resInfor) {
        exportElements = expEle;
        displayHtml = dspHtml;
        this.resInfor = resInfor;
    }


    public String getExportElement(String key) {
        return exportElements.get(key);
    }

    public String getExportElementByIndex(int index) {
        return (String) exportElements.values().toArray()[index];
    }


    public boolean hasElement(String key) {
        return exportElements.containsKey(key);
    }

    public String getDisplayHtml() {
        return displayHtml;
    }

    public String getImageUrl() {
        return resInfor.getImageUrl();
    }

    public String getImageName() {
        return resInfor.getImageName();
    }

    public String getAudioUrl() {
        return resInfor.getAudioUrl();
    }

    public String getAudioName() {
        return resInfor.getAudioName();
    }

    public String getAudioSuffix() {
        return resInfor.getAudioSuffix();
    }

    public String getSpell() { return resInfor.getSpell(); }

    public String getCssUrl() {
        return resInfor.getCssUrl();
    }

    public String getCssName() {
        return resInfor.getCssName();
    }

    public String getJsUrl() {
        return resInfor.getJsUrl();
    }

    public String getJsName() {
        return resInfor.getJsName();
    }


    //
    public static class ResInformation {
        String imageUrl;
        String imageName;
        String audioUrl;
        String audioName;
        String audioSuffix;
        String spell;
        String cssUrl;
        String cssName;
        String jsUrl;
        String jsName;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public void setAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
        }

        public String getAudioName() {
            return audioName;
        }

        public void setAudioName(String audioName) {
            this.audioName = audioName;
        }

        public String getAudioSuffix() {
            return audioSuffix;
        }

        public void setAudioSuffix(String audioSuffix) {
            this.audioSuffix = audioSuffix;
        }

        public String getSpell() {
            return spell;
        }

        public void setSpell(String spell) {
            this.spell = spell;
        }

        public String getCssName() {
            return cssName;
        }

        public void setCssName(String cssName) {
            this.cssName = cssName;
        }

        public String getCssUrl() {
            return cssUrl;
        }

        public void setCssUrl(String cssUrl) {
            this.cssUrl = cssUrl;
        }

        public String getJsName() {
            return jsName;
        }

        public void setJsName(String jsName) {
            this.jsName = jsName;
        }

        public String getJsUrl() {
            return jsUrl;
        }

        public void setJsUrl(String jsUrl) {
            this.jsUrl = jsUrl;
        }

        public ResInformation() {
            this("", "", "", "", "", "", "", "", "", "");
        }

        public ResInformation(String audioUrl, String audioName, String audioSuffix) {
            this("", "", audioUrl, audioName, audioSuffix);
        }

        public ResInformation(String audioUrl, String audioName, String audioSuffix, String spell) {
            this("", "", audioUrl, audioName, audioSuffix, spell);
        }

        public ResInformation(String imageUrl, String imageName, String audioUrl, String audioName, String audioSuffix) {
            this(imageUrl, imageName, audioUrl, audioName, audioSuffix, "");
        }

        public ResInformation(String imageUrl, String imageName, String audioUrl, String audioName, String audioSuffix, String spell) {
            this(imageUrl, imageName, audioUrl, audioName, audioSuffix, spell, "", "", "", "");
        }

        public ResInformation(String imageUrl, String imageName, String audioUrl, String audioName, String audioSuffix, String spell, String cssUrl, String cssName, String jsUrl, String jsName) {
            this.imageUrl = imageUrl;
            this.imageName = imageName;
            this.audioUrl = audioUrl;
            this.audioName = audioName;
            this.audioSuffix = audioSuffix;
            this.spell = spell;
            this.cssUrl = cssUrl;
            this.cssName = cssName;
            this.jsUrl = jsUrl;
            this.jsName =jsName;
        }
    }


}
