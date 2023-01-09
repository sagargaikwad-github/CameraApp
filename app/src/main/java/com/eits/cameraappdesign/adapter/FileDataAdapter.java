package com.eits.cameraappdesign.adapter;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eits.cameraappdesign.R;
import com.eits.cameraappdesign.model.fileData_modelData;

import java.util.ArrayList;

public class FileDataAdapter extends RecyclerView.Adapter<FileDataAdapter.holder> {
    ArrayList<fileData_modelData>arrayList;

    public FileDataAdapter(ArrayList<fileData_modelData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.filedata_item,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        fileData_modelData temp=arrayList.get(position);



        holder.filename.setText("File Name :\n  "+temp.getFilename());
        holder.dateTime.setText("Date & Time :\n 11/02/2022 11:09 AM");
        holder.component.setText("Component : "+temp.getComponent());
        holder.location.setText("Site/Location : "+temp.getLocation());
        holder.facility.setText("Facility : "+temp.getFacility());
        holder.min.setText(String.valueOf("Min\n"+temp.getMin()));
        holder.max.setText(String.valueOf("Max\n"+temp.getMax()));
        holder.average.setText(String.valueOf("Average\n"+temp.getAverage()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView filename,dateTime,component,location,facility,min,max,average;
        public holder(@NonNull View itemView) {
            super(itemView);
            filename=itemView.findViewById(R.id.fileName_TV);
            dateTime=itemView.findViewById(R.id.fileDateTime_TV);
            component=itemView.findViewById(R.id.fileComponent_TV);
            location=itemView.findViewById(R.id.fileSiteLocation_TV);
            facility=itemView.findViewById(R.id.fileFacility_TV);
            min=itemView.findViewById(R.id.fileMIN);
            max=itemView.findViewById(R.id.fileMax);
            average=itemView.findViewById(R.id.fileAverage);
        }
    }
}
