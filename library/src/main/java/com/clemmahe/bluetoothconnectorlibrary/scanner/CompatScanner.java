package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

/**
 * Abstract CompatScanner
 * Created by clem on 24/11/2016.
 */

public abstract class CompatScanner {

    private Context mContext;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * Constructor of compat Scanner
     * @param context Context
     */
    public CompatScanner(final Context context){
        this.mContext = context;
        this.mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
    }

}
