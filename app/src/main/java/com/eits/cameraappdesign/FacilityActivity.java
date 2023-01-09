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
    ArrayList<String> facilityList = new ArrayList<>();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor EditList;
    Gson gson = new Gson();

    Parcelable recyclerViewState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility);

        sharedPreferences = getSharedPreferences("FacilityList", MODE_PRIVATE);
        EditList = sharedPreferences.edit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        String json = sharedPreferences.getString(TAG, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        facilityList = gson.fromJson(json, type);

        toolBar();
        findIds();
        buttonClicks();

        if (!(facilityList == null)) {
            setAdapterData();

        } else {
            facilityList = new ArrayList<>();
            facilityTV.setText("Add Facility from add Button : ");
        }
    }

    private void setAdapterData() {
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
                String getVal = bottomSheetEditText.getText().toString().trim();
                if (getVal.isEmpty()) {
                    Toast.makeText(FacilityActivity.this, "Add Something Facility", Toast.LENGTH_SHORT).show();
                } else {
                    facilityList.add(getVal);
                    String json = gson.toJson(facilityList);

                    EditList.putString(TAG, json);
                    EditList.commit();

                    Toast.makeText(FacilityActivity.this, "Save", Toast.LENGTH_SHORT).show();
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
}