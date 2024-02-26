package com.example.deadreckoningapplication;

public interface SensorService {

    void registerListener();

    void unregisterListener();

    float[] getSensorResults();

    boolean sensorExistsOnDevice();
}
