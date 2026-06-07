package com.example.servicerecapexercise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView statusTextView;
    private MaterialButton btnBackgroundTask, btnForegroundService, btnBindService, btnUnbindService;

    private BoundCounterService boundService;
    private boolean isBound = false;

    // Executor for periodic UI updates (Concurrent equivalent to Coroutines for Java)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets for edge-to-edge support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI component initialization
        statusTextView = findViewById(R.id.statusTextView);
        btnBackgroundTask = findViewById(R.id.btnBackgroundTask);
        btnForegroundService = findViewById(R.id.btnForegroundService);
        btnBindService = findViewById(R.id.btnBindService);
        btnUnbindService = findViewById(R.id.btnUnbindService);

        // 1. Start Background Service (Unbounded)
        btnBackgroundTask.setOnClickListener(v -> {
            Intent intent = new Intent(this, BackgroundService.class);
            startService(intent);
            updateStatus("BACKGROUND ACTIVE", R.color.neon_blue);
            Toast.makeText(this, "Background Task Initiated", Toast.LENGTH_SHORT).show();
        });

        // 2. Start Foreground Service (Persistent Notification)
        btnForegroundService.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            updateStatus("FOREGROUND ACTIVE", R.color.neon_green);
            Toast.makeText(this, "Foreground Service Active", Toast.LENGTH_SHORT).show();
        });

        // 3. Bind to Service
        btnBindService.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoundCounterService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        });

        btnUnbindService.setOnClickListener(v -> {
            if (isBound) {
                unbindService(serviceConnection);
                isBound = false;
                updateBoundUI(false);
                updateStatus("STANDBY", R.color.neon_blue);
            }
        });

        // Legacy AsyncTask Implementation
        new LegacyDataLoaderTask().execute("SYSTEM CORE");

        // Start periodic polling for Bound Service updates
        startPollingCounter();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundCounterService.LocalBinder binder = (BoundCounterService.LocalBinder) service;
            boundService = binder.getService();
            isBound = true;
            updateBoundUI(true);
            updateStatus("BOUND CONNECTED", R.color.neon_purple);
            Log.d(TAG, "Service Connection: Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            updateBoundUI(false);
            updateStatus("CONNECTION LOST", R.color.neon_red);
            Log.d(TAG, "Service Connection: Disconnected");
        }
    };

    private void updateBoundUI(boolean bound) {
        btnBindService.setEnabled(!bound);
        btnUnbindService.setVisibility(bound ? View.VISIBLE : View.GONE);
    }

    private void updateStatus(String status, int colorResId) {
        statusTextView.setText(status);
        statusTextView.setTextColor(ContextCompat.getColor(this, colorResId));
    }

    private void startPollingCounter() {
        executorService.execute(() -> {
            while (!executorService.isShutdown()) {
                try {
                    Thread.sleep(1000);
                    if (isBound && boundService != null) {
                        int count = boundService.getCurrentCount();
                        mainHandler.post(() -> statusTextView.setText("PULSE: " + count));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private class LegacyDataLoaderTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            updateStatus("BOOTING...", R.color.neon_orange);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0] + " READY";
        }

        @Override
        protected void onPostExecute(String result) {
            updateStatus(result, R.color.neon_blue);
            Toast.makeText(MainActivity.this, "System Core Online", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        executorService.shutdownNow();
    }
}
