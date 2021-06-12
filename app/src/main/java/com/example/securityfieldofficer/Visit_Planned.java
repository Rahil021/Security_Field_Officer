package com.example.securityfieldofficer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securityfieldofficer.Adapters.PlannedVisitsAdapter;
import com.example.securityfieldofficer.LoadingAnim.LoadingWithAnim;
import com.example.securityfieldofficer.Models.CompanyDetailsModel;
import com.example.securityfieldofficer.Models.Login_pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Visit_Planned extends AppCompatActivity {

    RecyclerView recyclerView;
    final ExecutorService executorService = Executors.newSingleThreadExecutor();
    List<CompanyDetailsModel> list;
    PlannedVisitsAdapter myAdapter;
    ImageView back_btn;
    String header,retrieve_url;
    String result;
    String sfo_id;
    LoadingWithAnim loadingDialog;
    List<CompanyDetailsModel> model;
    TextView no_visits_planned,no_visits_planned2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_planned_activity);

        SharedPreferences prefs = getSharedPreferences("login",MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sfo_id = prefs.getString("SFO_ID","");
        Log.v("TAG","A :"+sfo_id.toString());

        header = getString(R.string.header);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.light_grey));
        }

        recyclerView = findViewById(R.id.recyclerview);
        back_btn = findViewById(R.id.back_button);

        no_visits_planned = findViewById(R.id.no_visits_planned);
        no_visits_planned2 = findViewById(R.id.no_visits_planned2);

        /*list.add(new CompanyDetailsModel("Hridhil Thakkar","Manjalpur,Vadodara",1));
        list.add(new CompanyDetailsModel("Rahil Thakkar","Manjalpur,Vadodara",0));

        myAdapter = new PlannedVisitsAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);*/

        loadingDialog = new LoadingWithAnim(this,2);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        retrieve();
    }

    public void retrieve(){

        {
            retrieve_url = header+"visit_planned.php?sfo_id="+sfo_id;
            Log.v("Login",""+retrieve_url);
            loadingDialog.startLoadingDialog();
            executorService.execute(new Runnable() {
                @Override
                public void run() {

                    try
                    {
                        JsonParser o = new JsonParser();
                        result = o.insert(retrieve_url);
                        model = new ArrayList<>();

                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("res");

                        Log.v("Login_DATA",""+result);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                            CompanyDetailsModel p = new CompanyDetailsModel();

                            p.setCompany_name(jsonObject11.getString("company_name"));
                            p.setCompany_city(jsonObject11.getString("company_city"));
                            p.setVisit_done(jsonObject11.getInt("visit_done"));
                            model.add(p);

                        }
                    }
                    catch ( Exception e)
                    {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //write your UI part here....

                            if(model.size() == 0){
                                no_visits_planned.setVisibility(View.VISIBLE);
                                no_visits_planned2.setVisibility(View.VISIBLE);
                            }else {
                                myAdapter = new PlannedVisitsAdapter(Visit_Planned.this, model);
                                recyclerView.setLayoutManager(new LinearLayoutManager(Visit_Planned.this));
                                recyclerView.setAdapter(myAdapter);
                            }

                            loadingDialog.dismissDialog();

                        }
                    });

                }
            });

        }


    }

}