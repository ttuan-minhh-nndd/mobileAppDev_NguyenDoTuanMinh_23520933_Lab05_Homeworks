package com.example.fitnesstracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.example.fitnesstracker.data.AppDatabase;
import com.example.fitnesstracker.data.StepEntity;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StepLoggerService extends Service {
    private static final String TAG = "StepLoggerService";
    private ScheduledExecutorService executorService;
    private AppDatabase database;
    private final Random random = new Random();

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadScheduledExecutor();
        startLogging();
    }

    private void startLogging() {
        executorService.scheduleAtFixedRate(() -> {
            try {
                int simulatedSteps = random.nextInt(10) + 1; // Simulate 1-10 steps
                StepEntity step = new StepEntity(System.currentTimeMillis(), simulatedSteps);
                database.stepDao().insertStep(step);
                Log.d(TAG, "Logged " + simulatedSteps + " steps to database.");
            } catch (Exception e) {
                Log.e(TAG, "Error logging steps", e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
