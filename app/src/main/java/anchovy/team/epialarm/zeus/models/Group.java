package anchovy.team.epialarm.zeus.models;

public class Group {
    private int id;
    private int idParent;
    private String name;
    private String path;
    private int count;
    private boolean isReadOnly;

    public Group(int id, int idParent, String name,
                 String path, int count, boolean isReadOnly,
                 int idSchool, String color, boolean isVisible) {
        this.id = id;
        this.idParent = idParent;
        this.name = name;
        this.path = path;
        this.count = count;
        this.isReadOnly = isReadOnly;
        this.idSchool = idSchool;
        this.color = color;
        this.isVisible = isVisible;
    }

    private int idSchool;
    private String color;
    private boolean isVisible;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdParent() {
        return idParent;
    }

    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public int getIdSchool() {
        return idSchool;
    }

    public void setIdSchool(int idSchool) {
        this.idSchool = idSchool;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean visible) {
        isVisible = visible;
    }

    public Group() {
    }

    @Override
    public String toString() {
        return "Group{"
                + "id=" + id
                + ", idParent=" + idParent
                + ", name='" + name + '\''
                + ", path='" + path + '\''
                + ", count=" + count
                + ", isReadOnly=" + isReadOnly
                + ", idSchool=" + idSchool
                + ", color='" + color + '\''
                + ", isVisible=" + isVisible
                + '}';
    }
}

