package com.example.fitnesstracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnesstracker.ai.GeminiAiClient;
import com.example.fitnesstracker.databinding.ActivityMainBinding;
import com.example.fitnesstracker.databinding.LayoutWorkoutSuggestionBinding;
import com.example.fitnesstracker.models.FitnessMetrics;
import com.example.fitnesstracker.services.FitnessDataEngine;
import com.example.fitnesstracker.services.LiveTrackingService;
import com.example.fitnesstracker.services.StepLoggerService;
import com.example.fitnesstracker.sync.WearSyncManager;
import com.example.fitnesstracker.tasks.MetricCalculatorTask;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FitnessDataEngine fitnessDataEngine;
    private boolean isBound = false;
    private WearSyncManager wearSyncManager;
    private GeminiAiClient aiClient;
    private final Set<Integer> unlockedMilestones = new HashSet<>();

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            FitnessDataEngine.LocalBinder binder = (FitnessDataEngine.LocalBinder) service;
            fitnessDataEngine = binder.getService();
            isBound = true;
            startDataPolling();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wearSyncManager = new WearSyncManager(this);
        // Replace with actual API Key or handle missing key
        aiClient = new GeminiAiClient("YOUR_GEMINI_API_KEY");

        startBackgroundServices();
        bindFitnessEngine();
    }

    private void startBackgroundServices() {
        Intent loggerIntent = new Intent(this, StepLoggerService.class);
        startService(loggerIntent);

        Intent liveIntent = new Intent(this, LiveTrackingService.class);
        startForegroundService(liveIntent);
    }

    private void bindFitnessEngine() {
        Intent intent = new Intent(this, FitnessDataEngine.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void startDataPolling() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isBound) {
                    fitnessDataEngine.getRealTimeStepCount(steps -> {
                        runOnUiThread(() -> updateUI(steps));
                    });
                }
                new android.os.Handler().postDelayed(this, 5000);
            }
        }, 5000);
    }

    private void updateUI(int steps) {
        binding.tvStepCount.setText(String.valueOf(steps));
        binding.stepProgressBar.setProgress(steps);

        // Pillar D: Legacy AsyncTask Computation
        new MetricCalculatorTask(metrics -> {
            binding.tvCalories.setText(String.format("%.1f", metrics.calories));
            binding.tvPoints.setText(String.valueOf(metrics.points));
            
            checkMilestones(metrics.steps);
            wearSyncManager.syncMetrics(metrics);
            fetchAiSuggestion(metrics);
        }).execute((long) steps);
    }

    private void checkMilestones(long steps) {
        int[] milestones = {1000, 5000, 10000};
        for (int m : milestones) {
            if (steps >= m && !unlockedMilestones.contains(m)) {
                unlockedMilestones.add(m);
                addBadge(m);
                Toast.makeText(this, "Milestone Unlocked: " + m + " steps!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addBadge(int milestone) {
        ImageView badge = new ImageView(this);
        badge.setImageResource(android.R.drawable.btn_star_big_on);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
        params.setMargins(8, 0, 8, 0);
        badge.setLayoutParams(params);
        binding.badgeContainer.addView(badge);
        
        badge.setScaleX(0);
        badge.setScaleY(0);
        badge.animate().scaleX(1).scaleY(1).setDuration(500).start();
    }

    private void fetchAiSuggestion(FitnessMetrics metrics) {
        if (metrics.steps % 50 == 0 && metrics.steps > 0) { // Only poll AI occasionally for efficiency
            aiClient.getWorkoutSuggestion(metrics, new GeminiAiClient.AiResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    runOnUiThread(() -> {
                        binding.aiSuggestionContainer.removeAllViews();
                        LayoutWorkoutSuggestionBinding aiBinding = LayoutWorkoutSuggestionBinding.inflate(getLayoutInflater());
                        
                        String[] parts = response.split("\\|");
                        aiBinding.tvSuggestionText.setText(parts[0].trim());
                        if (parts.length > 1) {
                            aiBinding.tvEncouragement.setText(parts[1].trim());
                        }
                        
                        binding.aiSuggestionContainer.addView(aiBinding.getRoot());
                    });
                }

                @Override
                public void onError(Throwable t) {
                    // Handle error silently or log
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
