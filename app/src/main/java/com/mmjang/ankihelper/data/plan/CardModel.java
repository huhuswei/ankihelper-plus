package com.mmjang.ankihelper.data.plan;

import java.util.Arrays;

/**
 * Created by liao on 2017/3/19.
 */

public class CardModel {
    private static final String MODEL_SPLITTER = "@@@";
    private static final String ELEMENT_SPLITTER = "\\|";

    private String[] cards;
    private String[] fields;
    private Layout[] layouts;
    private String css;
    private String name;

    public CardModel(String name, String[] cards, String[] fields, Layout[] layouts, String css) {
        this.name = name;
        this.cards = cards;
        this.fields = fields;
        this.css = css;
        this.layouts = layouts;
    }

    public String getName() {
        return name;
    }

    public String[] getCards() {
        return cards;
    }

    public String[] getFields() {
        return fields;
    }

    public String getCss() {
        return css;
    }

    public Layout[] getLayouts() {
        return layouts;
    }

    public static class Layout {
        private String front;
        private String back;

        public Layout(String front, String back) {
            this.front = front;
            this.back = back;
        }

        public String getFront() {
            return front;
        }

        public String getBack() {
            return back;
        }
    }

    public static CardModel buildCardModel(String fileName, String text) {
        String[] str = Arrays.stream(text.split(MODEL_SPLITTER)).map(s->s.trim()).toArray(String[]::new);
        if (str.length >= 5 && str.length % 2 == 1) {
            String[] cards = str[0].split(ELEMENT_SPLITTER);
            String[] fields = str[1].split(ELEMENT_SPLITTER);
            String css = str[str.length - 1];
            Layout[] layouts = new Layout[(str.length - 2) / 2];
            for (int i = 2; i < str.length - 1; i += 2) {
                String front = str[i];
                String back = str[i + 1];
                Layout layout = new Layout(front, back);
                layouts[(i - 2) / 2] = layout;
            }
            if (cards.length != 0 && fields.length != 0 && cards.length == layouts.length) {
                return new CardModel(fileName, cards, fields, layouts, css);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

