package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Group;
import java.util.List;

public class GroupsViewModel extends AbstractViewModel<Group> {

    @Override
    public List<Group> getCachedItems() {
        return cachedItems;
    }

    @Override
    public void setCachedItems(List<Group> cachedItems) {
        this.cachedItems = cachedItems;
    }
}
