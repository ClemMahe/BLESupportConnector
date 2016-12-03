package com.clemmahe.bluetoothconnectorlibrary.manager;

import android.bluetooth.BluetoothGatt;
import android.os.AsyncTask;

import com.clemmahe.bluetoothconnectorlibrary.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.nordicsemi.puckcentral.bluetooth.gatt.CharacteristicChangeListener;
import no.nordicsemi.puckcentral.bluetooth.gatt.GattOperationBundle;
import no.nordicsemi.puckcentral.bluetooth.gatt.operations.GattOperation;

/**
 * SingleGattCommandQueue
 * Created by clem on 02/12/2016.
 */

public class SingleGattCommandQueue {


    private ConcurrentLinkedQueue<GattOperation> mQueue;
    private GattOperation mCurrentOperation;
    private HashMap<UUID, ArrayList<CharacteristicChangeListener>> mCharacteristicChangeListeners;
    private AsyncTask<Void, Void, Void> mCurrentOperationTimeout;

    //Handle one gatt at the time
    private BluetoothGatt mCurrentGatt;

    //Background task timeout
    public SingleGattCommandQueue() {
        mQueue = new ConcurrentLinkedQueue<>();
        mCurrentOperation = null;
        mCharacteristicChangeListeners = new HashMap<>();
    }

    private void disconnect(){
        //Cancel if can
        if(mCurrentOperationTimeout!=null && !mCurrentOperationTimeout.isCancelled()) {
            mCurrentOperationTimeout.cancel(true);
        }
        mQueue.clear();
        mCharacteristicChangeListeners.clear();
        mCurrentOperation = null;
    }

    public void setGatt(final BluetoothGatt gatt){
        this.mCurrentGatt = gatt;
    }


    public synchronized void cancelCurrentOperationBundle() {
        if (mCurrentOperation != null && mCurrentOperation.getBundle() != null) {
            for (GattOperation op : mCurrentOperation.getBundle().getOperations()) {
                mQueue.remove(op);
            }
        }
        mCurrentOperation = null;
        drive();
    }

    public synchronized void queue(GattOperation gattOperation) {
        mQueue.add(gattOperation);
        drive();
    }

    private synchronized void drive() {
        if (mCurrentOperation != null) {
            Logger.e("tried to drive, but currentOperation was not null, " + mCurrentOperation);
            return;
        }
        if (mQueue.size() == 0) {
            Logger.v("Queue empty, drive loop stopped.");
            mCurrentOperation = null;
            return;
        }

        final GattOperation operation = mQueue.poll();
        Logger.v("Driving Gatt queue, size will now become: " + mQueue.size());
        setCurrentOperation(operation);

        //Timeout
        mCurrentOperationTimeout = new AsyncTask<Void, Void, Void>() {
            @Override
            protected synchronized Void doInBackground(Void... voids) {
                try {
                    Logger.v("Starting to do a background timeout");
                    wait(operation.getTimoutInMillis());
                } catch (InterruptedException e) {
                    Logger.v("was interrupted out of the timeout");
                }
                if(isCancelled()) {
                    Logger.v("The timeout was cancelled, so we do nothing.");
                    return null;
                }
                Logger.v("Timeout ran to completion, time to cancel the entire operation bundle. Abort, abort!");
                cancelCurrentOperationBundle();
                return null;
            }

            @Override
            protected synchronized void onCancelled() {
                super.onCancelled();
                notify();
            }
        }.execute();

        //Gatt - RUN Operation
        if (mCurrentGatt != null) {
            execute(mCurrentGatt, operation);
        }


    }


    /**
     * Execute operation
     * @param gatt BluetoothGatt
     * @param operation GattOperation
     */
    private void execute(BluetoothGatt gatt, GattOperation operation) {
        if(operation != mCurrentOperation) {
            return;
        }
        operation.execute(gatt);
        if(!operation.hasAvailableCompletionCallback()) {
            setCurrentOperation(null);
            drive();
        }
    }

    public synchronized void setCurrentOperation(GattOperation currentOperation) {
        mCurrentOperation = currentOperation;
    }


    public void addCharacteristicChangeListener(UUID characteristicUuid, CharacteristicChangeListener characteristicChangeListener) {
        if(!mCharacteristicChangeListeners.containsKey(characteristicUuid)) {
            mCharacteristicChangeListeners.put(characteristicUuid, new ArrayList<CharacteristicChangeListener>());
        }
        mCharacteristicChangeListeners.get(characteristicUuid).add(characteristicChangeListener);
    }

    public void queue(GattOperationBundle bundle) {
        for(GattOperation operation : bundle.getOperations()) {
            queue(operation);
        }
    }

    public class ConnectionStateChangedBundle {
        public final int mNewState;
        public final String mAddress;

        public ConnectionStateChangedBundle(String address, int newState) {
            mAddress = address;
            mNewState = newState;
        }
    }
}