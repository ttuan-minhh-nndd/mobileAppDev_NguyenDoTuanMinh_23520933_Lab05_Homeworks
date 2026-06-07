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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView statusTextView;
    private Button btnBackgroundTask, btnForegroundService, btnBindService, btnUnbindService;

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
            Toast.makeText(this, "Background Service Started", Toast.LENGTH_SHORT).show();
        });

        // 2. Start Foreground Service (Persistent Notification)
        btnForegroundService.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Thread Transition: Main Thread requesting system to start Foreground Service
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Toast.makeText(this, "Foreground Service Started", Toast.LENGTH_SHORT).show();
        });

        // 3. Bind to Service
        btnBindService.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoundCounterService.class);
            // Service Orchestration: Binding allows direct interaction with service instance
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        });

        btnUnbindService.setOnClickListener(v -> {
            if (isBound) {
                unbindService(serviceConnection);
                isBound = false;
                updateUIState(false);
                statusTextView.setText("Service Status: Unbound");
            }
        });

        // Legacy AsyncTask Implementation for structural learning
        // Handles a simulated synchronous data load on a background thread
        new LegacyDataLoaderTask().execute("Lab 05 Data");

        // Start periodic polling for Bound Service updates
        startPollingCounter();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Lifecycle Management: Successfully connected to the service
            BoundCounterService.LocalBinder binder = (BoundCounterService.LocalBinder) service;
            boundService = binder.getService();
            isBound = true;
            updateUIState(true);
            Log.d(TAG, "Service Connection: Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Teardown hook: Unexpected disconnection
            isBound = false;
            updateUIState(false);
            Log.d(TAG, "Service Connection: Disconnected");
        }
    };

    private void updateUIState(boolean bound) {
        btnBindService.setEnabled(!bound);
        btnUnbindService.setVisibility(bound ? View.VISIBLE : View.GONE);
    }

    private void startPollingCounter() {
        executorService.execute(() -> {
            while (!executorService.isShutdown()) {
                try {
                    // Thread Transition: Background Thread polling the service state
                    Thread.sleep(1000);
                    if (isBound && boundService != null) {
                        int count = boundService.getCurrentCount();
                        // Thread Transition: Pushing fresh UI updates safely to the Main Thread
                        mainHandler.post(() -> statusTextView.setText("Bound Counter: " + count));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Legacy AsyncTask for educational demonstration.
     * Deprecated in modern Android but used here for structural learning of thread transitions.
     */
    private class LegacyDataLoaderTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // UI Thread: Setup before background task
            statusTextView.setText("Async Task: Preparing data...");
        }

        @Override
        protected String doInBackground(String... params) {
            // Thread Transition: Moving work to Background Thread
            try {
                Thread.sleep(2000); // Simulate network latency
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Result for: " + params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            // Thread Transition: Returning result to UI Thread
            statusTextView.setText(result);
            Toast.makeText(MainActivity.this, "Async Task Complete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Proper service connection teardown
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        // Shutdown the executor to prevent memory leaks
        executorService.shutdownNow();
    }
}
