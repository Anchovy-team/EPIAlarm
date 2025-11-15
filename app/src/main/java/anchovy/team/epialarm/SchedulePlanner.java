package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Reservation;
import anchovy.team.epialarm.zeus.models.Room;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class SchedulePlanner {

    private SchedulePlanner() {}

    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");

    public static void scheduleForToday(Context context) {
        scheduleForDate(context, LocalDate.now(ZONE_PARIS));
    }

    public static void scheduleForTomorrow(Context context) {
        scheduleForDate(context, LocalDate.now(ZONE_PARIS).plusDays(1));
    }

    public static void scheduleForDate(Context context, LocalDate targetDate) {
        UserSession session = UserSession.getInstance(context);

        List<Reservation> allReservations = ScheduleRepository.getInstance()
                .fetchReservations(context).join();
        if (allReservations == null || allReservations.isEmpty()) {
            return;
        }

        List<Reservation> todayReservations = allReservations.stream()
                .filter(r -> r.getStartDate().toLocalDate().equals(targetDate))
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .collect(Collectors.toList());

        if (todayReservations.isEmpty()) {
            return;
        }

        Reservation firstClass = todayReservations.get(0);
        if (Settings.canDrawOverlays(context)) {
            setAlarm(
                    context,
                    session,
                    firstClass.getStartDate(),
                    firstClass.getName(),
                    firstClass.getRooms()
            );
        }

        boolean canNotify = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            canNotify = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }

        if (canNotify) {
            todayReservations.stream()
                    .skip(1)
                    .forEach(r -> setNotification(
                            context,
                            session,
                            r.getStartDate(),
                            r.getName(),
                            r.getRooms()
                    ));
        }
    }

    private static void setAlarm(Context context, UserSession session, LocalDateTime startTimeUtc,
                                 String className, Room[] rooms) {
        int advance = session.getAdvanceMinutesAlarm();
        if (advance <= 0) {
            return;
        }

        long triggerAt = startTimeUtc.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Paris"))
                .minusMinutes(advance)
                .toInstant()
                .toEpochMilli();
        if (triggerAt < System.currentTimeMillis()) {
            return;
        }

        String rooms_str = "";
        for (Room r : rooms) {
            rooms_str += r.getName() + ", ";
        }
        if (rooms_str != "") {
            rooms_str = rooms_str.substring(0, rooms_str.length() - 2);
        }

        Intent i = new Intent(context, AlarmReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", advance)
                .putExtra("rooms", rooms_str);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, (className + "_alarm").hashCode(),
                i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }

    private static void setNotification(Context context, UserSession session,
                                        LocalDateTime startTimeUtc, String className, Room[] rooms) {
        int advance = session.getAdvanceMinutesReminder();
        if (advance <= 0) {
            return;
        }

        long triggerAt = startTimeUtc.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Paris"))
                .minusMinutes(advance)
                .toInstant()
                .toEpochMilli();
        if (triggerAt < System.currentTimeMillis()) {
            return;
        }

        String rooms_str = "";
        for (Room r : rooms) {
            rooms_str += r.getName() + ", ";
        }
        if (rooms_str != "") {
            rooms_str = rooms_str.substring(0, rooms_str.length() - 2);
        }

        Intent i = new Intent(context, NotificationsBroadcastReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", advance)
                .putExtra("rooms", rooms_str);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, (className + "_notification").hashCode(),
                i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }
}