package com.mmjang.ankihelper.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

object AppRestartHelper {

    /**
     * 立即重启应用程序。
     * @param context 当前的 Context
     */
    fun stopApp() {
//        val packageManager = context.packageManager
//        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
//        intent?.apply {
//            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//        }

//        // 设置一个 PendingIntent 来启动应用
//        val restartIntent = PendingIntent.getActivity(
//            context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // 通过 AlarmManager 启动应用（延迟100毫秒）
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.set(
//            AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartIntent
//        )

        // 结束当前进程
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    /**
     * 立即重启应用程序。
     * @param context 当前的 Context
     */
    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // 设置一个 PendingIntent 来启动应用
        val restartIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 通过 AlarmManager 启动应用（延迟100毫秒）
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartIntent
        )

        // 结束当前进程
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    /**
     * 延迟重启应用程序。
     * @param context 当前的 Context
     * @param delayMillis 延迟的毫秒数
     */
    fun restartAppWithDelay(context: Context, delayMillis: Long) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // 设置一个 PendingIntent 来启动应用
        val restartIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 通过 AlarmManager 设置延迟启动
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delayMillis, restartIntent
        )

        // 结束当前进程
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }
}
