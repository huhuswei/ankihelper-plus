package com.mmjang.ankihelper.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.lzf.easyfloat.permission.rom.MiuiUtils;
import com.lzf.easyfloat.permission.rom.RomUtils;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: ScreenUtils
 * @Description: 获取屏幕的多种宽高信息
 * @Author: ss
 * @CreateDate: 2022/6/2 8:59 PM
 * @UpdateUser: ss
 * @UpdateDate: 2022/6/2 8:59 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */

public class ScreenUtils {
    /**
     * 返回包括虚拟键在内的总的屏幕高度
     * 即使虚拟按键显示着，也会加上虚拟按键的高度
     */
    public static int getTotalScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        return displayMetrics.heightPixels;

    }

    /**
     * 返回 void
     * 隐藏状态栏
     */
    public static void hideStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            if (RomUtils.INSTANCE.checkIsMiuiRom() && MiuiUtils.getMiuiVersion() != -1) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else if (RomUtils.INSTANCE.checkIsHuaweiRom()) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        } else {
            WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(activity.getWindow().getDecorView());
            controller.hide(WindowInsetsCompat.Type.navigationBars());
            controller.hide(WindowInsetsCompat.Type.statusBars());

        }
    }

    /**
     * 返回屏幕的宽度
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        return displayMetrics.widthPixels;

    }

    /**
     * 返回屏幕可用高度
     * 当显示了虚拟按键时，会自动减去虚拟按键高度
     */
    public static int getAvailableScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels;

    }

    /**
     * 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;

    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;

    }

    /**
     * 获取虚拟按键的高度
     * 会根据当前是否有显示虚拟按键来返回相应的值
     * 即如果隐藏了虚拟按键，则返回零
     */
    public static int getVirtualBarHeightIfRoom(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int usableHeight = displayMetrics.heightPixels;

        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        int realHeight = displayMetrics.heightPixels;

        return realHeight - usableHeight;

    }

    /**
     * 获取虚拟按键的高度，不论虚拟按键是否显示都会返回其固定高度
     */
    public static int getVirtualBarHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");

        return activity.getResources().getDimensionPixelSize(resourceId);

    }

    /**
     * 标题栏高度，如果隐藏了标题栏则返回零
     */
    public static int getTitleHeight(Activity activity) {
        return activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

    }

    /**
     * 将dp值转换为px值
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);

    }

    /**
     * 将px值转换为dp值
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);

    }

}