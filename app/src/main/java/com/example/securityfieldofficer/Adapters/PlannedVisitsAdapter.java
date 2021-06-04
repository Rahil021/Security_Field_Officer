package com.example.securityfieldofficer.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securityfieldofficer.Models.CompanyDetailsModel;
import com.example.securityfieldofficer.R;

import java.util.List;

public class PlannedVisitsAdapter extends RecyclerView.Adapter<PlannedVisitsAdapter.MyViewHolder> {

    List<CompanyDetailsModel> list;
    Context context;

    public PlannedVisitsAdapter(Context context, List<CompanyDetailsModel> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public PlannedVisitsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.destination_card_view, parent, false);

        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull PlannedVisitsAdapter.MyViewHolder holder, int position) {
        holder.company_name.setText(list.get(position).getCompany_name());
        holder.company_city.setText(list.get(position).getCompany_city());

        if(list.get(position).getVisit_done() == 1){
           // holder.visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.yes));
            holder.constraintLayout_recyler_view.setBackgroundColor(context.getResources().getColor(R.color.d_green));
        }else if(list.get(position).getVisit_done() == 0){
            //holder.visit_status_img.setImageDrawable(context.getResources().getDrawable(R.drawable.no));
        }

        Log.v("PlannedVisitsAdapter","Company name: "+list.get(position).getCompany_name());
        Log.v("PlannedVisitsAdapter","Company city: "+list.get(position).getCompany_city());

    }

    @Override
    public int getItemCount() {
        Log.v("PlannedVisitsAdapter","List size: "+list.size());
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView company_name;
        TextView company_city;
        ImageView visit_status_img;
        ConstraintLayout constraintLayout_recyler_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

                 company_name= itemView.findViewById(R.id.company_name);
                 company_city = itemView.findViewById(R.id.company_city);
                 visit_status_img = itemView.findViewById(R.id.visit_status_img);
                 constraintLayout_recyler_view = itemView.findViewById(R.id.constraintLayout_recyler_view);

            }

        }
    }



