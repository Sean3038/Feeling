package com.example.ffes.feeling;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ffes on 2017/12/14.
 */

public class Watch implements Bluetooth,GetFeeling {
    public static final String TAG="WATCH";
    public static final java.util.UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String DEVICE_NAME="NONE";


    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    Context mContext;

    ProgressDialog mProgressDialog;
    private boolean isBluetoothConnected=false;
    private BluetoothDevice currentDevice;

    float heartRate;
    float temperature;
    float humidity;

    Watch(Context context){
        mContext=context;
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void connect() {
        if(isBluetoothConnected){
            return;
        }
        for(BluetoothDevice device:mBluetoothAdapter.getBondedDevices()){
            if(device.getName().equals(DEVICE_NAME)){
                new ConnectFeelingAsyncTask().execute(device.getAddress());
                break;
            }
        }
    }

    @Override
    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
                isBluetoothConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public float getHeartRate() {
        return heartRate;
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public float getHumidity() {
        return humidity;
    }

    class ConnectFeelingAsyncTask extends AsyncTask<String, Void, Void> {

        boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(mContext, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(String... params) {
            if (socket == null || !isBluetoothConnected) {
                try {
                    currentDevice = mBluetoothAdapter.getRemoteDevice(params[0]);
                    socket = currentDevice.createInsecureRfcommSocketToServiceRecord(UUID);
                    mBluetoothAdapter.cancelDiscovery();
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    connectSuccess = false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (connectSuccess) {
                isBluetoothConnected = true;
                Toast.makeText(mContext, "連接成功",
                        Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                new ReceiveDataAsyncTask().execute();
            } else {
                Toast.makeText(mContext, "連接失敗",
                        Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        }
    }

    class ReceiveDataAsyncTask extends AsyncTask<Void, String, Void> {
        private InputStream mInputStream;
        private OutputStream mOutputStream;
        final byte delimiter = 10;
        int position=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (socket != null) {
                InputStream tmpIn = null;
                try {
                    tmpIn = socket.getInputStream();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mInputStream = tmpIn;
            } else {
                Toast.makeText(mContext, "失去裝置",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            while (true) {

                String incomingMessage = "";
                byte[] buffer = new byte[1024];
                try {
                    int len=mInputStream.available();
                    if(len>0){
                        byte[] packetBytes  = new byte[len];
                        mInputStream.read(buffer);
                        for(int i=0;i<len;i++){
                            byte b=packetBytes [i];
                            if(b==delimiter){
                                byte[] encodedBytes = new byte[position];
                                System.arraycopy(buffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                publishProgress(data);
                                position = 0;
                            }else{
                                buffer[position++]=b;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }
}
