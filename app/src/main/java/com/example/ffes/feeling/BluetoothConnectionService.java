package com.example.ffes.feeling;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by Ffes on 2017/12/10.
 */

public class BluetoothConnectionService {

    public static final java.util.UUID UUID= java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String DEVICE_NAME="Feeling";

    private BluetoothDevice mBluetoothDevice;
    private UUID device_uuid;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog progressDialog;

    BluetoothConnectionService(Context context){
        this.context=context;
        this.mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmBluetoothServerSocket;

        public  AcceptThread(){
            BluetoothServerSocket tmp=null;
            try {
                tmp=mBluetoothAdapter.listenUsingRfcommWithServiceRecord(DEVICE_NAME,UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmBluetoothServerSocket=tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket=null;
            while(true){
                try {
                    socket=mmBluetoothServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(socket!=null){
                    try {
                        connected(socket,mBluetoothDevice);
                        mmBluetoothServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void cancel(){
            try {
                mmBluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice bluetoothDevice,UUID uuid) {
            mBluetoothDevice=bluetoothDevice;
            device_uuid=uuid;
        }

        @Override
        public void run() {
            BluetoothSocket tmp=null;
            try {
                tmp=mBluetoothDevice.createRfcommSocketToServiceRecord(device_uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket=tmp;

            mBluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
            } catch (IOException e) {
                try {
                    mSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            connected(mSocket,mBluetoothDevice);
        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket){
            mSocket=bluetoothSocket;
            InputStream tmpIn=null;
            OutputStream tmpOut=null;

            progressDialog.dismiss();
            try {
                tmpIn=mSocket.getInputStream();
                tmpOut=mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mInputStream=tmpIn;
            mOutputStream=tmpOut;
        }

        @Override
        public void run() {
            byte[] buffer=new byte[1024];

            int bytes;

            while(true){
                String incomingMessage="";
                try {
                    bytes=mInputStream.read(buffer);
                    incomingMessage=new String(buffer,0,bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public void write(byte[] bytes){
            String text=new String(bytes, Charset.defaultCharset());
            try {
                mOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel(){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start(){
        if(mConnectThread!=null){
            mConnectThread.cancel();
            mConnectThread=null;
        }
        if(mAcceptThread==null){
            mAcceptThread=new AcceptThread();
            mAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice mBluetoothDevice,UUID uuid){
        progressDialog=ProgressDialog.show(context,"Connecting Bluetooth","Please Wait....",true);
        mConnectThread=new ConnectThread(mBluetoothDevice, uuid);
        mConnectThread.start();
    }

    private void connected(BluetoothSocket bluetoothSocket,BluetoothDevice bluetoothDevice){
        mConnectedThread=new ConnectedThread(bluetoothSocket);
        mConnectedThread.start();
    }
}
