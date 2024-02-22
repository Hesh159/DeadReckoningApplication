package com.example.deadreckoningapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



public class BuildingMapActivity extends AppCompatActivity {

    private static final long RUN_PERIOD = 250;
    private static final long MAP_SIZE_IN_DP = 373;
    private static final long MAP_SIZE_IN_METERS = 102;

    private ImageView userIcon;
    private SensorHelperService sensorHelperService;
    private Handler mapActivityHandler = new Handler(Looper.myLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateUserIconPosition();
            mapActivityHandler.postDelayed(this, RUN_PERIOD);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorHelperService = new SensorHelperService(this);
        sensorHelperService.registerListeners();
        setContentView(R.layout.activity_building_map_view);
        userIcon = findViewById(R.id.user_icon);
        mapActivityHandler.postDelayed(runnable, RUN_PERIOD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHelperService.unregisterListeners();
        mapActivityHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorHelperService.registerListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapActivityHandler.removeCallbacks(runnable);
    }

    private void updateUserIconPosition() {
        float[] distanceTravelled = sensorHelperService.getDistanceTravelled();
        if (distanceTravelled == null) {
            return;
        }

        int dpToMove = getDpToMove(distanceTravelled);
        ViewGroup.MarginLayoutParams userIconMargins = (ViewGroup.MarginLayoutParams) userIcon.getLayoutParams();
        int newTopMargin = userIconMargins.topMargin - dpToMove;
        userIconMargins.setMargins(userIconMargins.leftMargin, newTopMargin, userIconMargins.rightMargin, userIconMargins.bottomMargin);
        userIcon.setLayoutParams(userIconMargins);
    }

    private int getDpToMove(float[] distanceTravelled) {
        float zAxisDistanceTravelled = distanceTravelled[2];
        long dpPerMeter = MAP_SIZE_IN_DP / MAP_SIZE_IN_METERS;
        return Math.round(zAxisDistanceTravelled * dpPerMeter);
    }

    //implement step counter together with accelerometer to improve accuracy
    //research ways to remove the affect of gravity
    //add the alpha documentation from the
    //magnetometer going in pocket, acceleration still moves same direction
    //

}
