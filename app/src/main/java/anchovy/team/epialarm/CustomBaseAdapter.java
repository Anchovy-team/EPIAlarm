package anchovy.team.epialarm;

import anchovy.team.epialarm.zeus.models.Reservation;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    Map<LocalDate, List<Reservation>> reservationsGrouped;
    List<ListItem> displayList;

    public CustomBaseAdapter(Context ctx,  Map<LocalDate, List<Reservation>> reservationsGrouped) {
        inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.reservationsGrouped = reservationsGrouped;
        this.displayList = new ArrayList<>();

        for (Map.Entry<LocalDate, List<Reservation>> entry : new TreeMap<>(reservationsGrouped)
                .entrySet()) {
            displayList.add(new DateHeaderItem(entry.getKey()));
            entry.getValue().stream()
                    .sorted(Comparator.comparing(Reservation::getStartDate))
                    .forEach(r -> {
                        displayList.add(new ReservationItem(r));
                    });
        }
    }

    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public Object getItem(int position) {
        return displayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        if (type == 0) {
            convertView = inflater.inflate(R.layout.item_date_header, null);
            convertView.setOnClickListener(null);
            convertView.setClickable(false);
            convertView.setFocusable(false);
            DateHeaderItem headerItem = (DateHeaderItem) displayList.get(position);
            TextView headerText = convertView.findViewById(R.id.dateHeaderText);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MM", Locale.ENGLISH);
            headerText.setText(headerItem.date.format(formatter));
        } else {
            ReservationItem item = (ReservationItem) displayList.get(position);
            Reservation reservation = item.getReservation();
            convertView = inflater.inflate(R.layout.item_reservation, null);
            TextView textView1 = convertView.findViewById(R.id.TextView1);
            TextView textView2 = convertView.findViewById(R.id.TextView2);

            String className = reservation.getName();
            String classroom = "";
            if (reservation.getRooms().length > 0) {
                classroom = reservation.getRooms()[0].getName();
            }
            textView1.setText(className);
            textView2.setText(classroom);

            LocalTime startTime = reservation.getStartDate().toLocalTime();
            LocalTime endTime = reservation.getEndDate().toLocalTime();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String classTime = timeFormatter.format(startTime) + "\n" + timeFormatter.format(endTime);
            TextView textViewTime = convertView.findViewById(R.id.time);
            textViewTime.setText(classTime);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (displayList.get(position) instanceof DateHeaderItem) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
