package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Group;
import anchovy.team.epialarm.zeus.models.Reservation;
import anchovy.team.epialarm.zeus.models.Room;
import anchovy.team.epialarm.zeus.models.Teacher;
import anchovy.team.epialarm.zeus.services.ReservationService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimetableFragment extends Fragment {

    Map<LocalDate, List<Reservation>> reservationsGrouped;
    ZeusApiClient clientService = new ZeusApiClient();
    ReservationService reservationService;
    List<Reservation> reservations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        ListView listView = view.findViewById(R.id.LessonsListView);
        TextView emptyMessage = view.findViewById(R.id.empty_message);

        SharedPreferences prefs = requireContext().getSharedPreferences("prefs",
                Context.MODE_PRIVATE);
        String token = prefs.getString("user_token", null);
        long groupId = prefs.getLong("groupId", -1);

        if (token == null) {
            emptyMessage.setText("Nothing to see here, you are not authorized");
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return view;
        } else if (groupId == -1) {
            emptyMessage.setText("You have to choose a group!");
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return view;
        } else {
            emptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        try {
            clientService.authenticate(token).thenAccept(token1 -> {}).join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        reservationService = new ReservationService(clientService);
        reservationsGrouped = new TreeMap<>();
        List<Long> groups = new ArrayList<>();
        groups.add(groupId);
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime oneWeekForward = LocalDateTime.now().plusWeeks(2);

        reservationService.getReservationsByFilter(groups, new ArrayList<>(), new ArrayList<>(),
                today, oneWeekForward).thenAccept(reservations1 -> {
                    reservations = reservations1;
                    requireActivity().runOnUiThread(() -> {
                        setReservations(reservations, view);
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemRead = parent.getItemAtPosition(position);
                if (itemRead instanceof DateHeaderItem) {
                    return;
                }
                ReservationItem reservationItem = (ReservationItem) parent.getItemAtPosition(
                        position);
                Reservation item = reservationItem.getReservation();

                Bundle args = new Bundle();
                args.putString("className", item.getName());
                args.putString("activityType", item.getTypeName());
                LocalDateTime start = item.getStartDate();
                LocalDateTime end = item.getEndDate();
                DateTimeFormatter dateFormatter = DateTimeFormatter
                        .ofPattern("dd/MM/yyyy HH:mm");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                String result = dateFormatter.format(start) + " - " + timeFormatter.format(end);
                args.putString("classHours", result);

                Teacher[] teachers = item.getTeachers();
                StringBuilder builder1 = new StringBuilder();
                for (int i = 0; i < teachers.length; i++) {
                    builder1.append(teachers[i].getFirstname()).append(" ").append(teachers[i]
                            .getName());
                    if (i < teachers.length - 1) {
                        builder1.append(", ");
                    }
                }
                args.putString("professorName", builder1.toString());

                Room[] rooms = item.getRooms();
                StringBuilder builder2 = new StringBuilder();
                for (int i = 0; i < rooms.length; i++) {
                    builder2.append(rooms[i].getName());
                    if (i < rooms.length - 1) {
                        builder2.append(", ");
                    }
                }
                args.putString("classroom", builder2.toString());

                Group[] groups = item.getGroups();
                StringBuilder builder3 = new StringBuilder();
                for (int i = 0; i < groups.length; i++) {
                    builder3.append(groups[i].getName());
                    if (i < groups.length - 1) {
                        builder3.append(", ");
                    }
                }
                args.putString("group", builder3.toString());

                ClassInfoFragment dialog = new ClassInfoFragment();
                dialog.setArguments(args);
                dialog.show(getParentFragmentManager(), "classDialog");
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadData(View view) {
        if (isAdded() && getContext() != null) {
            CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(requireContext(),
                    reservationsGrouped);
            ListView listView = view.findViewById(R.id.LessonsListView);
            listView.setAdapter(customBaseAdapter);
        }
    }

    public void setReservations(List<Reservation> reservations, View view) {
        if (reservations == null) {
            return;
        }
        for (Reservation r : reservations) {
            ZoneId parisZone = ZoneId.of("Europe/Paris");
            ZonedDateTime parisDateTime = r.getStartDate().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(parisZone);
            LocalDate date = parisDateTime.toLocalDate();
            r.setStartDate(parisDateTime.toLocalDateTime());
            r.setEndDate(r.getEndDate().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(parisZone).toLocalDateTime());
            reservationsGrouped.computeIfAbsent(date, k -> new ArrayList<>()).add(r);
        }
        loadData(view);
    }
}