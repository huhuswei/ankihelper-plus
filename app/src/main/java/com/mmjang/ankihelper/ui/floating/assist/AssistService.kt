package com.mmjang.ankihelper.ui.floating.assist

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEventSource
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import androidx.annotation.RequiresApi
import com.lzf.easyfloat.permission.PermissionUtils
import com.mmjang.ankihelper.MyApplication
import com.mmjang.ankihelper.R
import com.mmjang.ankihelper.data.Settings
import com.mmjang.ankihelper.domain.ScreenListener
import com.mmjang.ankihelper.domain.ScreenListener.ScreenStateListener
import com.mmjang.ankihelper.showCustomToast
import com.mmjang.ankihelper.ui.popup.PopupActivity
import com.mmjang.ankihelper.util.ColorThemeUtils
import com.mmjang.ankihelper.util.Constant
import com.mmjang.ankihelper.util.CrashManager
import com.mmjang.ankihelper.util.SentenceExtractor
import com.mmjang.ankihelper.util.TextFragmentUtils
import com.mmjang.ankihelper.util.Trace
import kotlin.math.abs


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
    private val mMainHandler = Handler(Looper.getMainLooper())
    private val mNodeInfos: MutableList<AccessibilityNodeInfo> = ArrayList()


    override fun onCreate() {
        ColorThemeUtils.setColorTheme(this@AssistService, Constant.StyleBaseTheme.Transparent)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        if(screenOrientationReceiver != null) {
            unregisterReceiver(screenOrientationReceiver);
            screenOrientationReceiver = null;
        }
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

        val className = event.className.toString().lowercase() ?: ""
        val packname = event.packageName.toString().lowercase()?:""
        val PackageNames = settings.get(Settings.ACCESSIBILITY_PACKE_NAMES, Constant.ACCESSIBILITY_PACKE_NAMES).split("\n").toSet()

        Trace.i("onAccessibilityEvent", "package-class" + packname + " " +className)

        if ( (packname == "com.mmjang.ankihelper" ||
            packname == "com.android.systemui")
            && className == "android.widget.framelayout"
        ) {
            return
        }

        // Handle different types of accessibility events
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
//                if (rootInActiveWindow != null) {
//                    val node = rootInActiveWindow
//                    if (node == null) return
//                    try {
//                        val rect = Rect()
//                        node.getBoundsInScreen(rect)
//                        val point = Point(rect.centerX(), rect.centerY())
//                        mMainHandler.postDelayed({
//                            simulateClick(this@AssistService, point)
//                        }, 100)
//                    } catch (_: Exception) { }
//                }

//                if (packname in PackageNames && rootInActiveWindow.getChild(0) != null) {
//                    val node = rootInActiveWindow.getChild(0)
//                    try {
//                        val rect = Rect()
//                        node.getBoundsInScreen(rect)
//                        val point = Point(rect.centerX(), rect.centerY())
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            mMainHandler.postDelayed({
//                                simulateClick(this@AssistService, point)
//                            }, 100)
//                        }
//                    } catch (_: Exception) { }
//                    // Log the package and class name
//                    Trace.i("onAccessibilityEvent", "package: " + packname + "\t" + className)
//                }

                // Check if the package or class name indicates a keyboard or popup activity
                if (
                    packname.contains("keyboard") ||
                    packname.contains("input") ||
                    packname.contains("softinput") ||
                    packname.contains("com.osfans.trime") ||
                    className.contains("popupactivity") ||
                    settings.get(Settings.POPUP_DISPLAY_STATE, false)
                ) {
                    settings.put(Settings.KEYBOARD_STATE, true)
                } else {
                    settings.put(Settings.KEYBOARD_STATE, false)
                }

                if (rootInActiveWindow != null) {
                    val node = rootInActiveWindow
                    if (node == null) return
                    try {
                        val rect = Rect()
                        node.getBoundsInScreen(rect)
                        val point = Point(rect.centerX(), rect.centerY())
                        mMainHandler.postDelayed({
                            simulateClick(this@AssistService, point)
                        }, 200)
                    } catch (_: Exception) { }
                }
            }

            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {

                if (packname == Constant.ANKIHELPER_PACKAGE_NAME
                    || className == "android.widget.edittext"
                    || (packname !in PackageNames)) return

                processTypeViewSelectionChanged(event)
            }
            else -> {
            }
        }
    }

    private fun processTypeViewSelectionChanged(event: AccessibilityEvent) {
        Trace.i("Event@", ""+event.eventType)
        // When the text selection changes in a view
        val source = event.source
        // Check if the source node is not null
        source?.let { node ->
            try {
                // Attempt to extract text from the accessibility node
//                        val text = getSentenceAroundSelectionEnhanced(event,getFullTextFromNode(node))
                val word = getSelectedText(event)
                val textWithPosition = getFullParagraphText(event,node)
                val text = getSentenceAroundSelectionEnhanced( textWithPosition, word)
                Trace.i("worder", word + " @ " + text)

                // If text is successfully extracted and is not empty
                if (!text.isEmpty() && !word.isNullOrEmpty()) {
//                            settings.put(Settings.ACTION_TRANSLATE_CONTEXT_SENTENCE, text)
                    val url = TextFragmentUtils.generateBasicTextFragment(
                        TextFragmentUtils.removeTextFragment(getCurrentBrowserUrl()), text)
//                    notify.showNotification(word, text, url)


                    // 创建点击通知后打开的 Intent
                    val intent = Intent(MyApplication.getContext(), PopupActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.setAction("android.intent.action.SEND")
                    intent.putExtra("android.intent.extra.TEXT", text)
                    intent.putExtra(Constant.INTENT_ANKIHELPER_TARGET_WORD, word)
                    if (!url.isEmpty()) intent.putExtra(Constant.INTENT_ANKIHELPER_TARGET_URL, url)
                    intent.setType("text/plain")

                    val pendingIntent = PendingIntent.getActivity(
                        this,
                        System.currentTimeMillis().toInt(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    showCustomToast(
                        message = if (url.isNotEmpty()) "🔗 $text" else text,
                        title = word,
                        duration = 3000L,
                        position = AssistCustomToastPositionUtils.getCurrentPosition(MyApplication.getContext()),
                        iconResId = R.drawable.icon_light,
                        onDismiss = null,
                        onClick = {
                            pendingIntent.send()
                        }
                    )

                }
            } finally {
                // Ensure the accessibility node is recycled to prevent memory leaks
                node.recycle()
            }
        }
    }
    private fun getSelectedText(event: AccessibilityEvent): String? {
        return try {
            val source = event.source ?: return null
            val selectedText = source.text ?: return null

            if (selectedText.isEmpty()) return null

            var start = event.fromIndex.coerceIn(0, selectedText.length)
            var end = event.toIndex.coerceIn(0, selectedText.length)

            if (start > end) {
                start = end.also { end = start }
            }

            if (end > start) {
                selectedText.subSequence(start, end).toString().trim()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun String.countOccurrence(target: String): Int {
        if (target.isEmpty()) return 0
        return this.split(target).size -1
    }

    data class TextWithPosition(
        val text: String,          // 完整文本
        val start: Int,            // 被选择单词在完整文本中的起始位置
        val end: Int               // 被选择单词在完整文本中的结束位置
    )

    private fun getFullParagraphText(event: AccessibilityEvent, node: AccessibilityNodeInfo?): TextWithPosition? {
        if (node == null) return null

        var current = node.parent
        var clickedText = getSelectedText(event)?:""
        var start: Int = event.fromIndex
        var end: Int = event.toIndex
        var currentFullText = getFullTextFromNode(node)?:""
        Trace.i("as_get_curr_full_text", currentFullText)

        // 向上遍历父节点
        while (current != null) {

            // 获取当前节点的完整文本
            val fullText = getFullTextFromNode(current)?:clickedText
            Trace.i("as_get_full_text", fullText)

            // 如果完整文本包含点击的文本，说明找到了段落容器
            if (fullText.isNotEmpty() && fullText.indexOf(currentFullText) > -1) {
                current.recycle()

                if (fullText == currentFullText || (currentFullText.length > clickedText.length && fullText.length > currentFullText.length && fullText.countOccurrence(currentFullText) > 1)) {
                    start = currentFullText.indexOf(clickedText)
                    end = start + clickedText.length - 1
                    return TextWithPosition(currentFullText, start, end)
                } else {
                    start = fullText.indexOf(currentFullText)
                    end = start + currentFullText.length - 1
                    return TextWithPosition(fullText, start, end)
                }
            }

            val parent = current.parent
            if (current != node) current.recycle()
            current = parent
        }

        Trace.i ("as_textWithPosition", "null")
        return TextWithPosition(currentFullText, start, end)
    }


    private fun getFullTextFromNode(node: AccessibilityNodeInfo): String? {
        val text = StringBuilder()

        // 当前节点文本
        node.text?.toString()?.trim()?.let {
            if (it.isNotEmpty()) {
                text.append(it)
            }
        }

        // 递归获取所有子节点文本
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            child?.let {
                try {
                    if (child.packageName in settings.get(Settings.ACCESSIBILITY_PACKE_NAMES,
                            Constant.ACCESSIBILITY_PACKE_NAMES)) {
                        getFullTextFromNode(it)?.let { childText ->
                            if (childText.isNotEmpty()) {
                                if (text.isNotEmpty() && !childText.startsWith(' ')) {
                                    text.append(" ")
                                }
                                if (text.toString() != childText) {
                                    text.append(childText)
                                }
                                Trace.i("as_get_full_text_" + i, text.toString())
                            }
                        }
                    }

                } finally {
                    it.recycle()
                }
            }
        }

        Trace.i("as_get_full_text_final", text.toString())
        return if (text.isNotEmpty()) text.toString().trim() else null
    }

    fun getSentenceAroundSelectionEnhanced(textWithPosition: TextWithPosition?, word: String?): String {
        if (textWithPosition == null) return ""

        var fromIndex = textWithPosition.start
        var toIndex = textWithPosition.end
        val fullText = textWithPosition.text

        if (fullText.isEmpty()
            || word.isNullOrEmpty()
            || fromIndex < 0
            || toIndex < 0
            || fullText.length <= abs(toIndex - fromIndex)
            ) {
            return ""
        }

        Trace.i("as_text", fullText)
        Trace.i("as_word", word)
        return SentenceExtractor.extractSentence(fullText, fromIndex, toIndex).trim()
    }

    fun AccessibilityService.getCurrentBrowserUrl(): String {
        return try {
            val rootNode = rootInActiveWindow ?: return ""

            // 优先尝试通过地址栏查找
            val urlFromAddressBar = findUrlFromAddressBar(rootNode)
            if (urlFromAddressBar.isNotEmpty()) {
                return urlFromAddressBar
            }

            // 尝试通过页面标题栏查找
            val urlFromTitleBar = findUrlFromTitleBar(rootNode)
            if (urlFromTitleBar.isNotEmpty()) {
                return urlFromTitleBar
            }

            // 最后尝试通用URL查找
            findUrlFromGeneric(rootNode)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun findUrlFromAddressBar(node: AccessibilityNodeInfo): String {
        // 完整的浏览器地址栏资源ID列表
        val addressBarIds = listOf(
            // Kiwi Browser
            "com.kiwibrowser.browser:id/url_bar",
            "com.kiwibrowser.browser:id/search_box_text",
            "com.kiwibrowser.browser:id/location_bar",

            // Chrome 和基于Chromium的浏览器
            "com.android.chrome:id/url_bar",
            "com.android.chrome:id/location_bar",
            "com.chrome.beta:id/url_bar",
            "com.chrome.dev:id/url_bar",
            "com.chrome.canary:id/url_bar",

            // Firefox
            "org.mozilla.firefox:id/url_bar",
            "org.mozilla.firefox:id/mozac_browser_toolbar_url_view",
            "org.mozilla.fenix:id/url_bar",

            // Opera
            "com.opera.browser:id/url_field",
            "com.opera.browser:id/address_bar",
            "com.opera.mini.native:id/url_field",

            // Brave
            "com.brave.browser:id/url_bar",
            "com.brave.browser:id/location_bar",

            // Edge
            "com.microsoft.emmx:id/url_bar",
            "com.microsoft.emmx:id/location_bar",

            // Samsung Internet
            "com.sec.android.app.sbrowser:id/location_bar_edit_text",
            "com.samsung.android.app.sbrowser:id/location_bar_edit_text",

            // UC Browser
            "com.UCMobile.intl:id/address_bar",
            "com.uc.browser:id/address_bar",

            // QQ Browser
            "com.tencent.mtt:id/address_bar",

            // 通用标识
            "url_bar",
            "address_bar",
            "location_bar",
            "search_box",
            "omnibox"
        )

        // 查找地址栏编辑框
        val editTextNodes = mutableListOf<AccessibilityNodeInfo>()
        collectEditTextNodes(node, editTextNodes)

        for (editText in editTextNodes) {
            // 检查节点ID
            val nodeId = editText.viewIdResourceName ?: ""
            if (addressBarIds.any { nodeId.contains(it, ignoreCase = true) }) {
                val text = getNodeUrlText(editText)
                if (isValidUrl(text)) {
                    return normalizeUrl(text)
                }
            }

            // 检查内容描述
            val contentDesc = editText.contentDescription?.toString() ?: ""
            if (contentDesc.contains("url", ignoreCase = true) ||
                contentDesc.contains("address", ignoreCase = true) ||
                contentDesc.contains("search", ignoreCase = true)) {
                val text = getNodeUrlText(editText)
                if (isValidUrl(text)) {
                    return normalizeUrl(text)
                }
            }
        }

        return ""
    }

    private fun findUrlFromTitleBar(node: AccessibilityNodeInfo): String {
        // 有些浏览器在标题栏显示URL
        val titleBarIds = listOf(
            "com.kiwibrowser.browser:id/toolbar",
            "com.android.chrome:id/toolbar",
            "org.mozilla.firefox:id/toolbar",
            "com.opera.browser:id/toolbar",
            "toolbar",
            "action_bar"
        )

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            child?.let {
                val nodeId = it.viewIdResourceName ?: ""
                if (titleBarIds.any { id -> nodeId.contains(id, ignoreCase = true) }) {
                    val text = getNodeUrlText(it)
                    if (isValidUrl(text)) {
                        return normalizeUrl(text)
                    }

                    // 递归查找标题栏内的URL
                    val urlInToolbar = findUrlInToolbar(it)
                    if (urlInToolbar.isNotEmpty()) {
                        return urlInToolbar
                    }
                }
            }
        }

        return ""
    }

    private fun findUrlInToolbar(node: AccessibilityNodeInfo): String {
        // 在工具栏内查找URL文本视图
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            child?.let {
                // 检查文本视图
                if (it.className?.toString()?.contains("TextView") == true) {
                    val text = getNodeUrlText(it)
                    if (isValidUrl(text)) {
                        return normalizeUrl(text)
                    }
                }

                // 递归查找
                val result = findUrlInToolbar(it)
                if (result.isNotEmpty()) {
                    return result
                }
            }
        }
        return ""
    }

    private fun findUrlFromGeneric(node: AccessibilityNodeInfo): String {
        // 通用方法：遍历所有节点查找URL
        val urlCandidates = mutableListOf<String>()

        fun traverseNodes(currentNode: AccessibilityNodeInfo) {
            val text = getNodeUrlText(currentNode)
            if (isValidUrl(text)) {
                urlCandidates.add(normalizeUrl(text))
            }

            for (i in 0 until currentNode.childCount) {
                val child = currentNode.getChild(i)
                child?.let { traverseNodes(it) }
            }
        }

        traverseNodes(node)

        // 返回最长的URL（通常是完整的URL）
        return urlCandidates.maxByOrNull { it.length } ?: ""
    }

    private fun collectEditTextNodes(node: AccessibilityNodeInfo, list: MutableList<AccessibilityNodeInfo>) {
        if (node.className?.toString()?.contains("edittext") == true) {
            list.add(node)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            child?.let { collectEditTextNodes(it, list) }
        }
    }

    private fun getNodeUrlText(node: AccessibilityNodeInfo): String {
        return when {
            node.text != null -> node.text.toString().trim()
            node.contentDescription != null -> node.contentDescription.toString().trim()
            else -> ""
        }
    }

    private fun isValidUrl(text: String): Boolean {
        // 使用Android内置的URL匹配器
        val isWebUrl = Patterns.WEB_URL.matcher(text).matches()

        return isWebUrl
    }

    private fun normalizeUrl(url: String): String {
        var normalized = url.trim()

        // 移除常见的非URL前缀
        val prefixesToRemove = listOf("网址：", "URL：", "地址：", "url:", "address:")
        prefixesToRemove.forEach { prefix ->
            if (normalized.startsWith(prefix, ignoreCase = true)) {
                normalized = normalized.substring(prefix.length).trim()
            }
        }

        // 确保URL有协议前缀
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
//            if (normalized.startsWith("www.")) {
                normalized = "https://$normalized"
//            }
        }

        return normalized
    }


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

    /**
     * 解析屏幕 node 树，找到文本节点
     */
/*    private fun parseRootNode(event: AccessibilityEvent) {
        if(this.rootInActiveWindow != null)
            Thread(Runnable {
                getNodesFromWindows()?.let {
                    AssistUtil.parseNodes(it).apply {
                        reverse()
                        if (it.packageName in settings.get(Settings.ACCESSIBILITY_PACKE_NAMES,
                                Constant.ACCESSIBILITY_PACKE_NAMES)) {
                                mNodeInfos.addAll(this)
                                val rect = Rect()
                                it.getBoundsInScreen(rect)
                                val point = Point(rect.centerX(), rect.centerY())
                            mMainHandler.postDelayed({
                                AssistUtil.simulateClick(this@AssistService, point)
                            }, 50)
                            var newEvent = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED)
                            newEvent.packageName = it.packageName
                            newEvent.eventTime  = System.currentTimeMillis()
                            processTypeViewSelectionChanged(event)
                        }
                    }
                }

            }).start()
    }*/

    private fun getNodesFromWindows(): ArrayList<AccessibilityNodeInfo>? {
        val windows: List<AccessibilityWindowInfo> = this.windows
        val nodes = ArrayList<AccessibilityNodeInfo>()
        if (windows.size > 0) {
            for (window in windows) {
                nodes.add(window.root)
            }
        }
        return try {
            nodes
        } catch (e:NoSuchElementException) {
            null    //?
        }
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
        Trace.i("result", result.toString())
    }
}
