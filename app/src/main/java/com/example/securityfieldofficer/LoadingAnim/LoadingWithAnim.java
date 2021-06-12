package com.example.securityfieldofficer.LoadingAnim;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;

import com.example.securityfieldofficer.R;

public class LoadingWithAnim {

    private Activity activity;
    //Context context;
    //progress_dialog pd;
    //CharSequence text;
    private AlertDialog alertDialog;
    private Fragment fragment;
    int i = 0;
    //String s;
    //View view;
    //public TextView tv;
    //tv = (TextView)findViewById(R.id.custom_loading_text);

    public LoadingWithAnim(Activity myactivity){
        this.activity = myactivity;
        i = 1;
    }

    public LoadingWithAnim(Activity myactivity,Integer id){
        this.activity = myactivity;
        i = id;
    }


    public void startLoadingDialog(){
        if (i==1) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.fetching_coordinates_progress_bar, null));
            builder.setCancelable(false);

            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }else if(i == 2){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.loading_progress_bar, null));
            builder.setCancelable(false);

            alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }

}
