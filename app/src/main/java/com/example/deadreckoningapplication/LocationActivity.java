package com.example.deadreckoningapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.snackbar.Snackbar;

public class LocationActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LocationPermissionsRequestCode = 1;

    private static final int CLOSE_NORMALLY_RESPONSE_CODE = 100;
    private static final int SENSOR_UNAVAILABLE_RESPONSE_CODE = 200;
    private static final String SENSOR_UNAVAILABLE_TEXT = "Error! The requisite sensors to run this application are unavailable on this device";
    private boolean locationPermissionsEnabled = true;
    private GoogleMap map;
    private Button wgbMapButton;
    private Intent wgbMapIntent;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                int resultCode = activityResult.getResultCode();
                if (resultCode == SENSOR_UNAVAILABLE_RESPONSE_CODE) {
                    Snackbar.make(findViewById(R.id.googleMapsViewLayout), SENSOR_UNAVAILABLE_TEXT, Snackbar.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_view);

        wgbMapButton = findViewById(R.id.wgbButton);
        wgbMapButton.setOnClickListener(l -> {
            wgbMapIntent = new Intent(LocationActivity.this, BuildingMapActivity.class);
            activityResultLauncher.launch(wgbMapIntent);
        });

        SupportMapFragment mapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LocationPermissionsRequestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            locationPermissionsEnabled = false;
        }
    }


    private void enableLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationPermissionsRequestCode);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (!locationPermissionsEnabled) {
            Toast.makeText(this, "Location Permissions are required to use this application, please enable", Toast.LENGTH_SHORT).show();
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LocationPermissionsRequestCode);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
