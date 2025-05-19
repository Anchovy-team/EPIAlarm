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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchGroupFragment extends DialogFragment {

    private List<String> allGroups = Arrays.asList("Lyon M1", "PREPA PARIS",
            "Toulouse M2", "PREPA Lyon", "ME Paris", "KB L1", "KB L2", "KB L3");
    private List<String> filteredGroups = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search_group, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search group...");
        ListView listView = view.findViewById(R.id.groupListView);
        filteredGroups.addAll(allGroups);
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                filteredGroups);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredGroups.clear();
                for (String group : allGroups) {
                    if (group.toLowerCase().contains(newText.toLowerCase())) {
                        filteredGroups.add(group);
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedGroup = filteredGroups.get(position);
            SharedPreferences prefs = requireContext().getSharedPreferences("prefs",
                    Context.MODE_PRIVATE);
            prefs.edit().putString("user_group", selectedGroup).apply();
            dismiss();
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