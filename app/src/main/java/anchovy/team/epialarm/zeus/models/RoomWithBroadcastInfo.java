package anchovy.team.epialarm.zeus.models;

public class RoomWithBroadcastInfo {
    private Room room;
    private boolean isBroadcastRoom;

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isBroadcastRoom() {
        return isBroadcastRoom;
    }

    public void setBroadcastRoom(boolean broadcastRoom) {
        isBroadcastRoom = broadcastRoom;
    }

    @Override
    public String toString() {
        return "RoomWithBroadcastInfo{"
                + "room=" + room
                + ", isBroadcastRoom=" + isBroadcastRoom
                + '}';
    }
}