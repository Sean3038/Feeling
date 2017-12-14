package com.example.ffes.feeling;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ffes on 2017/12/14.
 */

public class GPS implements LocationListener {
    public static final String TAG = "GPS";
    private double mLongitude;
    private double mLatitude;
    private String mAddress;

    Context mContext;
    LocationManager mLocationManager;
    Criteria mCriteria;
    Geocoder gc;

    GPS(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        gc = new Geocoder(mContext, Locale.TRADITIONAL_CHINESE);
        mCriteria = new Criteria();
        String provider = mLocationManager.getBestProvider(mCriteria, true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 1000, 1, this);
    }

    public void disconnect(){
        mLocationManager.removeUpdates(this);
    }

    public String getAddress(){
        if(mAddress==null){
            return "None";
        }
        return mAddress;
    }

    public double getLongitude(){
        return mLongitude;
    }

    public double getLaitude(){
        return mLatitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude=location.getLatitude();
        mLongitude=location.getLongitude();
        List<Address> lstAddress = null;
        try {
            lstAddress = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String returnAddress = lstAddress.get(0).getAddressLine(0);
            mAddress=returnAddress;
        } catch (IOException e) {
            Toast.makeText(mContext, "找不到該位置",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(mContext, "位置狀態改變",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(mContext, "位置功能打開",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(mContext, "位置功能關閉",
                Toast.LENGTH_LONG).show();
    }
}
