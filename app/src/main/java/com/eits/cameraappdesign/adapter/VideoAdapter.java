package com.eits.cameraappdesign.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eits.cameraappdesign.R;
import com.eits.cameraappdesign.VideoPlayActivity;
import com.eits.cameraappdesign.model.VideoModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.holder> {
    ArrayList<VideoModel> arrayList;
    Context context;

    public VideoAdapter(ArrayList<VideoModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.filedata_item, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, @SuppressLint("RecyclerView") int position) {
        File file=new File(arrayList.get(position).getPath());
        holder.filename.setText(" Video Name :\n "+file.getName());

        Glide.with(context)
                .load(file.getPath())
                .into(holder.fileImage_iv);

        String date=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(new File(arrayList.get(position).getPath()).lastModified()));
        holder.dateTime.setText("Date & Time : \n"+date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, VideoPlayActivity.class);
                intent.putExtra("VideoUrl",new File(arrayList.get(position).getPath()).getAbsolutePath());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView filename, dateTime, component, location, facility, min, max, average;
        ImageView fileImage_iv;

        public holder(@NonNull View itemView) {
            super(itemView);
            fileImage_iv = itemView.findViewById(R.id.fileImage_iv);
            filename = itemView.findViewById(R.id.fileName_TV);
            dateTime = itemView.findViewById(R.id.fileDateTime_TV);
            component = itemView.findViewById(R.id.fileComponent_TV);
            location = itemView.findViewById(R.id.fileSiteLocation_TV);
            facility = itemView.findViewById(R.id.fileFacility_TV);
            min = itemView.findViewById(R.id.fileMIN);
            max = itemView.findViewById(R.id.fileMax);
            average = itemView.findViewById(R.id.fileAverage);
        }
    }
}
