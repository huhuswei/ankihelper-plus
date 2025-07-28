package com.mmjang.ankihelper.widget;

import com.mmjang.ankihelper.R;

// CustomToastPosition 枚举类
public enum CustomToastPosition {
    TOP(R.string.position_top),
    CENTER(R.string.position_center),
    BOTTOM(R.string.position_bottom),
    TOP_START(R.string.position_top_start),
    TOP_END(R.string.position_top_end),
    BOTTOM_START(R.string.position_bottom_start),
    BOTTOM_END(R.string.position_bottom_end);

    private final int nameId;

    CustomToastPosition(int nameId) {
        this.nameId = nameId;
    }

    public int getNameId() {
        return nameId;
    }
}
