package com.example.stopwatch;import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button startButton, stopButton, holdButton;
    private TextView elapsedTimeText;
    private boolean isRunning = false;
    private long startTime = 0;
    private long elapsedTime = 0;
    private long timeStopped = 0;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateElapsedTime();
            handler.postDelayed(this, 10); // Update every 10 milliseconds for better precision
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        elapsedTimeText = findViewById(R.id.elapsedTimeText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        holdButton = findViewById(R.id.holdButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopwatch();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStopwatch();
            }
        });

        holdButton.setOnTouchListener(new View.OnTouchListener() {
            long startTimeHold = 0;
            long timeInMilliseconds = 0;

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    if (isRunning) {
                        startTimeHold = SystemClock.elapsedRealtime();
                        stopStopwatch();
                    }
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (!isRunning) {
                        timeStopped += SystemClock.elapsedRealtime() - startTimeHold;
                        startStopwatch();
                    }
                }
                return true;
            }
        });
    }

    private void startStopwatch() {
        if (!isRunning) {
            startTime = SystemClock.elapsedRealtime() - elapsedTime - timeStopped;
            handler.postDelayed(runnable, 10); // Start the handler with 10ms delay
            isRunning = true;
        }
    }

    private void stopStopwatch() {
        if (isRunning) {
            elapsedTime = SystemClock.elapsedRealtime() - startTime - timeStopped;
            handler.removeCallbacks(runnable); // Stop the handler
            isRunning = false;
        }
    }

    private void updateElapsedTime() {
        long currentTime = SystemClock.elapsedRealtime();
        elapsedTime = currentTime - startTime - timeStopped;
        int milliseconds = (int) (elapsedTime % 1000);
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);
        String timeStr = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
        elapsedTimeText.setText(timeStr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Remove callback to avoid memory leak
    }
}
