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
import android.widget.NumberPicker;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class AlarmFragment extends Fragment {

    private UserSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = UserSession.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        NumberPicker hourPicker = view.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = view.findViewById(R.id.minutePicker);
        NumberPickerHelper.configureHourPicker(hourPicker);
        NumberPickerHelper.configureMinutePicker(minutePicker);

        Button setAlarmButton = view.findViewById(R.id.SetAlarmButton);
        setAlarmButton.setOnClickListener(v -> {
            int advance = hourPicker.getValue() * 60 + minutePicker.getValue();
            SwitchCompat vibration = view.findViewById(R.id.vibrateSwitch);
            session.setAdvanceMinutes(advance);
             /*for (reservation : reservations_today) {
                setAlarmNotification(reservation.time, reservation.name, advance,
                        "alarm", vibration.isChecked());
            }*/
            setAlarmNotification("2025-05-28T10:00:00.0Z", "Advanced IAM",
                    advance, "alarm", vibration.isChecked());
        });

        return view;
    }

    public void setAlarmNotification(String startClass, String className, Integer advanceMinutes,
                                     String type, Boolean vibration) {
        Context context = requireContext();

        Instant instant = Instant.parse(startClass);
        ZoneId parisZone = ZoneId.of("Europe/Paris");
        ZonedDateTime eventTimeParis = instant.atZone(parisZone);
        ZonedDateTime notifyTimeParis = eventTimeParis.minusMinutes(advanceMinutes);
        ZonedDateTime nowParis = ZonedDateTime.now(parisZone);
        long delayMillis = Duration.between(nowParis, notifyTimeParis).toMillis();
        long triggerTimeMillis = System.currentTimeMillis() + delayMillis;

        Intent intent = new Intent(context, NotificationsBroadcastReceiver.class);
        intent.putExtra("alarmOrNotification", type);
        intent.putExtra("className", className);
        intent.putExtra("advanceMinutes", advanceMinutes);
        intent.putExtra("vibration", vibration);
        intent.putExtra("triggerTime", triggerTimeMillis);
        intent.putExtra("action", type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                123,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMillis, pendingIntent);
    }
}