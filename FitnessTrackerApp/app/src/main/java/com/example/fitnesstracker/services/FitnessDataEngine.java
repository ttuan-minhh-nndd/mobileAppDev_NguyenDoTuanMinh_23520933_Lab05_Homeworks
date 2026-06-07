package com.example.fitnesstracker.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.example.fitnesstracker.data.AppDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FitnessDataEngine extends Service {
    private final IBinder binder = new LocalBinder();
    private AppDatabase database;
    private final ExecutorService queryExecutor = Executors.newSingleThreadExecutor();

    public class LocalBinder extends Binder {
        public FitnessDataEngine getService() {
            return FitnessDataEngine.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public interface DataCallback<T> {
        void onDataLoaded(T data);
    }

    public void getRealTimeStepCount(DataCallback<Integer> callback) {
        queryExecutor.execute(() -> {
            int steps = database.stepDao().getTotalSteps();
            callback.onDataLoaded(steps);
        });
    }
}
