package com.eits.cameraappdesign.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eits.cameraappdesign.Interface.SortBy_Interface;
import com.eits.cameraappdesign.R;
import com.eits.cameraappdesign.model.sortBy_modelData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SortByAdapter extends RecyclerView.Adapter<SortByAdapter.holder> {
    ArrayList<sortBy_modelData> arrayList;
    holder lastClickHolder;
    SortBy_Interface sortBy_interface;

    public SortByAdapter(ArrayList<sortBy_modelData> arrayList, SortBy_Interface sortBy_interface) {
        this.arrayList = arrayList;
        this.sortBy_interface = sortBy_interface;
    }

    @NonNull
    @Override
    public SortByAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sort_by_item, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SortByAdapter.holder holder, @SuppressLint("RecyclerView") int position) {
        holder.sortBy_TV.setText(arrayList.get(position).getSortByName());
        holder.sortBy_TV_cancel.setText(arrayList.get(position).getSortByName());

        String sortName = arrayList.get(position).getSortByName();


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               sortPosition=position;
//                if(sortPosition>-1)
//                {
//                   if(holder.sortBy_TV.getVisibility()==View.VISIBLE)
//                   {
//                       holder.sortBy_TV_cancel.setVisibility(View.VISIBLE);
//                       holder.sortBy_TV.setVisibility(View.GONE);
//                   }
//                  else if(holder.sortBy_TV_cancel.getVisibility()==View.VISIBLE)
//                    {
//                        holder.sortBy_TV_cancel.setVisibility(View.GONE);
//                        holder.sortBy_TV.setVisibility(View.VISIBLE);
//                    }
//                }

                if(lastClickHolder==null)
                {
                    lastClickHolder=holder;
                    openNew(holder);
                    sortBy_interface.sortBy(arrayList.get(position).getSortByName());
                }
                else
                {
                    if(lastClickHolder==holder)
                    {
                       closeSame(lastClickHolder);
                       lastClickHolder=null;
                        sortBy_interface.sortBy(null);
                    }else
                    {
                        closePrevious(lastClickHolder);
                        lastClickHolder=holder;
                        openNew(holder);
                        sortBy_interface.sortBy(arrayList.get(position).getSortByName());
                    }
                }



//                for (int i = 0; i < 3; i++) {
//                    if (i == position) {
//                        if (arrayList.get(position).isShow() == true) {
//                            arrayList.get(position).setShow(false);
//                        } else {
//                            arrayList.get(position).setShow(true);
//                        }
//                    } else {
//                        arrayList.get(i).setShow(false);
//                    }
//                }
//
//                if (arrayList.get(position).isShow()) {
//
//                    holder.sortBy_TV_cancel.setVisibility(View.VISIBLE);
//                    holder.sortBy_TV.setVisibility(View.GONE);
//                } else {
//
//                    holder.sortBy_TV_cancel.setVisibility(View.GONE);
//                    holder.sortBy_TV.setVisibility(View.VISIBLE);
//                }
//

            }

        });


//        holder.sortBy_TV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.sortBy_TV.setVisibility(View.GONE);
//                holder.sortBy_TV_cancel.setVisibility(View.VISIBLE);
//
//                if (!sortList.contains(sortName)) {
//                    sortList.add(sortName);
//                    sortBy_interface.sortBy(sortList);
//                }
//
//
//            }
//        });
//
//        holder.sortBy_TV_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String sortName = arrayList.get(position).getSortByName();
//
//                holder.sortBy_TV.setVisibility(View.VISIBLE);
//                holder.sortBy_TV_cancel.setVisibility(View.GONE);
//
//                sortList.remove(sortName);
//                sortBy_interface.sortBy(sortList);
//                if(sortList.size()==0) {
//                    sortBy_interface.sortByCancel();
//                }
//
//
//
//            }
//        });


    }

    private void closeSame(holder lastClickHolder) {
        lastClickHolder.sortBy_TV.setVisibility(View.VISIBLE);
        lastClickHolder.sortBy_TV_cancel.setVisibility(View.GONE);
    }

    private void openNew(holder holder) {
        holder.sortBy_TV.setVisibility(View.GONE);
        holder.sortBy_TV_cancel.setVisibility(View.VISIBLE);
    }

    private void closePrevious(holder lastClickHolder) {
        lastClickHolder.sortBy_TV.setVisibility(View.VISIBLE);
        lastClickHolder.sortBy_TV_cancel.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        TextView sortBy_TV;
        TextView sortBy_TV_cancel;
        LinearLayout sort_item_LL;

        public holder(@NonNull View itemView) {
            super(itemView);
            sort_item_LL = itemView.findViewById(R.id.sort_by_LL);
            sortBy_TV = itemView.findViewById(R.id.sort_by_item_textView);
            sortBy_TV_cancel = itemView.findViewById(R.id.sort_by_item_textView_cancel);
        }
    }
}
