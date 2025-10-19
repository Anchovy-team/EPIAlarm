package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Reservation;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public final class SchedulePlanner {

    private SchedulePlanner() {}

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public static void scheduleForToday(Context context) {
        UserSession session = UserSession.getInstance(context);

        Application app = (Application) context.getApplicationContext();
        TimetableViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(app)
                .create(TimetableViewModel.class);

        List<Reservation> allReservations = viewModel.reservations;
        if (allReservations == null || allReservations.isEmpty()) {
            return;
        }

        ZoneId zone = ZoneId.of("Europe/Paris");
        LocalDate today = LocalDate.now(zone);

        List<Reservation> todayReservations = allReservations.stream()
                .filter(r -> r.getStartDate().toLocalDate().equals(today))
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .toList();

        if (todayReservations.isEmpty()) {
            return;
        }

        Reservation firstClass = todayReservations.get(0);
        if (Settings.canDrawOverlays(context)) {
            setAlarm(
                    context,
                    session,
                    firstClass.getStartDate().atZone(zone).toInstant().toString(),
                    firstClass.getName()
            );
        }

        boolean canNotify = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED;

        if (canNotify) {
            todayReservations.stream()
                    .skip(1)
                    .forEach(r -> setNotification(
                            context,
                            session,
                            r.getStartDate().atZone(zone).toInstant().toString(),
                            r.getName()
                    ));
        }
    }

    private static void setAlarm(Context context, UserSession session, String startTimeIso,
                                 String className) {
        int advance = session.getAdvanceMinutesAlarm();
        if (advance <= 0) {
            return;
        }

        long triggerAt = Instant.parse(startTimeIso)
                .minus(advance, ChronoUnit.MINUTES)
                .toEpochMilli();
        if (triggerAt < System.currentTimeMillis()) {
            return;
        }

        Intent i = new Intent(context, AlarmReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", advance);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, (className + "_alarm").hashCode(),
                i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }

    private static void setNotification(Context context, UserSession session, String startTimeIso,
                                        String className) {
        int advance = session.getAdvanceMinutesReminder();
        if (advance <= 0) {
            return;
        }

        long triggerAt = Instant.parse(startTimeIso)
                .minus(advance, ChronoUnit.MINUTES)
                .toEpochMilli();
        if (triggerAt < System.currentTimeMillis()) {
            return;
        }

        Intent i = new Intent(context, NotificationsBroadcastReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", advance);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, (className + "_notification").hashCode(),
                i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }
}