package anchovy.team.epialarm;

import anchovy.team.epialarm.utils.NumberPickerHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmFragment extends Fragment {

    private UserSession session;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private RadioButton radioAlarm;
    private Context context;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                    boolean permissionType = radioAlarm.isChecked();
                        if (isGranted) {
                            if (permissionType) {
                                Toast.makeText(context, "Overlay permission is granted, "
                                        + "now you can Save Settings", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Notification permission is granted,"
                                                + " now you can Save Settings",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (permissionType) {
                                Toast.makeText(context,
                                        "Overlay permission is denied, you will not have alarms",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context,
                                        "Notification permission is denied,"
                                                + " you will not receive notifications",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (Settings.canDrawOverlays(context)) {
                Toast.makeText(context, "Overlay permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Overlay permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        context = requireContext();
        session = UserSession.getInstance(context);
        scheduleDailyWork();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void onSaveClicked() {
        int totalMinutes = hourPicker.getValue() * 60 + minutePicker.getValue();
        boolean alarmMode = radioAlarm.isChecked();
        if (alarmMode) {
            if (Settings.canDrawOverlays(context)) {
                session.setAdvanceMinutesAlarm(totalMinutes);
            } else {
                new AlertDialog.Builder(context)
                    .setTitle("Permission Required")
                    .setMessage("To show alarm window, please allow overlay")
                    .setPositiveButton("Allow", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:anchovy.team.epialarm"));
                        startActivityForResult(intent, 1001);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
            }
        } else {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                session.setAdvanceMinutesReminder(totalMinutes);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(context,
                        "Notifications can not be sent, because you denied notification request",
                        Toast.LENGTH_LONG).show();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }

        }

        //setAlarm("2025-10-19T01:00:00.0Z", "Intro to Javascript");
        //setNotification("2025-10-19T01:00:00.0Z", "IAM Fundamentals");
        SchedulePlanner.scheduleForToday(context);
        LocalTime now = LocalTime.now(ZoneId.of("Europe/Paris"));
        if (now.isAfter(LocalTime.NOON)) {
            SchedulePlanner.scheduleForTomorrow(context);
        }
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

    private void scheduleDailyWork() {
        long now = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        if (cal.getTimeInMillis() < now) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        long initialDelayMs = cal.getTimeInMillis() - now;

        PeriodicWorkRequest daily = new PeriodicWorkRequest.Builder(
                AlarmWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
                .addTag("DailyScheduler")
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("DailySchedulerWork",
                ExistingPeriodicWorkPolicy.UPDATE, daily);
    }
}