package com.example.ffes.feeling;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BTTest extends AppCompatActivity implements DeviceAdapter.OnItemClick, LocationListener {
    public static final int BLUETOOTH_REQUEST = 1;
    public static final int ENABLE_BLUETOOTH = 3;

    public static final java.util.UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter mBluetoothAdapter;
    DeviceAdapter deviceAdapter;
    BluetoothDevice currentDevice;
    BluetoothSocket socket;
    boolean isBluetoothConnected = false;

    LocationManager mLocationManager;
    Location mLocation;
    Criteria mCriteria;
    Geocoder gc;

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.dlist)
    RecyclerView dlist;
    ProgressDialog mProgressDialog;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.gpsContent)
    TextView gpsContent;
    @BindView(R.id.address)
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bttest);
        ButterKnife.bind(this);
        checkpermission();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkLoaction();
        checkBT();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    loadBTDevice();
                }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BLUETOOTH_REQUEST:
                boolean flag = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "please accept " + permissions[i], Toast.LENGTH_LONG).show();
                        flag = false;
                    }
                }
                if (flag) {
                    Toast.makeText(this, "加載成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "加載失敗", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    private void checkpermission() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.BLUETOOTH
        };
        ActivityCompat.requestPermissions(this, permissions, BLUETOOTH_REQUEST);
    }

    private void checkBT() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE_BLUETOOTH);
            Toast.makeText(getApplicationContext(), "Turned on"
                    , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on",
                    Toast.LENGTH_LONG).show();
            loadBTDevice();
        }
    }

    private void checkLoaction() {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            loadLocation();

        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
        }
    }

    private void loadBTDevice() {
        deviceAdapter = new DeviceAdapter(new ArrayList<>(mBluetoothAdapter.getBondedDevices()), this);
        dlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        dlist.setAdapter(deviceAdapter);
    }

    private void loadLocation() {
        mCriteria = new Criteria();
        String provider = mLocationManager.getBestProvider(mCriteria, true);
        mLocationManager.requestLocationUpdates(provider, 1000, 1, this);
    }

    @Override
    public void onClick(String address) {
        startConnect(address);
    }

    private void startConnect(String address) {
        new ConnectFeelingAsyncTask().execute(address);
    }

    private void disconnect() {
        if (socket != null) {
            try {
                socket.close();
                isBluetoothConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        gpsContent.setText("經度: " + location.getLongitude() + "\n緯度: " + location.getLatitude());
        List<Address> lstAddress = null;
        try {
            lstAddress = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String returnAddress = lstAddress.get(0).getAddressLine(0);
            address.setText(returnAddress);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "找不到該位置",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(getApplicationContext(), "位置狀態改變",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(), "位置功能打開",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "位置功能關閉",
                Toast.LENGTH_LONG).show();
    }

    class ConnectFeelingAsyncTask extends AsyncTask<String, Void, Void> {

        boolean connectSuccess = true;

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(BTTest.this, "Connecting...", "Please wait!!!");
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
                Toast.makeText(getApplicationContext(), "連接成功",
                        Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                new ReceiveDataAsyncTask().execute();
            } else {
                Toast.makeText(getApplicationContext(), "連接失敗",
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
                Toast.makeText(getApplicationContext(), "失去裝置",
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
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values[0].length()>0) {
                int l = values[0].lastIndexOf("#");
                int s = values[0].lastIndexOf("~");
                if(l<s){
                    String result = values[0].substring(l+1, s);
                    content.setText(result);
                }
            }
        }
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, BTTest.class);
        activity.startActivity(intent);
    }

}
