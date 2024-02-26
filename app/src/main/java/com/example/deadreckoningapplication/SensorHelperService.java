package com.example.deadreckoningapplication;

import android.content.Context;
import android.hardware.SensorManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SensorHelperService {

    private static final int ACCELERATION_UPDATE_TIME_MILLI = 500;

    private final SensorManager sensorManager;
    private SensorService accelerometerService;
    private SensorService magnetometerService;
    private final List<SensorService> sensorServices = new ArrayList<>();
    private boolean sensorsCreated = false;
    private long timeSinceAccelerationUpdate = 0;

    public SensorHelperService(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void registerListeners() {
        setupSensors();
        for (SensorService sensorService : sensorServices) {
            sensorService.registerListener();
        }
    }

    private void setupSensors() {
        if (sensorsCreated) {
            return;
        }

        accelerometerService = new AccelerometerService(sensorManager);
        magnetometerService = new MagnetometerService(sensorManager);
        sensorServices.add(accelerometerService);
        sensorServices.add(magnetometerService);
        sensorsCreated = true;
    }

    public void unregisterListeners() {
        for (SensorService sensorService : sensorServices) {
            sensorService.unregisterListener();
        }
    }

    public float[] getDistanceTravelled() {
        long millisecondsSinceEpoch = Instant.now().toEpochMilli();
        if (!(millisecondsSinceEpoch - timeSinceAccelerationUpdate > ACCELERATION_UPDATE_TIME_MILLI)) {
            return null;
        }

        timeSinceAccelerationUpdate = millisecondsSinceEpoch;
        return accelerometerService.getSensorResults();
    }
}
