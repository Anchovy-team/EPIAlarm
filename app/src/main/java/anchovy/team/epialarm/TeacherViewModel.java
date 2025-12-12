package anchovy.team.epialarm;

import java.util.List;
import anchovy.team.epialarm.zeus.models.Teacher;

public class TeacherViewModel extends AbstractViewModel<Teacher>{
    @Override
    public List<Teacher> getCachedItems() {
        return cachedItems;
    }

    @Override
    public void setCachedItems(List<Teacher> cachedItems) {
        this.cachedItems = cachedItems;
    }
}
