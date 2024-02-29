package com.example.deadreckoningapplication.sensors;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class AccelerometerService extends SensorReaderService implements SensorEventListener {

    private static final int START_INDEX  = 0;
    private static final int ARRAY_LENGTH = 3;
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
        float[] averageAcceleration = getAverageSensorReading(accelerationValues);
        return removeNoiseFromResult(averageAcceleration, NOISE_THRESHOLD);
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

        adjustedAccelerationResults[X_AXIS_INDEX] = (removeNoise(accelerationResults[X_AXIS_INDEX], NOISE_THRESHOLD)) - gravity[X_AXIS_INDEX];
        adjustedAccelerationResults[Y_AXIS_INDEX] = (removeNoise(accelerationResults[Y_AXIS_INDEX], NOISE_THRESHOLD)) - gravity[Y_AXIS_INDEX];
        adjustedAccelerationResults[Z_AXIS_INDEX] = (removeNoise(accelerationResults[Z_AXIS_INDEX], NOISE_THRESHOLD)) - gravity[Z_AXIS_INDEX];
        accelerationValues.add(adjustedAccelerationResults);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


