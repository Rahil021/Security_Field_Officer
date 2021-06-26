package com.example.securityfieldofficer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.securityfieldofficer.Adapters.SpinnerAdapter;
import com.example.securityfieldofficer.LoadingAnim.LoadingWithAnim;
import com.example.securityfieldofficer.Models.CompanyDetailsModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Unplanned_Visits extends AppCompatActivity {

    Button fetch_button, save_button;
    TextView your_coordinates_tv;
    ImageView back_btn;
    String header,update_url;
    String sfo_id;
    LoadingWithAnim loadingDialog,loadingForRetrieve;
    TextView your_address_output;
    FusedLocationProviderClient fusedLocationProviderClient;
    String str_latitude="0",str_longitude="0";
    EditText company_name,company_city;
    Boolean sure = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unplanned_visits_activity);
        AndroidNetworking.initialize(getApplicationContext());


        fetch_button = findViewById(R.id.unplanned_fetch_button);
        save_button = findViewById(R.id.unplanned_save_button);
        your_coordinates_tv = findViewById(R.id.unplanned_your_coordinates_tv);
        back_btn = findViewById(R.id.back_button);
        your_address_output = findViewById(R.id.unplanned_your_address_output);
        company_name = findViewById(R.id.unplanned_company_name);
        company_city = findViewById(R.id.unplanned_company_city);

        loadingDialog = new LoadingWithAnim(this);
        loadingForRetrieve = new LoadingWithAnim(this,2);

        header = getString(R.string.header);

        SharedPreferences prefs = getSharedPreferences("login",MODE_PRIVATE);
        sfo_id = prefs.getString("SFO_ID","");
        Log.v("TAG","A :"+sfo_id.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey));
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetch_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                //loadingWithAnim.startLoadingDialog();  StopLoading();

                if (company_name.getText().toString().length()==0 ) {
                    company_name.setError("Company Name can't be empty");
                }else if(company_city.getText().length()==0){
                    company_city.setError("Company City can't be empty");
                }else{

                    if(checkInternet()){
                        getlocation();
                    }else{
                        Toast.makeText(Unplanned_Visits.this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInternet()){
                    add_visit();
                }else {
                    Toast.makeText(Unplanned_Visits.this, "No Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void add_visit(){

        update_url = header+"unplanned_visit.php?sfo_id="+sfo_id+"&company_name="+company_name.getText().toString()+"&company_city="+company_city.getText().toString()+"&latitude="+str_latitude+"&longitude="+str_longitude+"&visit_done="+2;
        Log.v("add_visits_url",""+update_url);
        loadingForRetrieve.startLoadingDialog();

        AndroidNetworking.post(header+"unplanned_visit.php")
            .addBodyParameter("sfo_id",sfo_id)
            .addBodyParameter("company_name",company_name.getText().toString())
            .addBodyParameter("company_city",company_city.getText().toString())
            .addBodyParameter("latitude",str_latitude)
            .addBodyParameter("longitude",str_longitude)
            .addBodyParameter("visit_done","2")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsString(new StringRequestListener() {
                @Override
                public void onResponse(String response) {

                    if(response.equals("New record created successfully")){
                        Toast.makeText(Unplanned_Visits.this, "Visit Updated", Toast.LENGTH_SHORT).show();

                        overridePendingTransition(0,0);
                        startActivity(getIntent());
                        overridePendingTransition(0,0);
                        finish();

                    }else{
                        Toast.makeText(Unplanned_Visits.this, "Failed to update your visit", Toast.LENGTH_SHORT).show();

                    }

                    loadingForRetrieve.dismissDialog();

                    Log.v("Response",""+response);

                }

                @Override
                public void onError(ANError anError) {
                    loadingForRetrieve.dismissDialog();
                    Toast.makeText(Unplanned_Visits.this, "Errot :"+anError, Toast.LENGTH_SHORT).show();

                }
            });

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getlocation() {

        if (checkPermission()) {

            if (LocationEnabled()) {

                sure = true;

                loadingDialog.startLoadingDialog();

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            location.getLongitude();
                            location.getLatitude();

                            your_coordinates_tv.setText(""+location.getLatitude());
                            your_address_output.setText(""+location.getLongitude());

                            str_latitude = String.valueOf(location.getLatitude());
                            str_longitude = String.valueOf(location.getLongitude());

                            your_coordinates_tv.setVisibility(View.VISIBLE);
                            your_address_output.setVisibility(View.VISIBLE);

                            loadingDialog.dismissDialog();
                            fetch_button.setVisibility(View.GONE);
                            save_button.setVisibility(View.VISIBLE);

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
                Geocoder geocoder = new Geocoder(Unplanned_Visits.this, Locale.getDefault());
                try {
                    List<Address> req_addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    String address = req_addresses.get(0).getAddressLine(0);

                    your_coordinates_tv.setText(""+location.getLatitude()+" "+location.getLongitude());
                    your_address_output.setText(""+address);

                    your_coordinates_tv.setVisibility(View.VISIBLE);
                    your_address_output.setVisibility(View.VISIBLE);

                    str_latitude = String.valueOf(location.getLatitude());
                    str_longitude = String.valueOf(location.getLongitude());

                    loadingDialog.dismissDialog();
                    fetch_button.setVisibility(View.GONE);
                    save_button.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public boolean checkPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        Log.v("Sure",""+sure);

        if(sure){

            AlertDialog.Builder builder = new AlertDialog.Builder(Unplanned_Visits.this);
            builder.setTitle("Are you sure!!")
                    .setMessage("You want to go back?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            startActivity(new Intent(Unplanned_Visits.this,Home.class));
                            finish();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }else {
            super.onBackPressed();
        }

    }

    Boolean checkInternet() {

        NoInternet obj = new NoInternet();

        if (obj.isNetworkAvailable(Unplanned_Visits.this)) {

            return true;

        } else {
            return false;
        }
    }

}