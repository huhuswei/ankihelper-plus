package com.mmjang.ankihelper.ui.floating.assist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;

import com.mmjang.ankihelper.CustomToast;
import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.widget.CustomToastPosition;


public class AssistCustomToastPositionUtils {

    public static void selectPositionDialog(Context context) {
        Settings settings = Settings.getInstance(context);
        int savedPositionIndex = settings.get(Settings.TOAST_POSITION_INDEX, 0);

        CustomToastPosition[] positions = CustomToastPosition.values();
        String[] positionNameArr = new String[positions.length];
        for (int i = 0; i < positions.length; i++) {
            positionNameArr[i] = context.getResources().getString(positions[i].getNameId());
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.str_select_position);
        dialog.setSingleChoiceItems(positionNameArr, savedPositionIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomToastPosition selectedPosition = positions[which];
                        // 保存选择的位置索引
                        settings.put(Settings.TOAST_POSITION_INDEX, which);
                        // 应用位置设置
                        applyPositionSettings(context, selectedPosition);
                        CustomToast ct = CustomToast.Companion.getInstance(context);
                        ct.dismiss();
                        ct.show(
                                "测试",
                                "test",
                                3000L,
                                selectedPosition,
                                R.drawable.icon_light,
                                null,
                                null
                        );
                    }
                });
//        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.show();
    }

    public static int getGravityForPosition(CustomToastPosition position) {
        switch (position) {
            case TOP:
                return Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            case CENTER:
                return Gravity.CENTER;
            case BOTTOM:
                return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            case TOP_START:
                return Gravity.TOP | Gravity.START;
            case TOP_END:
                return Gravity.TOP | Gravity.END;
            case BOTTOM_START:
                return Gravity.BOTTOM | Gravity.START;
            case BOTTOM_END:
                return Gravity.BOTTOM | Gravity.END;
            default:
                return Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        }
    }

    public static int getCurrentPositionGravity(Context context) {
        CustomToastPosition currentPosition = getCurrentPosition(context);
        return getGravityForPosition(currentPosition);
    }

    public static CustomToastPosition getCurrentPosition(Context context) {
        Settings settings = Settings.getInstance(context);
        int index = settings.get(Settings.TOAST_POSITION_INDEX, 0);
        return CustomToastPosition.values()[index];
    }

    public static int getCurrentPositionIndex(Context context) {
        Settings settings = Settings.getInstance(context);
        return settings.get(Settings.TOAST_POSITION_INDEX, 0);
    }

    private static void applyPositionSettings(Context context, CustomToastPosition position) {
        int gravity = getGravityForPosition(position);
        // 这里可以添加应用位置设置的逻辑
        // 例如：更新全局的 Toast 位置设置
    }
}

