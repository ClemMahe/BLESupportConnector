package com.clemmahe.bluetoothconnectorlibrary.manager;

import android.content.Context;
import android.os.Handler;

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

    //Handler in current thread (uithread or not)
    private Handler mHandler;


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
        this.mHandler = new Handler();
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
    public void connectToDevice(final BluetoothCompatDevice deviceConnect,
                                final IConnectionListener connectionListener,
                                final long timeoutMs){

        //Cannot connect
        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCompatScanner.stopCompatScan();
                connectionListener.onError();
            }
        },timeoutMs);

        //Start compat scan
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
                //Scan failed - as there will be multiple scans done, no need for callbacks
            }
            @Override
            public void onScanEnded() {
                //Scan ended - as there will be multiple scans done, no need for callbacks
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
