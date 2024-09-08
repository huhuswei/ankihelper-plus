package com.mmjang.ankihelper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.widget
 * @ClassName: InlineScrollView
 * @Description: java类作用描述
 * @Author: 唐朝
 * @CreateDate: 2022/10/10 11:17 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/10/10 11:17 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class InlineScrollView extends ScrollView {

    public InlineScrollView(Context context) {
        super(context);
    }

    public InlineScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InlineScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxY = getChildAt(0).getMeasuredHeight() - getMeasuredHeight();
    }

    //在Y轴上可以滑动的最大距离 = 总长度 - 当前展示区域长度
    private int maxY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (getScrollY() >= 0 && getScrollY() <= maxY)
                    getParent().requestDisallowInterceptTouchEvent(true);
                else
                    getParent().requestDisallowInterceptTouchEvent(false);
                /*if (getScrollY()==0)
                    Log.i("kkk","滑到头了");
                if (getChildAt(0).getMeasuredHeight() == getScrollY() + getHeight())
                    Log.i("kkk","滑到底了");*/
                break;
            case MotionEvent.ACTION_UP:
                getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
