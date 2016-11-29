package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

/**
 * Abstract CompatScanner
 * Created by clem on 24/11/2016.
 */

public abstract class CompatScanner {


    private static final long PERIOD_SCAN_MS = 3000;


    private Context mContext;
    private Handler mScanHandler;

    protected BluetoothManager mBluetoothManager;
    protected BluetoothAdapter mBluetoothAdapter;
    protected ScanCompatCallback mScanCallback;

    protected boolean isScanning;


    /**
     * Constructor of compat Scanner
     * @param context Context
     */
    protected CompatScanner(final Context context){
        this.mContext = context;
        this.mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        this.mScanHandler = new Handler();
    }


    /**
     * Builder
     * @param context Context
     * @return CompatScanner
     */
    public static CompatScanner getInstance(final Context context){
        CompatScanner instance = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            instance = new LollipopScanner(context);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            instance = new JellyBeanScanner(context);
        }
        return instance;
    }


    /**
     * Stop scan method
     */
    abstract protected void stopScan();

    /**
     * Stop scan method
     */
    abstract protected void startScan();


    /**
     * Start compat scan
     * @param callback ScanCompatCallback
     */
    public void startCompatScan(final ScanCompatCallback callback,
                                long periodScanMs, long pauseBetweenScanMs){
        this.mScanCallback = callback;
        this.startScan();
        this.mScanHandler.post(scanRunnable);
    }


    public void stopCompatScan(){
        this.mScanHandler.removeCallbacks(scanRunnable);
    }


    /**
     * Scan runnable
     */
    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
            mScanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                    mScanHandler.post(scanRunnable);
                }
            },PERIOD_SCAN_MS);
        }
    };

    /**
     * Get Scanning status
     * @return boolean
     */
    public boolean isScanning() {
        return isScanning;
    }

    /**
     * Set Scanning
     * @param scanning boolean
     */
    protected void setScanning(boolean scanning) {
        isScanning = scanning;
    }
}
