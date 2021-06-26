package com.example.securityfieldofficer;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

public class Home extends AppCompatActivity {

    MaterialCardView visit_planned,save_location,unplanned_visits;
    ImageView logout_btn;
    SharedPreferences sp;

    TextView name_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        String name = sp.getString("name","Zukerberg");
        String image_url = sp.getString("image_url","");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_black, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_black));
        }

        visit_planned = findViewById(R.id.visits_planned_cardView);
        save_location = findViewById(R.id.save_visited_locations_cardView);
        unplanned_visits = findViewById(R.id.unplanned_visits_cardview);
        logout_btn = findViewById(R.id.logout_btn);
        name_tv = findViewById(R.id.name);

        name_tv.setText("Mr. "+name);

        visit_planned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,Visit_Planned.class));
            }
        });

        save_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,Save_Location.class));
            }
        });

        unplanned_visits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,Unplanned_Visits.class));
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.apply();
                                //sp.edit().putBoolean("logged",false).apply();
                                startActivity(new Intent(Home.this,Login.class));
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

            }
        });

    }
}