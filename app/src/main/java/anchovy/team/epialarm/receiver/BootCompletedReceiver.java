package anchovy.team.epialarm.receiver;

import anchovy.team.epialarm.AlarmService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device rebooted, restoring alarms");
            AlarmService alarmService = new AlarmService(context);
            alarmService.restoreAllAlarms();
        }
    }
}