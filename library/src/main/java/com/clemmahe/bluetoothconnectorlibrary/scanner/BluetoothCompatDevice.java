package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.bluetooth.BluetoothDevice;

/**
 * BluetoothCompatDevice element
 * Created by clem on 24/11/2016.
 */

public class BluetoothCompatDevice {

    private BluetoothDevice mBluetoothDevice;
    private int mPeripheralRssi;
    private byte[] mPeripheralRecord;

    /**
     * Constructor of BluetoothCompatDevice
     * @param device BluetoothDevice
     * @param rssi rssi
     * @param record record
     */
    public BluetoothCompatDevice(final BluetoothDevice device, final int rssi, final byte[] record){
        this.mBluetoothDevice = device;
        this.mPeripheralRssi = rssi;
        this.mPeripheralRecord = record;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public int getPeripheralRssi() {
        return mPeripheralRssi;
    }

    public void setPeripheralRssi(int mPeripheralRssi) {
        this.mPeripheralRssi = mPeripheralRssi;
    }

    public byte[] getPeripheralRecord() {
        return mPeripheralRecord;
    }

    public void setPeripheralRecord(byte[] mPeripheralRecord) {
        this.mPeripheralRecord = mPeripheralRecord;
    }
}
