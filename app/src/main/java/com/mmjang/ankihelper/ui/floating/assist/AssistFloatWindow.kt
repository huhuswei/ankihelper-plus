package com.mmjang.ankihelper.ui.floating.assist

import android.app.PendingIntent
import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnFloatCallbacks
import com.mmjang.ankihelper.MyApplication
import com.mmjang.ankihelper.R
import com.mmjang.ankihelper.data.Settings
import com.mmjang.ankihelper.ui.floating.assist.ScreenUtil.getScreenWidthPixels
import com.mmjang.ankihelper.ui.floating.screenshot.ScreenCaptureActivity
import com.mmjang.ankihelper.ui.popup.PopupActivity
import com.mmjang.ankihelper.util.ColorThemeUtils
import com.mmjang.ankihelper.util.Constant
import com.mmjang.ankihelper.util.Trace

/**
 * 创建和传递logoViwe和事件
 * @author yueban fbzhh007@gmail.com
 * @date 2020-01-12
 */
class AssistFloatWindow private constructor() {
    private var settings = Settings.getInstance(MyApplication.getContext())
    private var callbackChangeState: OnSharedPreferenceChangeListener? = null
    private var pinState: Boolean? = null
    private var autoSideState: Boolean? = null
//    private var sidelineWidth: Int? = null
    private var toActive: Boolean = false
    private var longClickedState: Boolean = false
    private var portraitState: Boolean = true
    private var keyboardState: Boolean = false
    private var popupDisplayState: Boolean = false

    fun open(floatWindowCallback: FloatWindowCallback) {
        // 以屏幕左上角为原点，设置x、y初始值
        launchFloatingButton(floatWindowCallback)
        onListeningPinFloating(floatWindowCallback)
    }

    private fun onListeningPinFloating(floatWindowCallback: FloatWindowCallback) {
        pinState = settings.floatBallEnable
        autoSideState = settings.get(Settings.FlOATING_BUTTON_AUTO_SIDE, false)
//        sidelineWidth = settings.sidelineSize[0]


        callbackChangeState =  SharedPreferences.OnSharedPreferenceChangeListener {
                sharePreferences, key ->
            val nowPinState = settings.get(Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false)
            val nowAutoSideState = settings.get(Settings.FlOATING_BUTTON_AUTO_SIDE, false)
            val nowOrientationState = settings.get(Settings.ORIENTATION_PORTRAIT, true)
            val nowKeyboardState = settings.get(Settings.KEYBOARD_STATE, false)
            val nowPopupDisplayState = settings.get(Settings.POPUP_DISPLAY_STATE, false)

            if (nowPinState != pinState ||
                nowAutoSideState != autoSideState ||
                nowOrientationState != portraitState ||
                (nowKeyboardState != keyboardState && !popupDisplayState) ||
                (nowPopupDisplayState != popupDisplayState && !keyboardState)
            ) {
                pinState = nowPinState
                autoSideState = nowAutoSideState
                portraitState = settings.get(Settings.ORIENTATION_PORTRAIT, true)
                keyboardState = nowKeyboardState
                popupDisplayState = nowPopupDisplayState

                val point = settings.floatingButtonPosition
                if (nowAutoSideState) {
                    val screenWidth = getScreenWidthPixels(MyApplication.getContext())
                    if (point.x >= screenWidth/2) {
                        point.x = screenWidth
                    } else {
                        point.x = 1
                    }
                    settings.floatingButtonPosition = point
                }

                if(settings.floatBallEnable && AssistUtil.isAccessibilitySettingsOn(MyApplication.getContext(), AssistService::class.java)) {
                    EasyFloat.dismissAppFloat()
                    launchFloatingButton(floatWindowCallback)
                }
            }
//            else if (nowSidelineSize != sidelineSize) {
//                ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), EasyFloat.getAppFloatView(), false)
//            }
        }
        settings.sharedPreferences.registerOnSharedPreferenceChangeListener(callbackChangeState)
    }

    private fun launchFloatingButton(floatWindowCallback: FloatWindowCallback) {
        val point: Point =
            if(settings.get(Settings.KEYBOARD_STATE, false) ||
                    settings.get(Settings.POPUP_DISPLAY_STATE, false))
                Point(settings.floatingButtonPosition.x, 0)
            else
                settings.floatingButtonPosition
        val snowPinState = settings.get(Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false)
        val snowAutoSideState = settings.get(Settings.FlOATING_BUTTON_AUTO_SIDE, false)

        EasyFloat.with(MyApplication.getContext()).setLayout(R.layout.layout_assist_float_window)
            .setShowPattern(ShowPattern.ALL_TIME)
//            .setAnimator(DefaultAnimator())
            .setAppFloatAnimator(null)
            .setLocation(point.x, point.y)
            .setSidePattern(
                if (snowPinState ||
                    !snowAutoSideState)
                    SidePattern.DEFAULT
                else SidePattern.RESULT_SIDE)
            .setGravity(Gravity.END, 0, ScreenUtil.getScreenHeightPixels(MyApplication.getContext()) * 1 / 2)
//            .setGravity(Gravity.CENTER)
            .registerCallbacks(object : OnFloatCallbacks {
                override fun createdResult(
                    isCreated: Boolean, msg: String?, view: View?
                ) {
                    var logoView = view?.findViewById<ImageView>(R.id.ic_logo)
                    ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), logoView, toActive)
                    logoView?.let { floatWindowCallback.onLogoWindowCreated(it) }
                    logoView?.setOnClickListener {
                        performAction(settings.get(Settings.FLOATING_SNIP_SEARCH_SWITCH, false))
                    }
                    logoView?.setOnLongClickListener{
                        performAction(!settings.get(Settings.FLOATING_SNIP_SEARCH_SWITCH, false))
                        return@setOnLongClickListener true
                    }
                }

                override fun show(view: View) {
                    floatWindowCallback.show(view)
                }
                override fun hide(view: View) {
                    floatWindowCallback.hide(view)
                }
                override fun dismiss() {}
                override fun touchEvent(view: View, event: MotionEvent) {
                    var logoView = view.findViewById<ImageView>(R.id.ic_logo)
                    when(event.action) {
//                        MotionEvent.ACTION_DOWN -> {
//                            toActive = true
//                        }
//                        MotionEvent.ACTION_MOVE -> {
//                            if (toActive) {
//                                ColorThemeUtils.setFloatingLogo(
//                                    MyApplication.getContext(),
//                                    logoView,
//                                    toActive
//                                )
//                                toActive = false
//                            }
//                        }
                        MotionEvent.ACTION_UP -> {
                            if(longClickedState) {
                                val handler = Handler()
                                handler.postDelayed({
                                    ColorThemeUtils.setFloatingLogo(
                                        MyApplication.getContext(),
                                        logoView,
                                        false
                                    )
                                    longClickedState = false
                                }, 100)
                            }
                        }

                    }
                    floatWindowCallback.touchEvent(view, event) //2.调用AssistService的touchEvent方法
                }
                override fun drag(view: View, event: MotionEvent) {
//                    toActive = true
                    floatWindowCallback.onDrag(view, event)
                }
                override fun dragEnd(view: View) {
//                    toActive = false
                    if(!settings.get(Settings.FLOATING_BUTTON_PIN_POSITION_ENABLE, false)) {
                        if(settings.get(Settings.FlOATING_BUTTON_AUTO_SIDE, false)) {
                            val screenWidth = getScreenWidthPixels(MyApplication.getContext())
                            val location = IntArray(2)
                            view.getLocationOnScreen(location)
                            val rect = Rect()
                            view.getWindowVisibleDisplayFrame(rect)
                            val point = Point(if(location[0]<=screenWidth/2) 1 else screenWidth, location[1] - rect.top)
                            settings.floatingButtonPosition = point
                        } else {
                            val location = IntArray(2)
                            view.getLocationOnScreen(location)
                            val rect = Rect()
                            view.getWindowVisibleDisplayFrame(rect)
                            val point = Point(location[0], location[1] - rect.top)
                            settings.floatingButtonPosition = point
                        }
                    } else {
                        val screenWidth = getScreenWidthPixels(MyApplication.getContext())
                        val location = IntArray(2)
                        view.getLocationOnScreen(location)
                        val rect = Rect()
                        view.getWindowVisibleDisplayFrame(rect)

                        if((location[0]<=8 || (location[0]+settings.floatingButtonSize) >= screenWidth) &&
                            (location[1] - rect.top) != settings.floatingButtonPosition.y) {
                            val point = Point(location[0], location[1] - rect.top)
                            settings.floatingButtonPosition = point
                        } else {
                            EasyFloat.dismissAppFloat()
                            launchFloatingButton(floatWindowCallback)
                        }

                    }
                    floatWindowCallback.onDragEnd(view)
                }

            }).show()
    }

    fun openSelection(callback: SelectionFloatWindowCallback) {
        EasyFloat.with(MyApplication.getContext()).setTag(FLOAT_WINDOW_SELECTION).setLayout(R.layout.view_float_window_hierarchy)
            .setAnimator(null)
            .setShowPattern(ShowPattern.ALL_TIME).setMatchParent(true, true).setAppFloatAnimator(null)
            .registerCallbacks(object : OnFloatCallbacks {
                override fun createdResult(
                    isCreated: Boolean, msg: String?, view: View?
                ) {
                    view?.apply {
                        val hierarchyView: HierarchyView = findViewById(R.id.hierarchy_view)
                        callback.onSelectionWindowCreated(hierarchyView)
                    }
                }

                override fun show(view: View) {}
                override fun hide(view: View) {}
                override fun dismiss() {}
                override fun touchEvent(view: View, event: MotionEvent) {}
                override fun drag(view: View, event: MotionEvent) {}
                override fun dragEnd(view: View) {}
            }).show()
    }

    fun show(tag: String?) {
        EasyFloat.showAppFloat(tag)
    }

    fun show() {
        EasyFloat.showAppFloat()
    }
    
    fun hide() {
        EasyFloat.hideAppFloat()
    }
    fun dismiss() {
        EasyFloat.dismissAppFloat()
    }

    fun dismiss(tag: String?) {
        EasyFloat.dismissAppFloat(tag)
    }

    fun isShowing(): Boolean {
        return EasyFloat.appFloatIsShow(null)
    }

    fun isShowing(tag: String?): Boolean {
        return EasyFloat.appFloatIsShow(tag)
    }

    companion object {

        const val FLOAT_WINDOW_SELECTION = "AssistSelectionWindow"
        val instance: AssistFloatWindow by lazy {
            AssistFloatWindow()
        }

        var screenshotPermission: Intent? = null
    }

    private fun putTextToPopupWindow(text: String) {
        processing(text)
    }

    private fun performClipboardCheck() {
        processing(Constant.FLOATING_USE_CLIPBOARD_CONTENT_FLAG)
    }

    private fun processing(EXTRA_FLAG:String) {
        val vibList = LongArray(1)
        vibList[0] = 10L
        val intent = Intent(MyApplication.getContext(), PopupActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra(Intent.EXTRA_TEXT, EXTRA_FLAG)
//        intent.putExtra(Constant.INTENT_ANKIHELPER_ACTION, true)
        val pendingIntent =
            PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        var mBundle = Bundle()
//        startActivity(MyApplication.getContext(), intent, mBundle)
        try {
            pendingIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }
    }

    private fun performAction(toggle:Boolean) {
        when(toggle) {
            false -> {
                val intent = Intent()
                intent.setClass(MyApplication.getContext(), ScreenCaptureActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                MyApplication.getContext().startActivity(intent)
            }
            true -> {
                performClipboardCheck()
//                        toActive = false
                toActive = true
                longClickedState = true
            }
        }
    }
}
