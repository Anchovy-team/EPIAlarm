package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.AssignRoom;
import anchovy.team.epialarm.zeus.models.FilterAllAvailableRooms;
import anchovy.team.epialarm.zeus.models.FilterAvailableRoomRequest;
import anchovy.team.epialarm.zeus.models.FilterAvailableRoomResponse;
import anchovy.team.epialarm.zeus.models.Room;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoomService {
    private final ZeusApiClient apiClient;
    
    public RoomService(ZeusApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public CompletableFuture<List<Room>> getAllRooms() {
        return apiClient.get("/api/room", new TypeReference<List<Room>>() {});
    }
    
    public CompletableFuture<Room> getRoomById(Long id) {
        return apiClient.get("/api/room/" + id, new TypeReference<Room>() {});
    }
    
    public CompletableFuture<List<Room>> getRoomsWithPaging(int pageNumber,
                                                            int pageSize, String name) {
        String url = String.format("/api/room/withpaging?PageNumber=%d&PageSize=%d",
                pageNumber, pageSize);
        if (name != null && !name.isEmpty()) {
            url += "&Name=" + name;
        }
        return apiClient.get(url, new TypeReference<List<Room>>() {});
    }
    
    public CompletableFuture<List<AssignRoom>> getRoomUsage(Long id) {
        return apiClient.get("/api/room/" + id + "/usedby",
                new TypeReference<List<AssignRoom>>() {});
    }
    
    public CompletableFuture<List<FilterAvailableRoomResponse>>
        findAvailableRooms(FilterAvailableRoomRequest request) {
        return apiClient.post("/api/room/available", request,
                new TypeReference<List<FilterAvailableRoomResponse>>() {});
    }
    
    public CompletableFuture<List<Room>> getAllAvailableRooms(FilterAllAvailableRooms request) {
        return apiClient.post("/api/room/available/all",
                request, new TypeReference<List<Room>>() {});
    }
    
    public CompletableFuture<byte[]> getRoomCalendar(Long id, String token, String startDate) {
        String url = "/api/room/" + id + "/ics/" + token;
        if (startDate != null && !startDate.isEmpty()) {
            url += "?startDate=" + startDate;
        }
        return apiClient.get(url, new TypeReference<byte[]>() {});
    }
}