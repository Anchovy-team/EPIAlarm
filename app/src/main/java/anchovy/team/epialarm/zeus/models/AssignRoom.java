package anchovy.team.epialarm.zeus.models;

public class AssignRoom {
    private Long id;
    private Long idRoom;
    private Long idReservation;
    private boolean isBroadcastRoom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(Long idRoom) {
        this.idRoom = idRoom;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public boolean isBroadcastRoom() {
        return isBroadcastRoom;
    }

    public void setBroadcastRoom(boolean broadcastRoom) {
        isBroadcastRoom = broadcastRoom;
    }

    @Override
    public String toString() {
        return "AssignRoom{"
                + "id=" + id
                + ", idRoom=" + idRoom
                + ", idReservation=" + idReservation
                + ", isBroadcastRoom=" + isBroadcastRoom
                + '}';
    }
}