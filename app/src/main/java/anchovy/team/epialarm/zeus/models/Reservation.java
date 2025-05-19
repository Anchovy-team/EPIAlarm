package anchovy.team.epialarm.zeus.models;

import java.time.LocalDateTime;

public class Reservation {
    private Teacher[] teachers;
    private int idReservation;
    private int idCourse;
    private String name;
    private int idType;
    private String typeName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isOnline;
    private Room[] rooms;
    private Group[] groups;
    private int idSchool;
    private String schoolName;
    private String constraintGroupId;

    public Teacher[] getTeachers() {
        return teachers;
    }

    public void setTeachers(Teacher[] teachers) {
        this.teachers = teachers;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(int idCourse) {
        this.idCourse = idCourse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean online) {
        isOnline = online;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }

    public int getIdSchool() {
        return idSchool;
    }

    public void setIdSchool(int idSchool) {
        this.idSchool = idSchool;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getConstraintGroupId() {
        return constraintGroupId;
    }

    public Reservation(Teacher[] teachers, int idReservation, int idCourse,
                       String name, int idType, String typeName,
                       LocalDateTime startDate, LocalDateTime endDate, boolean isOnline,
                       Room[] rooms, Group[] groups, int idSchool,
                       String schoolName, String constraintGroupId) {
        this.teachers = teachers;
        this.idReservation = idReservation;
        this.idCourse = idCourse;
        this.name = name;
        this.idType = idType;
        this.typeName = typeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isOnline = isOnline;
        this.rooms = rooms;
        this.groups = groups;
        this.idSchool = idSchool;
        this.schoolName = schoolName;
        this.constraintGroupId = constraintGroupId;
    }

    public void setConstraintGroupId(String constraintGroupId) {
        this.constraintGroupId = constraintGroupId;
    }

    public Reservation() {}

}