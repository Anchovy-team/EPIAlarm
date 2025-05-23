package anchovy.team.epialarm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.Objects;
import java.util.UUID;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {

    public static String channelID = "5555";
    public String className;
    public Integer advanceMinutes;
    public Boolean vibration;
    public String alarmId;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getStringExtra("action");
        final String alarmOrNotification = intent.getStringExtra("alarmOrNotification");
        className = intent.getStringExtra("className");
        advanceMinutes = intent.getIntExtra("advanceMinutes", 0);
        vibration = intent.getBooleanExtra("vibration", false);
        alarmId = intent.getStringExtra("alarmId");

        if (action == null && isOrderedBroadcast()) {
            return;
        }

        if ("close".equals(action)) {

            NotificationManagerCompat.from(context).cancel(0);

        } else if ("snooze".equals(action)) {

            NotificationManagerCompat.from(context).cancel(0);

            Intent snoozeIntent = new Intent(context, NotificationsBroadcastReceiver.class);
            snoozeIntent.putExtra("alarmOrNotification", "alarm");
            snoozeIntent.putExtra("action", "alarm");
            snoozeIntent.putExtra("className", className);
            snoozeIntent.putExtra("advanceMinutes", advanceMinutes);
            snoozeIntent.putExtra("vibration", vibration);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    UUID.randomUUID().toString().hashCode(),
                    snoozeIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

            //TODO: use 5 minutes as the postpone gap, 15 seconds are for testing
            // decrease advanceMinutes variable each time
            long delay = System.currentTimeMillis() + 15 * 1000;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context
                    .ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delay, pendingIntent);

        } else if (Objects.equals(alarmOrNotification, "alarm")
                || Objects.equals(alarmOrNotification, "notification")) {
            sendAlarmOrNotification(context, alarmOrNotification);
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void sendAlarmOrNotification(Context context, String alarmOrNotification) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager
                    .class);
            if (notificationManager.getNotificationChannel(channelID) == null) {
                NotificationChannel channel = new NotificationChannel(channelID, "Alarm Channel",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Class Notification")
                    .setContentText(className + " in " + advanceMinutes.toString() + " minutes")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            if (Objects.equals(alarmOrNotification, "alarm")) {
                Intent closeIntent = new Intent(context, NotificationsBroadcastReceiver.class);
                closeIntent.putExtra("action", "close");
                final PendingIntent closePendingIntent = PendingIntent.getBroadcast(context,
                        UUID.randomUUID().toString().hashCode(), closeIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                Intent snoozeIntent = new Intent(context, NotificationsBroadcastReceiver.class);
                snoozeIntent.putExtra("action", "snooze");
                snoozeIntent.putExtra("className", className);
                snoozeIntent.putExtra("advanceMinutes", advanceMinutes);
                PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context,
                        UUID.randomUUID().toString().hashCode(), snoozeIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                Intent fullScreenIntent = new Intent(context, NotificationsBroadcastReceiver.class);
                PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        fullScreenIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                builder
                        .setOngoing(true)
                        .setTimeoutAfter(0)
                        .setAutoCancel(false)
                        .setFullScreenIntent(fullScreenPendingIntent, true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                        .addAction(android.R.drawable.ic_menu_close_clear_cancel, "CLOSE",
                                closePendingIntent)
                        .addAction(android.R.drawable.ic_menu_revert, "POSTPONE",
                                snoozePendingIntent);

                if (vibration) {
                    builder.setVibrate(new long[]{0, 500, 1000});
                }
            }

            NotificationManagerCompat.from(context).notify(0, builder.build());
        }
    }
}
