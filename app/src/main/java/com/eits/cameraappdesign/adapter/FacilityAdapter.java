package com.eits.cameraappdesign.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.eits.cameraappdesign.ComponentActivity;
import com.eits.cameraappdesign.FacilityActivity;
import com.eits.cameraappdesign.MainActivity;
import com.eits.cameraappdesign.R;
import com.eits.cameraappdesign.model.FacilityModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.holder> {
    Context context;
    ArrayList<FacilityModel> arrayList;
    View mdecorView;


    public FacilityAdapter(Context context, ArrayList<FacilityModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     LayoutInflater inflater=LayoutInflater.from(parent.getContext());
     View view=inflater.inflate(R.layout.facility_item,parent,false);
     return new holder(view);
    }

    private void hideSystemUI() {
        mdecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, @SuppressLint("RecyclerView") int position) {

        holder.facility_item_name_TV.setText(arrayList.get(position).getFacName());
        holder.facility_item_location_TV.setText(arrayList.get(position).getFacLocation());

        holder.facility_item_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(context, ComponentActivity.class);
//                intent.putExtra("FacID",arrayList.get(position).getFacID());
//                context.startActivity(intent);


                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

                mdecorView=bottomSheetDialog.getWindow().getDecorView();

                hideSystemUI();

                BottomSheetBehavior<View> bottomSheetBehavior;
                View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottomsheet_inspection, null);
                bottomSheetDialog.setContentView(bottomSheetView);

                bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                TextView startInspection=bottomSheetDialog.findViewById(R.id.bottomSheet_inspection_startInspection);
                TextView uploadInspection=bottomSheetDialog.findViewById(R.id.bottomSheet_inspection_uploadInspection);

                bottomSheetDialog.show();
                hideSystemUI();



                startInspection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context, MainActivity.class);
                        intent.putExtra("FacID",arrayList.get(position).getFacID());
                        context.startActivity(intent);
                        hideSystemUI();
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
           return arrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        LinearLayout facility_item_LL;
        TextView facility_item_name_TV,facility_item_location_TV;
        public holder(@NonNull View itemView) {
            super(itemView);
            facility_item_LL=itemView.findViewById(R.id.facility_item_LL);
            facility_item_name_TV=itemView.findViewById(R.id.facility_item_name_TV);
            facility_item_location_TV=itemView.findViewById(R.id.facility_item_location_TV);
        }
    }


}
