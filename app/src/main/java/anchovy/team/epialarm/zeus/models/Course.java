package anchovy.team.epialarm.zeus.models;

public class Course {
    private Long id;
    private String name;
    private String code;
    private int duration;
    private Long idSchool;
    
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
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public Long getIdSchool() {
        return idSchool;
    }
    
    public void setIdSchool(Long idSchool) {
        this.idSchool = idSchool;
    }
}