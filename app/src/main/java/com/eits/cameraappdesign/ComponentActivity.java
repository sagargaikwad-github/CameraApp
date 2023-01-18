package com.eits.cameraappdesign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
    Spinner spinner;
    Button startCameraBTN;
    ArrayList<ComponentModel>ComponentList=new ArrayList<>();
    String[] componentArray;
    SqliteModel sqliteModel;

    TextInputEditText siteLocation;
    TextInputEditText note;

    int FacId;
    int SpinnerSelectedItem;
    String SiteLocation=null;
    String Note=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sqliteModel=new SqliteModel(this);

        Bundle getValues=getIntent().getExtras();
        FacId=getValues.getInt("FacID");


        ComponentList=sqliteModel.getComponentData();
        componentArray=new String[ComponentList.size()];

        for(int i=0;i<ComponentList.size();i++)
        {
            componentArray[i]=ComponentList.get(i).getCompName();
        }

        toolBar();
        findIds();
        setAdapter();


        startCameraBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ComponentActivity.this,CameraActivity.class);
                if(SpinnerSelectedItem==0)
                {
                    Toast.makeText(ComponentActivity.this, "Choose Component from Dropdown", Toast.LENGTH_SHORT).show();
                }else
                {
                    SiteLocation=siteLocation.getText().toString();
                    Note=note.getText().toString();

                    intent.putExtra("FacID",FacId);
                    intent.putExtra("CompID",SpinnerSelectedItem);
                    intent.putExtra("SiteLocation",SiteLocation);
                    intent.putExtra("Note",Note);


                    startActivity(intent);
                }

            }
        });
    }

    private void setAdapter() {
        ArrayAdapter spinnerAdapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,componentArray);
        spinner.setAdapter(spinnerAdapter);
    }

    private void findIds() {
        spinner=findViewById(R.id.componentTypeSpinner);
        spinner.setOnItemSelectedListener(this);
        startCameraBTN=findViewById(R.id.startCameraBTN);

        siteLocation=findViewById(R.id.componentLocation_EditText);
        note=findViewById(R.id.componentNote_EditText);
    }



    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.component_Toolbar);
        setSupportActionBar(toolbar);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        SpinnerSelectedItem=ComponentList.get(i).getCompId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
