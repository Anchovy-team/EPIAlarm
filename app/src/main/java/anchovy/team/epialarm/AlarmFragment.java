package anchovy.team.epialarm;

import anchovy.team.epialarm.utils.NumberPickerHelper;
import anchovy.team.epialarm.zeus.models.Reservation;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class AlarmFragment extends Fragment {

    private UserSession session;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private RadioButton radioAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = UserSession.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);

        hourPicker = v.findViewById(R.id.hourPicker);
        minutePicker = v.findViewById(R.id.minutePicker);
        radioAlarm = v.findViewById(R.id.radioAlarm);

        NumberPickerHelper.configureHourPicker(hourPicker);
        NumberPickerHelper.configureMinutePicker(minutePicker);

        updatePickersFromAdvance(session.getAdvanceMinutesAlarm());

        RadioGroup modeGroup = v.findViewById(R.id.modeRadioGroup);
        modeGroup.setOnCheckedChangeListener((group, id) -> {
            int mins = id == R.id.radioAlarm
                    ? session.getAdvanceMinutesAlarm()
                    : session.getAdvanceMinutesReminder();
            updatePickersFromAdvance(mins);
        });

        v.findViewById(R.id.saveSettingsButton).setOnClickListener(btn -> onSaveClicked());
        v.findViewById(R.id.openAlarmsButton).setOnClickListener(view -> openScheduledList());
        return v;
    }

    private void onSaveClicked() {
        int totalMinutes = hourPicker.getValue() * 60 + minutePicker.getValue();
        boolean alarmMode = radioAlarm.isChecked();

        if (alarmMode) {
            session.setAdvanceMinutesAlarm(totalMinutes);
        } else {
            session.setAdvanceMinutesReminder(totalMinutes);
        }

        //setAlarm("2025-10-19T01:00:00.0Z", "Intro to Javascript");
        //setNotification("2025-10-19T01:00:00.0Z", "IAM Fundamentals");
        scheduleTodayEvents();
    }

    private void scheduleTodayEvents() {
        var viewModel = new ViewModelProvider(requireActivity()).get(TimetableViewModel.class);
        if (viewModel.reservations == null || viewModel.reservations.isEmpty()) {
            return;
        }

        ZoneId zone = ZoneId.of("Europe/Paris");
        LocalDate today = LocalDate.now(zone);

        List<Reservation> todayList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            todayList = viewModel.reservations.stream()
                    .filter(r -> r.getStartDate().toLocalDate().equals(today))
                    .sorted(Comparator.comparing(Reservation::getStartDate))
                    .toList();
        }

        if (todayList == null || todayList.isEmpty()) {
            return;
        }

        Reservation first = todayList.get(0);
        setAlarm(first.getStartDate().atZone(zone).toInstant().toString(), first.getName());

        todayList.stream().skip(1)
                .forEach(r -> setNotification(
                        r.getStartDate().atZone(zone).toInstant().toString(),
                        r.getName()));
    }

    private void openScheduledList() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new ScheduledListFragment())
                .addToBackStack(null)
                .commit();
    }

    private void updatePickersFromAdvance(int totalMinutes) {
        hourPicker.setValue(totalMinutes / 60);
        minutePicker.setValue(totalMinutes % 60);
    }

    private void setAlarm(String startTimeIso, String className) {
        Context ctx = requireContext();
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

        Intent i = new Intent(ctx, AlarmReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", advance);

        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, (className + "_alarm").hashCode(),
                i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }

    private void setNotification(String startTimeIso, String className) {
        Context ctx = requireContext();
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

        Intent i = new Intent(ctx, NotificationsBroadcastReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", advance);

        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, (className + "_notification").hashCode(),
                i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }
}