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


    private Context mContext;
    private Handler mScanHandler;
    private long mPeriodScan;

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
    abstract protected boolean stopScan();

    /**
     * Stop scan method
     */
    abstract protected boolean startScan();


    /**
     * Start compat scan
     * @param callback ScanCompatCallback
     */
    public void startCompatScan(final ScanCompatCallback callback,
                                long periodScanMs){
        //Start scan
        this.mPeriodScan = periodScanMs;
        this.mScanCallback = callback;

        if(!isScanning) {
            this.startScan();
            this.mScanHandler.post(scanRunnable);
        }
    }


    public void stopCompatScan(){
        //Stop scan
        this.mScanHandler.removeCallbacks(scanRunnable);
        stopScan();
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
            },mPeriodScan);
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
