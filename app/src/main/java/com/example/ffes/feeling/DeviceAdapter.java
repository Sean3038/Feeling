package com.example.ffes.feeling;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

/**
 * Created by Ffes on 2017/12/11.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    List<BluetoothDevice> devices;
    OnItemClick callBack;

    DeviceAdapter(List<BluetoothDevice> devices,OnItemClick callBack){
        this.devices=devices;
        this.callBack=callBack;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BluetoothDevice bluetoothDevice=devices.get(holder.getAdapterPosition());
        holder.content.setText(bluetoothDevice.getName()+"\n"+bluetoothDevice.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onClick(bluetoothDevice.getAddress());
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            content=(TextView) itemView.findViewById(R.id.content);
        }
    }

    public interface OnItemClick{
        void onClick(String address);
    }
}
