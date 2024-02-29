package com.example.deadreckoningapplication.sensors;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class AccelerometerService implements SensorEventListener, SensorService {

    private static final int START_INDEX  = 0;
    private static final int ARRAY_LENGTH = 3;
    private static final int X_AXIS_INDEX = 0;
    private static final int Y_AXIS_INDEX = 1;
    private static final int Z_AXIS_INDEX = 2;
    private static final float NOISE_THRESHOLD = 0.4f;

    private final SensorHelperService sensorHelperService;
    private final SensorManager sensorManager;
    private final Context context;
    private final float[] gravity = new float[3];
    private final ArrayList<float[]> accelerationValues = new ArrayList<>();

    public AccelerometerService(SensorHelperService sensorHelperService, SensorManager sensorManager, Context context) {
        this.sensorHelperService = sensorHelperService;
        this.sensorManager = sensorManager;
        this.context = context;
    }

    public void registerListener() {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public float[] getSensorResults() {
        return getAverageAcceleration();
    }

    @Override
    public boolean sensorExistsOnDevice() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            getCurrentGravityReading();
            setAcceleration(event);
    }

    private void getCurrentGravityReading() {
        float[] gravitySensorResults = sensorHelperService.getGravity();
        System.arraycopy(gravitySensorResults, START_INDEX, gravity, START_INDEX, ARRAY_LENGTH);
    }

    private void setAcceleration(SensorEvent accelerometerResults) {
        float[] accelerationResults = accelerometerResults.values;
        float[] adjustedAccelerationResults = new float[3];

        adjustedAccelerationResults[X_AXIS_INDEX] = removeAccelerationNoise(accelerationResults[X_AXIS_INDEX]) - gravity[X_AXIS_INDEX];
        adjustedAccelerationResults[Y_AXIS_INDEX] = removeAccelerationNoise(accelerationResults[Y_AXIS_INDEX]) - gravity[Y_AXIS_INDEX];
        adjustedAccelerationResults[Z_AXIS_INDEX] = removeAccelerationNoise(accelerationResults[Z_AXIS_INDEX]) - gravity[Z_AXIS_INDEX];
        accelerationValues.add(adjustedAccelerationResults);
    }

    private float removeAccelerationNoise(float acceleration) {
        if (acceleration < NOISE_THRESHOLD && acceleration > (NOISE_THRESHOLD * -1)) {
            acceleration = 0;
        }

        return acceleration;
    }

    public float[] getAverageAcceleration() {
        float[] averageAcceleration = new float[3];
        averageAcceleration[X_AXIS_INDEX] = getAverageAccelerationForAxis(X_AXIS_INDEX);
        averageAcceleration[Y_AXIS_INDEX] = getAverageAccelerationForAxis(Y_AXIS_INDEX);
        averageAcceleration[Z_AXIS_INDEX] = getAverageAccelerationForAxis(Z_AXIS_INDEX);
        accelerationValues.clear();
        return averageAcceleration;
    }

    private float getAverageAccelerationForAxis(int axis) {
        float totalAccelerationForAxis = 0;
        for (float[] accelerationValue : accelerationValues) {
            totalAccelerationForAxis += accelerationValue[axis];
        }

        float averageAcceleration = totalAccelerationForAxis / accelerationValues.size();
        return removeAccelerationNoise(averageAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

