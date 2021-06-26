package com.example.securityfieldofficer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.securityfieldofficer.Models.CompanyDetailsModel;
import com.example.securityfieldofficer.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<CompanyDetailsModel> {

    Context context;
    LayoutInflater layoutInflater;
    CompanyDetailsModel companyDetailsModel;
    List<CompanyDetailsModel> list;


    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<CompanyDetailsModel> list)
    {
        super(context, resource, list);
        this.context = context.getApplicationContext();
        layoutInflater = LayoutInflater.from(context);
        this.list=list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        TextView company_name, company_city;
        ImageView visit_status_img;
        ConstraintLayout drop_down_constraintLayout;

        convertView = layoutInflater.inflate(R.layout.drop_down, null, false);

        company_name = convertView.findViewById(R.id.company_name);
        company_city = convertView.findViewById(R.id.company_city);
        visit_status_img = convertView.findViewById(R.id.visit_status_img);
        drop_down_constraintLayout = convertView.findViewById(R.id.drop_down_constraintLayout);

        company_city.setText(list.get(position).getCompany_city());
        company_name.setText(list.get(position).getCompany_name());

        if(list.get(position).getVisit_done() == 1){
            //visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            drop_down_constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.d_green));
        }else if(list.get(position).getVisit_done() == 0){
            //visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
        }else if(list.get(position).getVisit_done() == 2){
            drop_down_constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.d_blue));
        }else {
            visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.drop_arrow));
            visit_status_img.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        
        convertView = layoutInflater.inflate(R.layout.simple_spinner_item, null, false);

        TextView commpany_name, company_city;
        ImageView visit_status_img;
        ConstraintLayout simple_spinner_item_constrain_layout;

        commpany_name = convertView.findViewById(R.id.company_name);
        company_city = convertView.findViewById(R.id.company_city);
        visit_status_img = convertView.findViewById(R.id.visit_status_img);
        simple_spinner_item_constrain_layout = convertView.findViewById(R.id.simple_spinner_item_constrain_layout);

        commpany_name.setText(list.get(position).getCompany_name());
        company_city.setText(list.get(position).getCompany_city());

        if(list.get(position).getVisit_done() == 1){
           // visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            simple_spinner_item_constrain_layout.setBackgroundColor(context.getResources().getColor(R.color.d_green));
        }else if(list.get(position).getVisit_done() == 0){
           // visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
        }else if(list.get(position).getVisit_done() == 2){
            simple_spinner_item_constrain_layout.setBackgroundColor(context.getResources().getColor(R.color.d_blue));
        }else {
            visit_status_img.setVisibility(View.VISIBLE);
           // company_city.setVisibility(View.GONE);
            visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.up_arrow));
        }

        return convertView;
    }

}
