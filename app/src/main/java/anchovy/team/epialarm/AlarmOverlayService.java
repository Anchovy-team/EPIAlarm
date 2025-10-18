package anchovy.team.epialarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
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
    private static final int POSTPONE_MIN = 8;
    private WindowManager wm;
    private View overlay;
    private MediaPlayer player;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm != null && nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, "Alarm Overlay", NotificationManager.IMPORTANCE_HIGH));
        }

        startForeground(1, new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSilent(true)
                .setSmallIcon(R.drawable.alarm_24px)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        String className = intent.getStringExtra("className");
        int advance = intent.getIntExtra("advance", 0);
        if (advance <= 0) {
            stopSelf();
            return START_NOT_STICKY;
        }

        showOverlay(className, advance);
        return START_STICKY;
    }

    private void showOverlay(String className, int advance) {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlay = LayoutInflater.from(this).inflate(R.layout.alarm_overlay, null);

        ((TextView) overlay.findViewById(R.id.textTitle)).setText(className);
        ((TextView) overlay.findViewById(R.id.textCountdown))
                .setText(String.format("In %d minutes", advance));

        overlay.findViewById(R.id.btnClose).setOnClickListener(v -> close(false, null));
        overlay.findViewById(R.id.btnPostpone).setOnClickListener(v -> close(true, className));

        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                flags,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.CENTER;

        overlay.setAlpha(0f);
        overlay.setScaleX(0.8f);
        overlay.setScaleY(0.8f);

        wm.addView(overlay, lp);
        overlay.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(300)
                .withEndAction(this::startAlarm)
                .start();
    }

    private void startAlarm() {
        wakeScreen();

        player = MediaPlayer.create(this, R.raw.sound_file_1);
        if (player != null) {
            player.setLooping(true);
            player.start();
        }

        vibrator = ContextCompat.getSystemService(this, Vibrator.class);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 500, 300, 500}, 0));
        }
    }

    @SuppressLint("Wakelock")
    private void wakeScreen() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "epialarm:wake");
            wakeLock.acquire(5 * 60 * 1000L);
        }
    }

    private void close(boolean postpone, String className) {
        stopAlarm();
        if (overlay != null) {
            overlay.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f)
                    .setDuration(250)
                    .withEndAction(() -> {
                        try {
                            wm.removeViewImmediate(overlay);
                        } catch (Exception ignored) {
                            throw new RuntimeException();
                        }
                        overlay = null;
                        if (postpone && className != null) {
                            scheduleSnooze(className, POSTPONE_MIN);
                        }
                        stopSelf();
                    }).start();
        } else {
            if (postpone && className != null) {
                scheduleSnooze(className, POSTPONE_MIN);
            }
            stopSelf();
        }
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
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    private void scheduleSnooze(String className, int minutes) {
        if (minutes <= 0) {
            return;
        }

        long triggerAt = System.currentTimeMillis() + minutes * 60_000L;

        Intent i = new Intent(this, AlarmReceiver.class)
                .putExtra("className", className)
                .putExtra("advance", minutes);

        int requestCode = ("snooze_" + className).hashCode();

        PendingIntent pi = PendingIntent.getBroadcast(
                this, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }

    @Override
    public void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }
}