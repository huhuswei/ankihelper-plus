package com.mmjang.ankihelper.util;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

public class Informations {
    public static void printAccessibilityNodeInfoProperties(AccessibilityNodeInfo node) {
        if (node == null) {
            System.out.println("AccessibilityNodeInfo is null");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== AccessibilityNodeInfo Properties ===\n");

        // 基本信息
        sb.append("Basic Info:\n");
        sb.append("  Class: ").append(node.getClassName()).append("\n");
        sb.append("  Package: ").append(node.getPackageName()).append("\n");
        sb.append("  View ID: ").append(node.getViewIdResourceName()).append("\n");
        sb.append("  Text: ").append(node.getText()).append("\n");
        sb.append("  Content Description: ").append(node.getContentDescription()).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            sb.append("  Unique ID: ").append(node.getUniqueId()).append("\n");
        }

        // 位置和大小
        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        sb.append("Position & Size:\n");
        sb.append("  Bounds: ").append(bounds.toShortString()).append("\n");
        sb.append("  Width: ").append(bounds.width()).append("\n");
        sb.append("  Height: ").append(bounds.height()).append("\n");

        // 可访问性属性
        sb.append("Accessibility Properties:\n");
        sb.append("  Checkable: ").append(node.isCheckable()).append("\n");
        sb.append("  Checked: ").append(node.isChecked()).append("\n");
        sb.append("  Clickable: ").append(node.isClickable()).append("\n");
        sb.append("  Enabled: ").append(node.isEnabled()).append("\n");
        sb.append("  Focusable: ").append(node.isFocusable()).append("\n");
        sb.append("  Focused: ").append(node.isFocused()).append("\n");
        sb.append("  Visible: ").append(node.isVisibleToUser()).append("\n");
        sb.append("  Selected: ").append(node.isSelected()).append("\n");
        sb.append("  Editable: ").append(node.isEditable()).append("\n");
        sb.append("  Password: ").append(node.isPassword()).append("\n");
        sb.append("  Long Clickable: ").append(node.isLongClickable()).append("\n");
        sb.append("  Dismissable: ").append(node.isDismissable()).append("\n");
        sb.append("  Scrollable: ").append(node.isScrollable()).append("\n");
        sb.append("  Multi Line: ").append(node.isMultiLine()).append("\n");

        // 操作信息
        sb.append("Available Actions:\n");
        for (AccessibilityNodeInfo.AccessibilityAction action : node.getActionList()) {
            sb.append("  - ").append(getActionName(action.getId())).append(" (ID: ").append(action.getId()).append(")\n");
        }

        // 层级信息
        sb.append("Hierarchy Info:\n");
        String s = node.getChildCount()<1|| node.getChild(node.getChildCount()-1)==null || node.getChild(node.getChildCount()-1).getText() ==null?"null":node.getChild(node.getChildCount()-1).getText().toString();
        sb.append("  Child Count: ").append(node.getChildCount()+ " " + s).append("\n");
        sb.append("  Parent: ").append(node.getParent() != null ? "Available" : "Null").append("\n");

        // 额外信息
        sb.append("Extra Info:\n");
        Bundle extras = node.getExtras();
        if (extras != null && !extras.isEmpty()) {
            for (String key : extras.keySet()) {
                sb.append("  ").append(key).append(": ").append(extras.get(key)).append("\n");
            }
        } else {
            sb.append("  No extras\n");
        }

        // 错误信息
        sb.append("Error Info:\n");
        sb.append("  Error: ").append(node.getError()).append("\n");

        // 输入类型
        sb.append("Input Type:\n");
        sb.append("  Input Type: ").append(node.getInputType()).append("\n");

        // 文本选择
        sb.append("Text Selection:\n");
        sb.append("  Selection Start: ").append(node.getTextSelectionStart()).append("\n");
        sb.append("  Selection End: ").append(node.getTextSelectionEnd()).append("\n");

        // 绘制顺序
        sb.append("Drawing Order: ").append(node.getDrawingOrder()).append("\n");

        // 窗口信息
        sb.append("Window Info:\n");
        sb.append("  Window ID: ").append(node.getWindowId()).append("\n");
        AccessibilityWindowInfo window = node.getWindow();
        if (window != null) {
            sb.append("  Window Type: ").append(window.getType()).append("\n");
            sb.append("  Window Layer: ").append(window.getLayer()).append("\n");
        }

        Trace.i("AccessibilityNodeInfo", sb.toString());
    }

    private static String getActionName(int actionId) {
        switch (actionId) {
            case AccessibilityNodeInfo.ACTION_FOCUS:
                return "ACTION_FOCUS";
            case AccessibilityNodeInfo.ACTION_CLEAR_FOCUS:
                return "ACTION_CLEAR_FOCUS";
            case AccessibilityNodeInfo.ACTION_SELECT:
                return "ACTION_SELECT";
            case AccessibilityNodeInfo.ACTION_CLEAR_SELECTION:
                return "ACTION_CLEAR_SELECTION";
            case AccessibilityNodeInfo.ACTION_CLICK:
                return "ACTION_CLICK";
            case AccessibilityNodeInfo.ACTION_LONG_CLICK:
                return "ACTION_LONG_CLICK";
            case AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS:
                return "ACTION_ACCESSIBILITY_FOCUS";
            case AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS:
                return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
            case AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY:
                return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
            case AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY:
                return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
            case AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT:
                return "ACTION_NEXT_HTML_ELEMENT";
            case AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT:
                return "ACTION_PREVIOUS_HTML_ELEMENT";
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD:
                return "ACTION_SCROLL_FORWARD";
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD:
                return "ACTION_SCROLL_BACKWARD";
            case AccessibilityNodeInfo.ACTION_COPY:
                return "ACTION_COPY";
            case AccessibilityNodeInfo.ACTION_PASTE:
                return "ACTION_PASTE";
            case AccessibilityNodeInfo.ACTION_CUT:
                return "ACTION_CUT";
            case AccessibilityNodeInfo.ACTION_SET_SELECTION:
                return "ACTION_SET_SELECTION";
            case AccessibilityNodeInfo.ACTION_EXPAND:
                return "ACTION_EXPAND";
            case AccessibilityNodeInfo.ACTION_COLLAPSE:
                return "ACTION_COLLAPSE";
            case AccessibilityNodeInfo.ACTION_DISMISS:
                return "ACTION_DISMISS";
            case AccessibilityNodeInfo.ACTION_SET_TEXT:
                return "ACTION_SET_TEXT";
//            case AccessibilityNodeInfo.ACTION_SHOW_ON_SCREEN:
//                return "ACTION_SHOW_ON_SCREEN";
            default:
                return "CUSTOM_ACTION (" + actionId + ")";
        }
    }
}
