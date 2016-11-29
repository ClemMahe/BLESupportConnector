package com.clemmahe.bluetoothconnectorlibrary.manager;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.clemmahe.bluetoothconnectorlibrary.scanner.BluetoothCompatDevice;

import java.lang.reflect.Method;

/**
 * Handles gatt calls on UiThread
 * Created by clem on 29/11/2016.
 */

public class GattManager {

    private Context mContext;
    private Handler uiHandler;
    private BluetoothGatt mGatt;


    /**
     * Constructor
     * @param context Context
     */
    public GattManager(final Context context){
        this.mContext = context;
        this.uiHandler = new Handler(Looper.getMainLooper());
    }


    /**
     * Connect Gatt
     * @param device BluetoothCompatDevice
     */
    public void connectGatt(final BluetoothCompatDevice device){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                mGatt = device.getBluetoothDevice().connectGatt(mContext, false, mGattCallback);
            }
        });
    }


    /**
     * Disconnect Gatt
     */
    public void disconnectGatt(){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mGatt!=null){
                    refreshDeviceCache(mGatt);
                    mGatt.disconnect();
                    mGatt.close();
                    mGatt = null;
                }
            }
        });
    }


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };


    /**
     * refreshDeviceCache
     * @param gatt BluetoothGatt
     * @return boolean
     */
    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            //Cannot call methods & cannot refresh
        }
        return false;
    }

}
