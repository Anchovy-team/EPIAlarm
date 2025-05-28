package anchovy.team.epialarm;

public class UserSession {
    /*private static UserSession instance;

    private String token;
    private long groupId;
    private long userId;

    private ZeusApiClient zeusClient;
    private ReservationService reservationService;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void initialize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        token = prefs.getString("user_token", null);
        groupId = prefs.getLong("groupId", -1);
        userId = prefs.getLong("userId", -1); // если надо

        if (token != null) {
            zeusClient = new ZeusApiClient();
            zeusClient.authenticate(token).join();
            reservationService = new ReservationService(zeusClient);
        }
    }

    public boolean isAuthenticated() {
        return token != null && zeusClient != null;
    }

    public String getToken() {
        return token;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(Context context, long groupId) {
        this.groupId = groupId;
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefs.edit().putLong("groupId", groupId).apply();
    }

    public ReservationService getReservationService() {
        return reservationService;
    }

    public void clear() {
        token = null;
        groupId = -1;
        userId = -1;
        zeusClient = null;
        reservationService = null;
    }*/
}
