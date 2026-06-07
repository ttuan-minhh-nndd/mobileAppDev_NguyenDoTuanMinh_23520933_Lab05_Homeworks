package com.example.servicerecapexercise;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private boolean isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Background Service Started");
        if (!isRunning) {
            isRunning = true;
            // Start a simulated long-running task on a new background thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int counter = 0;
                    while (isRunning) {
                        try {
                            // Thread Transition: Background Thread performing work
                            Log.d(TAG, "Iteration: " + counter);
                            counter++;
                            Thread.sleep(2000); // Simulate work
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    Log.d(TAG, "Background Service Thread Stopped");
                }
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d(TAG, "Background Service Destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Unbounded service
    }
}
