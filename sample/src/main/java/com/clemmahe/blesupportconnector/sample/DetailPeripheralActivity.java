package com.clemmahe.blesupportconnector.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.clemmahe.blesupportconnector.R;
import com.clemmahe.bluetoothconnectorlibrary.baseactivity.BlePermissionsActivity;
import com.clemmahe.bluetoothconnectorlibrary.manager.BleManager;
import com.clemmahe.bluetoothconnectorlibrary.manager.IConnectionListener;
import com.clemmahe.bluetoothconnectorlibrary.scanner.BluetoothCompatDevice;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailPeripheralActivity extends BlePermissionsActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textview_detailperipheral_stateconnection)
    TextView textviewDetailperipheralStateconnection;


    private BluetoothCompatDevice mDevice;
    private BleManager bleManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_peripheral);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Show icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        checkBundle(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkBundle(intent);
    }

    /**
     * Get BluetoothCompatDevice out of the intent
     *
     * @param intent Intent
     */
    private void checkBundle(Intent intent) {
        if (intent != null && intent.hasExtra(BluetoothCompatDevice.INTENT_KEY_BLUETOOTHCOMPATDEVICE)) {
            mDevice = intent.getParcelableExtra(BluetoothCompatDevice.INTENT_KEY_BLUETOOTHCOMPATDEVICE);
            String name = mDevice.getBluetoothDevice().getName();
            getSupportActionBar().setTitle(getString(R.string.title_activity_detail_peripheral,name));
        }
    }


    /**
     * Conntect gatt
     */
    private void connectGatt() {
        if (mDevice != null) {
            bleManager.connectToDevice(mDevice, mConnectionListener);
        }else{
            showStateError(R.string.detailperipheral_status_null);
        }
    }

    private void disconnectGatt() {
        if(mDevice!=null){
            bleManager.finish();
        }
    }


    @Override
    protected void onDestroy() {
        disconnectGatt();
        super.onDestroy();
    }



    @Override
    protected void appBluetoothReady(boolean ready, int status) {
        bleManager = BleManager.getInstance(getApplicationContext());
        if (ready && mDevice != null) {
            connectGatt();
        } else {
            finish();
        }
    }

    /**
     * Class instance of connection listener
     */
    private IConnectionListener mConnectionListener = new IConnectionListener() {
        @Override
        public void onConnecting() {
            showStateOk(R.string.detailperipheral_status_connecting);
        }

        @Override
        public void onConnected() {
            showStateOk(R.string.detailperipheral_status_connected);
        }

        @Override
        public void onDisconnecting() {
            showStateOk(R.string.detailperipheral_status_disconnecting);
        }

        @Override
        public void onDisconnected() {
            showStateError(R.string.detailperipheral_status_disconnected);
        }

        @Override
        public void onServicesDiscovered() {
            showStateOk(R.string.detailperipheral_status_servicediscovered);
        }

        @Override
        public void onError() {
            showStateError(R.string.detailperipheral_status_disconnected);
        }
    };

    /**
     * Show state error with message
     * @param messageResId Message. has to be in res/strings
     */
    private void showStateError(final int messageResId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textviewDetailperipheralStateconnection.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(),R.color.colorDetailPeripheralStateBackgroundError));
                textviewDetailperipheralStateconnection.setText(getString(messageResId));
            }
        });
    }

    /**
     * Show state ok with message
     * @param messageResId Message. has to be in res/strings
     */
    private void showStateOk(final int messageResId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textviewDetailperipheralStateconnection.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(),R.color.colorDetailPeripheralStateBackgroundOk));
                textviewDetailperipheralStateconnection.setText(getString(messageResId));
            }
        });
    }
}
