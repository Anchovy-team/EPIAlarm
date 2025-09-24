package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Group;
import anchovy.team.epialarm.zeus.models.Reservation;
import anchovy.team.epialarm.zeus.models.Room;
import anchovy.team.epialarm.zeus.models.Teacher;
import anchovy.team.epialarm.zeus.services.ReservationService;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TimetableFragment extends Fragment {

    Map<LocalDate, List<Reservation>> reservationsGrouped;
    ZeusApiClient clientService = new ZeusApiClient();
    ReservationService reservationService;
    List<Reservation> reservations;
    private TimetableViewModel viewModel;
    private TextView emptyMessage;
    private ListView listView;
    private UserSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TimetableViewModel.class);
        session = UserSession.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timetable, container, false);

        listView = view.findViewById(R.id.LessonsListView);
        listView.setClickable(true);
        emptyMessage = view.findViewById(R.id.empty_message);

        if (viewModel.reservations != null && !viewModel.reservations.isEmpty()) {
            reservationsGrouped = viewModel.groupedReservations;
            reservations = viewModel.reservations;
            loadData(view);
            return view;
        }

        if (session.getToken() == null) {
            emptyMessage.setText("Nothing to see here, you are not authorized");
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return view;
        } else if (session.getChosenType() == null) {
            emptyMessage.setText("You have to choose a group or a teacher!");
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return view;
        } else {
            emptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        try {
            clientService.authenticate(session.getToken()).thenAccept(token1 -> {}).join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        reservationService = new ReservationService(clientService);
        reservationsGrouped = new TreeMap<>();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime oneWeekForward = LocalDateTime.now().plusWeeks(2);

        if ("group".equals(session.getChosenType())) {

            List<Long> groups = new ArrayList<>();
            groups.add(session.getGroupId());

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

        } else if ("teacher".equals(session.getChosenType())) {

            List<Long> teachers = new ArrayList<>();
            teachers.add(session.getTeacherId());

            reservationService.getReservationsByFilter(new ArrayList<>(), new ArrayList<>(),
                    teachers, today, oneWeekForward).thenAccept(reservations1 -> {
                        reservations = reservations1;
                        requireActivity().runOnUiThread(() -> {
                            setReservations(reservations, view);
                        });
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemRead = parent.getItemAtPosition(position);
                if (itemRead instanceof DateHeaderItem) {
                    return;
                }
                ReservationItem reservationItem = (ReservationItem) parent.getItemAtPosition(
                        position);
                openClassInfo(reservationItem.getReservation());
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
            if (listView != null) {
                listView.setAdapter(customBaseAdapter);
            }
            if (customBaseAdapter.isEmpty()) {
                emptyMessage.setText("No classes found :(");
                emptyMessage.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }
    }

    public void setReservations(List<Reservation> reservations, View view) {
        if (reservations == null) {
            return;
        }

        for (Reservation r : reservations) {
            System.out.println(r.getName());
            ZoneId parisZone = ZoneId.of("Europe/Paris");
            ZonedDateTime parisDateTime = r.getStartDate().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(parisZone);
            LocalDate date = parisDateTime.toLocalDate();
            r.setStartDate(parisDateTime.toLocalDateTime());
            r.setEndDate(r.getEndDate().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(parisZone).toLocalDateTime());
            reservationsGrouped.computeIfAbsent(date, k -> new ArrayList<>()).add(r);
        }

        viewModel.reservations = reservations;
        viewModel.groupedReservations = reservationsGrouped;

        loadData(view);
    }

    private void openClassInfo(Reservation item) {
        Bundle args = new Bundle();
        args.putString("className", item.getName());
        args.putString("activityType", item.getTypeName());
        LocalDateTime start = item.getStartDate();
        LocalDateTime end = item.getEndDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String result = dateFormatter.format(start) + " - " + timeFormatter.format(end);
        args.putString("classHours", result);

        Teacher[] teachers = item.getTeachers();
        String professorName = Arrays.stream(teachers)
                .map(Teacher::getFullName)
                .collect(Collectors.joining(", "));
        args.putString("professorName", professorName);

        Room[] rooms = item.getRooms();
        String classroom = Arrays.stream(rooms)
                .map(Room::getName)
                .collect(Collectors.joining(", "));
        args.putString("classroom", classroom);

        Group[] groups = item.getGroups();
        String groupNames = Arrays.stream(groups)
                .map(Group::getName)
                .collect(Collectors.joining(", "));
        args.putString("group", groupNames);

        ClassInfoFragment dialog = new ClassInfoFragment();
        dialog.setArguments(args);
        dialog.show(getParentFragmentManager(), "classDialog");
    }
}