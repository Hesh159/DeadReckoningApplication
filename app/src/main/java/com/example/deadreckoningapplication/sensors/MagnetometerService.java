package com.example.deadreckoningapplication.sensors;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class MagnetometerService extends SensorReaderService implements SensorEventListener {

    private static final float NOISE_THRESHOLD = 0.2f;

    private final SensorManager sensorManager;
    private final Context context;

    private final ArrayList<float[]> magnetometerValues = new ArrayList<>();

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
        float[] averageMagnetometerReading = getAverageSensorReading(magnetometerValues);
        return removeNoiseFromResult(averageMagnetometerReading, NOISE_THRESHOLD);
    }

    @Override
    public boolean sensorExistsOnDevice() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
    }

    @Override
    public void onSensorChanged(SensorEvent magnetometerReading) {
        setMagnetometerReading(magnetometerReading);
    }

    private void setMagnetometerReading(SensorEvent magnetometerReading) {
        float[] magnetometerResults = magnetometerReading.values;
        float[] adjustedMagnetometerResults = new float[3];

        adjustedMagnetometerResults[X_AXIS_INDEX] = removeNoise(magnetometerResults[X_AXIS_INDEX], NOISE_THRESHOLD);
        adjustedMagnetometerResults[Y_AXIS_INDEX] = removeNoise(magnetometerResults[Y_AXIS_INDEX], NOISE_THRESHOLD);
        adjustedMagnetometerResults[Z_AXIS_INDEX] = removeNoise(magnetometerResults[Z_AXIS_INDEX], NOISE_THRESHOLD);
        magnetometerValues.add(adjustedMagnetometerResults);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
