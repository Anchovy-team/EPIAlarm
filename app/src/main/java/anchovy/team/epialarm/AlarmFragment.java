package anchovy.team.epialarm;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import anchovy.team.epialarm.utils.NumberPickerHelper;


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
            setAlarmNotification("2025-09-22T23:50:00.0Z", "Advanced IAM",
                    advance, "alarm", vibration.isChecked());
        });

        return view;
    }

    public void setAlarmNotification(String startClass, String className, long advanceMinutes,
                                     String type, Boolean vibration) {
        Context context = requireContext();

        Instant classStartTime = Instant.parse(startClass);
        Instant notifyTime = classStartTime.minus(advanceMinutes, ChronoUnit.MINUTES);

        OneTimeWorkRequest alarmWorkRequest = //set request
                new OneTimeWorkRequest.Builder(AlarmWorker.class).setInitialDelay(Duration.between(Instant.now(), notifyTime)) //set delay
                        .setInputData(
                                new Data.Builder()
                                        .putString("className", className)//set vars
                                        .putString("type", type)
                                        .putBoolean("vibration", vibration)
                                        .build()
                        )
                        .build();
        WorkManager.getInstance(context)
                .enqueueUniqueWork("setAlarm", ExistingWorkPolicy.REPLACE, alarmWorkRequest);//execute
    }
}