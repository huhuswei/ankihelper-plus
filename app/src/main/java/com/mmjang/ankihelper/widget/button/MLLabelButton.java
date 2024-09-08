package com.mmjang.ankihelper.widget.button;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.util.Trace;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.widget
 * @ClassName: HtmlButton
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/7/6 9:23 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/6 9:23 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MLLabelButton extends AppCompatButton {

    public MLLabelButton(Context context) {
        this(context, null);
    }

    public MLLabelButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        autoFitTextSize();
//    }
//
//    private void autoFitTextSize() {
//        Paint p = getPaint();
//        p.setTypeface(getTypeface());
//        p.setTextSize(getTextSize());
//
//        float needWidth = getPaddingLeft()+getPaddingRight()+p.measureText(getText().toString());
//        if (needWidth > getWidth()) {
//            setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize()-0.5f);
//            setWidth(ScreenUtils.dp2px(getContext(),32));
//            autoFitTextSize();
//        }
//    }

    private void init() {
        setAllCaps(false);
        this.setBackgroundResource(R.drawable.no_solid_background);
        setPadding(0,0,0,0);
//        setWidth(ScreenUtils.dp2px(getContext(),36));
//        setHeight(ScreenUtils.dp2px(getContext(),36));
//        final int color;
//        if (Settings.getInstance(getContext()).getPinkThemeQ()) {
//            color = R.color.colorPrimaryPink;
//        } else {
//            color = R.color.colorPrimary;
//        }
//        this.setTextColor(getResources().getColor(color));
//        this.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//        this.setBackgroundResource(R.color.transparent);
    }

    public void setupHtmlTag(final EditText edt, MLLabel htmltag) {
        this.init();
        this.setText(htmltag.getName());
        String front = htmltag.getFront();
        String behind = htmltag.getBehind();

        this.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable edit = edt.getText();
                        int start = edt.getSelectionStart();
                        int end = edt.getSelectionEnd();
                        Trace.i("start : end", String.format("%d : %d", start, end));
                        if(start < end) {
                            edt.setText(edit.toString().substring(0, start) +
                                    front + edit.toString().substring(start, end) + behind +
                                    edit.toString().substring(end));
                            Spannable spanText = (Spannable) edt.getText();
                            Selection.setSelection(spanText, end + front.length() + behind.length());
                        } else {
                            edt.setText(edit.toString().substring(0, start) + front + behind + edit.toString().substring(end));
                            Spannable spanText = (Spannable) edt.getText();
                            Selection.setSelection(spanText, start + front.length());
                        }
                        edt.setFocusable(true);
                        edt.setFocusableInTouchMode(true);
                        edt.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(edt, 0);
                    }
                }
        );
    }

//    public void setupHtmlTag(final EditText edt, HtmlTagEnums tag) {
//        this.init();
////        this.setText(tag.name());
//        String front;
//        String behind;
//        switch(tag) {
//            case b:
//                this.setText(R.string.html_tag_bold);
//                front = "<b>";
//                behind = "</b>";
//                break;
//            case i:
//                this.setText(R.string.html_tag_italic);
//                front = "<i>";
//                behind = "</i>";
//                break;
//            case br:
//                this.setText(R.string.html_tag_br);
//                front = "<br/>";
//                behind = "";
//                break;
//            case h1:
//                this.setText(R.string.html_tag_h1);
//                front = "<h1>";
//                behind = "</h1>";
//                break;
//            case h2:
//                this.setText(R.string.html_tag_h2);
//                front = "<h2>";
//                behind = "</h2>";
//                break;
//            case h3:
//                this.setText(R.string.html_tag_h3);
//                front = "<h3>";
//                behind = "</h3>";
//                break;
//            case a:
//                this.setText(R.string.html_tag_a);
//                front = "<a href=\"\">";
//                behind = "</a>";
//                break;
//            default:
//                front = "";
//                behind = "";
//                break;
//        }
//        this.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Editable edit = edt.getText();
//                        int start = edt.getSelectionStart();
//                        int end = edt.getSelectionEnd();
//                        Trace.e("start : end", String.format("%d : %d", start, end));
//                        if(start < end) {
//                            edt.setText(StringUtil.appendTagAll(edit.toString(), edit.toString().substring(start, end), front, behind));
//                            Spannable spanText = (Spannable) edt.getText();
//                            Selection.setSelection(spanText, end + front.length() + behind.length());
//                        } else {
//                            edt.setText(edit.toString().substring(0, start) + front + behind + edit.toString().substring(start));
//                            Spannable spanText = (Spannable) edt.getText();
//                            Selection.setSelection(spanText, start + front.length());
//                        }
//                        edt.setFocusable(true);
//                        edt.setFocusableInTouchMode(true);
//                        edt.requestFocus();
//                        InputMethodManager inputMethodManager = (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                        inputMethodManager.showSoftInput(edt, 0);
//                    }
//                }
//        );
//    }
//    public enum HtmlTagEnums {
//        b,
//        i,
//        br,
//        h1,
//        h2,
//        h3,
//        a;
//    }
}
