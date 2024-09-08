package com.mmjang.ankihelper.ui.intelligence.mathpix.api.request;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;

public class SingleProcessRequest {
    @SerializedName("url")
    private String url;
//    private boolean include_shape_data;

    public SingleProcessRequest(Bitmap bm) {
        url = "data:image/png;base64," + bitmapToBase64(bm);
//        include_shape_data = true;
    }

    public SingleProcessRequest(byte[] fileBytes) {
        url = "data:image/png;base64," + Base64.encodeToString(fileBytes, Base64.DEFAULT);
    }

    private String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, os);
        return Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
    }
}
