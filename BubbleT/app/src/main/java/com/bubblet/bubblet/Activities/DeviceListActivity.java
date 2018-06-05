package com.bubblet.bubblet.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bubblet.bubblet.Adapter.DeviceListAdapter;
import com.bubblet.bubblet.Model.DeviceItem;
import com.bubblet.bubblet.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {
    SharedPreferences prefs;
    private static final int REQUEST_BLUETOOTH = 1111;
    private Toolbar mToolbarDevices;
    private RecyclerView mRecyDevicelist;
    DeviceListAdapter deviceListAdapter;
    private BluetoothAdapter BTAdapter;
    private ArrayList<DeviceItem> deviceItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        initView();
        initlist();
        getBluetoothdevices();


    }


    private void initlist() {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyDevicelist.setLayoutManager(mLayoutManager);
        mRecyDevicelist.setItemAnimator(new DefaultItemAnimator());

    }

    private void initView() {

        prefs = getSharedPreferences(getString(R.string.storeDevice), 0);
        mToolbarDevices = (Toolbar) findViewById(R.id.toolbar_devices);
        mRecyDevicelist = (RecyclerView) findViewById(R.id.recy_devicelist);

        setSupportActionBar(mToolbarDevices);


    }

    private void getBluetoothdevices() {
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        } else
            getdevices();
    }

    private void getdevices() {
        deviceItemList = new ArrayList<DeviceItem>();

        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                deviceItemList.add(newDevice);
            }

            deviceListAdapter = new DeviceListAdapter(this, deviceItemList, prefs);
            mRecyDevicelist.setAdapter(deviceListAdapter);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH)
            if (resultCode == RESULT_OK)
                getdevices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.devicemenu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                getBluetoothdevices();
                return true;

            case R.id.done:
                savetoDB();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void savetoDB() {

        SharedPreferences.Editor editor = prefs.edit();
         int _size = 0;
         int counter = 0;
        editor.clear();

        for (int i = 0; i < deviceItemList.size(); i++) {
            if (deviceItemList.get(i).isChecked) {
                editor.putString("getdevice_" + (counter++), deviceItemList.get(i).address);

            }
        }

        editor.putInt("_size" , counter);
        editor.apply();
        finish();
    }
}
