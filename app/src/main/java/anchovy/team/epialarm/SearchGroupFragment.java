package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Group;
import anchovy.team.epialarm.zeus.services.GroupsService;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchGroupFragment extends DialogFragment {
    private List<Group> allGroups;
    private List<String> groupNames;
    private ArrayAdapter<String> adapter;
    private final List<String> filteredGroups = new ArrayList<>();
    private final ZeusApiClient clientService = new ZeusApiClient();
    private UserSession session;
    private Context context;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireContext();
        View view = inflater.inflate(R.layout.fragment_search_group, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_group_hint));

        session = UserSession.getInstance(context);
        GroupsViewModel groupsViewModel = new ViewModelProvider(requireActivity()).get(
                GroupsViewModel.class);

        if (groupsViewModel.hasCachedGroups()) {
            allGroups = groupsViewModel.getCachedGroups();
            updateGroupListView(view);
        } else {
            clientService.authenticate(session.getToken()).thenAccept(authToken ->
                    fetchAndCacheGroups(view, groupsViewModel));
        }

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
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8),
                    (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5)
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void updateGroupListView(View view) {

        groupNames = allGroups.stream()
                .map(Group::getName)
                .collect(Collectors.toList());

        filteredGroups.clear();
        filteredGroups.addAll(groupNames);

        ListView listView = view.findViewById(R.id.groupListView);

        if (adapter == null) {
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                    filteredGroups);
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
                TimetableViewModel viewModel = new ViewModelProvider(requireActivity()).get(
                        TimetableViewModel.class);
                viewModel.reservations = null;
                viewModel.groupedReservations.clear();
                session.setChosenType("group");
                session.setGroupId(selectedGroup.getId());
                session.setGroupName(selectedGroupName);
                getParentFragmentManager().setFragmentResult("closed", new Bundle());
                dismiss();
            }
        });
    }

    private void fetchAndCacheGroups(View view, GroupsViewModel groupsViewModel) {
        GroupsService groupsService = new GroupsService(clientService);

        groupsService.getAllGroups().thenAccept(groups -> {
            allGroups = groups;
            groupsViewModel.setCachedGroups(groups);
            requireActivity().runOnUiThread(() -> updateGroupListView(view));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}