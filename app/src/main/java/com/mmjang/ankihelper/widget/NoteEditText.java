package com.mmjang.ankihelper.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;

import com.google.android.material.textfield.TextInputEditText;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.util.MathUtil;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.widget
 * @ClassName: NoteEditText
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/7/5 1:46 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/5 1:46 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class NoteEditText extends TextInputEditText {
    public NoteEditText(Context context) {
        super(context);
        init();
    }

    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setPadding(60,getPaddingTop(),10,getPaddingBottom());
        setGravity(Gravity.TOP|Gravity.START);
    }

    private int getCurrentLine() {//获取光标所在行
        int selectionStart = getSelectionStart();
        Layout layout = getLayout();
        if (selectionStart != -1 && layout != null) {
            return layout.getLineForOffset(selectionStart);
        }
        return -1;
    }
    Paint lineHeightPaint = new Paint();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //        //set bg color in theme.
        int[] attrArr = {R.attr.colorPrimary};
        TypedArray typedArr = getContext().obtainStyledAttributes(attrArr);


        lineHeightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        lineHeightPaint.setColor(Color.argb(30, 90, 164, 161));
        lineHeightPaint.setColor(typedArr.getColorStateList(0).withAlpha(30).getDefaultColor());
        int currentCursorPositionLine = getCurrentLine();
        int getCurrentTop = getLayout().getLineTop(currentCursorPositionLine);//如果是0 获取第0 行的顶部
        int getCurrentBottom = getLayout().getLineBottom(currentCursorPositionLine);
        int paddingTop = getPaddingTop();
        int lineCount = getLineCount();
        canvas.drawRect(0, getCurrentTop+paddingTop, getWidth(), getCurrentBottom+paddingTop, lineHeightPaint);
        Paint lineNumberPaint = getPaint();
//        lineNumberPaint.setColor(Color.argb(100, 90, 164, 161));
        lineNumberPaint.setColor(typedArr.getColorStateList(0).withAlpha(100).getDefaultColor());
        int digits = MathUtil.digits(lineCount);
        for (int i=0;i<lineCount;i++){
            int lineBottom = getLayout().getLineBottom(i);
            String serial;
            if(i < 99)
                serial = String.valueOf(i+1);
            else
                serial = "🤡";
            //这里的y 是基线，需要得到这个基线
            canvas.drawText(String.format("%4s", serial), 0, lineBottom-lineNumberPaint.descent()+paddingTop, lineNumberPaint);
        }
    }
}
