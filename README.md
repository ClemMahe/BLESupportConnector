# BLESupportConnector
Working recently on a BLE project, and facing problems with Android BLE stack, this project goal is to includes all needed workarounds (I definitively hate this word) to assure compatibility towards Android 4.4 to Android 7.1. It also provide a manager class which will handle GATT call on Uithread, and manage instance of scanner & listeners.

Workarounds (:sigh:) included :
- On disconnection, call Gatt method "refresh" before disconnecting & closing Gatt.
- Scan will be called and restarted every two seconds, you will have error logs warning "BtGatt.GattService: App is scanning too frequently" but that will assure you to discover quickly your device. Also on some device, the callback "onDeviceFound" is only called once per scan, so multiple scan is needed.

Be sure to "stop" scan only when you don't need it anymore. If you stop scan too early before beeing connected, you may not be able to connect on some phone. For example, it will be working on N5X/S6/S7 but won't work at all on Samsung A3/A5.


## Sample 

A sample app is included as a module of the project.
