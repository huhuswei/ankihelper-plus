package com.mmjang.ankihelper.ui.floating.assist

import android.accessibilityservice.AccessibilityService
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import com.lzf.easyfloat.EasyFloat
import com.mmjang.ankihelper.MyApplication
import com.mmjang.ankihelper.data.Settings
import com.mmjang.ankihelper.ui.popup.PopupActivity
import com.mmjang.ankihelper.util.ColorThemeUtils
import com.mmjang.ankihelper.util.Constant
import com.mmjang.ankihelper.util.ToastUtil
import com.mmjang.ankihelper.util.Trace
import kotlin.math.pow


/**
 * AccessibilityService事件
 * @author yueban fbzhh007@gmail.com
 * @date 2020-01-12
 */
class AssistDragDelegate private constructor(service: AccessibilityService) :
    SelectionFloatWindowCallback {
    private val mService: AccessibilityService = service
    private val mMainHandler = Handler(Looper.getMainLooper())
    private val settings = Settings.getInstance(MyApplication.getContext())


    /**
     * 存储当前屏幕内所有文本 node
     */
    private val mNodeInfos: MutableList<AccessibilityNodeInfo> = ArrayList()

    /**
     * 准备读取微信文字预览页内容
     */
    private var mPendingPreview: Boolean = false

    /**
     * 悬浮球
     */
    private var mLogoView: View? = null

    /**
     * 文本框高亮绘制
     */
    private var mHierarchyView: HierarchyView? = null

    /**
     * 正在拖拽
     */
    private var mIsDragging = false

    private var mIsLongClick = false

    /**
     * 当前point
     */
     private var mPrePoint: Point = Point(0,0)

    /**
     * 悬停记录器
     */
    private var counter: Long = 0
    private var startingTime: Long = System.currentTimeMillis();

    /**
     * 悬浮球创建完成回调
     *
     * @param logoView 悬浮球 view
     */
    fun onLogoWindowCreated(logoView: View?) {
        mLogoView = logoView
//        ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), mLogoView, false) //1
        mLogoView?.setOnClickListener {
//            performClipboardCheck()
        }
    }

    override fun onSelectionWindowCreated(hierarchyView: HierarchyView) {
        mHierarchyView = hierarchyView
    }

    /**
     * 屏幕 window 内容变化事件
     */
    fun onTypeWindowStateChanged(event: AccessibilityEvent) {
        val className = event.className
        if (!TextUtils.isEmpty(className) && className.toString() == AssistUtil.CLASS_NAME_WECHAT_TEXT_PREVIEW){
            if (mPendingPreview) {
                getNodesFromWindows()?.let {
                    AssistUtil.getWechatPreviewTextNode(
                        mService.rootInActiveWindow,//测试 2024.1.6从 it 改成 mService.rootInActiveWindow
                        object : PreviewTextNodeCallback {
                            override fun onFound(nodeInfo: AccessibilityNodeInfo?) {
                                nodeInfo?.let {
                                    val rect = Rect()
                                    it.getBoundsInScreen(rect)
                                    val point = Point(rect.centerX(), rect.centerY())
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        mMainHandler.postDelayed({
                                            AssistUtil.simulateClick(mService, point)
                                            executeSelectNode(it)
                                        }, 50)
                                    }
                                }
                            }
                        })
                }
            }
            mPendingPreview = false
        }
    }

    fun hide(view: View) {
        if(!settings.floatBallEnable)
            mLogoView?.visibility = View.GONE
    }

    fun show(view: View) {
        if(settings.floatBallEnable)
            mLogoView?.visibility = View.VISIBLE
    }
    fun change(view: View) {
//        if (Settings.getInstance(MyApplication.getContext()).getPinkThemeQ()) {
//            mLogoView?.setBackgroundResource(R.drawable.ic_float_search_pink);
//        } else {
//            mLogoView?.setBackgroundResource(R.drawable.ic_float_search);
//        }
        ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), mLogoView, false)
        mLogoView?.alpha=(Settings.getInstance(MyApplication.getContext()).floatingButtonAlpha).toFloat()/100
        val params: ViewGroup.LayoutParams = mLogoView!!.layoutParams
        val size = Settings.getInstance(MyApplication.getContext()).floatingButtonSize
        params.width = size
        params.height = size
        mLogoView!!.layoutParams = params
    }

    /**
     * touch 触摸动作
     *
     * @param view 触摸 view
     * @param event 触摸动作 事件
     */
    fun onTouch(view: View, event: MotionEvent) {
//        if(event.action == MotionEvent.ACTION_DOWN) {
////            if(true) {
//////                finishPopupWindow()
////            }
//
//        }
//        if(event.action == MotionEvent.ACTION_UP) {
//            if(mIsLongClick)
//                mIsLongClick = false
//            else
//                performClipboardCheck()
//        }

//        when(event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                if (!mIsDragging) {
//                    ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), mLogoView, true)
//                    true
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), mLogoView, false)
//                true
//            }
//        }
    }

    /**
     * 拖拽进行中
     *
     * @param view 拖拽 view
     */
    fun onDrag(view: View) {
        mLogoView?.isLongClickable?:false
        if (!mIsDragging) {
            mNodeInfos.clear()
            parseRootNode()
            AssistFloatWindow.instance.openSelection(this)
            mIsDragging = true
            mIsLongClick = true
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val point = Point(location[0] + view.width / 2, location[1] + view.height / 2)
//        val point = Point(location[0] + view.width / 4 * 3, location[1] + view.height / 3)

        var nodeInfo = captureNode(point)
        var distance = Math.sqrt((point.x - mPrePoint.x).toDouble().pow(2.0) + (point.y - mPrePoint.y).toDouble().pow(2.0)).toInt()

        if(distance < 4)
            counter = System.currentTimeMillis() - startingTime;
        else {
            counter = 0
            startingTime = System.currentTimeMillis();
        }
        mPrePoint = point
        mHierarchyView?.setSelectedNode(nodeInfo, point, counter)

        Trace.i("couter", counter.toString());
    }


    /**
     * 拖拽停止
     */
    fun onDragEnd(view:View) {
        mLogoView?.isLongClickable ?:false
        if (mIsDragging) {
//            ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), mLogoView, true)
//            //获取logo坐标
//            val location = IntArray(2)
//            view.getLocationOnScreen(location)
//            val point = Point(location[0], location[1])
//            Settings.getInstance(MyApplication.getContext()).floatingButtonPosition = point
//            //end
            val hoveringTime = settings.get(Settings.FLOATING_HOVERING_MILLISECOND, Constant.FLOATING_HOVERING_DEFAULT_TIME_MS)
            if(counter>hoveringTime)
                mHierarchyView?.getSelectedNode()?.let { executeSelectNode(it) }
            EasyFloat.dismissAppFloat(AssistFloatWindow.FLOAT_WINDOW_SELECTION)
            mIsDragging = false
            mHierarchyView = null
        }
//        else {
//            ColorThemeUtils.setFloatingLogo(MyApplication.getContext(), mLogoView, false)
//        }
    }

    fun onStopCapture() {
        if (mIsDragging) {
            EasyFloat.dismissAppFloat(AssistFloatWindow.FLOAT_WINDOW_SELECTION)
            mIsDragging = false
            mHierarchyView = null
        }
    }

    /**
     * 捕获坐标点对应的 node
     *
     * @param point 坐标
     * @return 坐标对应的 node
     */
    private fun captureNode(point: Point): AccessibilityNodeInfo? {
        var targetNode: AccessibilityNodeInfo? = null
        for (item in mNodeInfos) {
            val itemRect = Rect()
            item.getBoundsInScreen(itemRect)
            if (!itemRect.contains(point.x, point.y)) {
                continue
            }
            if (targetNode == null) {
                targetNode = item
            } else {
                val targetRect = Rect()
                targetNode.getBoundsInScreen(targetRect)
                if (itemRect.width() < targetRect.width() && itemRect.height() < targetRect.height()) {
                    targetNode = item
                }
            }
        }
        return targetNode
    }

    private fun getNodesFromWindows(): AccessibilityNodeInfo? {
        val windows: List<AccessibilityWindowInfo> = mService.windows
        val nodes = ArrayList<AccessibilityNodeInfo>()
        if (windows.size > 0) {
            for (window in windows) {
                nodes.add(window.root)
            }
        }
        return try {
            nodes.last()
        } catch (e:NoSuchElementException) {
            null    //?
        }
    }

    /**
     * 解析屏幕 node 树，找到文本节点
     */
    private fun parseRootNode() {
//        if(mService.rootInActiveWindow != null)
            Thread(Runnable {
                getNodesFromWindows()?.let {
                    AssistUtil.parseNodes(it).apply {
                        reverse()
                        mNodeInfos.addAll(this)
                    }
                }
            }).start()
    }

    /**
     * 节点选中处理逻辑
     *
     * @param selectedNode 选中的节点
     */
    private fun executeSelectNode(selectedNode: AccessibilityNodeInfo) {
        if (AssistUtil.isWechatMsgNode(selectedNode)) {
            val rect = Rect()
            selectedNode.getBoundsInScreen(rect)
            val point = Point(rect.centerX(), rect.centerY())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                mMainHandler.postDelayed(Runnable {
//                    AssistUtil.simulateClick(mService, point)
//                }, 100)
                mMainHandler.postDelayed(Runnable {
                    AssistUtil.simulateClick(mService, point)
                    mPendingPreview = true
                }, 200)
            }
        }

        putTextToPopupWindow(selectedNode.text.toString().trim())

        Trace.i("Fxservice", "performClipboardCheck")
//        Toast.makeText(MyApplication.getContext(), "text: $text", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(service: AccessibilityService): AssistDragDelegate {
            return AssistDragDelegate(service)
        }
    }

    private fun putTextToPopupWindow(text: String) {
        if(text == "") {
            ToastUtil.show("解析失败，无法获取文本");
            return
        }

//        //是否复制文本
//        if(Settings.getInstance(MyApplication.getContext()).get(Settings.COPY_MARKED_TEXT, false)) {
//            val clipboard = MyApplication.getApplication().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = ClipData.newPlainText(Settings.COPY_MARKED_TEXT, text)
//            clipboard.setPrimaryClip(clip)
//        }

        processing(text)
        Trace.i("AssistDragDelegate", "performClipboardCheck" + " " + text)
    }
    private fun performClipboardCheck() {
        processing(Constant.USE_FX_SERVICE_CB_FLAG)
        Trace.i("AssistDragDelegate", "performClipboardCheck")
    }

    private fun processing(content:String) {
        val vibList = LongArray(1)
        vibList[0] = 10L
        val intent = Intent(MyApplication.getContext(), PopupActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra(Intent.EXTRA_TEXT, Constant.USE_FX_SERVICE_CB_FLAG)
        intent.putExtra(Constant.FLOATING_GET_CONTENT, content);
        intent.putExtra(Constant.INTENT_ANKIHELPER_ACTION, true);

        val pendingIntent =
            PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        var mBundle = Bundle()
//        startActivity(MyApplication.getContext(), intent, mBundle)
        try {
            if(!settings.get(Settings.POPUP_DISPLAY_STATE, false))
                settings.put(Settings.POPUP_DISPLAY_STATE, true)
            pendingIntent.send()
        } catch (e: CanceledException) {
            e.printStackTrace()
        }
    }
}