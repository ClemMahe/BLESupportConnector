package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.annotation.TargetApi;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * LollipopScanner - is using new Lollipop scan methods
 * Created by clem on 24/11/2016.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LollipopScanner extends CompatScanner{

    private BluetoothLeScanner mScanner;

    /**
     * Constructor of compat Scanner
     * @param context Context
     */
    protected LollipopScanner(Context context) {
        super(context);
        this.mScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    @Override
    public void stopScan() {
        isScanning = false;
        mScanner.stopScan(mLollipopScanCallback);
    }

    @Override
    public void startScan() {
        isScanning = true;

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC);
        ScanSettings scanSettings = scanSettingsBuilder.build();

        mScanner.startScan(filters, scanSettings, mLollipopScanCallback);
    }


    /**
     * Lollipop scan callback
     */
    private ScanCallback mLollipopScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


}
