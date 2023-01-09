package com.eits.cameraappdesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.eits.cameraappdesign.adapter.FileDataAdapter;
import com.eits.cameraappdesign.adapter.SortByAdapter;
import com.eits.cameraappdesign.model.fileData_modelData;
import com.eits.cameraappdesign.model.sortBy_modelData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView sortByRecyclerView, displayFileDataRecyclerview;
    SortByAdapter sortByAdapter;
    FileDataAdapter fileDataAdapter;
    Context context;
    Button inspectionBTN,uploadBTN;

    ArrayList<sortBy_modelData> sortBy_List = new ArrayList<>();
    ArrayList<fileData_modelData> fileData_List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AllArrayListsData();
        findIds();
        setAdapterData();
        buttonClicks();

    }

    private void buttonClicks() {
        inspectionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,FacilityActivity.class);
                startActivity(intent);
            }
        });
        uploadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void AllArrayListsData() {
        sortBy_List.add(new sortBy_modelData("Date"));
        sortBy_List.add(new sortBy_modelData("Filename"));
        sortBy_List.add(new sortBy_modelData("Facility"));

        fileData_List.add(new fileData_modelData("VIDEO_20230105_16553_12345678900987.MP4",
                ".mp4",
                "Pune",
                "Null",
                23 / 12 / 20231129,
                0.00,
                12.00,
                34
        ));
        fileData_List.add(new fileData_modelData("VIDEO_20230105_16553_12345678900987.MP4",
                ".mp4",
                "Pune",
                "Null",
                23 / 12 / 20231129,
                0.00,
                12.00,
                34
        ));
        fileData_List.add(new fileData_modelData("VIDEO_20230105_16553_12345678900987.MP4",
                ".mp4",
                "Pune",
                "Null",
                23 / 12 / 20231129,
                0.00,
                12.00,
                34
        ));
        fileData_List.add(new fileData_modelData("VIDEO_20230105_16553_12345678900987.MP4",
                ".mp4",
                "Pune",
                "Null",
                23 / 12 / 20231129,
                0.00,
                12.00,
                34
        ));
    }

    private void findIds() {
        sortByRecyclerView = findViewById(R.id.sortByRecyclerview);
        displayFileDataRecyclerview = findViewById(R.id.displayFileDataRecyclerview);
        inspectionBTN = findViewById(R.id.inspectionBTN);
        uploadBTN = findViewById(R.id.uploadBTN);
    }

    private void setAdapterData() {
        sortByRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        sortByAdapter = new SortByAdapter(sortBy_List);
        sortByRecyclerView.setAdapter(sortByAdapter);

        displayFileDataRecyclerview.setLayoutManager(new GridLayoutManager(context,2));
        fileDataAdapter = new FileDataAdapter(fileData_List);
        displayFileDataRecyclerview.setAdapter(fileDataAdapter);


    }


}