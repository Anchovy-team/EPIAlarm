package anchovy.team.epialarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class AlarmOverlayService extends Service {
    private static final String CHANNEL_ID = "alarm_overlay_channel";
    private static final int postponeTime = 8;
    private WindowManager windowManager;
    private View overlayView;
    private MediaPlayer player;
    private Vibrator vibrator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, "Alarm Overlay",
                            NotificationManager.IMPORTANCE_HIGH));
        }

        startForeground(1, new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("EpiAlarm")
                .setContentText("Active alarm")
                .setSmallIcon(R.drawable.alarm_24px)
                .build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showOverlay(intent.getStringExtra("className"),
                intent.getIntExtra("advance", 0));
        return START_NOT_STICKY;
    }

    private void showOverlay(String className, int advance) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayView = LayoutInflater.from(this).inflate(R.layout.alarm_overlay, null);

        ((TextView) overlayView.findViewById(R.id.textTitle)).setText(className);
        ((TextView) overlayView.findViewById(R.id.textCountdown))
                .setText(String.format("In %d minutes", advance));

        overlayView.findViewById(R.id.btnClose)
                .setOnClickListener(v -> closeOverlay(false, null));
        overlayView.findViewById(R.id.btnPostpone)
                .setOnClickListener(v -> closeOverlay(true, className));

        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.TOP;

        overlayView.setAlpha(0f);
        overlayView.setTranslationY(-200f);
        windowManager.addView(overlayView, p);
        overlayView.animate()
                .alpha(1f).translationY(0f)
                .setDuration(300)
                .withEndAction(this::startAlarm)
                .start();
    }

    private void startAlarm() {
        player = MediaPlayer.create(this, R.raw.sound_file_1);
        if (player != null) {
            player.setLooping(true);
            player.start();
        }
        vibrator = ContextCompat.getSystemService(this, Vibrator.class);
        if (vibrator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 500, 300, 500}, 0));
        }
    }

    private void closeOverlay(boolean postpone, String className) {
        stopAlarm();
        if (overlayView == null) {
            if (postpone && className != null) {
                scheduleWorker(className, postponeTime);
            }
            stopSelf();
            return;
        }

        overlayView.animate()
                .alpha(0f).translationY(-200f)
                .setDuration(250)
                .withEndAction(() -> {
                    try {
                        windowManager.removeViewImmediate(overlayView);
                    } catch (Exception ignored) {
                        throw new RuntimeException();
                    }
                    overlayView = null;
                    if (postpone && className != null) {
                        scheduleWorker(className, postponeTime);
                    }
                    stopSelf();
                })
                .start();
    }

    private void stopAlarm() {
        if (player != null) {
            try {
                if (player.isPlaying()) {
                    player.stop();
                }
            } catch (Exception ignored) {
                throw new RuntimeException();
            }
            player.release();
            player = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }

    private void scheduleWorker(String className, int minutes) {
        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(minutes, TimeUnit.MINUTES)
                .setInputData(new Data.Builder().putString("className", className).build())
                .build();
        WorkManager.getInstance(this)
                .enqueueUniqueWork("setAlarm", ExistingWorkPolicy.REPLACE, req);
    }

    @Override
    public void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }
}