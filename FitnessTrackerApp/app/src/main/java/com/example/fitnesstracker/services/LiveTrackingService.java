package com.example.fitnesstracker.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingCornerPathEffect;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import com.example.fitnesstracker.MainActivity;
import com.example.fitnesstracker.R;
import com.example.fitnesstracker.data.AppDatabase;

public class LiveTrackingService extends Service {
    private static final String CHANNEL_ID = "LiveTrackingChannel";
    private static final int NOTIFICATION_ID = 101;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private AppDatabase database;
    
    private final String[] motivations = {
        "Keep it up, Champion!",
        "Every step counts!",
        "Quest for greatness!",
        "Feel the burn!",
        "Legend in the making!"
    };
    private int motivationIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification("Starting Quest..."));
        startTracking();
    }

    private void startTracking() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(() -> {
                    int totalSteps = database.stepDao().getTotalSteps();
                    String msg = motivations[motivationIndex];
                    motivationIndex = (motivationIndex + 1) % motivations.length;
                    
                    updateNotification("Steps: " + totalSteps + " | " + msg);
                }).start();
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void updateNotification(String contentText) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, getNotification(contentText));
    }

    private Notification getNotification(String contentText) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Live Fitness Tracker")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Live Tracking", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
