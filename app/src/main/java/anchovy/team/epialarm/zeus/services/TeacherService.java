package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.models.Teacher;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeacherService extends AbstractService<Teacher> {

    private TeacherService() {}

    private static final TeacherService teacherServiceInstance = new TeacherService();

    @Override
    public CompletableFuture<List<Teacher>> getAllItems() {
        return apiClient.get("/api/teacher/public", new TypeReference<List<Teacher>>() {});
    }

    public static TeacherService getTeacherServiceInstance() {
        return teacherServiceInstance;
    }
}
