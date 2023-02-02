package com.eits.cameraappdesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.eits.cameraappdesign.Interface.SortBy_Interface;
import com.eits.cameraappdesign.adapter.SortByAdapter;
import com.eits.cameraappdesign.adapter.VideoAdapter;
import com.eits.cameraappdesign.model.FileModel;
import com.eits.cameraappdesign.model.SqliteModel;
import com.eits.cameraappdesign.model.VideoModel;
import com.eits.cameraappdesign.model.sortBy_modelData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements SortBy_Interface {
    private static final int REQUEST_STORAGE_ABOVE_R = 101;
    private static final int REQUEST_STORAGE_ABOVE_M = 102;

    RecyclerView sortByRecyclerView, displayFileDataRecyclerview;
    TextView folderEmpty;
    SortByAdapter sortByAdapter;

    SharedPreferences sharedPreferences;
    VideoAdapter videoAdapter;

    Parcelable state;
    String SortBy;

    Context context;
    Button inspectionBTN, uploadBTN;

    ArrayList<sortBy_modelData> sortBy_List = new ArrayList<>();

    ArrayList<VideoModel> video_List = new ArrayList<>();
    ArrayList<FileModel> sqLiteArrayList = new ArrayList<>();
    ArrayList<FileModel> tempList = new ArrayList<>();
    SqliteModel sqliteModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqliteModel = new SqliteModel(this);

        AllArrayListsData();
        findIds();
        buttonClicks();
        checkStoragePermissions();
    }

    private void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getVideosList();
                checkVideoExistOrNot();
            } else {
                requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ABOVE_R);
            }
        }
       else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getVideosList();
                checkVideoExistOrNot();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ABOVE_M);
            }
        }else
        {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_ABOVE_R) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getVideosList();
                checkVideoExistOrNot();
            } else {
                    permissionDialog("Storage");
            }
        }
        if (requestCode==REQUEST_STORAGE_ABOVE_M)
        {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getVideosList();
                checkVideoExistOrNot();
            }else
            {
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        setAdapterData();
        getVideosList();
        checkVideoExistOrNot();

        sharedPreferences = getSharedPreferences("Sort_by", MODE_PRIVATE);
        String sortvia = sharedPreferences.getString("SortVia", "");
     
        if(sortvia!=null)
        {
            sortBy(sortvia);
        }


    }

    private void checkVideoExistOrNot() {
        SqliteModel sqliteModel = new SqliteModel(this);
        sqLiteArrayList = sqliteModel.getVideoFileList();

        String filePath = null;
        String path = null;

        tempList.clear();

        for (int i = 0; i < video_List.size(); i++) {
            filePath = video_List.get(i).getPath();
            for (int j = 0; j < sqLiteArrayList.size(); j++) {
                path = sqLiteArrayList.get(j).getFilePath();

                if (filePath.equals(path)) {
                    tempList.add(sqLiteArrayList.get(j));
                }
            }
        }

        if (!tempList.isEmpty()) {
            if (state!=null)
            {
                folderEmpty.setVisibility(View.GONE);
                displayFileDataRecyclerview.setVisibility(View.VISIBLE);

                displayFileDataRecyclerview.setLayoutManager(new GridLayoutManager(context, 2));
                videoAdapter = new VideoAdapter(this, tempList);
                displayFileDataRecyclerview.setAdapter(videoAdapter);
                displayFileDataRecyclerview.getLayoutManager().onRestoreInstanceState(state);
            }else
            {
                folderEmpty.setVisibility(View.GONE);
                displayFileDataRecyclerview.setVisibility(View.VISIBLE);

                displayFileDataRecyclerview.setLayoutManager(new GridLayoutManager(context, 2));
                videoAdapter = new VideoAdapter(this, tempList);
                displayFileDataRecyclerview.setAdapter(videoAdapter);
            }
        }else
        {
            folderEmpty.setVisibility(View.VISIBLE);
            displayFileDataRecyclerview.setVisibility(View.GONE);
        }


    }

    private void getVideosList() {
        video_List.clear();
        String path = "/storage/emulated/0/Download/InspRec";
        File file = new File(path);
        File[] files = file.listFiles();

        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            for (File file1 : files) {
                if (file1.getPath().endsWith(".mp4")) {
                    video_List.add(new VideoModel(file1.getPath()));
                }
            }
        }
    }
    private void permissionDialog(String name) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(name + " permission Denied ")
                .setMessage(name + " permission not granted. Please grant " + name + " permission from app settings.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void buttonClicks() {
        inspectionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = displayFileDataRecyclerview.getLayoutManager().onSaveInstanceState();

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

    }

    private void findIds() {
        sortByRecyclerView = findViewById(R.id.sortByRecyclerview);
        displayFileDataRecyclerview = findViewById(R.id.displayFileDataRecyclerview);
        inspectionBTN = findViewById(R.id.inspectionBTN);
        uploadBTN = findViewById(R.id.uploadBTN);
        folderEmpty = findViewById(R.id.folderEmptyTV);
    }

    private void setAdapterData() {
        sortByRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        sortByAdapter = new SortByAdapter(sortBy_List, this);
        sortByRecyclerView.setAdapter(sortByAdapter);
    }

    public void FindSortList(ArrayList<FileModel> arrayList, String name) {
        Collections.sort(arrayList, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel fileData, FileModel t1) {
                if (name == "Date") {
                    return fileData.getFileDateTime().compareTo(t1.getFileDateTime());
                }
                if (name == "Filename") {
                    return fileData.getFileName().compareTo(t1.getFileName());
                }
                if (name == "Facility") {
                    String facname=sqliteModel.getFacilityName(fileData.getFacID());
                    String facname2=sqliteModel.getFacilityName(t1.getFacID());

                    return facname.compareTo(facname2);
                }
                return 0;
            }
        });
    }

    @Override
    public void sortBy(String s) {
        SortBy=s;

        ArrayList<FileModel> dateSortList = new ArrayList<>(tempList);
        ArrayList<FileModel> filenameSortList = new ArrayList<>(tempList);
        ArrayList<FileModel> facilitySortList = new ArrayList<>(tempList);

        displayFileDataRecyclerview.setLayoutManager(new GridLayoutManager(context, 2));
        if (s == "Date") {
            state = displayFileDataRecyclerview.getLayoutManager().onSaveInstanceState();
            FindSortList(dateSortList, "Date");
            videoAdapter = new VideoAdapter(this, dateSortList);
            displayFileDataRecyclerview.setAdapter(videoAdapter);
        } else if (s == "Filename") {
            state = displayFileDataRecyclerview.getLayoutManager().onSaveInstanceState();
            FindSortList(filenameSortList, "Filename");
            videoAdapter = new VideoAdapter(this, filenameSortList);
            displayFileDataRecyclerview.setAdapter(videoAdapter);
        } else if (s == "Facility") {
            state = displayFileDataRecyclerview.getLayoutManager().onSaveInstanceState();
            FindSortList(facilitySortList, "Facility");
            videoAdapter = new VideoAdapter(this, facilitySortList);
            displayFileDataRecyclerview.setAdapter(videoAdapter);
        } else {
            videoAdapter = new VideoAdapter(this, tempList);
            displayFileDataRecyclerview.setAdapter(videoAdapter);
        }
        displayFileDataRecyclerview.getLayoutManager().onRestoreInstanceState(state);
    }

    @Override
    protected void onPause() {
        super.onPause();
        state = displayFileDataRecyclerview.getLayoutManager().onSaveInstanceState();

        sharedPreferences=getSharedPreferences("Sort_by",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SortVia", SortBy);
        editor.apply();

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



