package anchovy.team.epialarm;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class AlarmWorker extends Worker {

    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String className = getInputData().getString("className");
        int advance = getInputData().getInt("advance", 0);

        callAlarm(className, advance);

        return Result.success();
    }

    private void callAlarm(String className, int advance) {
        Context context = getApplicationContext();

        Intent service = new Intent(context, AlarmOverlayService.class);
        service.putExtra("className", className);
        service.putExtra("advance", advance);

        ContextCompat.startForegroundService(context, service);
    }

}