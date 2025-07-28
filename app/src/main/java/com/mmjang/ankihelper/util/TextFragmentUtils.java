package com.mmjang.ankihelper.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 文本片段URL生成工具类（符合Chrome标准）
 * 参考：https://github.com/GoogleChromeLabs/text-fragments-polyfill
 */
public class TextFragmentUtils {

    private static final String TAG = "TextFragmentUtils";
    private static final String TEXT_FRAGMENT_PREFIX = "#:~:text=";

    // Chrome文本片段配置
    private static final int MAX_EXACT_MATCH_LENGTH = 300;
    private static final int MAX_PREFIX_SUFFIX_LENGTH = 50;

    /**
     * 移除URL中的文本片段（健壮版）
     * 处理各种边界情况：
     * 1. 只有文本片段
     * 2. 文本片段前面有其他片段
     * 3. 多个片段共存
     * 4. 异常的URL格式
     */
    public static String removeTextFragment(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        if (!hasTextFragment(url)) {
            return url;
        }

        try {
            // 方法1：直接分割（最常见情况）
            String[] parts = url.split(TEXT_FRAGMENT_PREFIX, 2);
            if (parts.length > 0) {
                return parts[0];
            }

        } catch (IndexOutOfBoundsException e) {
            Trace.e(TAG, "URL索引越界", e);
            // 方法2：使用索引查找
            return removeByIndex(url);
        } catch (Exception e) {
            Trace.e(TAG, "移除文本片段失败", e);
            // 方法3：回退方案
            return fallbackRemove(url);
        }

        return url;
    }

    /**
     * 使用索引查找方式移除片段
     */
    private static String removeByIndex(String url) {
        int fragmentIndex = url.indexOf(TEXT_FRAGMENT_PREFIX);
        if (fragmentIndex != -1) {
            return url.substring(0, fragmentIndex);
        }
        return url;
    }

    /**
     * 回退移除方案
     */
    private static String fallbackRemove(String url) {
        try {
            // 尝试找到第一个 # 字符
            int firstHashIndex = url.indexOf('#');
            if (firstHashIndex != -1) {
                return url.substring(0, firstHashIndex);
            }
            return url;
        } catch (Exception e2) {
            Trace.e(TAG, "回退方案也失败", e2);
            return url;
        }
    }

    /**
     * 验证URL是否包含文本片段
     */
    public static boolean hasTextFragment(String url) {
        return url != null && url.contains(TEXT_FRAGMENT_PREFIX);
    }

    /**
     * 从URL中提取文本片段参数
     */
    public static String extractTextFragment(String url) {
        if (!hasTextFragment(url)) {
            return null;
        }

        try {
            int fragmentIndex = url.indexOf(TEXT_FRAGMENT_PREFIX);
            if (fragmentIndex != -1) {
                return url.substring(fragmentIndex + TEXT_FRAGMENT_PREFIX.length());
            }
        } catch (Exception e) {
            Trace.e(TAG, "提取文本片段失败", e);
        }
        return null;
    }

    /**
     * 安全地添加文本片段到URL（先移除旧的，再添加新的）
     */
    public static String safeAddTextFragment(String url, String selectedText) {
        String cleanUrl = removeTextFragment(url);
        return generateBasicTextFragment(cleanUrl, selectedText);
    }

    /**
     * 文本片段配置类
     */
    public static class FragmentConfig {
        public String textStart;      // 必须：匹配文本的开始部分
        public String textEnd;        // 可选：匹配文本的结束部分（用于范围匹配）
        public String prefix;         // 可选：前缀上下文
        public String suffix;         // 可选：后缀上下文

        public FragmentConfig(String textStart) {
            this.textStart = textStart;
        }

        public FragmentConfig setTextEnd(String textEnd) {
            this.textEnd = textEnd;
            return this;
        }

        public FragmentConfig setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public FragmentConfig setSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }
    }

    /**
     * 生成符合Chrome标准的文本片段URL（主方法）
     */
    public static String generateTextFragment(String baseUrl, FragmentConfig fragment) {
        if (!isValidInput(baseUrl, fragment)) {
            return baseUrl;
        }

        try {
            String fragmentParam = buildFragmentParameter(fragment);
            return buildFullUrl(baseUrl, fragmentParam);

        } catch (Exception e) {
            Trace.e(TAG, "生成文本片段失败", e);
            return baseUrl;
        }
    }

    /**
     * 构建片段参数（核心方法，参考JavaScript实现）
     */
    private static String buildFragmentParameter(FragmentConfig fragment) {
        StringBuilder param = new StringBuilder();

        // 前缀（prefix）
        if (fragment.prefix != null && !fragment.prefix.trim().isEmpty()) {
            String encodedPrefix = encodeURIComponent(fragment.prefix.trim());
            param.append(encodedPrefix).append("-,%20");
        }

        // 文本开始（textStart）- 必须
        String encodedTextStart = encodeURIComponent(fragment.textStart.trim());
        param.append(encodedTextStart);

        // 文本结束（textEnd）- 可选，用于范围匹配
        if (fragment.textEnd != null && !fragment.textEnd.trim().isEmpty()) {
            String encodedTextEnd = encodeURIComponent(fragment.textEnd.trim());
            param.append(",").append(encodedTextEnd);
        }

        // 后缀（suffix）
        if (fragment.suffix != null && !fragment.suffix.trim().isEmpty()) {
            String encodedSuffix = encodeURIComponent(fragment.suffix.trim());
            param.append(",-%20").append(encodedSuffix);
        }

        return param.toString();
    }

    /**
     * URL编码（完全匹配JavaScript的encodeURIComponent行为）
     */
    private static String encodeURIComponent(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        try {
            // 使用URLEncoder，但需要调整以匹配JavaScript的encodeURIComponent
            String encoded = URLEncoder.encode(text, "UTF-8");

            // URLEncoder使用+表示空格，但encodeURIComponent使用%20
            encoded = encoded.replace("+", "%20");

            // URLEncoder会对更多字符编码，需要解码一些字符以匹配JavaScript行为
            encoded = encoded.replace("%21", "!")
                    .replace("%27", "'")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%2A", "*")
                    .replace("%7E", "~");

            // 但保留逗号和连字符的编码，因为它们在文本片段语法中有特殊含义
            encoded = encoded.replace(",", "%2C")
                    .replace("-", "%2D");

            return encoded;

        } catch (UnsupportedEncodingException e) {
            // 降级方案：手动编码关键字符
            return manualEncodeURIComponent(text);
        }
    }

    /**
     * 手动URL编码（兼容JavaScript encodeURIComponent）
     */
    private static String manualEncodeURIComponent(String text) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (isSafeCharacter(c)) {
                result.append(c);
            } else {
                result.append("%").append(String.format("%02X", (int) c));
            }
        }

        return result.toString();
    }

    /**
     * 判断字符是否安全（不需要编码）
     */
    private static boolean isSafeCharacter(char c) {
        // 字母数字
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
            return true;
        }

        // 安全字符集（参考JavaScript encodeURIComponent）
        String safeChars = "-_.!~*'()";
        return safeChars.indexOf(c) != -1;
    }

    /**
     * 构建完整URL
     */
    private static String buildFullUrl(String baseUrl, String fragmentParam) {
        try {
            // 清理现有片段
            String cleanUrl = baseUrl.split("#")[0];
            return cleanUrl + TEXT_FRAGMENT_PREFIX + fragmentParam;

        } catch (Exception e) {
            Trace.e(TAG, "构建完整URL失败", e);
            return baseUrl;
        }
    }

    /**
     * 简化的基础文本片段生成
     */
    public static String generateBasicTextFragment(String baseUrl, String selectedText) {
        FragmentConfig fragment = new FragmentConfig(selectedText);
        return generateTextFragment(baseUrl, fragment);
    }

    /**
     * 生成带上下文的文本片段
     */
    public static String generateContextTextFragment(String baseUrl, String selectedText,
                                                     String prefix, String suffix) {
        FragmentConfig fragment = new FragmentConfig(selectedText)
                .setPrefix(prefix)
                .setSuffix(suffix);
        return generateTextFragment(baseUrl, fragment);
    }

    /**
     * 生成范围匹配文本片段
     */
    public static String generateRangeTextFragment(String baseUrl, String textStart, String textEnd) {
        FragmentConfig fragment = new FragmentConfig(textStart)
                .setTextEnd(textEnd);
        return generateTextFragment(baseUrl, fragment);
    }

    /**
     * 从选中文本自动生成最优片段配置
     */
    public static FragmentConfig createOptimalFragment(String selectedText, String fullText) {
        if (selectedText == null || selectedText.trim().isEmpty()) {
            return null;
        }

        String trimmedText = selectedText.trim();
        FragmentConfig fragment = new FragmentConfig(trimmedText);

        // 如果文本较长，使用范围匹配
        if (trimmedText.length() > MAX_EXACT_MATCH_LENGTH) {
            return createRangeFragment(trimmedText);
        }

        // 如果提供了全文，尝试提取上下文
        if (fullText != null && !fullText.isEmpty()) {
            addContextFromFullText(fragment, trimmedText, fullText);
        }

        return fragment;
    }

    /**
     * 创建范围匹配片段
     */
    private static FragmentConfig createRangeFragment(String longText) {
        int splitPoint = longText.length() / 2;

        // 在空格处分割
        int spaceIndex = longText.lastIndexOf(' ', splitPoint);
        if (spaceIndex > longText.length() / 3) {
            splitPoint = spaceIndex;
        }

        String textStart = longText.substring(0, splitPoint).trim();
        String textEnd = longText.substring(splitPoint).trim();

        return new FragmentConfig(textStart).setTextEnd(textEnd);
    }

    /**
     * 从全文添加上下文
     */
    private static void addContextFromFullText(FragmentConfig fragment, String selectedText, String fullText) {
        int index = fullText.indexOf(selectedText);
        if (index == -1) return;

        // 提取前缀
        int prefixStart = Math.max(0, index - MAX_PREFIX_SUFFIX_LENGTH);
        String prefix = fullText.substring(prefixStart, index).trim();
        if (!prefix.isEmpty()) {
            fragment.setPrefix(prefix);
        }

        // 提取后缀
        int suffixEnd = Math.min(fullText.length(), index + selectedText.length() + MAX_PREFIX_SUFFIX_LENGTH);
        String suffix = fullText.substring(index + selectedText.length(), suffixEnd).trim();
        if (!suffix.isEmpty()) {
            fragment.setSuffix(suffix);
        }
    }

    private static boolean isValidInput(String baseUrl, FragmentConfig fragment) {
        return baseUrl != null && !baseUrl.isEmpty() &&
                fragment != null && fragment.textStart != null &&
                !fragment.textStart.trim().isEmpty();
    }

    /**
     * 调试方法：显示生成的URL结构
     */
    public static void debugFragment(String baseUrl, FragmentConfig fragment) {
        String generatedUrl = generateTextFragment(baseUrl, fragment);
        System.out.println("=== 文本片段调试 ===");
        System.out.println("基础URL: " + baseUrl);
        System.out.println("textStart: " + fragment.textStart);
        System.out.println("textEnd: " + fragment.textEnd);
        System.out.println("prefix: " + fragment.prefix);
        System.out.println("suffix: " + fragment.suffix);
        System.out.println("生成URL: " + generatedUrl);

        if (generatedUrl.contains(TEXT_FRAGMENT_PREFIX)) {
            String fragmentPart = generatedUrl.substring(generatedUrl.indexOf(TEXT_FRAGMENT_PREFIX) + TEXT_FRAGMENT_PREFIX.length());
            System.out.println("片段参数: " + fragmentPart);
        }
    }
}