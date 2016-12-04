package com.clemmahe.blesupportconnector.sample;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clemmahe.blesupportconnector.R;
import com.clemmahe.bluetoothconnectorlibrary.scanner.BluetoothCompatDevice;

import java.util.List;

/**
 * PeripheralsAdapter
 * Created by clem on 03/12/2016.
 */

public class PeripheralsAdapter extends RecyclerView.Adapter<PeripheralsAdapter.PeripheralViewHolder> {


    private List<BluetoothCompatDevice> mListDevices;


    /**
     * Constructor
     * @param listDevice List<BluetoothCompatDevice>
     */
    public PeripheralsAdapter(List<BluetoothCompatDevice> listDevice) {
        this.mListDevices = listDevice;
    }


    @Override
    public PeripheralViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_listperipheral, parent, false);
        PeripheralViewHolder vh = new PeripheralViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PeripheralViewHolder holder, int position) {
        BluetoothDevice device = this.mListDevices.get(position).getBluetoothDevice();
        if(device!=null) {
            //Name
            String name = this.mListDevices.get(position).getBluetoothDevice().getName();
            if(name!=null && name!="null") {
                holder.deviceName.setText(this.mListDevices.get(position).getBluetoothDevice().getName());
            }else{
                holder.deviceName.setText("n/a");
            }
            holder.deviceRssi.setText(String.valueOf(this.mListDevices.get(position).getPeripheralRssi()));
            holder.deviceAdresseMac.setText(this.mListDevices.get(position).getBluetoothDevice().getAddress());
        }else{
            holder.deviceName.setText("Device cannot be found");
            holder.deviceAdresseMac.setText("-");
            holder.deviceRssi.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        return  this.mListDevices.size();
    }



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PeripheralViewHolder extends RecyclerView.ViewHolder {
        public TextView deviceName;
        public TextView deviceAdresseMac;
        public TextView deviceRssi;
        public PeripheralViewHolder(View v) {
            super(v);
            deviceName = (TextView) v.findViewById(R.id.textview_listperipheral_item_name);
            deviceAdresseMac = (TextView) v.findViewById(R.id.textview_listperipheral_item_macaddress);
            deviceRssi = (TextView) v.findViewById(R.id.textview_listperipheral_item_rssi);
        }
    }
}
