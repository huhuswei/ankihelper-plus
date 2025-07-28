package com.mmjang.ankihelper.util

/**
 * 句子提取工具类
 */
object SentenceExtractor {

    // 句子结束标点符号
    private val SENTENCE_ENDINGS = setOf('.', '!', '?', '。', '！', '？', '…')

    // 中文标点（后面通常不跟空格）
    private val CHINESE_PUNCTUATION = setOf('。', '！', '？', '，', '；', '：', '、')

    // 英文标点（后面通常跟空格）
    private val ENGLISH_PUNCTUATION = setOf('.', '!', '?', ',', ';', ':')


    fun extractSentenceSmart(text: String, selectStart: Int, selectEnd: Int): String {
        // 第一步：在选择位置附近找最近的句子边界
        val nearBoundaries = findNearestSentenceBoundaries(text, selectStart)

        // 第二步：提取这个范围内的文本
        val candidate = text.substring(nearBoundaries.first, nearBoundaries.second).trim()

        // 第三步：验证这个句子确实包含选择区域的内容
        val selectedContent = text.substring(selectStart, selectEnd)
        if (candidate.contains(selectedContent)) {
            return candidate
        }

        // 如果验证失败，扩大范围
        return extractSentence(text, selectStart, selectEnd)
    }

    private fun findNearestSentenceBoundaries(text: String, position: Int): Pair<Int, Int> {
        // 向前找最近的句子开始
        var start = position
        for (i in position downTo 0) {
            if (i == 0) {
                start = 0
                break
            }
            // 找到句子开始：前面有句号+空格，或者新行
            if (i >= 2 && ".!?".contains(text[i - 1]) && text[i] == ' ') {
                start = i
                break
            }
            if (text[i - 1] == '\n') {
                start = i
                break
            }
            // 限制搜索范围，避免找到太远的相同文本
            if (position - i > 100) {
                start = maxOf(0, position - 50)
                break
            }
        }

        // 向后找最近的句子结束
        var end = position
        for (i in position until text.length) {
            if (i == text.length - 1) {
                end = text.length
                break
            }
            // 找到句子结束：当前是句号，后面是空格或新行
            if (".!?".contains(text[i]) && (text[i + 1] == ' ' || text[i + 1] == '\n')) {
                end = i + 1
                break
            }
            if (text[i] == '\n') {
                end = i
                break
            }
            // 限制搜索范围
            if (i - position > 100) {
                end = minOf(text.length, position + 50)
                break
            }
        }

        return Pair(start, end)
    }
    /**
     * 提取包含指定位置的完整句子
     */
    fun extractSentence(text: String, fromIndex: Int, toIndex: Int): String {
        if (text.isEmpty() || fromIndex < 0 || toIndex > text.length || fromIndex > toIndex) {
            return ""
        }

        val start = findSentenceStart(text, fromIndex)
        val end = findSentenceEnd(text, toIndex)

        return if (start < end) text.substring(start, end).trim() else ""
    }

    /**
     * 向左寻找句子开头
     */
    private fun findSentenceStart(text: String, fromIndex: Int): Int {
        if (fromIndex <= 0) return 0

        // 向左搜索句子边界
        for (i in fromIndex - 1 downTo 0) {
            if (text[i] in SENTENCE_ENDINGS) {
                if (isRealSentenceEnd(text, i)) {
                    // 找到边界后的第一个字符
                    return i + 1
                }
            }

            // 段落边界（双换行）
            if (i > 0 && text[i] == '\n' && text[i - 1] == '\n') {
                return i + 1
            }

            // 文本开头
            if (i == 0) return 0
        }

        return 0
    }

    /**
     * 向右寻找句子结尾
     */
    private fun findSentenceEnd(text: String, toIndex: Int): Int {
        if (toIndex >= text.length) return text.length

        // 向右搜索句子边界
        for (i in toIndex until text.length) {
            if (text[i] in SENTENCE_ENDINGS) {
                if (isRealSentenceEnd(text, i)) {
                    return i + 1
                }
            }

            // 段落边界（双换行）
            if (i < text.length - 1 && text[i] == '\n' && text[i + 1] == '\n') {
                return i
            }

            // 文本结尾
            if (i == text.length - 1) return text.length
        }

        return text.length
    }

    /**
     * 判断是否是真正的句子结束
     * 修正逻辑：区分中英文标点的不同规则
     */
    private fun isRealSentenceEnd(text: String, pos: Int): Boolean {
        val punctuation = text[pos]

        // 如果是文本最后一个字符，肯定是句子结束
        if (pos == text.length - 1) return true

        val nextChar = text[pos + 1]

        // 中文标点：后面可以是任何字符（中文不需要空格）
        if (punctuation in CHINESE_PUNCTUATION) {
            return true
        }

        // 英文标点：后面必须有空格或文本结束
        if (punctuation in ENGLISH_PUNCTUATION) {
            return nextChar.isWhitespace() || nextChar == '\n' || nextChar == '\r'
        }

        // 其他标点（如省略号）
        return nextChar.isWhitespace() || nextChar == '\n' || nextChar == '\r'
    }
}

// 扩展函数
fun String.extractSentenceAround(index: Int): String {
    return SentenceExtractor.extractSentence(this, index, index)
}

fun String.extractSentenceAround(fromIndex: Int, toIndex: Int): String {
    return SentenceExtractor.extractSentence(this, fromIndex, toIndex)
}