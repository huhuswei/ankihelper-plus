package com.mmjang.ankihelper.ui.intelligence.mathpix;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.intelligence.Ocr;
import com.mmjang.ankihelper.ui.intelligence.mathpix.api.request.SingleProcessRequest;
import com.mmjang.ankihelper.ui.intelligence.mathpix.api.response.DetectionResult;
import com.mmjang.ankihelper.util.Constant;


import java.util.function.Function;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MathpixOcr extends AsyncTask<MathpixOcr.UploadParams, Void, MathpixOcr.Result> implements Ocr {

    private ResultListener listener;
    Context mContext;
    String mContent;
    Function<String, String> hookResult;
    boolean success;
    private final Settings settings = Settings.getInstance(MyApplication.getContext());

    public MathpixOcr(Context context) {
        mContext = context;
        mContent = "";
        success = false;
        hookResult = s->s;
    }

    @Override
    protected Result doInBackground(UploadParams... arr) {
        UploadParams params = arr[0];
        Result result;
        try {
            OkHttpClient client = new OkHttpClient();
            SingleProcessRequest singleProcessRequest = new SingleProcessRequest(params.image);
            MediaType JSON = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(new Gson().toJson(singleProcessRequest), JSON);

            Request request = new Request.Builder()
                    .url(Constant.MATHPIX_BASE_URL)
                    .addHeader("content-type", "application/json")
                    .addHeader("app_id", settings.get(Settings.OCR_MATHPIX_ID, ""))
                    .addHeader("app_key", settings.get(Settings.OCR_MATHPIX_KEY, ""))
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            Log.d("MathPix", responseString);
            DetectionResult detectionResult = new Gson().fromJson(responseString, DetectionResult.class);
            if (detectionResult != null && detectionResult.text != null) {
                result = new ResultSuccessful(detectionResult.text);
            } else if (detectionResult != null && detectionResult.error != null) {
                result = new ResultFailed(detectionResult.error);
            } else {
                result = new ResultFailed("Math not found");
            }
        } catch (Exception e) {
            result = new ResultFailed("Failed to send to server. Check your connection and try again");
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result instanceof ResultSuccessful) {
            ResultSuccessful successful = (ResultSuccessful) result;
            listener.onSuccess(successful.latex);
        } else if (result instanceof ResultFailed) {
            ResultFailed failed = (ResultFailed) result;
            listener.onError(failed.message);
        }
    }

    public String getText(Bitmap bitmap) {
        recognizeText(bitmap);
        return mContent;
    }

    public String getText(Bitmap image, Function<String, String> callback){
        this.hookResult = callback;
        return getText(image);
    }

    private void recognizeText(Bitmap bitmap) {
        try {
            UploadParams mathpixParams = new UploadParams(bitmap);
            listener = new ResultListener() {
                @Override
                public void onError(String message) {
                    hookResult.apply("");
                }

                @Override
                public void onSuccess(String latex) {
                    mContent = latex.trim();
                    hookResult.apply(mContent);
                }
            };
            execute(mathpixParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    public interface ResultListener {
        void onError(String message);

        void onSuccess(String url);
    }

    public static class UploadParams {
        private Bitmap image;

        public UploadParams(Bitmap image) {
            this.image = image;
        }
    }

    public static class Result {
    }

    private static class ResultSuccessful extends Result {
        String latex;

        ResultSuccessful(String latex) {
            this.latex = latex;
        }
    }

    private static class ResultFailed extends Result {
        String message;

        ResultFailed(String message) {
            this.message = message;
        }
    }
}
