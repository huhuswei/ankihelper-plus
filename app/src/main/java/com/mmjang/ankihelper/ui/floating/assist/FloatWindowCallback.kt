package com.mmjang.ankihelper.ui.floating.assist


import android.view.MotionEvent
import android.view.View

/**
 * @author yueban fbzhh007@gmail.com
 * @date 2020-01-12
 */
interface FloatWindowCallback {
    fun hide(view: View)
    fun show(view: View)
    fun onDrag(view: View, event: MotionEvent)
//    fun onTouch(view: View, event: MotionEvent)
    fun touchEvent(view: View, event: MotionEvent)
    fun onDragEnd(view: View)
    fun onLogoWindowCreated(logoView: View)
}