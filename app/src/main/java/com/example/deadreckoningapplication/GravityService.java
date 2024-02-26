package com.example.deadreckoningapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GravityService implements SensorEventListener, SensorService {

    private final static int START_INDEX = 0;
    private final static int ARRAY_LENGTH = 3;

    private SensorManager sensorManager;
    private Context context;
    private final float[] gravity = new float[3];

    public GravityService(SensorManager sensorManager, Context context) {
        this.sensorManager = sensorManager;
        this.context = context;
    }

    @Override
    public void registerListener() {
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public float[] getSensorResults() {
        return gravity;
    }

    @Override
    public boolean sensorExistsOnDevice() {
        return sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.arraycopy(event.values, START_INDEX, gravity, START_INDEX, ARRAY_LENGTH);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
