package com.mmjang.ankihelper.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.util.DarkModeUtils;
import com.x5.template.Chunk;
import com.x5.template.Theme;
import com.x5.template.providers.AndroidTemplates;

import org.apache.commons.text.StringEscapeUtils;

/**
 * @ClassName: MathxView
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/10/4 9:53 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/10/4 9:53 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

public class MathxView extends WebView {
    private static final int[] COLOR_BACKGROUND_ATTR = {android.R.attr.colorBackground};
    private int mScrollY = 0;
    private String TAG = "MathxView";
    private String mText = "";
    private String mFontColor = "0x000000";
    private String mBgColor = "#0xffffff";
    private String mConfig;
    private int mEngine;
    private int mMaxHeight;

    public MathxView(Context context) {
        this(context, null);
    }

    public MathxView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.mathxView);
    }

    public MathxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        autoSetMaxHeight();
    }

    private void autoSetMaxHeight() {
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) MathxView.this.getLayoutParams();
//        if (getMaxHeight() > 0 ) {
//            if (getMaxHeight() < getHeight() && layoutParams.height != getMaxHeight()) {
//                layoutParams.height = getMaxHeight();
//                setLayoutParams(layoutParams);
//            } else if (getContentHeight() < getHeight() && layoutParams.height == getMaxHeight()) {
//                layoutParams.height = getContentHeight();// + ScreenUtils.dp2px(getContext(), 20);
//                setLayoutParams(layoutParams);
////                ToastUtil.show(String.format("getContentHeight(): %d\ngetHeight: %d\ngetMaxHeight(): %d\n getMeasuredHeight(): %d", getContentHeight(), getHeight(), getMaxHeight(), getMeasuredHeight()));
//
//            }
//        }
        if (getMaxHeight() > 0) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) MathxView.this.getLayoutParams();
            layoutParams.height = getMaxHeight();
            setLayoutParams(layoutParams);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        else
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (attrs != null) {
            TypedArray math = context.obtainStyledAttributes(attrs, R.styleable.MathxView, defStyleAttr, R.style.MathxView);

            if (math.hasValue(R.styleable.MathxView_text))
                setText(math.getString(R.styleable.MathxView_text));
            if (math.hasValue(R.styleable.MathxView_engine))
                setEngine(math.getInt(R.styleable.MathxView_engine, 1));
            if (math.hasValue(R.styleable.MathxView_maxHeight))
                setMaxHeight(math.getDimension(R.styleable.MathxView_maxHeight, 0));
//            if (math.hasValue(R.styleable.MathxView_fontColor))
//                setFontColor(math.getColorStateList(R.styleable.MathxView_fontColor).getDefaultColor());
//            if (math.hasValue(R.styleable.MathxView_bgColor))
//                setBgColor(math.getColorStateList(R.styleable.MathxView_bgColor).getDefaultColor());

            math.recycle();

            if(DarkModeUtils.isDarkMode(getContext())) {
                setFontColor("#ffffff");
                setBgColor( "#262626");
            } else {
                setFontColor("#000000");
                setBgColor( "#ffffff");
            }

        }


        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mEngine == Engine.MATHJAX)
                    view.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
                view.setScrollY(mScrollY);
//                view.setScrollY(Settings.getInstance(MyApplication.getContext()).get(Settings.MATHXVIEW_SCROLL_POSITION_Y, 0));

            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                if (view.isFocused()) {
                    super.onScaleChanged(view, oldScale, newScale);
                    mScrollY = (int) (view.getScrollY() / newScale * oldScale);
                }
            }


        };
        setWebViewClient(webViewClient);

        update();
    }


    @Nullable
    public final String getText() {
        return this.mText;
    }

    public final void setText(@Nullable String text) {
        this.mText = text;
        Log.i("text", text);
        update();
    }

    private String getTemplate() {
        switch (mEngine) {
            case Engine.KATEX:
                return String.format("<!DOCTYPE html>" +
                        "<html lang=\"en\">\n" +
                        "    <head>\n" +
                        "        <meta charset=\"UTF-8\">\n" +
                        "        <title>Auto-render test</title>\n" +
                        "        <link rel=\"stylesheet\" type=\"text/css\" href=\"katex/katex.min.css\">\n" +
                        "        <link rel=\"stylesheet\" type=\"text/css\" href=\"themes/style.css\">\n" +
                        "        <script type=\"text/javascript\" src=\"katex/katex.min.js\"></script>\n" +
                        "        <script type=\"text/javascript\" src=\"katex/contrib/auto-render.min.js\"></script>\n" +
                        "    </head>\n" +
                        "    <body>\n" +
                        "        <script>\n" +
                        "var s = \" %s \";" +
                        "document.body.style.color = \"%s\";" +
                        "document.body.style.backgroundColor = \"%s\";" +
                        "document.write(s);\n" +
                        "          renderToString(\n" +
                        "              document.body, \"body\"\n" +
                        "          );\n" +
                        "        </script>\n" +
                        "    </body>\n" +
                        "</html>", StringEscapeUtils.escapeJava(this.getText()), getFontColor(), getBgColor());
            case Engine.MATHJAX:
            default:
                return String.format("<html lang=\"en\">\n" +
                        "<head>\n" +
                        "<script>" +
                        "MathJax = {" +
                        "tex: {inlineMath: [['$', '$'], ['\\\\(', '\\\\)']]}, " +
                        "svg: {fontCache: 'global'}, " +
                        "chtml: {scale: %s,}};" +
                        "</script>" +
                        "<script type=\"text/javascript\" async src=\"mathjax/tex-chtml.js\">\n" +
                        "</script>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<script>\n" +
                        "var s = \" %s \";" +
                        "document.body.style.color = \"%s\";" +
                        "document.body.style.backgroundColor = \"%s\";" +
                        "document.write(s);\n" +
                        "</script>\n" +
                        "</body>\n" +
                        "</html>", "1", StringEscapeUtils.escapeJava(this.getText()), getFontColor(), getBgColor());
        }
    }
    private void update() {
        loadDataWithBaseURL(
                "file:///android_asset/",
                getTemplate(),
                "text/html", "UTF-8", null
        );
//        ToastUtil.show(String.format("fontColor: %s\nbgColor: %s", getFontColor(), getBgColor()));
    }

    private Chunk getChunk() {
        String TEMPLATE_KATEX = "katex";
        String TEMPLATE_MATHJAX = "mathjax";
        String template = TEMPLATE_KATEX;
        AndroidTemplates loader = new AndroidTemplates(getContext());
        switch (mEngine) {
            case Engine.KATEX:
                template = TEMPLATE_KATEX;
                break;
            case Engine.MATHJAX:
                template = TEMPLATE_MATHJAX;
                break;
        }

        return new Theme(loader).makeChunk(template);
    }

//    public void setText(String text) {
//        mText = text;
//        Chunk chunk = getChunk();
//
//        String TAG_FORMULA = "formula";
//        String TAG_CONFIG = "config";
//        String TAG_TEXT_SCALE = "text_scale";
//        String TAG_TEXT_COLOR = "text_color";
//
//        chunk.set(TAG_FORMULA, mText);
//        chunk.set(TAG_CONFIG, mConfig);
//        chunk.set(TAG_TEXT_SCALE, 1);
//        chunk.set(TAG_TEXT_COLOR, "#000000");
//        this.loadDataWithBaseURL("file:///android_asset/", chunk.toString(), "text/html", "utf-8", "about:blank");
//    }
//
//    public String getText() {
//        return mText;
//    }

    /**
     * Tweak the configuration of MathJax.
     * The `config` string is a call statement for MathJax.Hub.Config().
     * For example, to enable auto line breaking, you can call:
     * config.("MathJax.Hub.Config({
     * CommonHTML: { linebreaks: { automatic: true } },
     * "HTML-CSS": { linebreaks: { automatic: true } },
     * SVG: { linebreaks: { automatic: true } }
     * });");
     * <p>
     * This method should be call BEFORE setText() and AFTER setEngine().
     * PLEASE PAY ATTENTION THAT THIS METHOD IS FOR MATHJAX ONLY.
     *
     * @param config
     */
    public void config(String config) {
        if (mEngine == Engine.MATHJAX) {
            this.mConfig = config;
        }
    }

    /**
     * Set the js engine used for rendering the formulas.
     *
     * @param engine must be one of the constants in class Engine
     *               <p>
     *               This method should be call BEFORE setText().
     */
    public void setEngine(int engine) {
        switch (engine) {
            case Engine.KATEX: {
                mEngine = Engine.KATEX;
                break;
            }
            case Engine.MATHJAX: {
                mEngine = Engine.MATHJAX;
                break;
            }
            default:
                mEngine = Engine.KATEX;
        }
    }

    public static class Engine {
        final public static int KATEX = 0;
        final public static int MATHJAX = 1;
    }


    public int getMaxHeight() {
        return mMaxHeight;
    }

    public void setMaxHeight(float mMaxHeight) {
        this.mMaxHeight = (int) mMaxHeight;
    }

    public String getFontColor() { return mFontColor; }

    public void setFontColor(String color) { this.mFontColor = color; }

    public String getBgColor() {
        return mBgColor;
    }

    public void setBgColor(String bgColor) {
        this.mBgColor = bgColor;
    }

    /*this method will return the latex into inline*/
    public static String inLine(String text) {
        return String.format("\\( %s \\)", text);
    }

    /*this method will return the latex into display form*/
    public static String display(String text) {
        return String.format("$$ %s $$", text);
    }

    public int getmScrollY() {
        return mScrollY;
    }

    public void setmScrollY(int mScrollY) {
        this.mScrollY = mScrollY;
    }

}