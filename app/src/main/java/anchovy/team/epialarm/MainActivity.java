package anchovy.team.epialarm;

import anchovy.team.epialarm.databinding.ActivityMainBinding;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new SettingsFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.settings);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.settings) {
                replaceFragment(new SettingsFragment());
            } else if (item.getItemId() == R.id.timetable) {
                replaceFragment(new TimetableFragment());
            } else if (item.getItemId() == R.id.alarm) {
                replaceFragment(new AlarmFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}