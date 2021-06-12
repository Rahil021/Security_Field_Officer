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
    SpinnerAdapter myAdapter;
    List<CompanyDetailsModel> list;
    Button fetch_button, save_button;
    TextView your_coordinates_tv;
    ImageView back_btn;
    String header,retrieve_url,update_url;
    String result;
    String sfo_id;
    LoadingWithAnim loadingDialog,loadingForRetrieve;
    List<CompanyDetailsModel> model;
    TextView your_address_output;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView no_visits_planned,no_visits_planned2;

    Integer visit_id;
    String str_latitude="22",str_longitude="22";

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
        list.add(new CompanyDetailsModel("Select Location", "Click Here!!", 2));
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
                update();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


//        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//        boolean enabled = service
//                .isProviderEnabled(LocationManager.GPS_PROVIDER);

//        // check if enabled and if not send user to the GSP settings
//        // Better solution would be to display a dialog and suggesting to
//        // go to the settings
//        if (!enabled) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
//        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadingWithAnim.startLoadingDialog();  StopLoading();
                save_button.setVisibility(View.VISIBLE);
            }
        });

        retrieve();

    }

    public void StopLoading() {
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                //oadingWithAnim.dismissDialog();
                save_button.setVisibility(View.VISIBLE);
                fetch_button.setVisibility(View.GONE);
                your_coordinates_tv.setVisibility(View.VISIBLE);
                your_address_output.setVisibility(View.VISIBLE);
                spinner.setEnabled(false);
                h.postDelayed(this, 5000);
            }
        }, 5000);
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
                startActivity(new Intent(Save_Location.this, Save_Location.class));
                finish();
                loadingForRetrieve.dismissDialog();
            }
        });

    }

}
