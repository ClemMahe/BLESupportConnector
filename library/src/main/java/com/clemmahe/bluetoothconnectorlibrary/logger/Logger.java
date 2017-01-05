package com.clemmahe.bluetoothconnectorlibrary.logger;

import android.util.Log;

import com.clemmahe.bluetoothconnectorlibrary.BuildConfig;

/**
 * Handles logs in one place
 * Created by clem on 01/12/2016.
 */

public class Logger{

    private static final String TAG = "BluetoothConnector";
    public static boolean DISPLAY_LOGS = true;

    public static void d(String message){
        if(DISPLAY_LOGS) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message){
        if(DISPLAY_LOGS) {
            Log.e(TAG, message);
        }
    }

    public static void v(String message){
        if(DISPLAY_LOGS) {
            Log.v(TAG, message);
        }
    }

    public static void w(String message){
        if(DISPLAY_LOGS) {
            Log.w(TAG, message);
        }
    }


}
