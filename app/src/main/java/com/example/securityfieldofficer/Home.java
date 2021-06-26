package com.example.securityfieldofficer;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

public class Home extends AppCompatActivity {

    MaterialCardView visit_planned,save_location,unplanned_visits;
    ImageView logout_btn;
    SharedPreferences sp;
    TextView name_tv;
    Boolean Internet = true;

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

                if(checkInternet()){
                    startActivity(new Intent(Home.this,Visit_Planned.class));
                }
            }
        });

        save_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInternet()){
                    startActivity(new Intent(Home.this,Save_Location.class));
                }
            }
        });

        unplanned_visits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkInternet()){
                    startActivity(new Intent(Home.this,Unplanned_Visits.class));
                }
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

    @Override
    public void onBackPressed() {

        if(Internet){
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setTitle("Exit")
                    .setMessage("Are you sure you want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

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
        }else{
            startActivity(new Intent(Home.this,Home.class));
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.animation_leave);
            finish();
        }

    }

    Boolean checkInternet() {
        Context context = this;
        Button retry_button;
        NoInternet obj = new NoInternet();

        if (!obj.isNetworkAvailable(context)) {

            LayoutInflater inflater = LayoutInflater.from(this); // 1
            View theInflatedView = inflater.inflate(R.layout.no_internet_layout, null);

            retry_button = theInflatedView.findViewById(R.id.retry_button);
            setContentView(theInflatedView);

            retry_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Home.this, Home.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.drop_down, R.anim.fade_out);
                }
            });

            Internet = false;

            return false;

        } else {
            Internet = true;
            return true;
        }
    }

}