package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Reservation;
import anchovy.team.epialarm.zeus.services.ReservationService;
import android.content.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScheduleRepository {

    private static ScheduleRepository instance;
    private final ZeusApiClient apiClient;
    private final ReservationService reservationService;

    private ScheduleRepository() {
        apiClient = ZeusApiClient.getZeusApiClientInstance();
        reservationService = new ReservationService();
    }

    public static synchronized ScheduleRepository getInstance() {
        if (instance == null) {
            instance = new ScheduleRepository();
        }
        return instance;
    }

    public CompletableFuture<List<Reservation>> fetchReservations(Context context) {
        UserSession session = UserSession.getInstance(context);
        String token = session.getToken();
        if (token == null) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        try {
            apiClient.authenticate(token).join();
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = LocalDateTime.now().plusWeeks(4);

        if ("group".equals(session.getChosenType())) {
            return reservationService.getReservationsByGroup(session.getGroupId(), from, to);
        } else if ("teacher".equals(session.getChosenType())) {
            return reservationService.getReservationsByTeacher(session.getTeacherId(), from, to);
        }

        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}