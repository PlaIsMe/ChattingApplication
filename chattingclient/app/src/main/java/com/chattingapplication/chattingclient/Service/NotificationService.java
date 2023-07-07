package com.chattingapplication.chattingclient.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.R;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.Arrays;

public class NotificationService {

    private static final String MESSAGE_GROUP = "com.chattingapplication.chattingclient.MESSAGE_GROUP";

    public static void sendNotification(Context context, Message message) {
        Gson gson = new Gson();
        Intent chattingIntent = new Intent(context, ChattingActivity.class);
        Log.d("debugSetTargetUser", message.getUser().toString());
        message.getChatRoom().setTargetUser(message.getUser());
        chattingIntent.putExtra("currentChatRoom", gson.toJson(message.getChatRoom()));
        TaskStackBuilder builder = TaskStackBuilder.create(context);
        builder.addNextIntentWithParentStack(chattingIntent);

        PendingIntent pendingIntent = builder.getPendingIntent(0,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

//        String channelId = "Chat Application Channel Id";

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ID_DEFAULT)
                        .setSmallIcon(R.drawable.chat_application_logo)
                        .setContentTitle(String.format("%s %s", message.getUser().getFirstName(), message.getUser().getLastName()))
                        .setContentText(message.getContent())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setGroup(NotificationChannels.CHANNEL_GROUP_ID);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }

        NotificationCompat.Builder summaryBuilder =
                new NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ID_DEFAULT)
                        .setContentTitle("OU Messenger")
                        // Set content text to support devices running API level < 24.
                        .setContentText("New message")
                        .setSmallIcon(R.drawable.chat_application_logo)
                        // Specify which group this notification belongs to.
                        .setGroup(NotificationChannels.CHANNEL_GROUP_ID)
                        // Set this notification as the summary for the group.
                        .setGroupSummary(true);

        notificationManager.notify(getNotificationId() /* ID of notification */,
                notificationBuilder.build());
        notificationManager.notify(0, summaryBuilder.build());
    }

    private static int getNotificationId(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().getSecond();
        }
        return 0;
    }
}
