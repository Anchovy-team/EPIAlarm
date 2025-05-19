package anchovy.team.epialarm;

public interface AuthResultHandler {
    void onAuthSuccess(String accessToken);

    void onSignedOut();
}
