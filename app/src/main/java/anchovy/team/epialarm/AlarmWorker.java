package anchovy.team.epialarm;

import static android.os.VibrationEffect.createOneShot;
import static android.os.VibrationEffect.createPredefined;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Objects;

public class AlarmWorker extends Worker {
    private final Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    public AlarmWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params){
        super(context, params);
        this.vibrator = ContextCompat.getSystemService(context, Vibrator.class);
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
            SystemClock.sleep(10000);
            CancelAlarm();
        }
        return Result.success();
    }

    private void CallAlarm(boolean vibration, String className){
        Context context = getApplicationContext();

        mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            long[] pattern = {0, 500, 300, 500};
            int repeatIndex = 0;
            VibrationEffect effect = VibrationEffect.createWaveform(pattern, repeatIndex);
            vibrator.vibrate(effect);
        }

    }

    private void CancelAlarm(){
        vibrator.cancel();
        mediaPlayer.stop();
        mediaPlayer.release();
    }


}
