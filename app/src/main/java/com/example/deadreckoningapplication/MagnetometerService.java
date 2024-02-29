package com.example.deadreckoningapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class MagnetometerService implements SensorEventListener, SensorService {

    private static final int X_AXIS_INDEX = 0;
    private static final int Y_AXIS_INDEX = 1;
    private static final int Z_AXIS_INDEX = 2;
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
        return getAverageMagnetometerReading();
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

        adjustedMagnetometerResults[X_AXIS_INDEX] = removeNoise(magnetometerResults[X_AXIS_INDEX]);
        adjustedMagnetometerResults[Y_AXIS_INDEX] = removeNoise(magnetometerResults[Y_AXIS_INDEX]);
        adjustedMagnetometerResults[Z_AXIS_INDEX] = removeNoise(magnetometerResults[Z_AXIS_INDEX]);
        magnetometerValues.add(adjustedMagnetometerResults);
    }

    private float removeNoise(float magnetometerReading) {
        if (magnetometerReading < NOISE_THRESHOLD && magnetometerReading > (NOISE_THRESHOLD * -1)) {
            magnetometerReading = 0;
        }

        return magnetometerReading;
    }

    public float[] getAverageMagnetometerReading() {
        float[] averageMagnetometerReading = new float[3];
        averageMagnetometerReading[X_AXIS_INDEX] = getAverageMagnetometerReadingForAxis(X_AXIS_INDEX);
        averageMagnetometerReading[Y_AXIS_INDEX] = getAverageMagnetometerReadingForAxis(Y_AXIS_INDEX);
        averageMagnetometerReading[Z_AXIS_INDEX] = getAverageMagnetometerReadingForAxis(Z_AXIS_INDEX);
        magnetometerValues.clear();
        return averageMagnetometerReading;
    }

    private float getAverageMagnetometerReadingForAxis(int axis) {
        float total = 0;
        for (float[] magnetometerReading : magnetometerValues) {
            total += magnetometerReading[axis];
        }

        float averageMagnetometerReading = total / magnetometerValues.size();
        return removeNoise(averageMagnetometerReading);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
