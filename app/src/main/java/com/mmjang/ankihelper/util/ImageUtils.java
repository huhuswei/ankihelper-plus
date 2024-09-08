package com.mmjang.ankihelper.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: ImageUtils
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/8/7 11:06 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/8/7 11:06 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ImageUtils {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Bitmap cleanLinesInImage(Bitmap bitmap) {
        Bitmap bufferedBitmap = bitmap;
        int h = bufferedBitmap.getHeight();
        int w = bufferedBitmap.getWidth();

        // 灰度化
        int[][] gray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = bufferedBitmap.getPixel(x, y);
                // 图像加亮（调整亮度识别率非常高）
                int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
                int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
                int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
                if (r >= 255) {
                    r = 255;
                }
                if (g >= 255) {
                    g = 255;
                }
                if (b >= 255) {
                    b = 255;
                }
                gray[x][y] = (int) Math
                        .pow((Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2)
                                * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
            }
        }

        // 二值化
        int threshold = ostu(gray, w, h);
        Bitmap binaryBufferedImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        w = binaryBufferedImage.getWidth();
        h = binaryBufferedImage.getHeight();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (gray[x][y] > threshold) {
                    gray[x][y] |= 0x00FFFF;
                } else {
                    gray[x][y] &= 0xFF0000;
                }
                binaryBufferedImage.setPixel(x, y, gray[x][y]);
            }
        }

        //去除干扰线条
//        for(int y = 1; y < h-1; y++){
//            for(int x = 1; x < w-1; x++){
//                boolean flag = false ;
//                if(isBlack(binaryBufferedImage.getRGB(x, y))){
//                    //左右均为空时，去掉此点
//                    if((binaryBufferedImage.getRGB(x-1, y)) && isWhite(binaryBufferedImage.getRGB(x+1, y))){
//                        flag = true;
//                    }
//                    //上下均为空时，去掉此点
//                    if(isWhite(binaryBufferedImage.getRGB(x, y+1)) && isWhite(binaryBufferedImage.getRGB(x, y-1))){
//                        flag = true;
//                    }
//                    //斜上下为空时，去掉此点
//                    if(isWhite(binaryBufferedImage.getRGB(x-1, y+1)) && isWhite(binaryBufferedImage.getRGB(x+1, y-1))){
//                        flag = true;
//                    }
//                    if(isWhite(binaryBufferedImage.getRGB(x+1, y+1)) && isWhite(binaryBufferedImage.getRGB(x-1, y-1))){
//                        flag = true;
//                    }
//                    if(flag){
//                        binaryBufferedImage.setRGB(x,y,-1);
//                    }
//                }
//            }
//        }
        //去除边框线
        for (int y = 1; y < h; y++) {
            for (int x = 1; x < w; x++) {
                boolean flag = true;
                int px = 15;
                if (isBlack(binaryBufferedImage.getColor(x, y))) {
                    if (x < w - px) {
                        for (int i = 1; i <= px; i++) {
                            //右15像素均为空时，去掉此行
                            if (isWhite(binaryBufferedImage.getColor(x + i, y))) {
                                flag = false;
                                if (!flag) {
                                    break;
                                }
                            }
                        }
                    }
                    for (int i = x; i < w; i++) {
                        if (flag) {
                            if (isBlack(binaryBufferedImage.getColor(i, y))) {
                                binaryBufferedImage.setPixel(i, y, -1);
                            } else {
                                break;
                            }
                        }
                    }
                    flag = true;
                    if (y < h - px) {
                        for (int i = 1; i <= px; i++) {
                            //下15像素均为空时，去掉此列
                            if (isWhite(binaryBufferedImage.getColor(x, y + i))) {
                                flag = false;
                                if (!flag) {
                                    break;
                                }
                            }
                        }
                    }
                    for (int i = y; i < h; i++) {

                        if (flag) {
                            if (isBlack(binaryBufferedImage.getColor(x, i))) {
                                binaryBufferedImage.setPixel(x, i, -1);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }


        // 矩阵打印
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (isBlack(binaryBufferedImage.getColor(x, y))) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

        return binaryBufferedImage;
    }

    public static boolean isBlack(Color color) {
        if (color.red() + color.green() + color.blue() <= 300) {
            return true;
        }
        return false;
    }

    public static boolean isWhite(Color color) {
  
        if (color.red() + color.green() + color.blue() > 300) {
            return true;
        }
        return false;
    }

    public static int isBlackOrWhite(Color color) {
        if (getColorBright(color) < 30 || getColorBright(color) > 730) {
            return 1;
        }
        return 0;
    }

    public static int getColorBright(Color color) {
        return (int) (color.red() + color.green() + color.blue());
    }

    public static int ostu(int[][] gray, int w, int h) {
        int[] histData = new int[w * h];
        // Calculate histogram
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int red = 0xFF & gray[x][y];
                histData[red]++;
            }
        }

        // Total number of pixels
        int total = w * h;

        float sum = 0;
        for (int t = 0; t < 256; t++) {
            sum += t * histData[t];
        }
        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histData[t]; // Weight Background
            if (wB == 0) {
                continue;
            }

            wF = total - wB; // Weight Foreground
            if (wF == 0) {
                break;
            }

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }
    
    public static Bitmap removedLines(Bitmap bitmap) {

        Bitmap bitmap2;
        bitmap2 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int h = bitmap2.getHeight();
        int w = bitmap2.getWidth();
        //去除干扰线条
        for(int y = 1; y < h-1; y++){
            for(int x = 1; x < w-1; x++){
                boolean flag = false ;
                if(isBlack(Color.valueOf(bitmap2.getPixel(x, y)))){
                    //左右均为空时，去掉此点
                    if(isBlack(Color.valueOf(bitmap2.getPixel(x-1, y))) && isWhite(Color.valueOf(bitmap2.getPixel(x+1, y)))){
                        flag = true;
                    }
                    //上下均为空时，去掉此点
                    if(isWhite(Color.valueOf(bitmap2.getPixel(x, y+1))) && isWhite(Color.valueOf(bitmap2.getPixel(x, y-1)))){
                        flag = true;
                    }
                    //斜上下为空时，去掉此点
                    if(isWhite(Color.valueOf(bitmap2.getPixel(x-1, y+1))) && isWhite(Color.valueOf(bitmap2.getPixel(x+1, y-1)))){
                        flag = true;
                    }
                    if(isWhite(Color.valueOf(bitmap2.getPixel(x+1, y+1))) && isWhite(Color.valueOf(bitmap2.getPixel(x-1, y-1)))){
                        flag = true;
                    }
                    if(flag){
                        bitmap2.setPixel(x,y,-1);
                    }
                }
            }
        }
        return bitmap2;
    }

    public static Bitmap ImageSizeCompress(Activity activity, Uri uri){
        InputStream Stream = null;
        InputStream inputStream = null;
        try {
            //根据uri获取图片的流
            inputStream = activity.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options的in系列的设置了，injustdecodebouond只解析图片的大小，而不加载到内存中去
            options.inJustDecodeBounds = true;
            //1.如果通过options.outHeight获取图片的宽高，就必须通过decodestream解析同options赋值
            //否则options.outheight获取不到宽高
            BitmapFactory.decodeStream(inputStream,null,options);
            //2.通过 btm.getHeight()获取图片的宽高就不需要1的解析，我这里采取第一张方式
//            Bitmap btm = BitmapFactory.decodeStream(inputStream);
            //以屏幕的宽高进行压缩
            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
            int heightPixels = displayMetrics.heightPixels;
            int widthPixels = displayMetrics.widthPixels;
            //获取图片的宽高
            int outHeight = options.outHeight;
            int outWidth = options.outWidth;
            //heightPixels就是要压缩后的图片高度，宽度也一样
            int a = (int) Math.ceil((outHeight/(float)heightPixels));
            int b = (int) Math.ceil(outWidth/(float)widthPixels);
            //比例计算,一般是图片比较大的情况下进行压缩
            int max = Math.max(a, b);
            if(max > 1){
                options.inSampleSize = max;
            }
            //解析到内存中去
            options.inJustDecodeBounds = false;
//            根据uri重新获取流，inputstream在解析中发生改变了
            Stream = activity.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(Stream, null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(Stream != null){
                    Stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  null;
    }


    private static byte[] bitmapToBytes(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static String bitmapToBase64(Bitmap image) {
        byte[] buffer = bitmapToBytes(image);
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    public static Bitmap toBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data , 0, data.length);
    }

    public static Bitmap rotate(Bitmap in, int angle) {
        Matrix mat = new Matrix();
        mat.postRotate(angle);
        return Bitmap.createBitmap(in, 0, 0, in.getWidth(), in.getHeight(), mat, true);
    }


    public static Bitmap vectorConvertBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap=null;
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }
}