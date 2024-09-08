package com.mmjang.ankihelper.ui.floating.assist

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.text.TextUtils.SimpleStringSplitter
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.mmjang.ankihelper.MyApplication
import com.mmjang.ankihelper.R
import com.mmjang.ankihelper.util.Trace
import com.mmjang.ankihelper.util.ViewUtil
import java.util.*

/**
 * @author yueban fbzhh007@gmail.com
 * @date 2020-01-12
 */
object AssistUtil {
    private const val PACKAGE_NAME_WECHAT = "com.tencent.mm"
    const val CLASS_NAME_WECHAT_TEXT_PREVIEW = "com.tencent.mm.ui.chatting.TextPreviewUI"

    fun parseNodes(nodeInfo: AccessibilityNodeInfo): MutableList<AccessibilityNodeInfo> {
        val result: MutableList<AccessibilityNodeInfo> = ArrayList()
        iterateNodes(nodeInfo, result)
        return result
    }

    private fun iterateNodes(
            nodeInfo: AccessibilityNodeInfo, result: MutableList<AccessibilityNodeInfo>
    ) {
        for (i in 0 until nodeInfo.childCount) {
            val childNode = nodeInfo.getChild(i)
            childNode?.let {
                if (!TextUtils.isEmpty(it.text) || isWechatMsgNode(it)) {
                    result.add(childNode)
                }
                iterateNodes(it, result)
            }
        }
    }

    fun isWechatMsgNode(accessibilityNodeInfo: AccessibilityNodeInfo): Boolean {
        val charSequence = if (accessibilityNodeInfo.contentDescription != null) accessibilityNodeInfo.contentDescription.toString() else null
        val packageName = accessibilityNodeInfo.packageName
        val className = accessibilityNodeInfo.className

        if (TextUtils.isEmpty(packageName) && TextUtils.isEmpty(className))
            Trace.i("wechat", String.format("%s %s %s", packageName.toString(), className.toString(), charSequence))

        return !TextUtils.isEmpty(packageName) && packageName.toString() == PACKAGE_NAME_WECHAT &&
                !TextUtils.isEmpty(className) && className.toString() == View::class.java.canonicalName &&
                TextUtils.isEmpty(charSequence) && accessibilityNodeInfo.isClickable &&
                accessibilityNodeInfo.isLongClickable && accessibilityNodeInfo.isEnabled &&
                !TextUtils.isEmpty(accessibilityNodeInfo.viewIdResourceName)
    }

    fun isAccessibilitySettingsOn(
            mContext: Context, clazz: Class<out AccessibilityService?>
    ): Boolean {
        var accessibilityEnabled = 0
        val service = mContext.packageName + "/" + clazz.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                    mContext.applicationContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 模拟屏幕点击
     *
     * @param service 辅助功能服务
     * @param point   点击位置
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun simulateClick(service: AccessibilityService, point: Point) {
        val clickPath = Path()
        clickPath.moveTo(point.x.toFloat(), point.y.toFloat())
        val strokeDescription = StrokeDescription(clickPath, 0, 10L)
        val gesture = GestureDescription.Builder().addStroke(strokeDescription).build()
        val result = service.dispatchGesture(gesture, null, null)
    }


    /**
     * 获取微信文本预览页文字内容
     *
     * @param node     文本预览页根节点
     * @param callback 结果回调
     */
    fun getWechatPreviewTextNode(node: AccessibilityNodeInfo, callback: PreviewTextNodeCallback) {
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            if (childNode.className == TextView::class.java.canonicalName) {
                callback.onFound(childNode)
                return
            }
            getWechatPreviewTextNode(childNode, callback)
        }
    }

//    fun logoViewSlideIn(logoView:ImageView, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
//        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
//            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
//            if(point.y > 0 && (point.y + ViewUtil.dp2px(60f)) < ScreenUtil.getScreenWidthPixels(
//                    MyApplication.getContext())) {
//                if (point.x == 0) {
//                    logoView.x = ViewUtil.dp2px(-40f).toFloat()
//                }
//                else {
//                    logoView.x = ViewUtil.dp2px(40f).toFloat()
//                }
////                logoView.maxWidth = ViewUtil.dp2px(20f)
//            } else if(point.x > 0) {
//                if (point.y == 0) {
//                    logoView.y = ViewUtil.dp2px(-40f).toFloat()
//                }
////                logoView.maxHeight = ViewUtil.dp2px(20f)
//            }
//        }
//    }
//
//    fun logoViewSlideOut(logoView: ImageView, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
//        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
//            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
//            if(point.y > 0 && (point.y + ViewUtil.dp2px(60f)) < ScreenUtil.getScreenWidthPixels(MyApplication.getContext())) {
//                logoView.x = 0f
////                logoView.maxWidth = ViewUtil.dp2px(60f)
//            } else if(point.x > 0) {
//                logoView.y = 0f
////                logoView.maxHeight = ViewUtil.dp2px(60f)
//            }
//        }
//    }

//    fun logoViewSlideIn(logoView:View, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
//        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
//            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
//            val params = logoView.layoutParams as RelativeLayout.LayoutParams
//            if(point.y > 0 && (point.y + ViewUtil.dp2px(60f)) < ScreenUtil.getScreenWidthPixels(
//                    MyApplication.getContext())) {
//                if (point.x == 0) {
//                    params.setMargins(ViewUtil.dp2px(-40f), params.topMargin, ViewUtil.dp2px(20f), params.bottomMargin)
//                } else {
//                    params.setMargins(ViewUtil.dp2px(-20f), params.topMargin, ViewUtil.dp2px(0f), params.bottomMargin)
//                }
//            } else if(point.x > 0) {
//                if (point.y == 0) {
//                    params.setMargins(params.leftMargin, ViewUtil.dp2px(-40f), params.rightMargin, ViewUtil.dp2px(20f))
//
//                }
//            }
//            logoView.layoutParams = params
//        }
//    }
//
//    fun logoViewSlideOut(logoView: View, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
//        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
//            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
//            val params = logoView.layoutParams as RelativeLayout.LayoutParams
//            if(point.y > 0 && (point.y + ViewUtil.dp2px(60f)) < ScreenUtil.getScreenWidthPixels(
//                    MyApplication.getContext())) {
//                if (point.x == 0) {
//                    params.setMargins(params.leftMargin + ViewUtil.dp2px(40f), params.topMargin, params.rightMargin + ViewUtil.dp2px(40f), params.bottomMargin)
//                }
//                else {
//                    params.setMargins(params.leftMargin + ViewUtil.dp2px(-40f), params.topMargin, params.rightMargin + ViewUtil.dp2px(-40f), params.bottomMargin)
//                }
//            } else if(point.x > 0) {
//                if (point.y == 0) {
//                    params.setMargins(params.leftMargin, params.topMargin + ViewUtil.dp2px(40f), params.rightMargin, params.bottomMargin + ViewUtil.dp2px(40f))
//
//                }
//            }
//            logoView.layoutParams = params
//        }
//    }



//    fun logoViewSlideIn(logoView:View, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
//        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
//            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
//            val params = logoView.layoutParams as RelativeLayout.LayoutParams
//            if(point.y > 0 && (point.y + ViewUtil.dp2px(60f)) < ScreenUtil.getScreenWidthPixels(
//                    MyApplication.getContext())) {
//                if (point.x == 0) {
////                    logoView.x = ViewUtil.dp2px(-40f).toFloat()
//                    params.leftMargin = ViewUtil.dp2px(-30f)
//                }
//                else {
////                    logoView.x = ViewUtil.dp2px(40f).toFloat()
//                    params.leftMargin = ViewUtil.dp2px(30f)
//                    params.rightMargin = ViewUtil.dp2px(-30f)
//
//                }
////                logoView.maxWidth = ViewUtil.dp2px(20f)
//            } else if(point.x > 0) {
//                if (point.y == 0) {
//                    params.topMargin = ViewUtil.dp2px(-30f)
//                }
////                logoView.maxHeight = ViewUtil.dp2px(20f)
//            }
//            logoView.layoutParams = params
//        }
//    }
//
//    fun logoViewSlideOut(logoView: View, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
//        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
//            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
//            val params = logoView.layoutParams as RelativeLayout.LayoutParams
//            if(point.y > 0 && (point.y + ViewUtil.dp2px(60f)) < ScreenUtil.getScreenWidthPixels(MyApplication.getContext())) {
//                if (point.x == 0) {
////                    logoView.x = ViewUtil.dp2px(-40f).toFloat()
//                    params.leftMargin = ViewUtil.dp2px(0f)
//                }
//                else {
////                    logoView.x = ViewUtil.dp2px(40f).toFloat()
//                    params.leftMargin = ViewUtil.dp2px(0f)
//
//                }
//            } else if(point.x > 0) {
//                params.topMargin = ViewUtil.dp2px(0f)
////                logoView.maxHeight = ViewUtil.dp2px(60f)
//            }
//            logoView.layoutParams = params
//        }
//    }

    fun isPinnedToTheSide(settings: com.mmjang.ankihelper.data.Settings): Boolean {
        return settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
        settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)
    }

    fun logoViewSlideIn(logoView:ImageView, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
            var params = logoView.layoutParams
            params.height = ViewUtil.dp2px(60f)
            params.width = ViewUtil.dp2px(30f)
            logoView.layoutParams = params
            logoView.setBackgroundResource(if (point.x > 0) R.drawable.corners_left else R.drawable.corners_right)
        }
    }

    fun logoViewSlideOut(logoView: ImageView, point: Point, settings: com.mmjang.ankihelper.data.Settings) {
        if (settings.get(com.mmjang.ankihelper.data.Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false) &&
            settings.get(com.mmjang.ankihelper.data.Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
            if(point.y > 0 && (point.y + ViewUtil.dp2px(60f)) < ScreenUtil.getScreenWidthPixels(MyApplication.getContext())) {
                logoView.x = 0f
//                logoView.maxWidth = ViewUtil.dp2px(60f)
            } else if(point.x > 0) {
                logoView.y = 0f
//                logoView.maxHeight = ViewUtil.dp2px(60f)
            }
        }
    }
}
