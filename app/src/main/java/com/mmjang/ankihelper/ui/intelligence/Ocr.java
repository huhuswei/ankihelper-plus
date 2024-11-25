package com.mmjang.ankihelper.ui.intelligence;

import android.graphics.Bitmap;
import android.net.Uri;

public interface Ocr {
    public String getText(Bitmap bitmap);
    public String getText(Uri uri);
    void onDestroy();
}
