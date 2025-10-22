package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Reservation;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ScheduledListFragment extends Fragment {

    private ListView scheduledList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scheduled_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scheduledList = view.findViewById(R.id.scheduledList);
        loadScheduled();
    }

    private void loadScheduled() {
        ZoneId parisZone = ZoneId.of("Europe/Paris");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        ScheduleRepository.getInstance()
                .fetchReservations(requireContext())
                .thenAccept(reservations -> requireActivity().runOnUiThread(() -> {

                    List<Map<String, String>> data = new ArrayList<>();

                    if (reservations == null || reservations.isEmpty()) {
                        setAdapter(data);
                        Log.d("test", "empty");
                        return;
                    }

                    LocalDate today = LocalDate.now(parisZone);
                    LocalDate tomorrow = today.plusDays(1);
                    boolean includeTomorrow = LocalTime.now(parisZone).isAfter(LocalTime.NOON);

                    UserSession session = UserSession.getInstance(requireContext());
                    Set<LocalDate> alarmAssigned = new HashSet<>();

                    List<Reservation> upcoming = reservations.stream()
                            .sorted(Comparator.comparing(Reservation::getStartDate))
                            .collect(Collectors.toList());

                    for (Reservation r : upcoming) {
                        LocalDate d = r.getStartDate().toLocalDate();

                        if (!d.equals(today) && !(includeTomorrow && d.equals(tomorrow))) {
                            continue;
                        }

                        var startParis = r.getStartDate()
                                .atZone(ZoneId.of("UTC"))
                                .withZoneSameInstant(parisZone);

                        boolean isAlarm = !alarmAssigned.contains(d);
                        if (isAlarm) {
                            alarmAssigned.add(d);
                        }

                        int advance = isAlarm
                                ? session.getAdvanceMinutesAlarm()
                                : session.getAdvanceMinutesReminder();
                        if (advance <= 0) {
                            continue;
                        }

                        var triggerParis = startParis.minusMinutes(advance);
                        if (triggerParis.toInstant().toEpochMilli() < System.currentTimeMillis()) {
                            continue;
                        }

                        String triggerTime = triggerParis.toLocalTime().format(fmt);
                        String dayLabel = d.equals(today) ? "Today" : "Tomorrow";

                        Map<String, String> row = new HashMap<>();
                        row.put("title", r.getName());
                        row.put("time", dayLabel + " • " + triggerTime);
                        row.put("type", isAlarm ? "Alarm" : "Reminder");
                        data.add(row);
                    }

                    setAdapter(data);
                }));
    }

    private void setAdapter(List<Map<String, String>> data) {
        SimpleAdapter adapter = new SimpleAdapter(
                requireContext(),
                data,
                R.layout.item_today_event,
                new String[]{"title", "time", "type"},
                new int[]{R.id.eventTitle, R.id.eventTime, R.id.eventType}
        );
        scheduledList.setAdapter(adapter);
    }
}