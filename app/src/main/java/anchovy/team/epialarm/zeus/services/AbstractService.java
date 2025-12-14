package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public abstract class AbstractService<T> {
    protected AbstractService() {}

    protected static final ZeusApiClient apiClient = ZeusApiClient.getZeusApiClientInstance();

    public abstract CompletableFuture<List<T>> getAllItems();
}
