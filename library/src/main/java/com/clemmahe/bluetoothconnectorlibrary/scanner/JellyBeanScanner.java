package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;

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
    public boolean stopScan() {
        isScanning = false;
        mScanCallback.onScanEnded();
        if(mBluetoothAdapter!=null) {
            mBluetoothAdapter.stopLeScan(mJellyBeanScanCallback);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean startScan() {
        isScanning = true;
        if(mBluetoothAdapter!=null) {
            mBluetoothAdapter.startLeScan(mJellyBeanScanCallback);
            return true;
        }else{
            return false;
        }
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
