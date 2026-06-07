package com.example.fitnesstracker.tasks;

import android.os.AsyncTask;
import com.example.fitnesstracker.models.FitnessMetrics;

public class MetricCalculatorTask extends AsyncTask<Long, Void, FitnessMetrics> {
    
    public interface OnMetricsCalculatedListener {
        void onMetricsCalculated(FitnessMetrics metrics);
    }

    private final OnMetricsCalculatedListener listener;

    public MetricCalculatorTask(OnMetricsCalculatedListener listener) {
        this.listener = listener;
    }

    @Override
    protected FitnessMetrics doInBackground(Long... params) {
        long steps = params[0];
        
        // Calories Burned Calculation: Steps * 0.04
        double calories = steps * 0.04;
        
        // Reward Points Allocation: Steps / 100
        int points = (int) (steps / 100);
        
        return new FitnessMetrics(steps, calories, points);
    }

    @Override
    protected void onPostExecute(FitnessMetrics metrics) {
        if (listener != null) {
            listener.onMetricsCalculated(metrics);
        }
    }
}
