package com.clemmahe.blesupportconnector.sample;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.clemmahe.blesupportconnector.R;
import com.clemmahe.bluetoothconnectorlibrary.baseactivity.BlePermissionsActivity;

public class ListPeripherals extends BlePermissionsActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_peripherals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    protected void appBluetoothReady(boolean ready, int status) {
        Toast.makeText(getApplicationContext(),"Ready:"+ready+" Status:"+status,Toast.LENGTH_SHORT).show();
    }
}
