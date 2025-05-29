package anchovy.team.epialarm.zeus.models;

public class Teacher {
    private int id;
    private String name;
    private String firstName;
    private boolean isInternal;

    public Teacher(int id, String name, String firstName,
                   boolean isInternal) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.isInternal = isInternal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean internal) {
        isInternal = internal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return firstName + " " + name;
    }
}