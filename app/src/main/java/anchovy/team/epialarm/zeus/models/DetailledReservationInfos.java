package anchovy.team.epialarm.zeus.models;

import java.util.List;

public class DetailledReservationInfos {
    private Long idReservation;
    private Long idCourse;
    private String name;
    private Long idType;
    private String startDate;
    private String endDate;
    private boolean isOnline;
    private String url;
    private String comment;
    private String creationDate;
    private String code;
    private int duration;
    private Long idSchool;
    private List<RoomWithBroadcastInfo> rooms;
    private List<Group> groups;
    private List<PublicTeacherInfos> teachers;

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public Long getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(Long idCourse) {
        this.idCourse = idCourse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
    }

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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Long getIdSchool() {
        return idSchool;
    }

    public void setIdSchool(Long idSchool) {
        this.idSchool = idSchool;
    }

    public List<RoomWithBroadcastInfo> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomWithBroadcastInfo> rooms) {
        this.rooms = rooms;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<PublicTeacherInfos> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<PublicTeacherInfos> teachers) {
        this.teachers = teachers;
    }
}
