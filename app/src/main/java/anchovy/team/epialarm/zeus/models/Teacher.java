package anchovy.team.epialarm.zeus.models;

public class Teacher {
    private int id;
    private String name;

    public Teacher(int id, String name, String firstname,
                   boolean isInternal) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.isInternal = isInternal;
    }

    private String firstname;
    private boolean isInternal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
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

    public Teacher() {}

    public String getFullName() {
        return firstname + " " + name;
    }
}