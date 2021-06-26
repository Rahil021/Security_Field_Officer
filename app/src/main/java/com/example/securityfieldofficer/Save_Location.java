package com.example.securityfieldofficer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securityfieldofficer.Adapters.PlannedVisitsAdapter;
import com.example.securityfieldofficer.Adapters.SpinnerAdapter;
import com.example.securityfieldofficer.LoadingAnim.LoadingWithAnim;
import com.example.securityfieldofficer.Models.CompanyDetailsModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

public class Save_Location extends AppCompatActivity {

    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    Spinner spinner;
    List<CompanyDetailsModel> list;
    Button fetch_button, save_button;
    TextView your_coordinates_tv;
    ImageView back_btn;
    String header,retrieve_url,update_url;
    String result;
    String sfo_id;
    LoadingWithAnim loadingDialog,loadingForRetrieve;
    TextView your_address_output;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView no_visits_planned,no_visits_planned2;

    Integer visit_id;
    String str_latitude="0",str_longitude="0";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_location_activity);

        spinner = findViewById(R.id.spinner);
        fetch_button = findViewById(R.id.fetch_button);
        save_button = findViewById(R.id.save_button);
        your_coordinates_tv = findViewById(R.id.your_coordinates_tv);
        back_btn = findViewById(R.id.back_button);
        your_address_output = findViewById(R.id.your_address_output);

        no_visits_planned = findViewById(R.id.no_visits_planned);
        no_visits_planned2 = findViewById(R.id.no_visits_planned2);

        header = getString(R.string.header);

        SharedPreferences prefs = getSharedPreferences("login",MODE_PRIVATE);
        sfo_id = prefs.getString("SFO_ID","");
        Log.v("TAG","A :"+sfo_id.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey));
        }

        list = new ArrayList<>();
        list.add(new CompanyDetailsModel("Select Location", "Click Here!!", -1));
//        list.add(new CompanyDetailsModel("Hridhil Thakkar", "Manjalpur,Vadodara", 1));
//        list.add(new CompanyDetailsModel("Rahil Thakkar", "Manjalpur,Vadodara", 0));
//
//
//        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.drop_down, list);
//        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
//        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (list.get(i).getVisit_done() == 0) {
                    fetch_button.setVisibility(View.VISIBLE);
                } else {
                    fetch_button.setVisibility(View.GONE);
                }

                visit_id = list.get(i).getVisit_id();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                fetch_button.setVisibility(View.GONE);
            }
        });

        loadingDialog = new LoadingWithAnim(Save_Location.this);
        loadingForRetrieve = new LoadingWithAnim(Save_Location.this,2);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInternet()){
                    update();
                }else {
                    Toast.makeText(Save_Location.this, "No Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInternet()){
                    getlocation();
                }else {
                    Toast.makeText(Save_Location.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        retrieve();

    }



    public void retrieve(){

        {
            retrieve_url = header+"visit_planned.php?sfo_id="+sfo_id;
            Log.v("Login save location",""+retrieve_url);
            loadingForRetrieve.startLoadingDialog();

            executorService.execute(new Runnable() {
                @Override
                public void run() {

                    try
                    {
                        JsonParser o = new JsonParser();
                        result = o.insert(retrieve_url);

                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("res");

                        Log.v("Login_DATA",""+result);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                            CompanyDetailsModel p = new CompanyDetailsModel();

                            p.setVisit_id(jsonObject11.getInt("visit_id"));
                            p.setCompany_name(jsonObject11.getString("company_name"));
                            p.setCompany_city(jsonObject11.getString("company_city"));
                            p.setVisit_done(jsonObject11.getInt("visit_done"));
                            list.add(p);

                        }
                    }
                    catch ( JSONException e)
                    {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //write your UI part here....

                            if(list.size() == 1){
                                no_visits_planned.setVisibility(View.VISIBLE);
                                no_visits_planned2.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.GONE);
                            }else{

                                no_visits_planned.setVisibility(View.GONE);
                                no_visits_planned2.setVisibility(View.GONE);
                                spinner.setVisibility(View.VISIBLE);

                                SpinnerAdapter spinnerAdapter = new SpinnerAdapter(Save_Location.this,R.layout.drop_down,list);
                                spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                                spinner.setAdapter(spinnerAdapter);
                            }

                            loadingForRetrieve.dismissDialog();

                        }
                    });

                }
            });

        }


    }

    //https://tourist-seed.000webhostapp.com/SFO/AndroidPHP/save_location.php?sfo_id=44&visit_id=7&latitude=12.0000&longitude=23.0000

    public void update(){

        update_url = header+"save_location.php?sfo_id="+sfo_id+"&visit_id="+visit_id+"&latitude="+str_latitude+"&longitude="+str_longitude;
        Log.v("Login save location",""+retrieve_url);
        loadingForRetrieve.startLoadingDialog();

        executorService.execute(new Runnable() {

            @Override
            public void run() {

                JsonParser o = new JsonParser();
                result = o.insert(update_url);

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Save_Location.this, "Location Saved Successfully", Toast.LENGTH_SHORT).show();
                finish();
                loadingForRetrieve.dismissDialog();
                overridePendingTransition(0,0);
                startActivity(getIntent());
                overridePendingTransition(0,0);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getlocation() {

        if (checkPermission()) {

            if (LocationEnabled()) {

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
                Geocoder geocoder = new Geocoder(Save_Location.this, Locale.getDefault());
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

    Boolean checkInternet() {

        NoInternet obj = new NoInternet();

        if (obj.isNetworkAvailable(Save_Location.this)) {

            return true;

        } else {
            return false;
        }
    }

}
