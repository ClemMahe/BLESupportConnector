package com.clemmahe.bluetoothconnectorlibrary.manager;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.clemmahe.bluetoothconnectorlibrary.BuildConfig;
import com.clemmahe.bluetoothconnectorlibrary.logger.Logger;
import com.clemmahe.bluetoothconnectorlibrary.scanner.BluetoothCompatDevice;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

/**
 * Handles gatt calls on UiThread
 * Created by clem on 29/11/2016.
 */

public class GattManager {

    private Context mContext;
    private Handler uiHandler;

    //Connection state
    private static boolean isConnected;

    //Single Gatt instance at any time
    private static BluetoothGatt mGatt;

    //Listeners
    private static IConnectionListener mConnectionListener;



    /**
     * Constructor
     * @param context Context
     */
    public GattManager(final Context context){
        this.mContext = context;
        this.uiHandler = new Handler(Looper.getMainLooper());
    }


    /**
     * Connect Gatt
     * @param device BluetoothCompatDevice
     * @param connectionListener IConnectionListener
     */
    public void connectGatt(final BluetoothCompatDevice device, final IConnectionListener connectionListener, final boolean autoConnect){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                mConnectionListener = connectionListener;
                if (device == null && connectionListener!=null) {
                    mConnectionListener.onError();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || !autoConnect) {
                        connectGattCompat(mGattManagerCallback, device.getBluetoothDevice(), autoConnect);
                    }else {
                        try {
                            Object iBluetoothGatt = getIBluetoothGatt(getIBluetoothManager());
                            if (iBluetoothGatt == null) {
                                connectGattCompat(mGattManagerCallback, device.getBluetoothDevice(), true);
                            }else {
                                BluetoothGatt bluetoothGatt = createBluetoothGatt(iBluetoothGatt, device.getBluetoothDevice());
                                if (bluetoothGatt == null) {
                                    connectGattCompat(mGattManagerCallback, device.getBluetoothDevice(), true);
                                }else{
                                    boolean connectedSuccessfully = connectUsingReflection(bluetoothGatt, mGattManagerCallback, true);
                                    if (!connectedSuccessfully) {
                                        bluetoothGatt.close();
                                        connectionListener.onError();
                                    }
                                }
                            }
                        } catch (NoSuchMethodException
                                | IllegalAccessException
                                | IllegalArgumentException
                                | InvocationTargetException
                                | InstantiationException
                                | NoSuchFieldException exception) {
                            connectGattCompat(mGattManagerCallback, device.getBluetoothDevice(), true);
                        }
                        mConnectionListener = connectionListener;
                        mGatt = device.getBluetoothDevice().connectGatt(mContext, true, mGattManagerCallback);
                    }
                }
            }
        });
    }

    //Connnect gatt compat
    private boolean connectUsingReflection(BluetoothGatt bluetoothGatt, BluetoothGattCallback bluetoothGattCallback, boolean autoConnect)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        setAutoConnectValue(bluetoothGatt, autoConnect);
        Method connectMethod = bluetoothGatt.getClass().getDeclaredMethod("connect", Boolean.class, BluetoothGattCallback.class);
        connectMethod.setAccessible(true);
        return (Boolean) (connectMethod.invoke(bluetoothGatt, true, bluetoothGattCallback));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private BluetoothGatt createBluetoothGatt(Object iBluetoothGatt, BluetoothDevice remoteDevice)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor bluetoothGattConstructor = BluetoothGatt.class.getDeclaredConstructors()[0];
        bluetoothGattConstructor.setAccessible(true);
        if (bluetoothGattConstructor.getParameterTypes().length == 4) {
            return (BluetoothGatt) (bluetoothGattConstructor.newInstance(mContext, iBluetoothGatt, remoteDevice, TRANSPORT_LE));
        } else {
            return (BluetoothGatt) (bluetoothGattConstructor.newInstance(mContext, iBluetoothGatt, remoteDevice));
        }
    }
    private BluetoothGatt connectGattCompat(BluetoothGattCallback bluetoothGattCallback, BluetoothDevice device, boolean autoConnect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return device.connectGatt(mContext, autoConnect, bluetoothGattCallback, TRANSPORT_LE);
        } else {
            return device.connectGatt(mContext, autoConnect, bluetoothGattCallback);
        }
    }
    private Object getIBluetoothGatt(Object iBluetoothManager)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (iBluetoothManager == null) {
            return null;
        }
        Method getBluetoothGattMethod = getMethodFromClass(iBluetoothManager.getClass(), "getBluetoothGatt");
        return getBluetoothGattMethod.invoke(iBluetoothManager);
    }
    private Method getMethodFromClass(Class<?> cls, String methodName) throws NoSuchMethodException {
        Method method = cls.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method;
    }
    private Object getIBluetoothManager() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return null;
        }
        Method getBluetoothManagerMethod = getMethodFromClass(bluetoothAdapter.getClass(), "getBluetoothManager");
        return getBluetoothManagerMethod.invoke(bluetoothAdapter);
    }
    private void setAutoConnectValue(BluetoothGatt bluetoothGatt, boolean autoConnect) throws NoSuchFieldException, IllegalAccessException {
        Field autoConnectField = bluetoothGatt.getClass().getDeclaredField("mAutoConnect");
        autoConnectField.setAccessible(true);
        autoConnectField.setBoolean(bluetoothGatt, autoConnect);
    }

    /**
     * Disconnect Gatt
     */
    public void disconnectGatt(){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mGatt!=null){
                    isConnected = false;
                    //Disconnect
                    refreshDeviceCache(mGatt);
                    mGatt.disconnect();
                    mGatt.close();
                    mGatt = null;
                }
            }
        });
    }

    /**
     * Discover services
     */
    private void discoverServices(){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                mGatt.discoverServices();
            }
        });
    }

    /**
     * Get Gatt callback - Single instance
     * @return BluetoothGattCallback
     */
    private BluetoothGattCallback mGattManagerCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Logger.d("GattManager onConnectionStateChange : Status:"+status+", NewState:"+newState);

            //Connection state
            if(newState == BluetoothProfile.STATE_CONNECTING){
                if(mConnectionListener!=null) mConnectionListener.onConnecting();
            }else if(newState == BluetoothProfile.STATE_CONNECTED){
                isConnected = true;
                //Discover services
                discoverServices();
                if(mConnectionListener!=null) mConnectionListener.onConnected();
                Logger.w("GattManager.STATE_CONNECTED");
            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                if(mConnectionListener!=null) mConnectionListener.onDisconnecting();
                isConnected = false;
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                if(mConnectionListener!=null) mConnectionListener.onDisconnected();
                //Assure if isConnected that gatt is disconnected
                Logger.w("GattManager.STATE_DISCONNECTED");
                if(isConnected){
                    disconnectGatt();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(mConnectionListener!=null) mConnectionListener.onServicesDiscovered();
            Logger.w("GattManager.SERVICES_DISCOVERED");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };


    /**
     * refreshDeviceCache
     * @param gatt BluetoothGatt
     * @return boolean
     */
    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt)).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            //Cannot call methods & cannot refresh - Exception is generic but it's a workaround anyway,
            //this method is used widely and should not be
        }
        return false;
    }

}
