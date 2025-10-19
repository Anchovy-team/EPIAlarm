package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Reservation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        TimetableViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(TimetableViewModel.class);

        ZoneId zone = ZoneId.of("Europe/Paris");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        List<Map<String, String>> data = new ArrayList<>();

        if (viewModel.reservations != null && !viewModel.reservations.isEmpty()) {
            LocalDate today = LocalDate.now(zone);

            List<Reservation> todayList = new ArrayList<>();
            for (Reservation r : viewModel.reservations) {
                if (r.getStartDate().toLocalDate().equals(today)) {
                    todayList.add(r);
                }
            }

            todayList.sort(Comparator.comparing(Reservation::getStartDate));

            if (!todayList.isEmpty()) {
                UserSession session = UserSession.getInstance(requireContext());

                for (int i = 0; i < todayList.size(); i++) {
                    Reservation r = todayList.get(i);
                    boolean isAlarm = i == 0;

                    int advance = isAlarm
                            ? session.getAdvanceMinutesAlarm()
                            : session.getAdvanceMinutesReminder();
                    if (advance <= 0) {
                        continue;
                    }

                    var startZoned = r.getStartDate().atZone(zone);
                    var triggerZoned = startZoned.minusMinutes(advance);
                    if (triggerZoned.toInstant().toEpochMilli() < System.currentTimeMillis()) {
                        continue;
                    }

                    String triggerTime = triggerZoned.toLocalTime().format(fmt);

                    Map<String, String> row = new HashMap<>();
                    row.put("title", r.getName());
                    row.put("time", triggerTime);
                    row.put("type", isAlarm ? "Alarm" : "Reminder");
                    data.add(row);
                }
            }
        }

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