package com.clemmahe.bluetoothconnectorlibrary.manager;

import android.content.Context;

import com.clemmahe.bluetoothconnectorlibrary.scanner.BluetoothCompatDevice;
import com.clemmahe.bluetoothconnectorlibrary.scanner.CompatScanner;
import com.clemmahe.bluetoothconnectorlibrary.scanner.ScanCompatCallback;

/**
 * BleManager handles scan, connect, disconnect, send data
 * Created by clem on 29/11/2016.
 */

public class BleManager{

    private static final long DEFAULT_SCAN_PERIOD = 2000;

    private Context mContext;
    private CompatScanner mCompatScanner;
    private GattManager mGattManager;


    //Instance
    private static BleManager instance;


    /**
     * Get instance
     * @param context Context
     * @return BleManager
     */
    public static BleManager getInstance(final Context context){
        if(instance==null){
            instance = new BleManager(context);
        }
        return instance;
    }


    /**
     * Constructor
     * @param context Context
     */
    private BleManager(final Context context) {
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
     * @param deviceConnect BluetoothCompatDevice
     * @param connectionListener IConnectionListener
     */
    public void connectToDevice(final BluetoothCompatDevice deviceConnect, final IConnectionListener connectionListener){
        this.mCompatScanner.startCompatScan(new ScanCompatCallback() {
            boolean found = false;
            @Override
            public void onDeviceFound(BluetoothCompatDevice deviceFound) {
                try {
                    if (deviceConnect.getBluetoothDevice().getAddress().equals(deviceFound.getBluetoothDevice().getAddress())) {
                        found = true;
                        mGattManager.connectGatt(deviceFound, connectionListener);
                    }
                }catch(NullPointerException e){
                    //Ignore onDeviceFound if device found has null address or null BLEdevice
                }
            }
            @Override
            public void onScanFailed() {
                if(!found) {
                    connectionListener.onError();
                }
            }
            @Override
            public void onScanEnded() {
                if(!found) {
                    connectionListener.onError();
                }
            }
        }, DEFAULT_SCAN_PERIOD);
    }

    /**
     * Finish operations
     */
    public void finish() {
        if(this.mCompatScanner!=null){
            this.mCompatScanner.stopCompatScan();
        }
        if(this.mGattManager!=null) {
            this.mGattManager.disconnectGatt();
        }
    }
}
