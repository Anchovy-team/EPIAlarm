package anchovy.team.epialarm;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Objects;

public class AlarmWorker extends Worker {
    public AlarmWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params){
        super(context, params);

    }


    @Override
    @NonNull
    public Result doWork() {
        String className = getInputData().getString("className");
        String type = getInputData().getString("type");
        boolean vibration = getInputData().getBoolean("vibration", false);

        if(Objects.equals(type, "notification")){
            System.out.println("notification");
        }
        else{
            CallAlarm(vibration, className);
        }
        return Result.success();
    }

    private void CallAlarm(boolean vibration, String className){
        Context context = getApplicationContext();
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1);


    }
}
