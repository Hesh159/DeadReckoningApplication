package com.example.deadreckoningapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;


public class BuildingMapActivity extends AppCompatActivity {

    private static final long RUN_PERIOD = 250;
    private static final long MAP_SIZE_IN_DP = 373;
    private static final long MAP_SIZE_IN_METERS = 102;

    private static final int X_AXIS_INDEX = 0;
    private static final int Y_AXIS_INDEX = 1;
    private static final int Z_AXIS_INDEX = 2;

    private static final int CLOSE_NORMALLY_RESPONSE_CODE = 100;
    private static final int SENSOR_UNAVAILABLE_RESPONSE_CODE = 200;

    private int forwardMovementAxisIndex = 2;
    private int sidewaysMovementAxisIndex = 0;
    private int rotationAxisIndex = 0;

    private ImageView userIcon;
    private SensorHelperService sensorHelperService;
    private final Handler mapActivityHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private final Runnable mapRunnable = new Runnable() {
        @Override
        public void run() {
            updateOrientation();
            updateUserIconPosition();
            mapActivityHandler.postDelayed(this, RUN_PERIOD);
        }
    };

    private float[] orientation = new float[3];
    private boolean deviceIsFlat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_map_view);
        userIcon = findViewById(R.id.user_icon);
        mapActivityHandler.postDelayed(mapRunnable, RUN_PERIOD);
        createSensors();
        validateSensors();
    }

    private void createSensors() {
        sensorHelperService = new SensorHelperService(this);
        sensorHelperService.registerListeners();
    }

    private void validateSensors() {
        boolean necessarySensorsExist = sensorHelperService.validateSensors();
        if (!necessarySensorsExist) {
            setResult(SENSOR_UNAVAILABLE_RESPONSE_CODE);
            close();
        }
    }

    private void close() {
        sensorHelperService.unregisterListeners();
        mapActivityHandler.removeCallbacks(mapRunnable);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHelperService.unregisterListeners();
        mapActivityHandler.removeCallbacks(mapRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorHelperService.registerListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateUserIconPosition() {
        float[] distanceTravelled = sensorHelperService.getDistanceTravelled();
        if (distanceTravelled == null) {
            return;
        }

        ViewGroup.MarginLayoutParams userIconMargins = (ViewGroup.MarginLayoutParams) userIcon.getLayoutParams();
        int newTopMargin = userIconMargins.topMargin - getDpToMove(distanceTravelled[forwardMovementAxisIndex]);
        int newLeftMargin = userIconMargins.leftMargin - getDpToMove(distanceTravelled[sidewaysMovementAxisIndex]);
        userIconMargins.setMargins(newLeftMargin, newTopMargin, userIconMargins.rightMargin, userIconMargins.bottomMargin);
        userIcon.setLayoutParams(userIconMargins);
    }

    private int getDpToMove(float distanceTravelled) {
        long dpPerMeter = MAP_SIZE_IN_DP / MAP_SIZE_IN_METERS;
        return Math.round(distanceTravelled * dpPerMeter);
    }

    private void updateOrientation() {
        float[] updatedOrientation = sensorHelperService.updateDirectionDeviceFacing();
        if (updatedOrientation != null) {
            System.arraycopy(updatedOrientation, 0, orientation, 0, 3);
        }
        deviceIsFlat = Math.toDegrees(orientation[Y_AXIS_INDEX]) > -45 || Math.toDegrees(orientation[Y_AXIS_INDEX]) < 45;
        setOrientationParams(deviceIsFlat);
        rotateIcon();
    }

    private void setOrientationParams(boolean deviceIsFlat) {
        if (deviceIsFlat) {
            forwardMovementAxisIndex = rotationAxisIndex = X_AXIS_INDEX;
            sidewaysMovementAxisIndex = Z_AXIS_INDEX;
        } else {
            forwardMovementAxisIndex = rotationAxisIndex = Z_AXIS_INDEX;
            sidewaysMovementAxisIndex = X_AXIS_INDEX;
        }
    }

    private void rotateIcon() {
        float rotationAngle = orientation[rotationAxisIndex] * -1;
        userIcon.setRotation((float) Math.toDegrees(rotationAngle));
    }

    //implement step counter together with accelerometer to improve accuracy
    //research ways to remove the affect of gravity
    //add the alpha documentation from the
    //magnetometer going in pocket, acceleration still moves same direction
    //
    // get the variance
    //kmann filter
    //wieghted average
    //get gravity over time and use average to tell if change floor / see if vibration of steps can be taken into account

    // mid march report - treat like a small version of full report

    //intro, analysis, design, implementation, testing/eval, conclusions,
    //use requirements analysis for analysis section

    //under what circumstances does it become inaccurate
    //sensor fusion - taking data from multiple sensors to make a decision rather than a single sensor
    //future work - role of machine learning

}
