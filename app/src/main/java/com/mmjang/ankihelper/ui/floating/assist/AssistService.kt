package com.mmjang.ankihelper.ui.floating.assist

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.lzf.easyfloat.permission.PermissionUtils
import com.mmjang.ankihelper.MyApplication
import com.mmjang.ankihelper.data.Settings
import com.mmjang.ankihelper.domain.ScreenListener
import com.mmjang.ankihelper.domain.ScreenListener.ScreenStateListener
import com.mmjang.ankihelper.ui.popup.PopupActivity
import com.mmjang.ankihelper.ui.popup.PopupSettingActivity
import com.mmjang.ankihelper.util.ActivityUtil
import com.mmjang.ankihelper.util.CrashManager
import com.mmjang.ankihelper.util.Trace



/**
 * @author ueban fbzhh007@gmail.comy
 * @date 2020-01-12
 */
class AssistService : AccessibilityService(), FloatWindowCallback {
    private var connectedState = false
    private var mAssistDragDelegate: AssistDragDelegate? = null
    private var mScreenListener: ScreenListener = ScreenListener(this)
    private var screenOrientationReceiver: BroadcastReceiver? = null
    private var settings: Settings = Settings.getInstance(MyApplication.getContext())

    override fun onCreate() {
        super.onCreate()
        //初始化 错误日志系统
        CrashManager.getInstance(this)
        mScreenListener.begin(object : ScreenStateListener {
            override fun onScreenOn() {
                Trace.i("AssistService", "屏幕已打开")
            }

            override fun onScreenOff() {
                Trace.i("AssistService", "屏幕已关闭")
                mAssistDragDelegate?.onStopCapture()
            }

            override fun onUserPresent() {
                Trace.i("AssistService", "屏幕已解锁")
            }
        })
        screenOrientationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
                    settings.put(Settings.ORIENTATION_PORTRAIT,
                        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                    )
                }
            }
        }

        var filter = IntentFilter()
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)
        registerReceiver(screenOrientationReceiver, filter)
//        settings.put(Settings.KEYBOARD_STATE, false)
//        settings.put(Settings.POPUP_DISPLAY_STATE, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenOrientationReceiver)
        settings.put(Settings.KEYBOARD_STATE, false)
        settings.put(Settings.POPUP_DISPLAY_STATE, false)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        connectedState = true
        mAssistDragDelegate = AssistDragDelegate.newInstance(this)

        Trace.i("Assis", "connected")
        if (PermissionUtils.checkPermission(MyApplication.getContext())) {
            if (!AssistFloatWindow.instance.isShowing()) {
                AssistFloatWindow.instance.open(this)   //1.把AssistService实例传递给AssisFloatWindow
                AssistFloatWindow.instance.show()
                if(!settings.floatBallEnable)
                    AssistFloatWindow.instance.hide()
            }
        }


    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Log the type of accessibility event
        Trace.i("eventType", event.eventType.toString())

        // Check if the floating ball feature is enabled and if the assistive float window is showing
        if (!settings.floatBallEnable ||
            !AssistFloatWindow.instance.isShowing()) {
            // Stop capturing if conditions are not met
            mAssistDragDelegate?.onStopCapture()
            return
        }

        // Return if the package name of the event is empty
        if (TextUtils.isEmpty(event.packageName)) {
            return
        }

        // Handle different types of accessibility events
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                // Get the class name and package name of the event in lowercase
                var className = event.className.toString().lowercase() ?: ""
                var packageName = event.packageName.toString().lowercase() ?: ""
                // Log the package and class name
                Trace.i("onAccessibilityEvent", "package: " + packageName + "\t" + className)

                // Check if the package or class name indicates a keyboard or popup activity
                if (
                    packageName.contains("keyboard") ||
                    packageName.contains("input") ||
                    packageName.contains("softinput") ||
                    className.contains("popupactivity") ||
                    (packageName.contains("ankihelper") && settings.get(Settings.KEYBOARD_STATE, false))
                ) {
                    // Log if the package is identified as a keyboard
                    Trace.i("onAccessibilityEvent", "package-keyboard: " + packageName)
                    // Set keyboard state to true in settings
                    settings.put(Settings.KEYBOARD_STATE, true)
                } else {
                    // Set keyboard state to false in settings
                    settings.put(Settings.KEYBOARD_STATE, false)
                }

                // Notify the delegate about the window state change
                mAssistDragDelegate?.onTypeWindowStateChanged(event)
            }
        }
    }


//    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        Trace.i("eventType", event.eventType.toString())
//
//
//        if (!settings.floatBallEnable ||
//            !AssistFloatWindow.instance.isShowing()) {
//            mAssistDragDelegate?.onStopCapture()
//            return
//        }
//
//        if (
//            TextUtils.isEmpty(event.packageName)
//        ) {
//            return
//        }
//
//        when (event.eventType) {
//            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
//                // 躲避输入法和Popup
//                var className = event.className.toString().lowercase() ?: ""
//                var packageName = event.packageName.toString().lowercase() ?: ""
//                Trace.i("onAccessibilityEvent", "package: " + packageName +"\t" + className)
//                if (
//                    packageName.contains("keyboard") ||
//                    packageName.contains("input") ||
//                    packageName.contains("softinput") ||
//                    className.contains("popupactivity") ||
//                    (packageName.contains("ankihelper") && settings.get(Settings.KEYBOARD_STATE, false))
//                    ) {
//                    Trace.i("onAccessibilityEvent", "package-keyboard: " +packageName)
//                    settings.put(Settings.KEYBOARD_STATE, true)
//                } else {
//                    settings.put(Settings.KEYBOARD_STATE, false)
//                }
//
////                if(className.contains("popupactivity")) {
////                    Trace.i("onAccessibilityEvent", "class-popup: " + className)
////
////                    settings.put(Settings.KEYBOARD_STATE, true)
////                }else {
////                    settings.put(Settings.KEYBOARD_STATE, false)
////                }
//
//                mAssistDragDelegate?.onTypeWindowStateChanged(event)
//            }
//        }
//    }

    override fun onUnbind(intent: Intent?): Boolean {
        //test accessibility service. 2023.1.23
//        mAssistDragDelegate = null
        if (PermissionUtils.checkPermission(MyApplication.getContext())) {
            AssistFloatWindow.instance.dismiss()
            AssistFloatWindow.instance.dismiss(AssistFloatWindow.FLOAT_WINDOW_SELECTION)
        }
        Trace.i("AssistService", "onUnbind!")
        connectedState = false
        return super.onUnbind(intent)
    }

    override fun hide(view: View) {
        mAssistDragDelegate?.hide(view)
    }
    override fun show(view: View) {
        Trace.i("Assis", "show")
        mAssistDragDelegate?.show(view)
        mAssistDragDelegate?.change(view)
    }

    override fun touchEvent(view: View, event: MotionEvent) {
//        mAssistDragDelegate?.onTouch(view, event)   //3. AssistFloatWindow通过调用AssistService的touchEvent方法，回调mAssistDragDelegate的onTouch方法
    }
    override fun onDrag(view: View, event: MotionEvent) {
        mAssistDragDelegate?.onDrag(view)
    }

//    override fun onTouch(view: View, event: MotionEvent) {
//        mAssistDragDelegate?.onTouch(view, event)
//    }

    override fun onDragEnd(view: View) {
        mAssistDragDelegate?.onDragEnd(view)

//        val location = IntArray(2)
//        view.getLocationOnScreen(location)
//        val point = Point(location[0] + view.width / 2, location[1] + view.height / 2)
//        settings.floatingButtonPosition = point
    }

    override fun onLogoWindowCreated(logoView: View) {
        mAssistDragDelegate?.onLogoWindowCreated(logoView)

    }

//    //暂时
//    fun stopService() {
//        mAssistDragDelegate?.onStopCapture()
//        val intent = Intent(MyApplication.getContext(), AssistService::class.java)
//        MyApplication.getContext().stopService(intent)
//    }
//
//    fun startService() {
//        val intent = Intent(MyApplication.getContext(), AssistService::class.java)
//        MyApplication.getContext().startService(intent)
//    }
}
