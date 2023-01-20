package com.eits.cameraappdesign;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.eits.cameraappdesign.model.ComponentModel;
import com.eits.cameraappdesign.model.SqliteModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;


public class ComponentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    SharedPreferences sharedPreferences;

    Spinner spinner;
    Button startCameraBTN;
    ArrayList<ComponentModel> ComponentList = new ArrayList<>();
    String[] componentArray;
    SqliteModel sqliteModel;

    Boolean isRecorded = false;

    TextInputEditText siteLocation;
    TextInputEditText note;

    int FacId;
    int SpinnerPosition;
    int SpinnerSelectedItem;
    String SiteLocation = null;
    String Note = null;



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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        sqliteModel = new SqliteModel(this);

        Bundle getValues = getIntent().getExtras();
        FacId = getValues.getInt("FacID");

        ComponentList = sqliteModel.getComponentData();
        componentArray = new String[ComponentList.size()];

        for (int i = 0; i < ComponentList.size(); i++) {
            componentArray[i] = ComponentList.get(i).getCompName();
        }

        toolBar();
        findIds();
        setAdapter();

        startCameraBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComponentActivity.this, CameraActivity.class);
                if (SpinnerSelectedItem == 0) {
                 //   Toast.makeText(ComponentActivity.this, "Choose Component from Dropdown", Toast.LENGTH_SHORT).show();
                } else {
                    SiteLocation = siteLocation.getText().toString();
                    Note = note.getText().toString();

                    intent.putExtra("FacID", FacId);
                    intent.putExtra("CompID", SpinnerSelectedItem);
                    intent.putExtra("SiteLocation", SiteLocation);
                    intent.putExtra("Note", Note);

                    SpinnerPosition = spinner.getSelectedItemPosition();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("SpinnerPosition", SpinnerPosition);
                    editor.putString("SiteLocation", SiteLocation);
                    editor.putString("Note", Note);
                    editor.apply();

//                    View decorView = getWindow().getDecorView();
//                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    View decorView = getWindow().getDecorView();
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    startActivity(intent);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences = getSharedPreferences("Component_Written_Data", MODE_PRIVATE);

        int spinner_sp = sharedPreferences.getInt("SpinnerPosition", 0);
        String site_sp = sharedPreferences.getString("SiteLocation", "");
        String note_sp = sharedPreferences.getString("Note", "");

        spinner.setSelection(spinner_sp);
        siteLocation.setText(site_sp);
        note.setText(note_sp);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                    getWindow().setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS);
                    return false;
            }
        });
    }


    private void setAdapter() {
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, componentArray);
        spinner.setAdapter(spinnerAdapter);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    private void findIds() {
        spinner = findViewById(R.id.componentTypeSpinner);
        spinner.setOnItemSelectedListener(this);
        startCameraBTN = findViewById(R.id.startCameraBTN);

        siteLocation = findViewById(R.id.componentLocation_EditText);
        note = findViewById(R.id.componentNote_EditText);

    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.component_Toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("SpinnerPosition", 0);
            editor.putString("SiteLocation", " ");
            editor.putString("Note", " ");
            editor.apply();

            super.onBackPressed();
            return true;

        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        SpinnerSelectedItem = ComponentList.get(i).getCompId();
        SpinnerPosition=i;



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("SpinnerPosition", 0);
        editor.putString("SiteLocation", " ");
        editor.putString("Note", " ");
        editor.apply();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
