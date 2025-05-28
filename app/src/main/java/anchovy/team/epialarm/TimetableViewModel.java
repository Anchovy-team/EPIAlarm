package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Reservation;
import androidx.lifecycle.ViewModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimetableViewModel extends ViewModel {
    public List<Reservation> reservations = null;
    public Map<LocalDate, List<Reservation>> groupedReservations = new TreeMap<>();
}