package com.clemmahe.bluetoothconnectorlibrary.scanner;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * BluetoothCompatDevice element
 * Created by clem on 24/11/2016.
 */

public class BluetoothCompatDevice implements Comparable<BluetoothCompatDevice>, Parcelable {

    public static final String INTENT_KEY_BLUETOOTHCOMPATDEVICE = "INTENT_BLEDEVICE";

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

    /**
     * Parcelable instanciation
     * @param in Parcel
     */
    protected BluetoothCompatDevice(Parcel in) {
        mBluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        mPeripheralRssi = in.readInt();
        mPeripheralRecord = in.createByteArray();
    }

    public static final Creator<BluetoothCompatDevice> CREATOR = new Creator<BluetoothCompatDevice>() {
        @Override
        public BluetoothCompatDevice createFromParcel(Parcel in) {
            return new BluetoothCompatDevice(in);
        }

        @Override
        public BluetoothCompatDevice[] newArray(int size) {
            return new BluetoothCompatDevice[size];
        }
    };

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


    @Override
    public String toString() {
        String stringObject = super.toString();
        if(mBluetoothDevice.getAddress()!=null){
            stringObject = mBluetoothDevice.getAddress();
        }
        return stringObject;
    }



    public static BluetoothCompatDevice searchObjectInListWithMacAddress(final List<BluetoothCompatDevice> list, BluetoothCompatDevice deviceToFind){
        BluetoothCompatDevice res = null;
        try{
            for(BluetoothCompatDevice device : list){
                if(device.getBluetoothDevice().getAddress().equals(deviceToFind.getBluetoothDevice().getAddress())){
                    res = device;
                    break;
                }
            }
        }catch(NullPointerException e){
            //Some objects are null and should not be
        }
        return res;
    }


    @Override
    public int compareTo(BluetoothCompatDevice bluetoothCompatDevice) {
        int res = 0;
        try{
            res = this.getBluetoothDevice().getAddress().compareTo(bluetoothCompatDevice.getBluetoothDevice().getAddress());
        }catch(NullPointerException e){
            //Some objects is null
        }
        return res;
    }


    //Parcelable is efficient when sending objects from activity to another

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(mBluetoothDevice,flags);
        parcel.writeInt(mPeripheralRssi);
        parcel.writeByteArray(mPeripheralRecord);
    }


}
