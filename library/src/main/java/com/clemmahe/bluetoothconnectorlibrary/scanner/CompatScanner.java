package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Abstract CompatScanner
 * Created by clem on 24/11/2016.
 */

public abstract class CompatScanner {


    private Context mContext;
    private long mPeriodScan;

    protected BluetoothManager mBluetoothManager;
    protected BluetoothAdapter mBluetoothAdapter;
    protected ScanCompatCallback mScanCallback;

    protected boolean isScanning;


    //Scheduler
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> taskScan;



    /**
     * Constructor of compat Scanner
     * @param context Context
     */
    protected CompatScanner(final Context context){
        this.mContext = context;
        this.mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }


    /**
     * Builder
     * @param context Context
     * @return CompatScanner
     */
    public static CompatScanner getInstance(final Context context){
        CompatScanner instance = new JellyBeanScanner(context);
        //Jellybeanscanner works better
        /*
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            instance = new LollipopScanner(context);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            instance
        }*/
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
        this.isScanning = true;
        startScanTask();
    }


    /**
     * Stop compat scan
     */
    public void stopCompatScan(){
        isScanning = false;
        if(mScanCallback!=null){
            mScanCallback.onScanEnded();
        }
        stopScanIfCan();
    }

    /**
     * Start scan task
     */
    private void startScanTask(){
        stopScanIfCan();
        taskScan = scheduledExecutorService.
                scheduleAtFixedRate(stopStartScanRunnable, 50, mPeriodScan, TimeUnit.MILLISECONDS);
    }

    /**
     * Stop scan if i can
     */
    private void stopScanIfCan(){
        if(taskScan!=null && !taskScan.isCancelled()){
            taskScan.cancel(true);
        }
        if(mBluetoothAdapter!=null) {
            stopScan();
        }
    }


    /**
     * Stop & startScan (called periodically)
     */
    private Runnable stopStartScanRunnable = new Runnable() {
        @Override
        public void run() {
            if(mBluetoothAdapter!=null){
                stopScan();
                startScan();
            }
        }
    };


    /**
     * Get Scanning status
     * @return boolean
     */
    public boolean isScanning() {
        return isScanning;
    }


}
