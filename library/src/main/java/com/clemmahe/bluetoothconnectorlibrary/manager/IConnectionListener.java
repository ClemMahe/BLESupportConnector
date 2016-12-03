package com.clemmahe.bluetoothconnectorlibrary.manager;

/**
 * IConnectionListener
 * Created by clem on 01/12/2016.
 */

public interface IConnectionListener {

    void onConnecting();

    void onConnected();

    void onDisconnecting();

    void onDisconnected();

    void onServicesDiscovered();

}
