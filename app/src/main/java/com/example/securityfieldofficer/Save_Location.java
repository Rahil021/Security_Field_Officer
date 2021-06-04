package com.example.securityfieldofficer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securityfieldofficer.Adapters.SpinnerAdapter;
import com.example.securityfieldofficer.LoadingAnim.LoadingWithAnim;
import com.example.securityfieldofficer.Models.CompanyDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class Save_Location extends AppCompatActivity {
    Spinner spinner;
    List<CompanyDetailsModel> list;
    Button fetch_button,save_button;
    LoadingWithAnim loadingWithAnim;
    TextView your_coordinates_tv;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_location_activity);

        spinner = findViewById(R.id.spinner);
        fetch_button = findViewById(R.id.fetch_button);
        save_button = findViewById(R.id.save_button);
        your_coordinates_tv = findViewById(R.id.your_coordinates_tv);
        back_btn = findViewById(R.id.back_button);

        list = new ArrayList<>();
        list.add(new CompanyDetailsModel("Select Location","Click Here!!",2));
        list.add(new CompanyDetailsModel("Hridhil Thakkar","Manjalpur,Vadodara",1));
        list.add(new CompanyDetailsModel("Rahil Thakkar","Manjalpur,Vadodara",0));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey));
        }

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this,R.layout.drop_down,list);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(list.get(i).getVisit_done() == 0){
                    fetch_button.setVisibility(View.VISIBLE);
                }else{
                    fetch_button.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                fetch_button.setVisibility(View.GONE);
            }
        });

        loadingWithAnim = new LoadingWithAnim(Save_Location.this);

        fetch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingWithAnim.startLoadingDialog();
                StopLoading(); //only for demo use
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Save_Location.this, "Location Saved Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Save_Location.this,Save_Location.class));
                finish();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void StopLoading()
    {
        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            @Override
            public void run() {
                loadingWithAnim.dismissDialog();
                save_button.setVisibility(View.VISIBLE);
                fetch_button.setVisibility(View.GONE);
                your_coordinates_tv.setVisibility(View.VISIBLE);
                spinner.setEnabled(false);
                h.postDelayed(this, 15000);
            }
        },15000);
    }

}