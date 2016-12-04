package com.clemmahe.blesupportconnector.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.clemmahe.blesupportconnector.R;
import com.clemmahe.bluetoothconnectorlibrary.baseactivity.BlePermissionsActivity;
import com.clemmahe.bluetoothconnectorlibrary.manager.BleManager;
import com.clemmahe.bluetoothconnectorlibrary.scanner.BluetoothCompatDevice;
import com.clemmahe.bluetoothconnectorlibrary.scanner.ScanCompatCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListPeripheralsActivity extends BlePermissionsActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_listdevices_list)
    RecyclerView recyclerviewListdevicesList;
    @BindView(R.id.content_list_peripherals)
    RelativeLayout contentListPeripherals;

    //AlertDialogs
    private AlertDialog dialogBluetooth;
    private AlertDialog dialogLocationServices;

    //RecyclerAdapter
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //List of devices
    private List<BluetoothCompatDevice> mListDevices;

    //Scanner
    private BleManager bleManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_peripherals);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Show icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //Init adapter
        initAdapter();
    }


    //Called in onResume
    @Override
    protected void appBluetoothReady(boolean ready, int status) {
        if (!ready && status == BlePermissionsActivity.REQUIREMENTS_ISSUE_SETTINGS_BLUETOOTH_NOT_ACTIVATED) {
            askForBluetooth();
        } else if (!ready && status == BlePermissionsActivity.REQUIREMENTS_ISSUE_SETTINGS_LOCATION_NOT_ACTIVATED) {
            askForLocationServices();
        } else if (!ready && status == BlePermissionsActivity.REQUIREMENTS_ISSUE_SETTINGS_LOCATION_AND_BLUETOOTH_NOT_ACTIVATED) {
            askForLocationServices();
            askForBluetooth();
        } else {
            //Bluetoothadapter should not be null now
            bleManager = BleManager.getInstance(getApplicationContext());
            startScan();
        }
    }

    /**
     * Start scan
     */
    private void startScan() {
        bleManager.listAvailableDevices(mScanCallback);
    }

    /**
     * init adapter
     */
    private void initAdapter() {
        this.mListDevices = new ArrayList<>();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerviewListdevicesList.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        recyclerviewListdevicesList.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PeripheralsAdapter(mListDevices,adapterListener);

        recyclerviewListdevicesList.setAdapter(mAdapter);
    }


    /**
     * Dialog ask for Bluetooth
     */
    protected void askForBluetooth() {
        if (dialogBluetooth == null || !dialogBluetooth.isShowing()) {
            AlertDialog.Builder bluetoothBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            bluetoothBuilder.setTitle(getString(R.string.dialog_requirements_bluetooth_title));
            bluetoothBuilder.setMessage(R.string.dialog_requirements_bluetooth_message);
            bluetoothBuilder.setCancelable(false);
            bluetoothBuilder.setPositiveButton(getString(R.string.dialog_requirements_activate), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intentOpenBluetoothSettings = new Intent();
                    intentOpenBluetoothSettings.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(intentOpenBluetoothSettings, REQUEST_SETTINGS_BLUETOOTH);
                    dialogInterface.dismiss();
                }
            });
            bluetoothBuilder.setNegativeButton(getString(R.string.dialog_requirements_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            dialogBluetooth = bluetoothBuilder.create();
            dialogBluetooth.show();
        }

    }

    /**
     * Dialog ask for Bluetooth
     */
    protected void askForLocationServices() {
        if (dialogLocationServices == null || !dialogLocationServices.isShowing()) {
            AlertDialog.Builder locationBuilder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            locationBuilder.setTitle(getString(R.string.dialog_requirements_location_title));
            locationBuilder.setMessage(R.string.dialog_requirements_location_message);
            locationBuilder.setCancelable(false);
            locationBuilder.setPositiveButton(getString(R.string.dialog_requirements_activate), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intentOpenBluetoothSettings = new Intent();
                    intentOpenBluetoothSettings.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intentOpenBluetoothSettings, REQUEST_SETTINGS_LOCATION);
                    dialogInterface.dismiss();
                }
            });
            locationBuilder.setNegativeButton(getString(R.string.dialog_requirements_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            dialogLocationServices = locationBuilder.create();
            dialogLocationServices.show();
        }
    }


    /**
     * Class instance of ScanCompatCallback
     */
    private ScanCompatCallback mScanCallback = new ScanCompatCallback() {
        @Override
        public void onDeviceFound(BluetoothCompatDevice device) {
            updateListDevices(device);
        }

        @Override
        public void onScanFailed() {

        }

        @Override
        public void onScanEnded() {

        }
    };


    /**
     * Adapter listener
     */
    private PeripheralsAdapter.IRecyclerAdapterConnectListener adapterListener = new PeripheralsAdapter.IRecyclerAdapterConnectListener() {
        @Override
        public void onClickConnect(BluetoothCompatDevice device) {
            //Either connect or call a new activity... in my case i open a new activity
            //The SDK will prevent you from connecting multiple times, if you click multiple times on a button...
            Intent startDetailPeripheral = new Intent(ListPeripheralsActivity.this, DetailPeripheralActivity.class);
            startDetailPeripheral.putExtra(BluetoothCompatDevice.INTENT_KEY_BLUETOOTHCOMPATDEVICE,device);
            startActivity(startDetailPeripheral);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bleManager.finish();
    }


    /**
     * updateListDevices
     *
     * @param device
     */
    private void updateListDevices(BluetoothCompatDevice device) {


        BluetoothCompatDevice d = BluetoothCompatDevice.searchObjectInListWithMacAddress(mListDevices, device);

        if (d != null) {
            //Update values/objects
            d.setBluetoothDevice(device.getBluetoothDevice());
            d.setPeripheralRssi(device.getPeripheralRssi());
            d.setPeripheralRecord(device.getPeripheralRecord());

            //!! RSSI will not be updated each time, because we don't call notifyDataSetChanged any time, it would be called to often
        } else {

            mListDevices.add(device);
            //Sort by name
            Collections.sort(mListDevices);
            mAdapter.notifyDataSetChanged();
        }

    }



}
