package anchovy.team.epialarm;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "5555";

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {
        String className = intent.getStringExtra("className");
        int advanceMinutes = intent.getIntExtra("advance", 0);
        sendNotification(context, className, advanceMinutes);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void sendNotification(Context context, String className, int advanceMinutes) {
        NotificationManager notificationManager = context.getSystemService(
                NotificationManager.class);

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Class Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)//TODO: Add micro icon for notification
                .setContentTitle("Upcoming Class")
                .setContentText(className + " starts in " + advanceMinutes + " minutes")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat.from(context).notify(className.hashCode(), builder.build());
    }
}