package com.clemmahe.bluetoothconnectorlibrary.logger;

import android.util.Log;

import com.clemmahe.bluetoothconnectorlibrary.BuildConfig;

/**
 * Handles logs in one place
 * Created by clem on 01/12/2016.
 */

public class Logger{

    private static final String TAG = "BluetoothConnector";


    public static void d(String message){
        if(BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message){
        if(BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void v(String message){
        if(BuildConfig.DEBUG) {
            Log.v(TAG, message);
        }
    }

    public static void w(String message){
        if(BuildConfig.DEBUG) {
            Log.w(TAG, message);
        }
    }


}
