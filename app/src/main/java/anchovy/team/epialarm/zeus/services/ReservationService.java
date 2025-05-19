package anchovy.team.epialarm.zeus.services;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.DetailledReservationInfos;
import anchovy.team.epialarm.zeus.models.FilterReservations;
import anchovy.team.epialarm.zeus.models.Reservation;
import anchovy.team.epialarm.zeus.models.ReservationInfosToDisplay;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class ReservationService {
    private final ZeusApiClient apiClient;
    
    public ReservationService(ZeusApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public CompletableFuture<Reservation> getReservationById(Long id) {
        return apiClient.get("/api/reservation/" + id, new TypeReference<Reservation>() {});
    }
    
    public CompletableFuture<DetailledReservationInfos> getReservationDetails(Long id) {
        return apiClient.get("/api/reservation/" + id + "/details",
                new TypeReference<DetailledReservationInfos>() {});
    }
    
    public CompletableFuture<List<ReservationInfosToDisplay>> getReservationsByFilter(
            FilterReservations filter) {
        return apiClient.post("/api/reservation/filter/displayable", filter,
                new TypeReference<List<ReservationInfosToDisplay>>() {});
    }
    
    public CompletableFuture<List<Reservation>> getReservationsByFilter(
            List<Long> groups, List<Long> rooms, List<Long> teachers, 
            LocalDateTime startDate, LocalDateTime endDate) {
        
        String url = "/api/reservation/filter/displayable?StartDate=" + startDate + "&EndDate="
                + endDate;
        
        if (groups != null && !groups.isEmpty()) {
            for (Long groupId : groups) {
                url += "&Groups=" + groupId;
            }
        }
        
        if (rooms != null && !rooms.isEmpty()) {
            for (Long roomId : rooms) {
                url += "&Rooms=" + roomId;
            }
        }
        
        if (teachers != null && !teachers.isEmpty()) {
            for (Long teacherId : teachers) {
                url += "&Teachers=" + teacherId;
            }
        }
        
        return apiClient.get(url, new TypeReference<List<Reservation>>() {});
    }
    
    public CompletableFuture<List<ReservationInfosToDisplay>> getReservationsWithPaging(
            int pageNumber, int pageSize, String name, LocalDateTime startDate,
            LocalDateTime endDate) {
        
        String url = String.format("/api/reservation/withpaging?PageNumber=%d&PageSize=%d",
                pageNumber, pageSize);
        
        if (name != null && !name.isEmpty()) {
            url += "&Name=" + name;
        }
        
        if (startDate != null) {
            url += "&StartDate=" + startDate;
        }
        
        if (endDate != null) {
            url += "&EndDate=" + endDate;
        }
        
        return apiClient.get(url, new TypeReference<List<ReservationInfosToDisplay>>() {});
    }
}