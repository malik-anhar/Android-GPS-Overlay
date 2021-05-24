package com.example.androidgps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.util.Strings;

import java.util.List;

public class LocationManagerActivity extends AppCompatActivity  {
    private final int LOCATION_REQUEST_CODE = 10001;
    private final long secondInterval = 2;
    private final float minDistanceToUpdate = 1f;

    private String provider;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        provider = getBestProvider(locationManager);
        locationListener = new MyLocationListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkLocationPermission()) {
//            getLastLocation();
            startLocationUpdates();
        } else {
            askLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("Location", "lat : " + location.getLatitude() + ", long : " + location.getLongitude());
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                secondInterval,
                minDistanceToUpdate,
                locationListener);
    }

    private @Nullable String getBestProvider(LocationManager locationManager) {
        Criteria criteria = new Criteria();

        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        String provider = locationManager.getBestProvider(criteria, true);

        if (Strings.isEmptyOrWhitespace(provider)) {
            List<String> providers = locationManager.getProviders(true);
            if (providers.size() > 0)
                provider = providers.get(0);
        }

        return provider;
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
//                getLastLocation();
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d("Location", "lat : " + location.getLatitude() + ", long : " + location.getLongitude());
        }
    }
}
