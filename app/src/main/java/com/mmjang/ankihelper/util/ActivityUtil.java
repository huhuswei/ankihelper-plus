package com.mmjang.ankihelper.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.anki.AnkiDroidHelper;

/**
 * @ProjectName: ankihelper
 * @Package: com.mmjang.ankihelper.util
 * @ClassName: ActivityUtil
 * @Description: java类作用描述
 * @Author: ss
 * @CreateDate: 2022/7/29 1:10 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2022/7/29 1:10 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ActivityUtil {
    public static boolean isStatusBarShown(Activity context) {
        WindowManager.LayoutParams params = context.getWindow().getAttributes();
        int paramsFlag = params.flags & (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return paramsFlag == params.flags;
    }

    public static boolean isActivityRunning(Context context, Class<? extends Activity> activityClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningTaskInfo info : activityManager.getRunningTasks(Integer.MAX_VALUE)) {
                if (info.baseActivity.getClassName().equals(activityClass.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkAndStartAnkiDroid(Activity activity) {
        if (AnkiDroidHelper.isApiAvailable(MyApplication.getContext()) && !MyApplication.getAnkiDroid().isAnkiDroidRunning()) {
            MyApplication.getAnkiDroid().startAnkiDroid();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(SystemUtils.isBackActivity(activity, Constant.ANKIHELPER_PACKAGE_NAME)) {
                                            Intent intent = activity.getIntent();
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            activity.startActivity(intent);
                                            handler.removeCallbacks(this);
                                        }
                                        else {
                                            handler.postDelayed(this, 200);
                                        }
                                    }
                                },
                    200);
            return false;
        }
        return true;
    }
}
