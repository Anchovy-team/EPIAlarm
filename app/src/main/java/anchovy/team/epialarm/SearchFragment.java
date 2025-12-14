package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.services.AbstractService;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class SearchFragment<T, K extends AbstractService<T>,
        E extends AbstractViewModel<T>> extends DialogFragment {
    protected final ZeusApiClient clientService = ZeusApiClient.getZeusApiClientInstance();
    protected Function<T, String> filterByString;
    protected UserSession session;
    protected ArrayAdapter<String> adapter;
    protected List<T> allItems;
    protected List<String> itemNames;
    protected Context context;
    private final List<String> filteredItems = new ArrayList<>();

    protected abstract void setSession(T selectedItem);

    protected abstract K getService();

    private E viewModel;
    private final int layoutId;
    private final int listViewId;
    private final Class<E> modelClass;

    protected SearchFragment(Function<T, String> filter, int layoutId, Class<E> modelClass,
                             int lvId) {
        this.filterByString = filter;
        this.layoutId = layoutId;
        this.modelClass = modelClass;
        this.listViewId = lvId;
        allItems = new ArrayList<T>();
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

    protected void updateListView(View view) {
        itemNames = allItems.stream()
                .map(filterByString)
                .collect(Collectors.toList());

        filteredItems.clear();
        filteredItems.addAll(itemNames);

        ListView listView = view.findViewById(listViewId);

        if (adapter == null) {
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                    filteredItems);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedItemName = filteredItems.get(position);
            T selectedItem = allItems.stream()
                    .filter(item -> filterByString.apply(item).equals(selectedItemName))
                    .findFirst()
                    .orElse(null);

            if (selectedItem != null) {
                TimetableViewModel viewModel = new ViewModelProvider(requireActivity()).get(
                        TimetableViewModel.class);
                viewModel.reservations = null;
                viewModel.groupedReservations.clear();

                setSession(selectedItem);

                getParentFragmentManager().setFragmentResult("closed", new Bundle());
                dismiss();
            }
        });
    }

    protected void fetchAndCacheItems(View view) {
        getService().getAllItems().thenAccept(items -> {
            allItems = items;
            cacheItems(items);
            requireActivity().runOnUiThread(() -> updateListView(view));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireContext();
        viewModel = new ViewModelProvider(requireActivity()).get(modelClass);
        View view = inflater.inflate(layoutId, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_group_hint));

        session = UserSession.getInstance(context);

        if (viewModel.hasCachedItems()) {
            allItems = viewModel.getCachedItems();
            updateListView(view);
        } else {
            clientService.authenticate(session.getToken())
                    .thenAccept(authToken ->
                    fetchAndCacheItems(view));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredItems.clear();
                if (newText.isEmpty()) {
                    filteredItems.addAll(itemNames);
                } else {
                    String lowerNewText = newText.toLowerCase();
                    if (itemNames != null) {
                        for (String item : itemNames) {
                            if (item.toLowerCase().contains(lowerNewText)) {
                                filteredItems.add(item);
                            }
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

    protected void cacheItems(List<T> items) {
        viewModel.setCachedItems(items);
    }
}
