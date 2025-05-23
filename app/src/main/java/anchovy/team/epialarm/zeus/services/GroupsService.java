package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Group;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GroupsService {
    private final ZeusApiClient apiClient;

    public GroupsService(ZeusApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<List<Group>> getAllGroups() {
        return apiClient.get("/api/group", new TypeReference<List<Group>>() {});
    }
}
