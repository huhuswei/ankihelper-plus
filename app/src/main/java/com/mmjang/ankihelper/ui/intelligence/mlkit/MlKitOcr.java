package com.mmjang.ankihelper.ui.intelligence.mlkit;

import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.mmjang.ankihelper.ui.intelligence.Ocr;


import java.util.function.Function;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.ui.ocr
 * @ClassName: MlKitOcr
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/7/23 10:25 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/23 10:25 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MlKitOcr implements Ocr{
    Context mContext;
    String mContent;
    Function<String, String> hookResult;
    TextRecognizer recognizer;
    LanguageText lang;


    public enum LanguageText {
        CHINESE(0), JAPANESE(1), KOREAN(2), LATIN(3), DEVANAGARI(4);

        private int index;

        private LanguageText(int index){
            this.index=index;
        }
        public int getIndex(){
            return this.index;
        }

        public static LanguageText getByValue(int value){
            for(LanguageText x:values()){
                if(x.getIndex()==value){
                    return x;
                }
            }
            return null;
        }
    }

    public MlKitOcr(Context context) {
        mContext = context;
        mContent = "";
        hookResult = s->s;
        lang = null;
    }

    public void setLanguageText(int index) { this.lang = LanguageText.values()[index]; }
    public void setLanguageText(String lang) {
        this.lang = LanguageText.valueOf(lang);
    }

    private void init() {
        switch (lang) {
            case CHINESE:
                recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                break;
            case JAPANESE:
                recognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
                break;
            case KOREAN:
                recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                break;
            case LATIN:
                recognizer = TextRecognition.getClient(new TextRecognizerOptions.Builder().build());
                break;
            case DEVANAGARI:
                recognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                break;

            default:
                recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
        }
    }

    private void recognizeText(Bitmap image, int rotationDegree) {
        // [START get_detector_default]
//        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // [END get_detector_default]
        InputImage inputImage = InputImage.fromBitmap(image, rotationDegree);
        // [START run_detector]
        Task<Text> result =
                recognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                mContent = visionText.getText();
                                hookResult.apply(mContent);
                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_text]
//                                for (Text.TextBlock block : visionText.getTextBlocks()) {
//                                    Rect boundingBox = block.getBoundingBox();
//                                    Point[] cornerPoints = block.getCornerPoints();
//                                    String text = block.getText();
//
//                                    for (Text.Line line: block.getLines()) {
//                                        // ...
//
//                                        for (Text.Element element: line.getElements()) {
//                                            // ...
//
//                                        }
//                                    }
//                                }
                                // [END get_text]
                                // [END_EXCLUDE]
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        hookResult.apply("");
                                        // ...
                                    }
                                });
        // [END run_detector]
    }

    private void processTextBlock(Text result) {
        // [START mlkit_process_text_block]
        String resultText = result.getText();
        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
        // [END mlkit_process_text_block]
    }

    public String getText(Bitmap image, int rotationDegree) {
        init();
        recognizeText(image, 0);
        return mContent;
    }

    public String getText(Bitmap image){
        return getText(image, 0);
    }

    public String getText(Bitmap image, Function<String, String> callaback){
        this.hookResult = callaback;
        return getText(image);
    }

    public void onDestroy() {
        recognizer.close();
    }
}
