package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Course;
import anchovy.team.epialarm.zeus.models.Reservation;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CourseService {
    private final ZeusApiClient apiClient;
    
    public CourseService(ZeusApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public CompletableFuture<List<Course>> getAllCourses() {
        return apiClient.get("/api/course", new TypeReference<List<Course>>() {});
    }
    
    public CompletableFuture<Course> getCourseById(Long id) {
        return apiClient.get("/api/course/" + id, new TypeReference<Course>() {});
    }
    
    public CompletableFuture<List<Reservation>> getCourseUsage(Long id) {
        return apiClient.get("/api/course/" + id + "/usedby",
                new TypeReference<List<Reservation>>() {});
    }
    
    public CompletableFuture<List<Course>> getCoursesByTeacher(int teacherId) {
        return apiClient.get("/api/course/teacher/" + teacherId,
                new TypeReference<List<Course>>() {});
    }
    
    public CompletableFuture<List<Course>> getCoursesWithPaging(int pageNumber, int pageSize,
                                                                String name, List<Long> groups) {
        String url = String.format("/api/course/withpaging?PageNumber=%d&PageSize=%d",
                pageNumber, pageSize);
        if (name != null && !name.isEmpty()) {
            url += "&Name=" + name;
        }
        
        return apiClient.post(url, groups, new TypeReference<List<Course>>() {});
    }
}