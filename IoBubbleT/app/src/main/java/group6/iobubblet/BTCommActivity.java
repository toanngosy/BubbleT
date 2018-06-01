package group6.iobubblet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BTCommActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btcomm);



        String address = getIntent().getStringExtra("address");

        connectDevice(address);

    }

    public void connectDevice(String address){



    }

}
