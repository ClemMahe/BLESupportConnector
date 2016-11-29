package com.clemmahe.bluetoothconnectorlibrary.scanner;

/**
 * ScanCompatCallback
 * Created by clem on 29/11/2016.
 */

public interface ScanCompatCallback {

    /**
     * When device is found.
     * @param device final BluetoothCompatDevice
     */
    public void onDeviceFound(final BluetoothCompatDevice device);

    /**
     * Scan failed
     */
    public void onScanFailed();

    /**
     * Scan ended
     */
    public void onScanEnded();

}
