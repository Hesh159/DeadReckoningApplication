package com.example.deadreckoningapplication.sensors;

import java.util.ArrayList;

public abstract class SensorReaderService implements SensorService {

    protected static final int X_AXIS_INDEX = 0;
    protected static final int Y_AXIS_INDEX = 1;
    protected static final int Z_AXIS_INDEX = 2;

    protected float[] removeNoiseFromResult(float[] sensorResults, float noiseThreshold) {
        float[] resultsWithoutNoise = new float[3];
        resultsWithoutNoise[X_AXIS_INDEX] = removeNoise(sensorResults[X_AXIS_INDEX], noiseThreshold);
        resultsWithoutNoise[Y_AXIS_INDEX] = removeNoise(sensorResults[Y_AXIS_INDEX], noiseThreshold);
        resultsWithoutNoise[Z_AXIS_INDEX] = removeNoise(sensorResults[Z_AXIS_INDEX], noiseThreshold);
        return resultsWithoutNoise;
    }

    protected float removeNoise(float sensorReading, float noiseThreshold) {
        if (sensorReading < noiseThreshold && sensorReading > (noiseThreshold * -1)) {
            sensorReading = 0;
        }

        return sensorReading;
    }

    protected float[] getAverageSensorReading(ArrayList<float[]> sensorReadings) {
        float[] averageReading = new float[3];
        averageReading[X_AXIS_INDEX] = getAverageReadingForAxis(sensorReadings, X_AXIS_INDEX);
        averageReading[Y_AXIS_INDEX] = getAverageReadingForAxis(sensorReadings, Y_AXIS_INDEX);
        averageReading[Z_AXIS_INDEX] = getAverageReadingForAxis(sensorReadings, Z_AXIS_INDEX);
        sensorReadings.clear();
        return averageReading;
    }

    private float getAverageReadingForAxis(ArrayList<float[]> sensorReadings, int axis) {
        float total = 0;
        for (float[] reading : sensorReadings) {
            total += reading[axis];
        }

        return total / sensorReadings.size();
    }
}
