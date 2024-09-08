package com.mmjang.ankihelper.ui.translation;

public interface Translator {

    String translate(String query);

    String translate(String query, String from, String to);

    String name();

    String getZh();
    String getAuto();
    String getEn();
    String getRus();
    String getFra();
    String getDeu();
    String getSpa();
    String getJpn();
    String getKor();
    String getTha();

}
