package anchovy.team.epialarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ClassInfoFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_class_info, container, false);

        TextView className = v.findViewById(R.id.className);
        TextView classHours = v.findViewById(R.id.classHours);
        TextView activityType = v.findViewById(R.id.activityType);
        TextView professorName = v.findViewById(R.id.professorName);
        TextView classroom = v.findViewById(R.id.classroom);
        TextView groupNumber = v.findViewById(R.id.groupNumber);

        Bundle args = getArguments();
        if (args != null) {
            className.setText(args.getString("className"));
            classHours.setText(args.getString("classHours"));
            activityType.setText(args.getString("activityType"));
            professorName.setText(args.getString("professorName"));
            classroom.setText(args.getString("classroom"));
            groupNumber.setText(args.getString("group"));
        }

        return v;
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