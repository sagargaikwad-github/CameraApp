package com.eits.cameraappdesign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.eits.cameraappdesign.Interface.SortBy_Interface;
import com.eits.cameraappdesign.adapter.FileDataAdapter;
import com.eits.cameraappdesign.adapter.SortByAdapter;
import com.eits.cameraappdesign.adapter.VideoAdapter;
import com.eits.cameraappdesign.model.VideoModel;
import com.eits.cameraappdesign.model.fileData_modelData;
import com.eits.cameraappdesign.model.sortBy_modelData;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SortBy_Interface {

    RecyclerView sortByRecyclerView, displayFileDataRecyclerview;
    SortByAdapter sortByAdapter;
    FileDataAdapter fileDataAdapter;

    VideoAdapter videoAdapter;

    Context context;
    Button inspectionBTN, uploadBTN;

    ArrayList<sortBy_modelData> sortBy_List = new ArrayList<>();
    ArrayList<fileData_modelData> fileData_List = new ArrayList<>();
    ArrayList<VideoModel> video_List = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AllArrayListsData();
        findIds();
        setAdapterData();
        buttonClicks();



    }

    @Override
    protected void onResume() {
        super.onResume();

        getVideosList();

        displayFileDataRecyclerview.setLayoutManager(new GridLayoutManager(context, 2));
        videoAdapter = new VideoAdapter(video_List, this);
        displayFileDataRecyclerview.setAdapter(videoAdapter);
    }

    private void getVideosList() {
        video_List.clear();
        String path="/storage/emulated/0/Download/InspRec";
        File file=new File(path);
        File[] files=file.listFiles();

        if(files!=null)
        {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            for(File file1 : files)
            {
                if(file1.getPath().endsWith(".mp4"))
                {
                    video_List.add(new VideoModel(file1.getPath()));
                }
            }
        }
    }

//    private void getVideosList() {
//        ContentResolver contentResolver=getContentResolver();
//        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        String selection=MediaStore.Video.Media.DATA+" like?";
//        String[] selectionArgs=new String[]{"%"+"InspRec"+"/%"};
//        Cursor cursor=contentResolver.query(uri,null,selection,selectionArgs,null);
//
//        if(cursor!=null && cursor.moveToFirst())
//        {
//            do{
//                @SuppressLint("Range") String title=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
//                VideoTitleList.add(title);
//            }while (cursor.moveToNext());
//        }
//
//        for (int i=0;i<VideoTitleList.size();i++)
//        {
//            System.out.println(VideoTitleList.get(i));
//        }
//    }

    private void buttonClicks() {
        inspectionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FacilityActivity.class);
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
        sortBy_List.add(new sortBy_modelData("Date", false));
        sortBy_List.add(new sortBy_modelData("Filename", false));
        sortBy_List.add(new sortBy_modelData("Facility", false));

        fileData_List.add(new fileData_modelData("ABCD",
                ".mp4",
                "Pune",
                "Null",
                new Date(2022, 11, 13),
                0.00,
                12.00,
                34
        ));
        fileData_List.add(new fileData_modelData("WXYZ",
                ".mp4",
                "Pune",
                "Null",
                new Date(2021, 01, 29),
                0.00,
                12.00,
                34
        ));
        fileData_List.add(new fileData_modelData("LMNP",
                ".mp4",
                "Pune",
                "Null",
                new Date(2000, 8, 31),
                0.00,
                12.00,
                34
        ));
        fileData_List.add(new fileData_modelData("STUV",
                ".mp4",
                "Pune",
                "Null",
                new Date(2016, 06, 19),
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
        sortByAdapter = new SortByAdapter(sortBy_List, this);
        sortByRecyclerView.setAdapter(sortByAdapter);

//        displayFileDataRecyclerview.setLayoutManager(new GridLayoutManager(context, 2));
//        fileDataAdapter = new FileDataAdapter(fileData_List);
//        displayFileDataRecyclerview.setAdapter(fileDataAdapter);
    }

    public void FindSortList(ArrayList<fileData_modelData> arrayList, String name) {
        Collections.sort(arrayList, new Comparator<fileData_modelData>() {
            @Override
            public int compare(fileData_modelData fileData_modelData, fileData_modelData t1) {
                if (name == "Date") {
                    return fileData_modelData.getDateTime().compareTo(t1.getDateTime());
                }
                if (name == "Filename") {
                    return fileData_modelData.getFilename().compareTo(t1.getFilename());
                }
                if (name == "Facility") {
                    return fileData_modelData.getFacility().compareTo(t1.getFacility());
                }
                return 0;
            }
        });
    }

    @Override
    public void sortBy(String s) {

        ArrayList<fileData_modelData> dateSortList = new ArrayList<>(fileData_List);
        ArrayList<fileData_modelData> filenameSortList = new ArrayList<>(fileData_List);
        ArrayList<fileData_modelData> facilitySortList = new ArrayList<>(fileData_List);

        displayFileDataRecyclerview.setLayoutManager(new GridLayoutManager(context, 2));
        if (s == "Date") {
            FindSortList(dateSortList, "Date");
            fileDataAdapter = new FileDataAdapter(dateSortList);
            displayFileDataRecyclerview.setAdapter(fileDataAdapter);
        }
       else if (s == "Filename") {
            FindSortList(filenameSortList, "Filename");
            fileDataAdapter = new FileDataAdapter(filenameSortList);
            displayFileDataRecyclerview.setAdapter(fileDataAdapter);
        }
       else if (s == "Facility") {
            FindSortList(facilitySortList, "Facility");
            fileDataAdapter = new FileDataAdapter(facilitySortList);
            displayFileDataRecyclerview.setAdapter(fileDataAdapter);
        }
        else  {
            fileDataAdapter = new FileDataAdapter(fileData_List);
            displayFileDataRecyclerview.setAdapter(fileDataAdapter);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
}



