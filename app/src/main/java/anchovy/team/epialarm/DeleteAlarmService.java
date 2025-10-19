package anchovy.team.epialarm;

import anchovy.team.epialarm.entity.AlarmData;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeleteAlarmService {
    private static final String TAG = "AlarmService";
    private static final String PREF_NAME = "alarms_pref";
    private static final String ALARMS_KEY = "alarms_list";

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public DeleteAlarmService(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public AlarmData addAlarm(String startTime, String className, int advanceMinutes,
                              boolean vibration) {
        AlarmData alarm = new AlarmData();
        alarm.setId(UUID.randomUUID().toString());
        alarm.setStartTime(startTime);
        alarm.setClassName(className);
        alarm.setAdvanceMinutes(advanceMinutes);
        alarm.setVibration(vibration);
        alarm.setActive(true);

        // Save to storage
        List<AlarmData> alarms = getAlarms();
        alarms.add(alarm);
        saveAlarms(alarms);

        // Schedule the alarm
        scheduleAlarm(alarm);
        
        return alarm;
    }

    public void updateAlarm(AlarmData alarm) {
        List<AlarmData> alarms = getAlarms();
        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).getId().equals(alarm.getId())) {
                alarms.set(i, alarm);
                break;
            }
        }
        saveAlarms(alarms);

        // Reschedule if active
        if (alarm.isActive()) {
            cancelAlarm(alarm.getId());
            scheduleAlarm(alarm);
        } else {
            cancelAlarm(alarm.getId());
        }
    }

    public void deleteAlarm(String alarmId) {
        List<AlarmData> alarms = getAlarms();
        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).getId().equals(alarmId)) {
                alarms.remove(i);
                break;
            }
        }
        saveAlarms(alarms);
        cancelAlarm(alarmId);
    }

    public void toggleAlarm(String alarmId, boolean active) {
        List<AlarmData> alarms = getAlarms();
        for (AlarmData alarm : alarms) {
            if (alarm.getId().equals(alarmId)) {
                alarm.setActive(active);
                
                if (active) {
                    scheduleAlarm(alarm);
                } else {
                    cancelAlarm(alarmId);
                }
                
                break;
            }
        }
        saveAlarms(alarms);
    }

    public List<AlarmData> getAlarms() {
        String alarmsJson = sharedPreferences.getString(ALARMS_KEY, null);
        if (alarmsJson == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<AlarmData>>(){}.getType();
        return gson.fromJson(alarmsJson, type);
    }

    private void saveAlarms(List<AlarmData> alarms) {
        String alarmsJson = gson.toJson(alarms);
        sharedPreferences.edit().putString(ALARMS_KEY, alarmsJson).apply();
    }

    public void scheduleAlarm(AlarmData alarm) {
        Intent intent = new Intent(context, NotificationsBroadcastReceiver.class);
        intent.putExtra("alarmOrNotification", "alarm");
        intent.putExtra("className", alarm.getClassName());
        intent.putExtra("advanceMinutes", alarm.getAdvanceMinutes());
        intent.putExtra("vibration", alarm.isVibration());
        intent.putExtra("alarmId", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.getId().hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Instant instant = Instant.parse(alarm.getStartTime());
        ZoneId parisZone = ZoneId.of("Europe/Paris");
        ZonedDateTime eventTimeParis = instant.atZone(parisZone);
        ZonedDateTime notifyTimeParis = eventTimeParis.minusMinutes(alarm.getAdvanceMinutes());
        ZonedDateTime nowParis = ZonedDateTime.now(parisZone);
        long delayMillis = Duration.between(nowParis, notifyTimeParis).toMillis();

        if (delayMillis > 0) {
            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + delayMillis, pendingIntent);
                // Log.d(TAG, "Alarm scheduled: " + alarm.getClassName() + " in "
                //        + alarm.getAdvanceMinutes() + " minutes");
            }
        } else {
            // Log.w(TAG, "Alarm time is in the past: " + alarm.getClassName());
        }
    }

    public void cancelAlarm(String id) {
        Intent intent = new Intent(context, NotificationsBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id.hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                    Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
                // Log.d(TAG, "Alarm canceled: " + id);
            }
        }
    }

    public void restoreAllAlarms() {
        List<AlarmData> alarms = getAlarms();
        for (AlarmData alarm : alarms) {
            if (alarm.isActive()) {
                scheduleAlarm(alarm);
            }
        }
        // Log.d(TAG, "Restored " + alarms.size() + " alarms after reboot");
    }
}