package anchovy.team.epialarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Group;
import anchovy.team.epialarm.zeus.models.Teacher;
import anchovy.team.epialarm.zeus.services.GroupsService;
import anchovy.team.epialarm.zeus.services.TeacherService;

public class SearchTeacherFragment extends DialogFragment {
    private final List<String> filteredTeachers = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private final ZeusApiClient clientService = new ZeusApiClient();
    private TeacherService teacherService;
    private List<Teacher> allTeachers;
    private List<String> teacherNames = new ArrayList<>();;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_teacher, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search teacher...");
        ListView listView = view.findViewById(R.id.groupListView);

        SharedPreferences prefs = requireContext().getSharedPreferences("prefs",
                Context.MODE_PRIVATE);
        String token = prefs.getString("user_token", null);

        clientService.authenticate(token).thenAccept(authToken -> {
            teacherService = new TeacherService(clientService);

            teacherService.getAllTeachers().thenAccept(teachers -> {
                allTeachers = teachers;

                teacherNames = teachers.stream()
                        .map(t -> t.getFirstname() + " " + t.getName())
                        .collect(Collectors.toList());

                filteredTeachers.clear();
                filteredTeachers.addAll(teacherNames);

                requireActivity().runOnUiThread(() -> {
                    if (adapter == null) {
                        adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_list_item_1, filteredTeachers);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                    listView.setOnItemClickListener((parent, v, position, id) -> {
                        String selectedTeacherName = filteredTeachers.get(position);
                        Teacher selectedTeacher = allTeachers.stream()
                                .filter(t -> (t.getFirstname() + " " + t.getName()).equals(selectedTeacherName))
                                .findFirst()
                                .orElse(null);

                        if (selectedTeacher != null) {
                            TimetableViewModel viewModel = new ViewModelProvider(requireActivity()).get(TimetableViewModel.class);
                            viewModel.reservations = null;
                            viewModel.groupedReservations.clear();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putLong("groupId", -1);
                            editor.putString("groupName", filteredTeachers.get(position));
                            editor.putLong("teacherId", selectedTeacher.getId());
                            editor.apply();
                            getParentFragmentManager().setFragmentResult("closed", new Bundle());
                            dismiss();
                        }
                    });
                });

            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredTeachers.clear();
                if (newText.isEmpty()) {
                    filteredTeachers.addAll(teacherNames);
                } else {
                    String lowerNewText = newText.toLowerCase();
                    for (String teacher : teacherNames) {
                        if (teacher.toLowerCase().contains(lowerNewText)) {
                            filteredTeachers.add(teacher);
                        }
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.8),
                    (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.5)
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}