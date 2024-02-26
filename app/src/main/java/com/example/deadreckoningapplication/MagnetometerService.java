package com.example.deadreckoningapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MagnetometerService implements SensorEventListener, SensorService {

    private final SensorManager sensorManager;
    private final Context context;

    public MagnetometerService(SensorManager sensorManager, Context context) {
        this.sensorManager = sensorManager;
        this.context = context;
    }

    @Override
    public void registerListener() {
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public float[] getSensorResults() {
        return new float[0];
    }

    @Override
    public boolean sensorExistsOnDevice() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
