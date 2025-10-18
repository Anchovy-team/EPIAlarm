package anchovy.team.epialarm;

import anchovy.team.epialarm.utils.NumberPickerHelper;
import anchovy.team.epialarm.zeus.models.Reservation;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.time.Duration;
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
                boolean permissionType = radioAlarm.isChecked();
                Context context = requireContext();
                if (isGranted) {
                    if(permissionType)
                        Toast.makeText(context, "Overlay permission is granted, now you can Save Settings", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, "Notification permission is granted,  now you can Save Settings", Toast.LENGTH_LONG).show();
                }
                else {
                    if (permissionType)
                        Toast.makeText(context, "Overlay permission is denied, you will not have alarms", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, "Notification permission is denied, you will not recieve notifications", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context context = requireContext();
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
        session = UserSession.getInstance();
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

        //setAlarm("2025-10-13T22:00:00.0Z", "JS 1");
        //setNotification("2025-10-13T22:00:00.0Z", "JS 2 PRO");

        v.findViewById(R.id.saveSettingsButton).setOnClickListener(btn -> onSaveClicked());
        v.findViewById(R.id.openAlarmsButton).setOnClickListener(view -> openScheduledList());
        return v;
    }

    private void onSaveClicked() {
        int totalMinutes = hourPicker.getValue() * 60 + minutePicker.getValue();
        boolean alarmMode = radioAlarm.isChecked();
        Context context = requireContext();
        if (alarmMode) {
            if(Settings.canDrawOverlays(context)) {
                session.setAdvanceMinutesAlarm(totalMinutes);
            }
            else {
                new AlertDialog.Builder(context)
                        .setTitle("Permission Required")
                        .setMessage("To show floating windows, please allow overlay permission in settings.")
                        .setPositiveButton("Allow", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:anchovy.team.epialarm"));
                            startActivityForResult(intent, 1001);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .show();
            }
        } else {
            if(ContextCompat.checkSelfPermission(context,  android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                session.setAdvanceMinutesReminder(totalMinutes);
            }else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(context, "Notifications can not be sent, because you denied notification request", Toast.LENGTH_LONG).show();
            }
            else{
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }

        }

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

        if(Settings.canDrawOverlays(requireContext())) setAlarm(first.getStartDate().atZone(zone).toInstant().toString(), first.getName());
        if(ContextCompat.checkSelfPermission(requireContext(),  android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            todayList.stream().skip(1)
                    .forEach(r -> setNotification(
                            r.getStartDate().atZone(zone).toInstant().toString(),
                            r.getName()));
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

    private void setAlarm(String startTimeIso, String className) {
        Context ctx = requireContext();
        int advance = session.getAdvanceMinutesAlarm();

        Duration delay = Duration.between(Instant.now(), Instant.parse(startTimeIso).minus(advance,
                ChronoUnit.MINUTES));

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(delay.isNegative() ? Duration.ZERO : delay)
                .setInputData(new Data.Builder()
                        .putString("className", className)
                        .putInt("advance", advance)
                        .build())
                .build();

        WorkManager.getInstance(ctx).enqueueUniqueWork("setAlarm", ExistingWorkPolicy.REPLACE, req);
    }

    private void setNotification(String startTimeIso, String className) {
        Context ctx = requireContext();
        int advance = session.getAdvanceMinutesReminder();

        long triggerAt = Instant.parse(startTimeIso).atZone(ZoneId.of("Europe/Paris"))
                .minusMinutes(advance).toInstant().toEpochMilli();

        Intent i = new Intent(ctx, NotificationsBroadcastReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", advance);

        PendingIntent pi = PendingIntent.getBroadcast(ctx, 123, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        ((AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE))
                .setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }
}