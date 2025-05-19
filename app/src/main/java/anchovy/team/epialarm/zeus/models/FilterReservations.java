package anchovy.team.epialarm.zeus.models;

import java.time.LocalDateTime;
import java.util.List;

public class FilterReservations {
    private List<Long> groups;
    private List<Long> rooms;
    private List<Long> teachers;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    public List<Long> getGroups() {
        return groups;
    }
    
    public void setGroups(List<Long> groups) {
        this.groups = groups;
    }
    
    public List<Long> getRooms() {
        return rooms;
    }
    
    public void setRooms(List<Long> rooms) {
        this.rooms = rooms;
    }
    
    public List<Long> getTeachers() {
        return teachers;
    }
    
    public void setTeachers(List<Long> teachers) {
        this.teachers = teachers;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}