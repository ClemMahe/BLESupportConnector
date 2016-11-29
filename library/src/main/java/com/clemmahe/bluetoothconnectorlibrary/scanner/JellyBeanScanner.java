package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.Build;

import java.util.UUID;

/**
 * JellyBeanScanner for Android 4.4 & also 4.3 (Jellybean)
 * Created by clem on 24/11/2016.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class JellyBeanScanner extends CompatScanner{

    /**
     * Constructor of compat Scanner
     * @param context Context
     */
    protected JellyBeanScanner(Context context) {
        super(context);
    }

    @Override
    public void stopScan() {
        isScanning = false;
        mScanCallback.onScanEnded();
        mBluetoothAdapter.stopLeScan(mJellyBeanScanCallback);
    }

    @Override
    public void startScan() {
        isScanning = true;
        mBluetoothAdapter.startLeScan(mJellyBeanScanCallback);
    }


    /**
     * JellyBean scancallback
     */
    private BluetoothAdapter.LeScanCallback mJellyBeanScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
            if(mScanCallback!=null){
                BluetoothCompatDevice device = new BluetoothCompatDevice(bluetoothDevice, rssi, bytes);
                mScanCallback.onDeviceFound(device);
            }
        }

    };
}
