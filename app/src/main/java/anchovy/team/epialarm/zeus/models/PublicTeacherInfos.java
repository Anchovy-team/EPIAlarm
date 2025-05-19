package anchovy.team.epialarm.zeus.models;

public class PublicTeacherInfos {
    private Long id;
    private String name;
    private String firstname;
    private boolean isInternal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setInternal(boolean internal) {
        isInternal = internal;
    }

    @Override
    public String toString() {
        return "PublicTeacherInfos{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", firstname='" + firstname + '\''
                + ", isInternal=" + isInternal
                + '}';
    }
}