package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Reservation;
import java.time.LocalDate;


public interface ListItem {}

class DateHeaderItem implements ListItem {
    LocalDate date;

    DateHeaderItem(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}

class ReservationItem implements ListItem {
    Reservation reservation;

    ReservationItem(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }
}