package group6.iobubblet;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BTScanActivity extends AppCompatActivity {

    private BluetoothAdapter btAdap;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    HashMap<String, Integer> signals = new HashMap<>();
    HashMap<String, BluetoothDevice> devices = new HashMap<>();


    protected void setList() {
        final ListView listview = findViewById(R.id.listview);

        ArrayList<String> list = new ArrayList<>();
        Iterator it = signals.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            list.add(""+pair.getKey()+pair.getValue()+devices.get(pair.getKey()).getName());
        }


        Toast.makeText(getApplicationContext(),"" + list.size(), Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // A Bluetooth device was found
                // Getting device information from the intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                signals.put(device.getAddress(), rssi);
                devices.put(device.getAddress(), device);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btscan);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdap = bluetoothManager.getAdapter();

        if (btAdap == null || !btAdap.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);


        final FloatingActionButton scanButton = findViewById(R.id.scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBluetooth();
            }
        });

    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            signals.put(result.getDevice().getAddress(), result.getRssi());
            //Log.i("bt", "Device found: " +  calcDistance(result.getRssi(), result.getTxPower()));
        }
    };

    public void scanBluetooth(){
        /*
        final BluetoothLeScanner btScanner  = btAdap.getBluetoothLeScanner();
        btScanner.startScan(scanCallback);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(scanCallback);
                setList();
            }
        }, SCAN_PERIOD);
*/

        btAdap.startDiscovery();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btAdap.cancelDiscovery();
                setList();
            }
        }, SCAN_PERIOD);

    }

    public double calcDistance(int rssi, int txp){

        double ratio = rssi / txp;
        if (rssi == 0.0) {
            return -1.0;
        }
        else if (ratio < 1.0) {
            return Math.pow(ratio, 10.0);
        }
        return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;

    }

}

