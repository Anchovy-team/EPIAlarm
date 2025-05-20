package anchovy.team.epialarm;

import static android.content.Context.ALARM_SERVICE;

import anchovy.team.epialarm.utils.NumberPickerHelper;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class AlarmFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        NumberPicker hourPicker = view.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = view.findViewById(R.id.minutePicker);
        NumberPickerHelper.configureHourPicker(hourPicker);
        NumberPickerHelper.configureMinutePicker(minutePicker);

        Button setAlarmButton = view.findViewById(R.id.SetAlarmButton);
        setAlarmButton.setOnClickListener(v -> {
            final int advance = hourPicker.getValue() * 60 + minutePicker.getValue();
            SwitchCompat vibration = view.findViewById(R.id.vibrateSwitch);
            setAlarmNotification("2025-04-16T13:00:00.890Z", "Advanced Python",
                    advance, "alarm", vibration.isChecked());
            //TODO: replace these test values with the values from API
        });

        return view;
    }

    public void setAlarmNotification(String startClass, String className, Integer advanceMinutes,
                                     String type, Boolean vibration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent intent = new Intent(requireContext(), NotificationsBroadcastReceiver.class);
            intent.putExtra("alarmOrNotification", type);
            intent.putExtra("className", className);
            intent.putExtra("advanceMinutes", advanceMinutes);
            intent.putExtra("vibration", vibration);
            Context context = requireContext();
            context.sendBroadcast(intent);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    UUID.randomUUID().toString().hashCode(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

            Instant instant = Instant.parse(startClass);
            ZoneId parisZone = ZoneId.of("Europe/Paris");
            ZonedDateTime eventTimeParis = instant.atZone(parisZone);
            ZonedDateTime notifyTimeParis = eventTimeParis.minusMinutes(advanceMinutes);
            ZonedDateTime nowParis = ZonedDateTime.now(parisZone);
            long delayMillis = Duration.between(nowParis, notifyTimeParis).toMillis();
            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(
                    ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + delayMillis, pendingIntent);
        }
    }
}