package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Group;
import anchovy.team.epialarm.zeus.services.GroupsService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchGroupFragment extends DialogFragment {

    private List<Group> allGroups;
    private List<String> groupNames;
    private ArrayAdapter<String> adapter;
    private final List<String> allGroupsTest = Arrays.asList("Lyon M1", "PREPA PARIS",
            "Toulouse M2", "PREPA Lyon", "ME Paris", "KB L1", "KB L2", "KB L3");
    private final List<String> filteredGroups = new ArrayList<>();
    private final ZeusApiClient clientService = new ZeusApiClient();
    private GroupsService groupsService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs = requireContext().getSharedPreferences("prefs",
                Context.MODE_PRIVATE);
        String token = prefs.getString("user_token", null);

        View view = inflater.inflate(R.layout.fragment_search_group, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search group...");
        ListView listView = view.findViewById(R.id.groupListView);

        clientService.authenticate(token).thenAccept(authToken -> {
            groupsService = new GroupsService(clientService);

            groupsService.getAllGroups().thenAccept(groups -> {
                allGroups = groups;

                groupNames = groups.stream()
                        .map(Group::getName)
                        .collect(Collectors.toList());

                filteredGroups.clear();
                filteredGroups.addAll(groupNames);

                requireActivity().runOnUiThread(() -> {
                    if (adapter == null) {
                        adapter = new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_list_item_1, filteredGroups);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                    listView.setOnItemClickListener((parent, v, position, id) -> {
                        String selectedGroupName = filteredGroups.get(position);
                        Group selectedGroup = allGroups.stream()
                                .filter(g -> g.getName().equals(selectedGroupName))
                                .findFirst()
                                .orElse(null);

                        if (selectedGroup != null) {
                            int groupId = selectedGroup.getId();
                            //System.out.println("send group Id: " + groupId);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putLong("groupId", groupId);
                            editor.apply();
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
                filteredGroups.clear();
                if (newText.isEmpty()) {
                    filteredGroups.addAll(groupNames);
                } else {
                    String lowerNewText = newText.toLowerCase();
                    for (String group : groupNames) {
                        if (group.toLowerCase().contains(lowerNewText)) {
                            filteredGroups.add(group);
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