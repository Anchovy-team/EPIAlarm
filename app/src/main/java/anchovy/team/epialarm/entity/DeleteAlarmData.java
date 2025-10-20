package anchovy.team.epialarm.entity;

import java.io.Serializable;

public class DeleteAlarmData implements Serializable {
    private String id;
    private String startTime;
    private String className;
    private int advanceMinutes;
    private boolean vibration;
    private boolean active;

    public DeleteAlarmData() {
    }

    public DeleteAlarmData(String id, String startTime,
                           String className, int advanceMinutes, boolean vibration, boolean active) {
        this.id = id;
        this.startTime = startTime;
        this.className = className;
        this.advanceMinutes = advanceMinutes;
        this.vibration = vibration;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getAdvanceMinutes() {
        return advanceMinutes;
    }

    public void setAdvanceMinutes(int advanceMinutes) {
        this.advanceMinutes = advanceMinutes;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "AlarmData{"
                + "id='" + id + '\''
                + ", className='" + className + '\''
                + ", advanceMinutes=" + advanceMinutes
                + '}';
    }
}