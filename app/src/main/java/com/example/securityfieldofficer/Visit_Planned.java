package com.example.securityfieldofficer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.securityfieldofficer.Adapters.PlannedVisitsAdapter;
import com.example.securityfieldofficer.Models.CompanyDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class Visit_Planned extends AppCompatActivity {

    RecyclerView recyclerView;
    List<CompanyDetailsModel> list;
    PlannedVisitsAdapter myAdapter;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_planned_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey));
        }

        recyclerView = findViewById(R.id.recyclerview);
        back_btn = findViewById(R.id.back_button);

        list = new ArrayList<>();

        list.add(new CompanyDetailsModel("Hridhil Thakkar","Manjalpur,Vadodara",1));
        list.add(new CompanyDetailsModel("Rahil Thakkar","Manjalpur,Vadodara",0));

        myAdapter = new PlannedVisitsAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}