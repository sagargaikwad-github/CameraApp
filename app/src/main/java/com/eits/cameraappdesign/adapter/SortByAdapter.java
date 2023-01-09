package com.eits.cameraappdesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eits.cameraappdesign.R;
import com.eits.cameraappdesign.model.sortBy_modelData;

import java.util.ArrayList;

public class SortByAdapter extends RecyclerView.Adapter<SortByAdapter.holder> {
    ArrayList<sortBy_modelData>arrayList;


    public SortByAdapter(ArrayList<sortBy_modelData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public SortByAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.sort_by_item,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SortByAdapter.holder holder, int position) {
       holder.sortBy_TV.setText(arrayList.get(position).getSortByName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView sortBy_TV;
        public holder(@NonNull View itemView) {
            super(itemView);
            sortBy_TV=itemView.findViewById(R.id.sort_by_item_textView);
        }
    }
}
