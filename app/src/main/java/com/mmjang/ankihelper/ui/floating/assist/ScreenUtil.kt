package com.mmjang.ankihelper.ui.floating.assist

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * @author yueban fbzhh007@gmail.com
 * @date 2020-01-12
 */
object ScreenUtil {
    /**
     * 获取屏幕宽度(像素)
     */
    fun getScreenWidthPixels(context: Context): Int {
        return getDisplayMetrics(context).widthPixels
    }

    /**
     * 获取屏幕高度(像素)
     */
    fun getScreenHeightPixels(context: Context): Int {
        return getDisplayMetrics(context).heightPixels
    }

    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        val outMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(outMetrics)
        return outMetrics
    }
}