package anchovy.team.epialarm;

import androidx.lifecycle.ViewModel;
import java.util.List;
import anchovy.team.epialarm.zeus.models.Teacher;

public class TeachersViewModel extends ViewModel {
    private List<Teacher> cachedTeachers;

    public List<Teacher> getCachedTeachers() {
        return cachedTeachers;
    }

    public void setCachedTeachers(List<Teacher> teachers) {
        this.cachedTeachers = teachers;
    }

    public boolean hasCachedTeachers() {
        return cachedTeachers != null && !cachedTeachers.isEmpty();
    }
}
