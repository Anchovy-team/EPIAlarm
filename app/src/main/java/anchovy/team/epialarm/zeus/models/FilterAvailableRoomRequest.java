package anchovy.team.epialarm.zeus.models;

import java.util.List;

public class FilterAvailableRoomRequest {
    private String startDate;
    private String endDate;
    private Integer startTime;
    private Integer endTime;
    private int duration = 60;
    private List<Long> groups;
    private List<Long> teachers;
    private Long location;
    private Integer capacity;
    private Long roomType;
    private Long room;
    private List<Long> devices;
    private Integer recurrenceday;
    private Boolean ignoreGroupConflict;

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

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Long> getGroups() {
        return groups;
    }

    public void setGroups(List<Long> groups) {
        this.groups = groups;
    }

    public List<Long> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Long> teachers) {
        this.teachers = teachers;
    }

    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getRoomType() {
        return roomType;
    }

    public void setRoomType(Long roomType) {
        this.roomType = roomType;
    }

    public Long getRoom() {
        return room;
    }

    public void setRoom(Long room) {
        this.room = room;
    }

    public List<Long> getDevices() {
        return devices;
    }

    public void setDevices(List<Long> devices) {
        this.devices = devices;
    }

    public Integer getRecurrenceday() {
        return recurrenceday;
    }

    public void setRecurrenceday(Integer recurrenceday) {
        this.recurrenceday = recurrenceday;
    }

    public Boolean getIgnoreGroupConflict() {
        return ignoreGroupConflict;
    }

    public void setIgnoreGroupConflict(Boolean ignoreGroupConflict) {
        this.ignoreGroupConflict = ignoreGroupConflict;
    }

    @Override
    public String toString() {
        return "FilterAvailableRoomRequest{"
                + "startDate='" + startDate + '\''
                + ", endDate='" + endDate + '\''
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", duration=" + duration
                + ", groups=" + groups
                + ", teachers=" + teachers
                + ", location=" + location
                + ", capacity=" + capacity
                + ", roomType=" + roomType
                + ", room=" + room
                + ", devices=" + devices
                + ", recurrence_day=" + recurrenceday
                + ", ignoreGroupConflict=" + ignoreGroupConflict
                + '}';
    }
}