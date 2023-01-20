package com.eits.cameraappdesign.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eits.cameraappdesign.R;
import com.eits.cameraappdesign.VideoPlayActivity;
import com.eits.cameraappdesign.model.FileModel;
import com.eits.cameraappdesign.model.SqliteModel;
import com.eits.cameraappdesign.model.VideoModel;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.holder> {
   Context context;
    ArrayList<FileModel> tempList;
    SqliteModel sqliteModel;

    public VideoAdapter(Context context, ArrayList<FileModel> tempList) {
        this.context = context;
        this.tempList = tempList;
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
        //File file = new File(arrayList.get(position).getPath());

//        SqliteModel sqliteModel = new SqliteModel(context);
//        sqLiteArrayList = sqliteModel.getVideoFileList();
//
//        String filePath = null;
//        String path = null;
//
//        tempList.clear();
//
//        for (int i = 0; i < arrayList.size(); i++) {
//            filePath = arrayList.get(i).getPath();
//            for (int j = 0; j < sqLiteArrayList.size(); j++) {
//                path = sqLiteArrayList.get(j).getFilePath();
//
//                if (filePath.equals(path)) {
//                    tempList.add(sqLiteArrayList.get(j));
//                }
//            }
//        }
//
//
//        if (!tempList.isEmpty()) {

           sqliteModel=new SqliteModel(context);

           int facID=tempList.get(position).getFacID();
           String Facility=sqliteModel.getFacilityName(facID);

        int compID=tempList.get(position).getCompID();
        String Component=sqliteModel.getComponentName(compID);



            holder.filename.setText(" Video Name :\n " + tempList.get(position).getFileName());
            Glide.with(context)
                    .load(tempList.get(position).getFilePath())
                    .into(holder.fileImage_iv);
            holder.dateTime.setText("Date & Time : \n " + tempList.get(position).getFileDateTime());
            holder.component.setText("Component : " +Component);
            holder.location.setText("Site/Location : " + tempList.get(position).getFileSiteLocation());
            holder.facility.setText("Facility : " +Facility);
            holder.duration.setText("Video Duration : " + tempList.get(position).getFileDuration());
            holder.notes.setText("Notes : " + tempList.get(position).getFileNote());
            holder.min.setText("Min : " + tempList.get(position).getFileMin());
            holder.max.setText("Max : " + tempList.get(position).getFileMax());
            holder.average.setText("Average : " + tempList.get(position).getFileAverage());
      //  }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isPlayable = videoFileIsCorrupted(tempList.get(position).getFilePath());
                if (isPlayable == true) {
                    Intent intent = new Intent(context, VideoPlayActivity.class);
                    intent.putExtra("VideoUrl", tempList.get(position).getFilePath());
                    context.startActivity(intent);
                } else {
                   // Toast.makeText(context, "Video Error , Can't Play", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
    return tempList.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView filename, dateTime, component, location, duration, facility, min, max, average, notes;
        ImageView fileImage_iv;

        public holder(@NonNull View itemView) {
            super(itemView);
            fileImage_iv = itemView.findViewById(R.id.fileImage_iv);
            filename = itemView.findViewById(R.id.fileName_TV);
            dateTime = itemView.findViewById(R.id.fileDateTime_TV);
            component = itemView.findViewById(R.id.fileComponent_TV);
            location = itemView.findViewById(R.id.fileSiteLocation_TV);
            facility = itemView.findViewById(R.id.fileFacility_TV);

            duration = itemView.findViewById(R.id.fileDuration_TV);
            min = itemView.findViewById(R.id.fileMIN);
            max = itemView.findViewById(R.id.fileMax);
            average = itemView.findViewById(R.id.fileAverage);
            notes = itemView.findViewById(R.id.fileNotes_TV);
        }
    }

    private boolean videoFileIsCorrupted(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(context, Uri.parse(path));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
        return "yes".equals(hasVideo);
    }

    private float fileDuration(String absolutePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, Uri.parse(absolutePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        float d = Float.parseFloat(duration);
        float seconds = (d / 1000);
        retriever.release();
        return seconds;
    }


}
