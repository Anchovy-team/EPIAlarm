package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Group;
import anchovy.team.epialarm.zeus.models.Reservation;
import anchovy.team.epialarm.zeus.models.Room;
import anchovy.team.epialarm.zeus.models.Teacher;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private Map<LocalDate, List<Reservation>> reservationsGrouped;
    private TimetableViewModel viewModel;
    private TextView emptyMessage;
    private ListView listView;
    private UserSession session;
    private ScheduleRepository scheduleRepository;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = requireContext();
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TimetableViewModel.class);
        session = UserSession.getInstance(context);
        scheduleRepository = ScheduleRepository.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyMessage = view.findViewById(R.id.empty_message);
        listView = view.findViewById(R.id.LessonsListView);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Object itemRead = parent.getItemAtPosition(position);
            if (itemRead instanceof DateHeaderItem) {
                return;
            }
            ReservationItem reservationItem = (ReservationItem) parent.getItemAtPosition(position);
            openClassInfo(reservationItem.getReservation());
        });

        if (viewModel.reservations != null && !viewModel.reservations.isEmpty()) {
            reservationsGrouped = viewModel.groupedReservations;
            loadData();
        } else if (session.getToken() == null) {
            showMessage("Nothing to see here, you are not authorized");
        } else if (session.getChosenType() == null) {
            showMessage("You have to choose a group or a teacher!");
        } else {
            emptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            reservationsGrouped = new TreeMap<>();

            scheduleRepository.fetchReservations(context)
                    .thenAccept(res -> requireActivity().runOnUiThread(() -> setReservations(res)))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        requireActivity().runOnUiThread(() -> showMessage("Timetable fail"));
                        return null;
                    });
        }
    }

    private void showMessage(String message) {
        emptyMessage.setText(message);
        emptyMessage.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    public void loadData() {
        if (isAdded() && getContext() != null) {
            CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(context,
                    reservationsGrouped);
            if (listView != null) {
                listView.setAdapter(customBaseAdapter);
            }
            if (customBaseAdapter.isEmpty()) {
                showMessage("No classes found :(");
            }
        }
    }

    public void setReservations(List<Reservation> reservations) {
        if (reservations == null) {
            showMessage("No classes found :(");
            return;
        }

        for (Reservation r : reservations) {
            //System.out.println(r.getName());
            ZoneId parisZone = ZoneId.of("Europe/Paris");
            ZonedDateTime parisDateTime = r.getStartDate().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(parisZone);
            LocalDate date = parisDateTime.toLocalDate();
            r.setStartDate(parisDateTime.toLocalDateTime());
            r.setEndDate(r.getEndDate().atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(parisZone)
                    .toLocalDateTime());
            reservationsGrouped.computeIfAbsent(date, k -> new ArrayList<>()).add(r);
        }

        viewModel.reservations = reservations;
        viewModel.groupedReservations = reservationsGrouped;

        loadData();
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