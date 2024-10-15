package com.example.gpsdemo_navars;
import android.os.Build;
import android.content.res.ColorStateList;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private TextView clockTextView;
    private Button startButton;
    private Handler handler;
    private Runnable clockRunnable;
    private boolean isRunning = true; // Clock starts running initially
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blue));
        }
        setContentView(R.layout.activity_main);

        clockTextView = findViewById(R.id.clockTextView);
        startButton = findViewById(R.id.startButton);
        handler = new Handler();

        // Initialize CameraManager for flashlight control
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        clockRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    updateTime();
                    handler.postDelayed(this, 100); // Update every 100 milliseconds (decisecond)
                }
            }
        };

        // Start the clock initially
        handler.post(clockRunnable);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    // Start button clicked (pause the clock)
                    isRunning = false;
                    startButton.setText("Reset");
                    turnOnFlashlight();
                } else {
                    // Reset button clicked (resume the clock)
                    isRunning = true;
                    startButton.setText("Send");
                    updateTime(); // Update immediately to get the latest time
                    handler.post(clockRunnable);
                    turnOffFlashlight();
                }
            }
        });
    }

    private void updateTime() {
        // TODO: Implement fetching time from an internet source
        // For now, use the device's time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS", Locale.getDefault());
        //sdf.setTimeZone(TimeZone.getTimeZone("IST")); // Use UTC for consistency
        String currentTime = sdf.format(new Date());
        clockTextView.setText(currentTime);
    }

    private void turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}