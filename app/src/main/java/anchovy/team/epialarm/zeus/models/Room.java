package anchovy.team.epialarm.zeus.models;

public class Room {
    private int id;
    private int capacity;
    private String name;
    private int idRoomType;
    private int idLocation;

    public Room(int id, int capacity, String name,
                int idRoomType, int idLocation, boolean isVisible) {
        this.id = id;
        this.capacity = capacity;
        this.name = name;
        this.idRoomType = idRoomType;
        this.idLocation = idLocation;
        this.isVisible = isVisible;
    }

    private boolean isVisible;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdRoomType() {
        return idRoomType;
    }

    public void setIdRoomType(int idRoomType) {
        this.idRoomType = idRoomType;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean visible) {
        isVisible = visible;
    }

    public Room() {}
}