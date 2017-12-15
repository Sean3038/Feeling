package com.example.ffes.feeling;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ffes on 2017/12/14.
 */

public class Watch implements Bluetooth,GetFeeling {
    public static final String TAG="WATCH";
    public static final java.util.UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String DEVICE_NAME="HENK";


    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    Context mContext;

    ProgressDialog mProgressDialog;
    private boolean isBluetoothConnected=false;
    private BluetoothDevice currentDevice;

    float heartRate;
    float temperature;
    float humidity;
    ConnectFeelingAsyncTask connectFeelingAsyncTask;
    ReceiveDataAsyncTask receiveDataAsyncTask;

    Watch(Context context){
        mContext=context;
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public synchronized void connect() {
        if(isBluetoothConnected){
            return;
        }
        for(BluetoothDevice device:mBluetoothAdapter.getBondedDevices()){
            if(device.getName().equals(DEVICE_NAME)){
                connectFeelingAsyncTask=new ConnectFeelingAsyncTask();
                connectFeelingAsyncTask.execute(device.getAddress());
                break;
            }
        }
    }

    @Override
    public synchronized void disconnect() {
        if (socket != null) {
            connectFeelingAsyncTask.cancel(true);
            receiveDataAsyncTask.cancel(true);
            try {
                socket.getInputStream().close();
                socket.getOutputStream().close();
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
                receiveDataAsyncTask=new ReceiveDataAsyncTask();
                receiveDataAsyncTask.execute();
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
                try {
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    byte[] buffer = new byte[512];
                    int len=0;
                    while((len = mInputStream.read(buffer)) != -1){
                        byteBuffer.write(buffer,0,len);
                        incomingMessage=new String(byteBuffer.toByteArray());
                        publishProgress(incomingMessage);
                    }
                    byteBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values[0].length()>0) {
                int l = values[0].lastIndexOf("#");
                int s = values[0].lastIndexOf("~");
                if(l<s){
                    String result = values[0].substring(l+1, s);
                    Toast.makeText(mContext, result,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
