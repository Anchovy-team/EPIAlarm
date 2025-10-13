package anchovy.team.epialarm;

import anchovy.team.epialarm.utils.NumberPickerHelper;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class AlarmFragment extends Fragment {

    private UserSession session;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private RadioGroup modeRadioGroup;
    private RadioButton radioAlarm;
    private RadioButton radioReminder;
    private int alarmAdvanceMinutes;
    private int reminderAdvanceMinutes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = UserSession.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        modeRadioGroup = view.findViewById(R.id.modeRadioGroup);
        radioAlarm = view.findViewById(R.id.radioAlarm);
        radioReminder = view.findViewById(R.id.radioReminder);
        ListView todayEventsList = view.findViewById(R.id.todayEventsList);

        NumberPickerHelper.configureHourPicker(hourPicker);
        NumberPickerHelper.configureMinutePicker(minutePicker);

        alarmAdvanceMinutes = session.getAdvanceMinutesAlarm();
        reminderAdvanceMinutes = session.getAdvanceMinutesReminder();

        updatePickersFromAdvance(alarmAdvanceMinutes);

        modeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioAlarm) {
                updatePickersFromAdvance(alarmAdvanceMinutes);
            } else if (checkedId == R.id.radioReminder) {
                updatePickersFromAdvance(reminderAdvanceMinutes);
            }
        });

        Button saveSettingsButton = view.findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(v -> {
            int totalMinutes = hourPicker.getValue() * 60 + minutePicker.getValue();

            if (radioAlarm.isChecked()) {
                alarmAdvanceMinutes = totalMinutes;
                session.setAdvanceMinutesAlarm(totalMinutes);
            } else {
                reminderAdvanceMinutes = totalMinutes;
                session.setAdvanceMinutesReminder(totalMinutes);
            }
            //TODO: auto set
            //setAlarm(...);
            setAlarm("2025-10-13T20:00:00.0Z", "JS 1");
            setNotification("2025-10-13T20:00:00.0Z", "JS 2 PRO");
            /*for (reservation : other_reservations_today)
                setNotification(reservation.time, reservation.name);*/
            //TODO: todayEventsList
            //TODO: userSession clears after closing the app
        });

        return view;
    }

    private void updatePickersFromAdvance(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        hourPicker.setValue(hours);
        minutePicker.setValue(minutes);
    }

    public void setAlarm(String startClass, String className) {
        Context ctx = requireContext();

        Duration delay = Duration.between(Instant.now(), Instant.parse(startClass)
                .minus(session.getAdvanceMinutesAlarm(), ChronoUnit.MINUTES));
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(delay.isNegative() ? Duration.ZERO : delay)
                .setInputData(new Data.Builder().putString("className", className).build())
                .build();

        WorkManager.getInstance(ctx).enqueueUniqueWork("setAlarm", ExistingWorkPolicy.REPLACE, req);
    }

    public void setNotification(String startClass, String className) {
        Context ctx = requireContext();
        int advance = session.getAdvanceMinutesReminder();

        long triggerTime = Instant.parse(startClass)
                .atZone(ZoneId.of("Europe/Paris"))
                .minusMinutes(advance)
                .toInstant()
                .toEpochMilli();

        Intent intent = new Intent(getContext(), NotificationsBroadcastReceiver.class);
        intent.putExtra("className", className);
        intent.putExtra("advance", advance);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx, 123, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}