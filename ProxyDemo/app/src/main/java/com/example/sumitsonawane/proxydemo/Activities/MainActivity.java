package com.example.sumitsonawane.proxydemo.Activities;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sumitsonawane.proxydemo.Model.DeviceItem;
import com.example.sumitsonawane.proxydemo.R;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Welcome
     */
    private TextView mTxtwelcome;
    /**
     * Username
     */
    private EditText mEdtUsername;
    /**
     * Password
     */
    private EditText mEdtPassword;
    /**
     * Login
     */
    private TextView mBtnLogin;
    /**
     * Config
     */
    ProgressDialog progressBar;
    private TextView mTxtConfig;
    private List<DeviceItem> deviceItemList;
    private int count = 0;
    SharedPreferences prefs;
    BroadcastReceiver bReciever;
    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTxtwelcome = (TextView) findViewById(R.id.txtwelcome);
        mEdtUsername = (EditText) findViewById(R.id.edt_username);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mBtnLogin = (TextView) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mTxtConfig = (TextView) findViewById(R.id.txt_config);
        mTxtConfig.setOnClickListener(this);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        prefs = getSharedPreferences(getString(R.string.storeDevice), 0);
    }

    private boolean isValid() {
        if (mEdtUsername.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Username", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mEdtPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_login:
                if (isValid())
                    checkProxymity();
                break;
            case R.id.txt_config:
                Intent intent = new Intent(this, DeviceListActivity.class);
                startActivity(intent);
                break;
        }
    }

    DeviceItem newDevice;

    private void checkProxymity() {

        doDiscovery();
        IntentFilter filter2 = new IntentFilter();

        filter2.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bReciever, filter2);
         count = 0;
        final int size = prefs.getInt("_size", 0);
        final String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString("getdevice_" + i, null);


        bReciever = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    for (int i = 0; i < array.length; i++) {
                        if (device.getAddress().equalsIgnoreCase(array[i]) && rssi > -50) {

                            if(count == 0)
                            count = count + (i+1);
                            else
                                count = count +1;
                            progressBar.setMessage("Device " + count + "found");
                        }

                    }
                    if (count == size) {

                        if (progressBar != null && progressBar.isShowing())
                            progressBar.dismiss();
                        if (mEdtUsername.getText().toString().trim().equalsIgnoreCase("admin") && mEdtPassword.getText().toString().trim().equalsIgnoreCase("admin")) {
                            Intent homepageIntent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(homepageIntent);

                            finish();
                        } else
                            Toast.makeText(MainActivity.this, "Please enter correct Username and Password", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Device not in range", Toast.LENGTH_SHORT).show();
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    if (progressBar != null && progressBar.isShowing()) {
                        progressBar.setMessage("Discovered");
                        if (mBtAdapter.isDiscovering()) {
                            mBtAdapter.cancelDiscovery();
                        }

                    }
                }
                if (progressBar != null && progressBar.isShowing())
                    progressBar.dismiss();
            }
        };

        //   registerReceiver(bReciever, new IntentFilter(BluetoothDevice.ACTION_FOUND));


    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        // Indicate scanning in the title
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);
        progressBar.setMessage("Discovering...");
        progressBar.show();
        // Turn on sub-title for new devices

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bReciever != null && bReciever.isOrderedBroadcast())
            unregisterReceiver(bReciever);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bReciever != null && bReciever.isOrderedBroadcast())
            unregisterReceiver(bReciever);
    }


}
