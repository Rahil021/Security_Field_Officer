package com.example.securityfieldofficer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationDemo extends AppCompatActivity {

    TextView lat, longitiude;
    Button button;
    FusedLocationProviderClient fusedLocationProviderClient;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_demo);

        lat = findViewById(R.id.lat);
        longitiude = findViewById(R.id.longitude);
        button = findViewById(R.id.button);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getlocation() {

        if (checkPermission()) {

            if (LocationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            location.getLongitude();
                            location.getLatitude();

                            lat.setText(""+location.getLatitude());
                            longitiude.setText(""+location.getLongitude());
                        }
                        else {

                            requestLocation();

                        }
                    }
                });
            }
            else{
                Toast.makeText(this, "Please Turn On Your GPS", Toast.LENGTH_SHORT).show();
            }

        } else {
            requestPermissions();
        }
    }

    private void requestLocation() {

        LocationRequest request  = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setFastestInterval(1000);
        request.setInterval(5000);
        checkPermission();
        fusedLocationProviderClient.requestLocationUpdates(request,callback, Looper.myLooper());
    }

    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null){
                Location location = locationResult.getLastLocation();

                Context context;
                Geocoder geocoder = new Geocoder(LocationDemo.this, Locale.getDefault());
                try {
                    List<Address> req_addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    String address = req_addresses.get(0).getAddressLine(0);

                    lat.setText(""+location.getLatitude()+" "+location.getLongitude());
                    longitiude.setText(""+address);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public boolean checkPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            return false;
        }
    }

    private void requestPermissions(){

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        },3);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==3 && grantResults.length>0){

            getlocation();
        }

        else {
            Toast.makeText(this, "Please provide permission", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public boolean  LocationEnabled(){

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if(locationManager.isLocationEnabled()) {
            return true;
        } else{
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void find(View v) {
        getlocation();
    }
}