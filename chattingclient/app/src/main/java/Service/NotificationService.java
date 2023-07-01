package Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.R;

import java.time.LocalDateTime;

public class NotificationService {

    public static void sendNotification(Context context, String messageBody) {

        String channelId = "my notification channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.baseline_notifications_24)
                        .setContentTitle("My notification message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);

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
