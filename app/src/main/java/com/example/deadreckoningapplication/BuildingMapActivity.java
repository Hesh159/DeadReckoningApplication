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

    private ImageView userIcon;
    private SensorHelperService sensorHelperService;
    private final Handler mapActivityHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));
    private final Runnable mapRunnable = new Runnable() {
        @Override
        public void run() {
            updateUserIconPosition();
            mapActivityHandler.postDelayed(this, RUN_PERIOD);
        }
    };

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
        int newTopMargin = userIconMargins.topMargin - getDpToMove(distanceTravelled[Z_AXIS_INDEX]);
        int newLeftMargin = userIconMargins.leftMargin - getDpToMove(distanceTravelled[X_AXIS_INDEX]);
        userIconMargins.setMargins(newLeftMargin, newTopMargin, userIconMargins.rightMargin, userIconMargins.bottomMargin);
        userIcon.setLayoutParams(userIconMargins);
    }

    private int getDpToMove(float distanceTravelled) {
        long dpPerMeter = MAP_SIZE_IN_DP / MAP_SIZE_IN_METERS;
        return Math.round(distanceTravelled * dpPerMeter);
    }

    //implement step counter together with accelerometer to improve accuracy
    //research ways to remove the affect of gravity
    //add the alpha documentation from the
    //magnetometer going in pocket, acceleration still moves same direction
    //

}
