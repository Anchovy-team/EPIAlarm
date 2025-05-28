package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Teacher;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeacherService {
    private final ZeusApiClient apiClient;

    public TeacherService(ZeusApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public CompletableFuture<List<Teacher>> getAllTeachers() {
        return apiClient.get("/api/teacher/public", new TypeReference<List<Teacher>>() {});
    }
}
