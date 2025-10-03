package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Group;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class GroupsViewModel extends ViewModel {
    private List<Group> cachedGroups;

    public List<Group> getCachedGroups() {
        return cachedGroups;
    }

    public void setCachedGroups(List<Group> cachedGroups) {
        this.cachedGroups = cachedGroups;
    }

    public boolean hasCachedGroups() {
        return cachedGroups != null && !cachedGroups.isEmpty();
    }
}
