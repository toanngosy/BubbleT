package com.bubblet.bubblet.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bubblet.bubblet.Model.DeviceItem;
import com.bubblet.bubblet.R;

import java.util.ArrayList;

/**
 * Created by sumitsonawane on 5/26/2018.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.Viewholder> {
    private final Context ctx;
    private final ArrayList<DeviceItem> deviceItemList;
    private SharedPreferences prefs;

    public DeviceListAdapter(Context ctx, ArrayList<DeviceItem> deviceItemList, SharedPreferences prefs) {
        this.ctx = ctx;
        this.deviceItemList = deviceItemList;
        this.prefs = prefs;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_devicelist, parent, false);

        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        holder.txt_device_name.setText(deviceItemList.get(position).name);
        holder.txt_device_address.setText(deviceItemList.get(position).address);

        if(holder.chk_device.isChecked())
        {
            holder.chk_device.setSelected(true);

        } else
        {
            holder.chk_device.setSelected(false);

        }


        if(prefs != null){
            final int size = prefs.getInt("_size", 0);
            final String array[] = new String[size];
            for (int i = 0; i < size; i++){
                for (DeviceItem deviceItem : deviceItemList) {
                      if(deviceItem.address.equalsIgnoreCase(array[i])) {
                          holder.chk_device.setSelected(true);

                      }
                }
            }



        }
    }

    @Override
    public int getItemCount() {
        return deviceItemList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView txt_device_name, txt_device_address;
        CheckBox chk_device;

        public Viewholder(View itemView) {
            super(itemView);
            txt_device_name = itemView.findViewById(R.id.txt_device_name);
            txt_device_address = itemView.findViewById(R.id.txt_device_address);
            chk_device = itemView.findViewById(R.id.chk_device);

            chk_device.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b)
                        deviceItemList.get(getAdapterPosition()).isSelected(true);
                    else
                        deviceItemList.get(getAdapterPosition()).isSelected(false);
                }
            });

        }
    }
}
