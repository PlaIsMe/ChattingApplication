package com.chattingapplication.chattingclient.Service;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannels{
    public static final String CHANNEL_GROUP_ID = "your_channel_group_id";
    public static final String CHANNEL_ID_DEFAULT = "channel_default";
    public static final String CHANNEL_ID_HIGH = "channel_high";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Set any additional properties for channel 2

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannelGroup(new NotificationChannelGroup(
                    CHANNEL_GROUP_ID,
                    "OU Messenger Group"
            ));

            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID_DEFAULT,
                    "Channel Default",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel1.setGroup(CHANNEL_GROUP_ID);
            // Set any additional properties for channel 1

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_ID_HIGH,
                    "Channel hHigh",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setGroup(CHANNEL_GROUP_ID);

            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
