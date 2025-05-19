package anchovy.team.epialarm.zeus.models;

public class AppAuthModel {
    private String accessToken;
    
    public AppAuthModel(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}