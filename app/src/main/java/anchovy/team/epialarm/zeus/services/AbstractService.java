package anchovy.team.epialarm.zeus.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;

public abstract class AbstractService<T> {
    protected AbstractService(){}
    protected final static ZeusApiClient apiClient = ZeusApiClient.getZeusApiClientInstance();
    public abstract CompletableFuture<List<T>> getAllItems();
}
