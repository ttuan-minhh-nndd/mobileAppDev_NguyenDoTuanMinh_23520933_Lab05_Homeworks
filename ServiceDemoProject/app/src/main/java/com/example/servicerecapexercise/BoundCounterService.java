package com.example.servicerecapexercise;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BoundCounterService extends Service {
    private static final String TAG = "BoundCounterService";
    private final IBinder binder = new LocalBinder();
    private int counter = 0;
    private boolean isRunning = true;

    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        BoundCounterService getService() {
            // Return this instance of BoundCounterService so clients can call public methods
            return BoundCounterService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Bound Service Created");
        // Start background counter
        new Thread(() -> {
            while (isRunning) {
                try {
                    // Thread Transition: Background Thread incrementing internal state
                    Thread.sleep(1000);
                    counter++;
                    Log.d(TAG, "Bound Counter: " + counter);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service Bound");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service Unbound");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d(TAG, "Bound Service Destroyed");
    }

    // Public method for clients
    public int getCurrentCount() {
        return counter;
    }
}
