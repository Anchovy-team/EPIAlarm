package anchovy.team.epialarm.zeus.models;

import java.util.List;

public class FilterAvailableRoomResponse {
    private int weekIndex;
    private String startDate;
    private String endDate;
    private Room room;
    private List<String> conflicts;

    public int getWeekIndex() {
        return weekIndex;
    }

    public void setWeekIndex(int weekIndex) {
        this.weekIndex = weekIndex;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<String> getConflicts() {
        return conflicts;
    }

    public void setConflicts(List<String> conflicts) {
        this.conflicts = conflicts;
    }

    @Override
    public String toString() {
        return "FilterAvailableRoomResponse{"
                + "weekIndex=" + weekIndex
                + ", startDate='" + startDate + '\''
                + ", endDate='" + endDate + '\''
                + ", room=" + room
                + ", conflicts=" + conflicts
                + '}';
    }
}