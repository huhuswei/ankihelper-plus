package com.mmjang.ankihelper.util;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: DarkModeUtils
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/10/26 10:42 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/10/26 10:42 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DarkModeUtils {

    public static final String KEY_CURRENT_MODEL = "night_mode_state_sp";

    private static int getNightModel(Context context) {
        Settings settings = Settings.getInstance(context);
        return settings.get(KEY_CURRENT_MODEL, AppCompatDelegate.MODE_NIGHT_YES);
    }

    public static void setNightModel(Context context, int nightMode) {
        Settings settings = Settings.getInstance(context);
        settings.put(KEY_CURRENT_MODEL, nightMode);
    }

    /**
     * ths method should be called in Application onCreate method
     *
     * @param application application
     */
    public static void init(Application application) {
        int nightMode = getNightModel(application);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    /**
     * 应用夜间模式
     */
    public static void applyNightMode(Context context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setNightModel(context, AppCompatDelegate.MODE_NIGHT_YES);
    }

    /**
     * 应用日间模式
     */
    public static void applyDayMode(Context context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setNightModel(context, AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * 跟随系统主题时需要动态切换
     */
    public static void applySystemMode(Context context) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setNightModel(context, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    /**
     * 判断App当前是否处于暗黑模式状态
     *
     * @param context 上下文
     * @return 返回
     */
    public static boolean isDarkMode(Context context) {
        int nightMode = getNightModel(context);
        if (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            int applicationUiMode = context.getResources().getConfiguration().uiMode;
            int systemMode = applicationUiMode & Configuration.UI_MODE_NIGHT_MASK;
            return systemMode == Configuration.UI_MODE_NIGHT_YES;
        } else {
            return nightMode == AppCompatDelegate.MODE_NIGHT_YES;
        }
    }

    public static void darkModeSettingDialog(Context activityContext) {
//        LinkedHashMap<String, Integer> nightModeMap = new LinkedHashMap<>();
//        nightModeMap.put("MODE_NIGHT_FOLLOW_SYSTEM", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//        nightModeMap.put("MODE_NIGHT_NO", AppCompatDelegate.MODE_NIGHT_NO);
//        nightModeMap.put("MODE_NIGHT_FOLLOW_SYSTEM", AppCompatDelegate.MODE_NIGHT_YES);

        Settings settings = Settings.getInstance(activityContext);
        int checkedIndex = settings.get(Settings.DARK_MODE_INDEX, 0);

        String[] modeNameArr = new String[Constant.DarkMode.values().length];
        for(int index=0; index < Constant.DarkMode.values().length; index++) {
            modeNameArr[index] = activityContext.getResources().getString(Constant.DarkMode.values()[index].getNameId());
        }

        boolean[] isCheckedArr = new boolean[modeNameArr.length];

        for(int i = 0; i < isCheckedArr.length; i++) {
            if(i == checkedIndex)
                isCheckedArr[i] = true;
            else
                isCheckedArr[i] = false;
        }

        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(activityContext);
        multiChoiceDialog.setTitle(R.string.str_dark_mode_q);
        multiChoiceDialog.setSingleChoiceItems(modeNameArr, checkedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Constant.DarkMode mode = Constant.DarkMode.values()[which];
                        switch(mode) {
                            case MODE_NIGHT_FOLLOW_SYSTEM:
                                applySystemMode(activityContext);
                                break;
                            case MODE_NIGHT_NO:
                                applyDayMode(activityContext);
                                break;
                            case MODE_NIGHT_YES:
                                applyNightMode(activityContext);
                                break;
                        }
                        settings.put(Settings.DARK_MODE_INDEX, which);
                        dialog.dismiss();
                    }
                });
        multiChoiceDialog.show();
    }


    public static void initDarkMode(Context activityContext) {
        Settings settings = Settings.getInstance(activityContext);
        Constant.DarkMode mode = Constant.DarkMode.values()[settings.get(Settings.DARK_MODE_INDEX, 0)];

        switch(mode) {
            case MODE_NIGHT_FOLLOW_SYSTEM:
                applySystemMode(activityContext);
                break;
            case MODE_NIGHT_NO:
                applyDayMode(activityContext);
                break;
            case MODE_NIGHT_YES:
                applyNightMode(activityContext);
                break;
        }
    }
}

