package com.mmjang.ankihelper.ui.floating;

import android.graphics.Rect;

import java.io.Serializable;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.floating
 * @ClassName: DrawInfo
 * @Description: java类作用描述
 * @Author: 唐朝
 * @CreateDate: 2022/7/20 10:44 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/20 10:44 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DrawInfo implements Serializable {
    private Rect mRect;

    public DrawInfo(Rect rect) {
        mRect = rect;
    }

    public Rect getmRect() {
        return mRect;
    }

}
