package com.mmjang.ankihelper.ui.floating.assist;

import static com.mmjang.ankihelper.util.Constant.INTENT_ANKIHELPER_TARGET_URL;
import static com.mmjang.ankihelper.util.Constant.INTENT_ANKIHELPER_TARGET_WORD;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.popup.PopupActivity;
import com.mmjang.ankihelper.util.StringUtil;

public class AssistNotificationHelper {
    private Context context;
    private final NotificationManager notificationManager;
    private static final String CHANNEL_ID = "com.mmjang.ankihelper";
    private static final int NOTIFICATION_ID = 2;

    public AssistNotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(); // 确保渠道被创建
    }

    private void createNotificationChannel() {
        // 检查是否已经创建过渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (existingChannel == null) {
                // 渠道不存在，创建新的
                CharSequence name = "AnkiHelper通知";
                String description = "AnkiHelper应用的通知渠道";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showNotification(String title, String message) {
        showNotification(title, message, "");
    }
    public void showNotification(String title, String message, String url) {
        // 再次确保渠道存在
        createNotificationChannel();

        // 创建点击通知后打开的 Intent
        Intent intent = new Intent(context, PopupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", message);
        intent.putExtra(INTENT_ANKIHELPER_TARGET_WORD, title);
        if(!url.isEmpty()) intent.putExtra(INTENT_ANKIHELPER_TARGET_URL, url);
        intent.setType("text/plain");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                Long.valueOf(System.currentTimeMillis()).intValue(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_light) // 通知图标
                .setContentTitle(title)
//                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 设置点击事件
                .setAutoCancel(true) // 点击后自动消失
                .setTimeoutAfter(3000); // 3后自动消失

        String messageHtml = StringUtil.appendTagAll(message, title, "<b>", "</b>");
        if (!url.isEmpty())
            messageHtml = String.format("🔗 %s<", messageHtml
                                    );
        else
            messageHtml = String.format("%s", messageHtml
                    );
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(messageHtml, Html.FROM_HTML_MODE_COMPACT)));

        // 显示通知
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // 取消通知的方法
    public void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}