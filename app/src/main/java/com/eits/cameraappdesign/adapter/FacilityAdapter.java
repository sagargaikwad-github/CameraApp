package com.eits.cameraappdesign.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eits.cameraappdesign.ComponentActivity;
import com.eits.cameraappdesign.FacilityActivity;
import com.eits.cameraappdesign.R;

import java.util.ArrayList;
import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.holder> {
    Context context;
    ArrayList<String> arrayList;

    public FacilityAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     LayoutInflater inflater=LayoutInflater.from(parent.getContext());
     View view=inflater.inflate(R.layout.facility_item,parent,false);
     return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, @SuppressLint("RecyclerView") int position) {

        holder.facility_item_TV.setText(arrayList.get(position));

        holder.facility_item_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ComponentActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
           return arrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView facility_item_TV;
        public holder(@NonNull View itemView) {
            super(itemView);
            facility_item_TV=itemView.findViewById(R.id.facility_item_TV);
        }
    }
}
