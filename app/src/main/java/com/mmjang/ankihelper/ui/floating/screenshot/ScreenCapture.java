package com.mmjang.ankihelper.ui.floating.screenshot;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.mmjang.ankihelper.util.FileUtils;
import com.mmjang.ankihelper.util.ScreenUtils;
import com.mmjang.ankihelper.util.StorageUtils;
import com.mmjang.ankihelper.util.ToastUtil;
import com.mmjang.ankihelper.util.Trace;
import com.mmjang.ankihelper.util.ViewUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenCapture {
    private static final String TAG = "ScreenCaptureActivity";
    public static final String MESSAGE = "message";
    public static final String FILE_NAME = "temp_file";
//    private File nameImage = null;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;

    public static int mResultCode = 0;
    public static Intent mResultData = null;
    public static MediaProjectionManager mMediaProjectionManager1 = null;

    private WindowManager mWindowManager1 = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private DisplayMetrics metrics = null;
    private int mScreenDensity = 0;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    Handler handler = new Handler(Looper.getMainLooper());
    private Rect mRect;
    private MarkSizeView.GraphicPath mGraphicPath;
    private ScreenCaptureActivity activity;


    public ScreenCapture(ScreenCaptureActivity activity, Intent intent, int resultCode
            , Rect mRect, MarkSizeView.GraphicPath mGraphicPath){
        this.activity=activity;
        mResultData=intent;
        mResultCode=resultCode;
        this.mRect = mRect;
        this.mGraphicPath = mGraphicPath;
        this.mScreenWidth = ViewUtil.getScreenWidth(activity);
        this.mScreenHeight = ViewUtil.getSceenHeight(activity);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
//        String strDate = dateFormat.format(new java.util.Date());
//        this.nameImage = StorageUtils.createCacheFile(strDate + ".png");
//        if(mRect != null) {
//            Trace.i("rect", String.format("sw: %d\nsh: %d\nl: %d\nt: %d\nr: %d\nb: %d",
//                    mScreenWidth, mScreenHeight,
//                    mRect.left, mRect.top, mRect.right, mRect.bottom));
//
//            ToastUtil.show(String.format("sw: %d\nsh: %d\nl: %d\nt: %d\nr: %d\nb: %d",
//                    mScreenWidth, mScreenHeight,
//                    mRect.left, mRect.top, mRect.right, mRect.bottom));
//        }

        try {
            createVirtualEnvironment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toCapture() {
        try {

//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    Trace.d(TAG, "before startVirtual");
//                    startVirtual();
//                    Trace.d(TAG, "after startVirtual");
//                }
//            }, 150);

//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    //capture the screen
//                    try {
//                        Trace.d(TAG, "before startCapture");
//                        startCapture();
//                        Trace.d(TAG, "after startCapture");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        sendBroadcastCaptureFail();
//                    }
//                }
//            }, 200);
            startVirtual();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Image image = mImageReader.acquireNextImage();
                                        if (image == null)
                                            handler.postDelayed(this, 10);
                                        else {
                                            handler.removeCallbacks(this);
                                            new SaveTask().doInBackground(image);
                                        }
                                    }
                                },
                    10);
            //this is a delay due to that record screen permission dialog has not dismissed on some devices cause take dialog graphic in screenshot
            //.@see<a href="https://github.com/weizongwei5/AndroidScreenShot_SysApi/issues/4">issues</a>
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcastCaptureFail() {
        ToastUtil.show("R.string.screen_capture_fail");
        activity.finish();
    }

    @SuppressLint("WrongConstant")
    private void createVirtualEnvironment() {
        mMediaProjectionManager1 = (MediaProjectionManager) activity.getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager) activity.getApplication().getSystemService(Context.WINDOW_SERVICE);
//        windowWidth = ViewUtil.getScreenWidth(activity);
//        windowHeight = ViewUtil.getSceenHeight(activity);
        metrics = new DisplayMetrics();
        mWindowManager1.getDefaultDisplay().getRealMetrics(metrics);
        windowWidth = metrics.widthPixels;
        windowHeight = metrics.heightPixels;
        mScreenDensity = (int) metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, PixelFormat.RGBA_8888, 1); //ImageFormat.RGB_565


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mImageReader = ImageReader.newInstance(
//                    windowWidth, windowHeight,
//                    PixelFormat.RGBA_8888,//此处必须和下面 buffer处理一致的格式 ，RGB_565在一些机器上出现兼容问题。
//                    1);
//        } else {
//            mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2);
//        }

        Trace.d(TAG, "prepared the virtual environment");
    }

    public void startVirtual() {
        if (mMediaProjection != null) {
            Trace.d(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            Trace.d(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    public void setUpMediaProjection() {
        try {
            mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
//            hideStatusBar();
        } catch (Exception e) {
            e.printStackTrace();
            Trace.d(TAG, "mMediaProjection defined");
        }

    }

    private void virtualDisplay() {
        try {
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                    windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
            Trace.d(TAG, "virtual displayed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void startCapture() throws Exception {
//        strDate = dateFormat.format(new java.util.Date());
//        nameImage = pathImage + strDate + ".png";
//
//        Image image = mImageReader.acquireLatestImage();
//
//        if (image==null){
//            Trace.d(TAG, "image==null,restart");
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    toCapture();
//                }
//            });
//            return;
//        }
//        int width = image.getWidth();
//        int height = image.getHeight();
//        final Image.Plane[] planes = image.getPlanes();
//        final ByteBuffer buffer = planes[0].getBuffer();
//        //每个像素的间距
//        int pixelStride = planes[0].getPixelStride();
//        //总的间距
//        int rowStride = planes[0].getRowStride();
//        int rowPadding = rowStride - pixelStride * width;
//        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
//        bitmap.copyPixelsFromBuffer(buffer);
//        image.close();
//        Trace.d(TAG, "image data captured");
//
//        // 获取状态栏高度
//        Rect frame = new Rect();
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        int statusBarHeight = frame.top;
//        Bitmap fullBitmap = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width + rowPadding / pixelStride, height - statusBarHeight);
//
//        if (width!=mScreenWidth ||rowPadding !=0){
//            int[] pixel=new int[width + rowPadding / pixelStride];
//            bitmap.getPixels(pixel,0,width + rowPadding / pixelStride,0,0,width + rowPadding / pixelStride,1);
//            int leftPadding=0;
//            int rightPadding=width + rowPadding / pixelStride;
//            for (int i=0;i<pixel.length;i++){
//                if (pixel[i]!=0){
//                    leftPadding=i;
//                    break;
//                }
//            }
//            for (int i=pixel.length-1;i>=0;i--){
//                if (pixel[i]!=0){
//                    rightPadding=i;
//                    break;
//                }
//            }
//            width=Math.min(width,mScreenWidth);
//            if (rightPadding-leftPadding>width){
//                rightPadding= width;
//            }
//            bitmap=Bitmap.createBitmap(bitmap,leftPadding, 0, rightPadding-leftPadding, height);
//            fullBitmap = Bitmap.createBitmap(bitmap, leftPadding, statusBarHeight, rightPadding-leftPadding, height - statusBarHeight);
//        }
//
//        Trace.d(TAG, "bitmap cuted first");
//        if (mGraphicPath!=null){
//            mRect=new Rect(mGraphicPath.getLeft(),mGraphicPath.getTop(),mGraphicPath.getRight(),mGraphicPath.getBottom());
//        }
//        if (mRect != null) {
//
//            if (mRect.left < 0)
//                mRect.left = 0;
//            if (mRect.right < 0)
//                mRect.right = 0;
//            if (mRect.top < 0)
//                mRect.top = 0;
//            if (mRect.bottom < 0)
//                mRect.bottom = 0;
//            int cut_width = Math.abs(mRect.left - mRect.right);
//            int cut_height = Math.abs(mRect.top - mRect.bottom);
//            if (cut_width > 0 && cut_height > 0) {
//                Bitmap cutBitmap = Bitmap.createBitmap(bitmap, mRect.left, mRect.top, cut_width, cut_height);
//                Trace.d(TAG, "bitmap cuted second");
//                if (mGraphicPath!=null){
//                    // 准备画笔
//                    Paint paint = new Paint();
//                    paint.setAntiAlias(true);
//                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
//                    paint.setColor(Color.WHITE);
//                    Bitmap temp = Bitmap.createBitmap(cut_width, cut_height, Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(temp);
//
//                    Path path = new Path();
//                    if (mGraphicPath.size() > 1) {
//                        path.moveTo((float) ((mGraphicPath.pathX.get(0)-mRect.left)), (float) ((mGraphicPath.pathY.get(0)- mRect.top)));
//                        for (int i = 1; i < mGraphicPath.size(); i++) {
//                            path.lineTo((float) ((mGraphicPath.pathX.get(i)-mRect.left)), (float) ((mGraphicPath.pathY.get(i)- mRect.top)));
//                        }
//                    } else {
//                        return;
//                    }
//                    canvas.drawPath(path, paint);
//                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//
//                    // 关键代码，关于Xfermode和SRC_IN请自行查阅
//                    canvas.drawBitmap(cutBitmap, 0 , 0, paint);
//                    Trace.d(TAG, "bitmap cuted third");
//
//                    saveCutBitmap(temp);
//
//                }else {
//                    saveCutBitmap(cutBitmap);
//                }
//            }
//        } else {
//            saveCutBitmap(fullBitmap);
//        }
//        bitmap.recycle();//自由选择是否进行回收
//    }

    @SuppressLint("StaticFieldLeak")
    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toCapture();
                    }
                });
                return null;
            }
//            Image image = mImageReader.acquireLatestImage();
            Image image = params[0];

            if (image == null) {
                Trace.d(TAG, "image==null,restart");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toCapture();
                    }
                });
                return null;
            }
            int width = Math.abs(image.getWidth());
            int height = Math.abs(image.getHeight());
            //debug
            if (width == 0 || height == 0)
                return null;


            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            image.close();
            Trace.d(TAG, "image data captured");

            // 获取状态栏高度
            Rect frame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//            int statusBarHeight = frame.top;
            int statusBarHeight = 0;

            Bitmap fullBitmap = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width + rowPadding / pixelStride, height - statusBarHeight);

            if (width != mScreenWidth || rowPadding != 0) {
                int[] pixel = new int[width + rowPadding / pixelStride];
                bitmap.getPixels(pixel, 0, width + rowPadding / pixelStride, 0, 0, width + rowPadding / pixelStride, 1);
                int leftPadding = 0;
                int rightPadding = width + rowPadding / pixelStride;
//                for (int i = 0; i < pixel.length; i++) {
//                    if (pixel[i] != 0) {
//                        leftPadding = i;
//                        break;
//                    }
//                }
//                for (int i = pixel.length - 1; i >= 0; i--) {
//                    if (pixel[i] != 0) {
//                        rightPadding = i;
//                        break;
//                    }
//                }

                width = Math.min(width, mScreenWidth);
                if (rightPadding - leftPadding > width) {
                    rightPadding = width;
                }
                Bitmap bm = Bitmap.createBitmap(bitmap, leftPadding, 0, Math.abs(rightPadding - leftPadding), height);
                fullBitmap = Bitmap.createBitmap(bm, leftPadding, statusBarHeight, rightPadding - leftPadding, height - statusBarHeight);
            }

            Trace.d(TAG, "bitmap cuted first");
            if (mGraphicPath != null) {
                mRect = new Rect(mGraphicPath.getLeft(), mGraphicPath.getTop(), mGraphicPath.getRight(), mGraphicPath.getBottom());
            }

            if (mRect != null) {

                if (mRect.left < 0)
                    mRect.left = 0;
                if (mRect.right < 0)
                    mRect.right = 0;
                if (mRect.top < 0)
                    mRect.top = 0;
                if (mRect.bottom < 0)
                    mRect.bottom = 0;
                if (mRect.right > mScreenWidth)
                    mRect.right = mScreenWidth - 1;
                if (mRect.bottom > mScreenHeight)
                    mRect.bottom = mScreenHeight - 1;

                int cut_width = Math.abs(mRect.left - mRect.right);
                int cut_height = Math.abs(mRect.top - mRect.bottom);
                if (cut_width > 0 && cut_height > 0) {
                    Trace.i("cutbmp", String.format("bw=%d bh=%d, %d %d cw=%d ch=%d, w=%d h=%d", bitmap.getWidth(), bitmap.getHeight(), mRect.left, mRect.top, cut_width, cut_height, width, height));
                    Bitmap cutBitmap = Bitmap.createBitmap(bitmap, mRect.left, mRect.top, cut_width, cut_height);
                    Trace.d(TAG, "bitmap cuted second");
                    if (mGraphicPath != null) {
                        // 准备画笔
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        paint.setColor(Color.WHITE);
                        Bitmap temp = Bitmap.createBitmap(cut_width, cut_height, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(temp);

                        Path path = new Path();
                        if (mGraphicPath.size() > 1) {
                            path.moveTo((float) ((mGraphicPath.pathX.get(0) - mRect.left)), (float) ((mGraphicPath.pathY.get(0) - mRect.top)));
                            for (int i = 1; i < mGraphicPath.size(); i++) {
                                path.lineTo((float) ((mGraphicPath.pathX.get(i) - mRect.left)), (float) ((mGraphicPath.pathY.get(i) - mRect.top)));
                            }
                        } else {
                            return null;
                        }
                        canvas.drawPath(path, paint);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

                        // 关键代码，关于Xfermode和SRC_IN请自行查阅
                        canvas.drawBitmap(cutBitmap, 0, 0, paint);
                        Trace.d(TAG, "bitmap cuted third");

                        saveCutBitmap(temp);

                    } else {
                        saveCutBitmap(cutBitmap);
                    }
                }
            } else {
                saveCutBitmap(fullBitmap);
            }
            bitmap.recycle();//自由选择是否进行回收
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    private void saveCutBitmap(Bitmap cutBitmap) {
//        File localFile = new File(activity.getFilesDir(), "temp.png");
//        String fileName=localFile.getAbsolutePath();
//        Trace.i("temp_png", fileName);
//        try {
//            if (!localFile.exists()) {
//                localFile.createNewFile();
//                Trace.d(TAG,"image file created");
//            }
//            FileOutputStream fileOutputStream = new FileOutputStream(localFile);
//            if (fileOutputStream != null) {
//                cutBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                fileOutputStream.flush();
//                fileOutputStream.close();
//            }
//        } catch (IOException e) {
//            sendBroadcastCaptureFail();
//            return;
//        }

//        Trace.i("temp_png", nameImage.getAbsolutePath());
//        try {
//            if (!nameImage.exists()) {
//                nameImage.createNewFile();
//                Trace.d(TAG,"image file created");
//            }
//            FileOutputStream fileOutputStream = new FileOutputStream(nameImage);
//            if (fileOutputStream != null) {
//                cutBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                fileOutputStream.flush();
//                fileOutputStream.close();
//            }
//        } catch (IOException e) {
//            sendBroadcastCaptureFail();
//            return;
//        }
//
//        Intent newIntent = new Intent(activity, CaptureResultActivity.class);
//        newIntent.putExtra(ScreenCapture.MESSAGE, "保存成功");
//        newIntent.putExtra(ScreenCapture.FILE_NAME, nameImage.getAbsolutePath());
//        activity.startActivity(newIntent);
//        activity.finish();

        Intent intent = new Intent(activity, CaptureResultActivity.class);
//        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), cutBitmap, "", ""));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String strDate = dateFormat.format(new java.util.Date());
        File file = StorageUtils.createCacheFile(strDate + ".png");
        try {
            cutBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
    //        Uri uri = FileUtils.getImageUri(activity, cutBitmap);
            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
//            Uri uri = FileUtils.getUri(file);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            activity.startActivity(intent);
            activity.finish();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//    private void hideStatusBar() {
////        if(MiuiUtils.getMiuiVersion() != -1) {
////            activity.getWindow().getDecorView().setSystemUiVisibility(
////                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
////                    View.SYSTEM_UI_FLAG_LOW_PROFILE |
////                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
////        } else {
////            activity.getWindow().getDecorView().setSystemUiVisibility(
////                    View.SYSTEM_UI_FLAG_FULLSCREEN |
////                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
////        }
//        ScreenUtils.hideStatusBar(activity);
//    }

    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
//        if (mMediaProjection != null) {
//            mMediaProjection.stop();
//            mMediaProjection = null;
//        }
//        Trace.d(TAG, "mMediaProjection undefined");
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
    }

//    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void stopVirtual() {
//        if (mVirtualDisplay == null) {
//            return;
//        }
//        mVirtualDisplay.release();
//        mVirtualDisplay = null;
//        Trace.d(TAG, "virtual display stopped");
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
    }

    public void onDestroy() {
        stopVirtual();
        tearDownMediaProjection();
        Trace.d(TAG, "application destroy");
    }

}