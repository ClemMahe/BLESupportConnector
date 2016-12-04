# BLESupportConnector
Working recently on a BLE project, and facing problems with Android BLE stack, this project goal is to includes all needed workarounds (I definitively hate this word) to assure compatibility towards Android 4.4 to Android 7.1. It also provide a manager class which will handle GATT calls on Uithread, and manage instance of scanner & listeners.

Workarounds (:sigh:) included :
- On disconnection, call Gatt method "refresh" before disconnecting & closing Gatt.
- Scan will be called and restarted every two seconds, you will have error logs warning "BtGatt.GattService: App is scanning too frequently" but that will assure you to discover quickly your device. Also on some device, the callback "onDeviceFound" is only called once per scan, so multiple scan is needed.

Be sure to "stop" scan only when you don't need it anymore. If you stop scan too early before beeing connected, you may not be able to connect on some phone. For example, it will be working on N5X/S6/S7 but won't work at all on Samsung A3/A5.

## Base activity

If your activity is focused on Bluetootk tasks, you may inherit your activity (or base activity) from <b>BlePermissionsActivity</b>, that will help you find out if Bluetooth is enabled & if location services are enabled (required if targetSDK>=21)

## Usage


1) Instanciate BleManager 

Get instance of BleManager when Bluetooh is ready. Either on "onResume" after checking if permissions & settings have been enabled.
If you inherit from <b>BlePermissionsActivity</b>, you can call it inside <b>appBluetoothReady(boolean ready, int status)</b> method, if ready.

```java 
BleManager bleManager = BleManager.getInstance(getApplicationContext());

/*You might want to assure to close bleManger when finish. Otherwise next scan may fail as device 
will be conneted & you may get "GATT errors" */
@Override
protected void onDestroy() {
    super.onDestroy();
    bleManager.finish();
}
```

2) Start scan 

```java 
bleManager.listAvailableDevices(mScanCallback);

 /**
 * Class instance of ScanCompatCallback
 */
private ScanCompatCallback mScanCallback = new ScanCompatCallback() {
    @Override
    public void onDeviceFound(BluetoothCompatDevice device) {
        updateListDevices(device);
    }

    @Override
    public void onScanFailed() {

    }

    @Override
    public void onScanEnded() {

    }
};
```

3) Connect 

Connection will be using <b>BluetoothCompatDevice</b> object. It contains android bluetooth class : <b>BluetoothDevice</b>, rssi and scanRecord.
Note that you don't have to bother with calling "discoverServices", it will be done inside the library when connected.

```java 
bleManager.connectToDevice(selectedBluetoothCompatDevice,mConnectionListener);

/**
 * Class instance of connection listener
 */
private IConnectionListener mConnectionListener = new IConnectionListener() {
    @Override
    public void onConnecting() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnecting() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onServicesDiscovered() {

    }
};
```

## Sample 

A sample app is included as a module of the project.

## Issue 

If you have any issue, don't hesitate to create tickets. If its related to scanning peripherals I may not have any solution.
I experienced some phones having difficulties scanning BLE peripherals : 
- Samsung Galaxy Note3 Android 4.4
- Samsung Galaxy S4 mini Android 4.4 (aside S4 Android 4.4 working well ...) 
