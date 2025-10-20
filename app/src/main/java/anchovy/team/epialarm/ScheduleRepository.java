package anchovy.team.epialarm;

import android.content.Context;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import anchovy.team.epialarm.zeus.client.ZeusApiClient;
import anchovy.team.epialarm.zeus.models.Reservation;
import anchovy.team.epialarm.zeus.services.ReservationService;

public class ScheduleRepository {

    private static ScheduleRepository instance;
    private final ZeusApiClient apiClient;
    private final ReservationService reservationService;

    private ScheduleRepository() {
        apiClient = new ZeusApiClient();
        reservationService = new ReservationService(apiClient);
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

        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = LocalDateTime.now().plusWeeks(1);

        if ("group".equals(session.getChosenType())) {
            List<Long> groups = List.of(session.getGroupId());
            return reservationService.getReservationsByFilter(groups, List.of(), List.of(), from, to);
        } else if ("teacher".equals(session.getChosenType())) {
            List<Long> teachers = List.of(session.getTeacherId());
            return reservationService.getReservationsByFilter(List.of(), List.of(), teachers, from, to);
        }

        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}