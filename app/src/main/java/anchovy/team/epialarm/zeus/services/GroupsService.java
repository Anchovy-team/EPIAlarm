package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.models.Group;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class GroupsService extends AbstractService<Group> {
    private static final GroupsService groupsService = new GroupsService();

    private GroupsService() {}

    @Override
    public CompletableFuture<List<Group>> getAllItems() {
        return apiClient.get("/api/group", new TypeReference<List<Group>>() {});
    }

    public static GroupsService getGroupsService() {
        return groupsService;
    }

}
