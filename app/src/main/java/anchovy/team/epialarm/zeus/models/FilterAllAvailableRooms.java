package anchovy.team.epialarm.zeus.models;

import java.util.List;

public class FilterAllAvailableRooms {
    private String startDate;
    private String endDate;
    private List<Long> groups;
    private Long location;
    private Long roomType;
    private Integer capacity;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Long> getGroups() {
        return groups;
    }

    public void setGroups(List<Long> groups) {
        this.groups = groups;
    }

    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
        this.location = location;
    }

    public Long getRoomType() {
        return roomType;
    }

    public void setRoomType(Long roomType) {
        this.roomType = roomType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "FilterAllAvailableRooms{"
                + "startDate='" + startDate + '\''
                + ", endDate='" + endDate + '\''
                + ", groups=" + groups
                + ", location=" + location
                + ", roomType=" + roomType
                + ", capacity=" + capacity
                + '}';
    }
}