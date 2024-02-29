package com.example.deadreckoningapplication.sensors;

public interface SensorService {

    void registerListener();

    void unregisterListener();

    float[] getSensorResults();

    boolean sensorExistsOnDevice();
}
