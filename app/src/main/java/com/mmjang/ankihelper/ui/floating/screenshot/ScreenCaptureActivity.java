package com.mmjang.ankihelper.ui.floating.screenshot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.domain.FinishActivityManager;
import com.mmjang.ankihelper.ui.floating.assist.AssistFloatWindow;
import com.mmjang.ankihelper.util.ColorThemeUtils;
import com.mmjang.ankihelper.util.Constant;
import com.mmjang.ankihelper.util.CrashManager;
import com.mmjang.ankihelper.util.ScreenUtils;
import com.mmjang.ankihelper.util.ToastUtil;
import com.mmjang.ankihelper.util.Trace;

// public class ScreenCaptureActivity extends BaseActivity {
public class ScreenCaptureActivity extends AppCompatActivity {
    private String TAG = "ScreenCaptureActivity";
    private int result = 0;
    private Intent intent = null;
//    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private MarkSizeView markSizeView;
    private Rect markedArea;
    private MarkSizeView.GraphicPath mGraphicPath;
//    private Magnifier magnifier;
    private TextView captureTips;
    private Button captureAll;
    private Button markType;
    private boolean isMarkRect=true;
    private ScreenCapture screenCaptureService;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ColorThemeUtils.setColorTheme(ScreenCaptureActivity.this, Constant.StyleBaseTheme.ScreenCaptureTheme);
        super.onCreate(null);
        FinishActivityManager.getManager().addActivity(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ToastUtil.show("R.string.can_not_capture_under_5_0");
            finish();
            return;
        }
        //初始化 错误日志系统
        CrashManager.getInstance(this);

        AssistFloatWindow.Companion.getInstance().hide();

        initWindow();
        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        setContentView(R.layout.activity_screen_capture);

        markSizeView = (MarkSizeView) findViewById(R.id.mark_size);
        captureTips = (TextView) findViewById(R.id.capture_tips);
        captureAll = (Button) findViewById(R.id.capture_all);
        markType = (Button) findViewById(R.id.mark_type);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            magnifier = new Magnifier(markSizeView);
//            magnifier.show(markSizeView.getWidth() / 2, markSizeView.getHeight() / 2);
//
//            markSizeView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getActionMasked()) {
//                        case MotionEvent.ACTION_DOWN:
//                        case MotionEvent.ACTION_MOVE: {
//                            final int[] viewPosition = new int[2];
//                            v.getLocationOnScreen(viewPosition);
//                            magnifier.show(event.getRawX() - viewPosition[0],
//                                    event.getRawY() - viewPosition[1]);
//                            break;
//                        }
//                        case MotionEvent.ACTION_CANCEL:
//                        case MotionEvent.ACTION_UP: {
//                            magnifier.dismiss();
//                        }
//                    }
//                    return true;
//                }
//            });
//        }

        markType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMarkRect=!isMarkRect;
                markSizeView.setIsMarkRect(isMarkRect);
                markType.setText(isMarkRect?R.string.capture_type_rect:R.string.capture_type_free);
            }
        });

        markSizeView.setmOnClickListener(new MarkSizeView.onClickListener() {
            @Override
            public void onConfirm(Rect markedArea) {
//                boolean isFirst = SPHelper.getBoolean("is_fist", true);
//                if(isFirst){
//                    ToastUtil.show(R.string.need_capture_perssion);
//                }
                ScreenCaptureActivity.this.markedArea = new Rect(markedArea);
                markSizeView.reset();
                markSizeView.setUnmarkedColor(getResources().getColor(R.color.transparent));
                markSizeView.setEnabled(false);
                startIntent();
            }

            @Override
            public void onConfirm(MarkSizeView.GraphicPath path) {
                mGraphicPath=path;
                markSizeView.reset();
                markSizeView.setUnmarkedColor(getResources().getColor(R.color.transparent));
                markSizeView.setEnabled(false);
                startIntent();
            }

            @Override
            public void onCancel() {
                captureTips.setVisibility(View.VISIBLE);
                captureAll.setVisibility(View.VISIBLE);
                markType.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTouch() {
                captureTips.setVisibility(View.GONE);
                captureAll.setVisibility(View.GONE);
                markType.setVisibility(View.GONE);
            }
        });

        captureAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isFirst = SPHelper.getBoolean("is_fist", true);
//                if (isFirst) {
//                    ToastUtil.show(R.string.need_capture_perssion);
//                }
                markSizeView.setUnmarkedColor(getResources().getColor(R.color.transparent));
                captureTips.setVisibility(View.GONE);
                captureAll.setVisibility(View.GONE);
                markType.setVisibility(View.GONE);
                startIntent();
            }
        });

        getScreenshotPermission();
    }

        private void hideStatusBar() {
//        if(MiuiUtils.getMiuiVersion() != -1) {
//            activity.getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_LOW_PROFILE |
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        } else {
//            activity.getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_FULLSCREEN |
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        }
//        ScreenUtils.hideStatusBar(this);
    }

//    private void initWindow() {
////        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent));
//
////        if(MiuiUtils.getMiuiVersion() != -1) {
////            getWindow().getDecorView().setSystemUiVisibility(
////                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
////                    View.SYSTEM_UI_FLAG_LOW_PROFILE |
////                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
////        } else {
////            getWindow().getDecorView().setSystemUiVisibility(
////                    View.SYSTEM_UI_FLAG_FULLSCREEN |
////                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
////        }
//
////        getWindow().getDecorView().setSystemUiVisibility(
//////                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
////                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
////                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//
//        ScreenUtils.hideStatusBar(this);
//    }


    private void initWindow() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        localLayoutParams.width = ((int) (localDisplayMetrics.widthPixels * 1.0D));
        localLayoutParams.gravity = Gravity.CENTER;
        localLayoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(localLayoutParams);
        getWindow().setGravity(17);
        getWindow().getAttributes().windowAnimations = R.anim.anim_scale_in;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
//                try {
                    if(AssistFloatWindow.Companion.getScreenshotPermission() == null)
                        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), Constant.REQUEST_MEDIA_PROJECTION);
                    else {
                        intent = (Intent) AssistFloatWindow.Companion.getScreenshotPermission().clone();
                        startScreenCapture(intent, Activity.RESULT_OK);
                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    requestScreenshotPermission();
//                }
            }
        });

    }

    @Override
    protected void onPause() {
        if (Settings.getInstance(MyApplication.getContext()).getFloatBallEnable())
            AssistFloatWindow.Companion.getInstance().show();
        super.onPause();
    }

    @Override
    protected void onResume() {
        AssistFloatWindow.Companion.getInstance().hide();
        hideStatusBar();
        super.onResume();
    }

    @Override
    protected void onStop() {
        markSizeView.reset();
        captureTips.setVisibility(View.VISIBLE);
        captureAll.setVisibility(View.VISIBLE);
        markType.setVisibility(View.VISIBLE);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (screenCaptureService!=null)
            screenCaptureService.onDestroy();
        if (Settings.getInstance(MyApplication.getContext()).getFloatBallEnable())
            AssistFloatWindow.Companion.getInstance().show();
        super.onDestroy();
    }

    private void startScreenCapture(Intent intent, int resultCode) {
        screenCaptureService = new ScreenCapture(this ,intent, resultCode,markedArea,mGraphicPath);
//        try {
            screenCaptureService.toCapture();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Trace.d(TAG, "进入了");
        if (requestCode == Constant.REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                finish();
            } else if (data != null && resultCode != 0) {
                AssistFloatWindow.Companion.setScreenshotPermission((Intent) data.clone());
                Trace.i(TAG, "user agree the application to capture screen");
//                result = resultCode;
//                intent = (Intent) data.clone();
//                startScreenCapture(intent, resultCode);
                Trace.i(TAG, "start service ScreenCaptureService");
            }
        }
    }

    protected static void setScreenshotPermission(final Intent permissionIntent) {
        AssistFloatWindow.Companion.setScreenshotPermission(permissionIntent);
    }

    protected void getScreenshotPermission() {
//        try {
            if(AssistFloatWindow.Companion.getScreenshotPermission() == null) {
//                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), Constant.REQUEST_MEDIA_PROJECTION);
                requestScreenshotPermission();
            }
//        } catch (final RuntimeException ignored) {
//            openScreenshotPermissionRequester();
//        }
    }

    protected void requestScreenshotPermission() {
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), Constant.REQUEST_MEDIA_PROJECTION);
    }

//    protected void openScreenshotPermissionRequester(){
//        final Intent intent = new Intent(this, ScreenCaptureActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

}