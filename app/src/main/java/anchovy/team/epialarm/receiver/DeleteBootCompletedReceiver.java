package anchovy.team.epialarm.receiver;

import anchovy.team.epialarm.DeleteAlarmService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeleteBootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompletedReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Device rebooted, restoring alarms");
            DeleteAlarmService alarmService = new DeleteAlarmService(context);
            alarmService.restoreAllAlarms();
        }
    }
}