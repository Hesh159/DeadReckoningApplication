package com.example.deadreckoningapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    final static Logger logger = LoggerFactory.getLogger("MainActivity.class");

    private SensorManager sensorManager;
    private TextView magnometerText;
    private TextView acceleratorText;
    private TextView stepCounterText;
    private TextView barometerText;
    private Button mapButton;

    private Sensor magnometer;

    Intent mapIntent;

    private Sensor accelerator;
    private Sensor stepCounter;
    private Sensor barometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }

        magnometerText = findViewById(R.id.magnometerText);
        acceleratorText = findViewById(R.id.accelText);
        stepCounterText = findViewById(R.id.stepCounterText);
        barometerText = findViewById(R.id.barometerText);
        mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v -> {
            mapIntent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(mapIntent);
            finish();
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            Log.i("MYTAG", "has step counter");
        }
        else {
            Log.i("MYTAG", "Has no step counter");
        }
    }

    private void registerSensors() {
        registerSensor(magnometer);
        registerSensor(accelerator);
        if (barometer != null) {
            registerSensor(barometer);
        } else {
            barometerText.setText("Pressure sensor is not available");
        }
        if (stepCounter != null) {
            registerSensor(stepCounter);
        } else {
            stepCounterText.setText("Step counter is not available");
        }
    }

    private void registerSensor(Sensor sensor) {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            logger.info("Sensor event detected from Magnetometer");
            magnometerText.setText("x: " + String.valueOf(round(value)) + ", y: " + String.valueOf(round(event.values[1])) + ", z: " + String.valueOf(round(event.values[2])));
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            logger.info("Sensor event detected from accelerometer");
            acceleratorText.setText("x: " + String.valueOf(round(value)) + ", y: " + String.valueOf(round(event.values[1])) + ", z: " + String.valueOf(round(event.values[2])));
        }
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.i("StepCounter", "Step counter update");
            logger.info("Sensor event detected from Step Counter");
            int stepCount = (int) value;
            stepCounterText.setText(String.valueOf(stepCount));
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            logger.info("Sensor event detected from Barometer");
            float height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value);
            barometerText.setText("Pressure: " + String.valueOf(value) + ", Height: " + String.valueOf(height));
        }
    }


    private float round(float value) {
        return (float) (Math.round(value * 100) / 100d);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
