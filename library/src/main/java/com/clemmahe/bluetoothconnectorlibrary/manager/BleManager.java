package com.clemmahe.bluetoothconnectorlibrary.manager;

import android.content.Context;

import com.clemmahe.bluetoothconnectorlibrary.scanner.BluetoothCompatDevice;
import com.clemmahe.bluetoothconnectorlibrary.scanner.CompatScanner;
import com.clemmahe.bluetoothconnectorlibrary.scanner.ScanCompatCallback;

/**
 * BleManager handles scan, connect, disconnect, send data
 * Created by clem on 29/11/2016.
 */

public class BleManager {

    private static final long DEFAULT_SCAN_PERIOD = 2000;

    private Context mContext;
    private CompatScanner mCompatScanner;
    private GattManager mGattManager;

    /**
     * Constructor
     * @param context Context
     */
    public BleManager(final Context context) {
        this.mContext = context;
        this.mCompatScanner = CompatScanner.getInstance(mContext);
        this.mGattManager = new GattManager(mContext);
    }

    /**
     * Scan devices
     * @param callBack ScanCompatCallback
     * @param periodScan long period
     */
    public void listAvailableDevices(final ScanCompatCallback callBack, long periodScan){
        this.mCompatScanner.startCompatScan(callBack, periodScan);
    }

    /**
     * Scan devices with default period
     * @param callBack ScanCompatCallback
     */
    public void listAvailableDevices(final ScanCompatCallback callBack){
        this.mCompatScanner.startCompatScan(callBack, DEFAULT_SCAN_PERIOD);
    }

    /**
     * Connect to device
     * @param device
     */
    public void connectToDevice(final BluetoothCompatDevice device){

    }


}
