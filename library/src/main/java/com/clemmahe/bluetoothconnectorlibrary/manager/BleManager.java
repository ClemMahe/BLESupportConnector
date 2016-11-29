package com.clemmahe.bluetoothconnectorlibrary.manager;

import android.content.Context;

import com.clemmahe.bluetoothconnectorlibrary.scanner.CompatScanner;

/**
 * BleManager handles scan, connect, disconnect, send data
 * Created by clem on 29/11/2016.
 */

public class BleManager {

    private Context mContext;

    private CompatScanner mCompatScanner;

    /**
     * Constructor
     * @param context Context
     */
    public BleManager(final Context context) {
        this.mContext = context;
        this.mCompatScanner = CompatScanner.getInstance(mContext);
    }




}
