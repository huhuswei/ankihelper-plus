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

    /**
     * 专门为CustomToast设计的暗黑模式检测方法
     * 使用Application Context并确保设置正确加载
     */
    public static boolean isDarkModeForCustomToast(Context context) {
        try {
            // 使用Application Context避免内存泄漏
            Context appContext = context.getApplicationContext();
            Settings settings = Settings.getInstance(appContext);
            int nightMode = settings.get(KEY_CURRENT_MODEL, AppCompatDelegate.MODE_NIGHT_YES);

            if (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                // 跟随系统模式
                int applicationUiMode = appContext.getResources().getConfiguration().uiMode;
                int systemMode = applicationUiMode & Configuration.UI_MODE_NIGHT_MASK;
                return systemMode == Configuration.UI_MODE_NIGHT_YES;
            } else {
                // 直接使用用户设置的模式
                return nightMode == AppCompatDelegate.MODE_NIGHT_YES;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果出现异常，回退到系统检测
            return isSystemDarkMode(context);
        }
    }

    /**
     * 系统暗黑模式检测（不使用app设置）
     */
    public static boolean isSystemDarkMode(Context context) {
        int applicationUiMode = context.getResources().getConfiguration().uiMode;
        int systemMode = applicationUiMode & Configuration.UI_MODE_NIGHT_MASK;
        return systemMode == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * 获取当前暗黑模式设置的字符串表示，用于调试
     */
    public static String getDarkModeStatus(Context context) {
        try {
            Settings settings = Settings.getInstance(context);
            int nightMode = settings.get(KEY_CURRENT_MODEL, AppCompatDelegate.MODE_NIGHT_YES);

            switch (nightMode) {
                case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                    return "FOLLOW_SYSTEM (System is: " +
                            (isSystemDarkMode(context) ? "DARK" : "LIGHT") + ")";
                case AppCompatDelegate.MODE_NIGHT_NO:
                    return "DAY_MODE";
                case AppCompatDelegate.MODE_NIGHT_YES:
                    return "NIGHT_MODE";
                default:
                    return "UNKNOWN: " + nightMode;
            }
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}

