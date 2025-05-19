package anchovy.team.epialarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment implements AuthResultHandler {

    private AuthService authService;
    Button loginButton;
    Button sourceCodeButton;
    Button searchButton;
    TextView connectionStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_connected, container, false);

        initializeUi(view);
        authService = new AuthService();
        authService.createClientApp(getContext(), this);
        updateui();

        return view;
    }

    private void initializeUi(@NonNull final View view) {

        connectionStatus = view.findViewById(R.id.connection_status);
        loginButton = view.findViewById(R.id.login_button);
        searchButton = view.findViewById(R.id.search_button);
        sourceCodeButton = view.findViewById(R.id.source_button);

        AuthResultHandler handler = this;
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (authService.getmAccount() == null) {
                    authService.signIn(getActivity(), handler);
                } else {
                    authService.signOut(handler);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SearchGroupFragment dialog = new SearchGroupFragment();
                dialog.show(getParentFragmentManager(), "searchDialog");
            }
        });

        sourceCodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://github.com/arcreane/android-project-ionis-group-alarm"));
            startActivity(intent);
        });
    }

    private void updateui() {
        //TODO: whether it's a teacher, change "Search Group" to "Search Teacher"
        if (authService.getmAccount() != null) {
            loginButton.setText("Logout");
            connectionStatus.setText("Connected");
            searchButton.setVisibility(View.VISIBLE);
            SharedPreferences prefs = requireContext().getSharedPreferences("prefs",
                    Context.MODE_PRIVATE);
            prefs.edit().putString("user_token", authService.getAccesToken()).apply();
        } else {
            loginButton.setText("Login");
            connectionStatus.setText("Not connected");
            searchButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAuthSuccess(String accessToken) {
        updateui();
    }

    @Override
    public void onSignedOut() {
        updateui();
    }
}