package anchovy.team.epialarm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class SettingsFragment extends Fragment implements AuthResultHandler {

    private AuthService authService;
    Button loginButton;
    Button searchGroupButton;
    Button searchTeacherButton;
    Button sourceCodeButton;
    TextView connectionStatus;
    TextView currentGroup;
    private UserSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = UserSession.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        connectionStatus = view.findViewById(R.id.connection_status);
        loginButton = view.findViewById(R.id.login_button);
        searchGroupButton = view.findViewById(R.id.search_group_button);
        searchTeacherButton = view.findViewById(R.id.search_teacher_button);
        currentGroup = view.findViewById(R.id.current_group);
        sourceCodeButton = view.findViewById(R.id.source_button);

        initializeUi();

        authService = new AuthService();
        authService.createClientApp(getContext(), this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener(
                "closed", getViewLifecycleOwner(), (requestKey, bundle) -> {
                    updateUi();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    private void initializeUi() {
        AuthResultHandler handler = this;

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (authService.getmAccount() == null) {
                    authService.signIn(requireActivity(), handler);
                } else {
                    authService.signOut(handler);
                }
            }
        });

        searchGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchGroupFragment dialog = new SearchGroupFragment();
                dialog.show(getParentFragmentManager(), "searchDialog");
            }
        });

        searchTeacherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchTeacherFragment dialog = new SearchTeacherFragment();
                dialog.show(getParentFragmentManager(), "searchDialog");
            }
        });

        sourceCodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Anchovy-team/EPIAlarm"));
            startActivity(intent);
        });
    }

    private void updateUi() {
        boolean isLoggedIn = authService.getmAccount() != null;
        searchGroupButton.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        searchTeacherButton.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);

        loginButton.setText(isLoggedIn ? "Logout" : "Login");
        connectionStatus.setText(isLoggedIn ? "Connected" : "Not connected");

        if (isLoggedIn) {
            if ("group".equals(session.getChosenType())) {
                currentGroup.setText("Chosen value: " + session.getGroupName());
            } else if ("teacher".equals(session.getChosenType())) {
                currentGroup.setText("Chosen value: " + session.getTeacherName());
            }
        } else {
            currentGroup.setText("Chosen value:");
        }
    }

    @Override
    public void onAuthSuccess(String accessToken) {
        session.setToken(accessToken);
        updateUi();
    }

    @Override
    public void onSignedOut() {
        session.clear();
        TimetableViewModel viewModel = new ViewModelProvider(requireActivity()).get(TimetableViewModel.class);
        viewModel.reservations = null;
        viewModel.groupedReservations.clear();
        updateUi();
    }
}