package com.chattingapplication.chattingclient.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.R;

import java.time.LocalDateTime;

public class NotificationService {

    public static void sendNotification(Context context, Message message) {
        Intent chattingIntent = new Intent(context, ChattingActivity.class);
        chattingIntent.putExtra("targetUser", message.getUser().toJsonString());
//        chattingIntent.putExtra("currentChatRoom", message.get)
        TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addNextIntentWithParentStack(chattingIntent);

        PendingIntent pendingIntent = builder.getPendingIntent(0,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "my notification channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.baseline_notifications_24)
                        .setContentTitle(String.format("%s %s", message.getUser().getFirstName(), message.getUser().getLastName()))
                        .setContentText(message.getContent())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId() /* ID of notification */,
                notificationBuilder.build());
    }

    private static int getNotificationId(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().getSecond();
        }
        return 0;
    }
}
