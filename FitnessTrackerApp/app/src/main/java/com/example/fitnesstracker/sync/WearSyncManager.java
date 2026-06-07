package com.example.fitnesstracker.sync;

import android.content.Context;
import android.util.Log;
import com.example.fitnesstracker.models.FitnessMetrics;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class WearSyncManager {
    private static final String TAG = "WearSyncManager";
    private static final String DATA_PATH = "/fitness_metrics";
    private final DataClient dataClient;

    public WearSyncManager(Context context) {
        this.dataClient = Wearable.getDataClient(context);
    }

    public void syncMetrics(FitnessMetrics metrics) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(DATA_PATH);
        putDataMapReq.getDataMap().putLong("steps", metrics.steps);
        putDataMapReq.getDataMap().putDouble("calories", metrics.calories);
        putDataMapReq.getDataMap().putInt("points", metrics.points);
        putDataMapReq.getDataMap().putLong("timestamp", System.currentTimeMillis());

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();

        dataClient.putData(putDataReq)
                .addOnSuccessListener(dataItem -> Log.d(TAG, "Successfully synced to WearOS"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to sync to WearOS", e));
    }
}
