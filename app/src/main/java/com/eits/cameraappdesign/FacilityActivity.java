package com.eits.cameraappdesign;


import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eits.cameraappdesign.adapter.FacilityAdapter;
import com.eits.cameraappdesign.model.FacilityModel;
import com.eits.cameraappdesign.model.SqliteModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FacilityActivity extends AppCompatActivity {
    Button add_facilityBTN;
    TextView facilityTV;
    RecyclerView facilityRV;
    FacilityAdapter facilityAdapter;
    ArrayList<FacilityModel> facilityList = new ArrayList<>();

    Parcelable recyclerViewState;

    SqliteModel sqliteModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility);

        sqliteModel=new SqliteModel(this);

    }

    @Override
    protected void onResume() {
        super.onResume();


        toolBar();
        findIds();
        buttonClicks();

        facilityList=sqliteModel.getFacilityData();

        if (!(facilityList.isEmpty())) {
            setAdapterData();
        } else {
            facilityList = new ArrayList<>();
            facilityTV.setText("Add Facility from add Button : ");
        }
    }

    private void setAdapterData() {
        facilityList.clear();
        facilityList=sqliteModel.getFacilityData();

        facilityTV.setText("Select Facility from List : ");

        facilityRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, true));
        facilityAdapter = new FacilityAdapter(this, facilityList);

        facilityRV.setAdapter(facilityAdapter);
        facilityAdapter.notifyDataSetChanged();

        try {
            facilityRV.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        } catch (Exception e) {

        }
    }

    private void buttonClicks() {
        add_facilityBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (facilityList.isEmpty()) {

                } else {
                    recyclerViewState = facilityRV.getLayoutManager().onSaveInstanceState();
                }

                bottomSheetDialog();

            }
        });

    }

    private void bottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        BottomSheetBehavior<View> bottomSheetBehavior;
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheet_facility, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        TextInputLayout bottomSheetLayout = bottomSheetView.findViewById(R.id.bottomSheetLayout);
        TextInputEditText bottomSheetEditText = bottomSheetDialog.findViewById(R.id.bottomSheetET);
        Button bottomSheetSave = bottomSheetDialog.findViewById(R.id.bottomSheetSave);
        Button bottomSheetCancel = bottomSheetDialog.findViewById(R.id.bottomSheetCancel);

        bottomSheetDialog.show();

        bottomSheetSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String facilityText = bottomSheetEditText.getText().toString().trim();
                if (facilityText.isEmpty()) {

                } else {
                     Boolean isDataAdded = sqliteModel.addInFacility(facilityText);
                     if(isDataAdded==true)
                     {
                         Toast.makeText(FacilityActivity.this, "Facility Added", Toast.LENGTH_SHORT).show();
                     }
                    setAdapterData();
                    bottomSheetDialog.dismiss();
                }
            }
        });

        bottomSheetCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });


    }

    private void findIds() {
        add_facilityBTN = findViewById(R.id.add_facilityBTN);
        facilityTV=findViewById(R.id.facilityTV);
        facilityRV = findViewById(R.id.FacilityRV);
    }


    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.facility_Toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return true;
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