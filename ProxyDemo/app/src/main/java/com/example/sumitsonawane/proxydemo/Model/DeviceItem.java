package com.example.sumitsonawane.proxydemo.Model;

/**
 * Created by sumitsonawane on 5/26/2018.
 */

public class DeviceItem {


    public final String name;
    public final String address;
    public final String aFalse;
    public boolean isChecked = false;

    public DeviceItem(String name, String address, String aFalse) {

        this.name = name;
        this.address = address;
        this.aFalse = aFalse;
    }

    public void isSelected(boolean isChecked){
         this.isChecked = isChecked;
    }



}
