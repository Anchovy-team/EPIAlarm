package anchovy.team.epialarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmOverlayService.class);
        i.putExtras(intent);
        ContextCompat.startForegroundService(context, i);
    }
}