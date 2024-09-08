package com.mmjang.ankihelper.ui.intelligence;

import android.graphics.Bitmap;

public interface Ocr {
    public String getText(Bitmap bitmap);
    void onDestroy();
}
