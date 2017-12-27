package com.example.ffes.feeling;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaopo.flying.sticker.StickerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateFeeling extends StickerTest implements TakePicture {

    public static final int MAKE_FEELING_REQUEST = 1;
    public static final int START_CAMERA = 2;
    public static final int ENABLE_BLUETOOTH = 3;

    Uri imageurl;

    Watch mWatch;
    GPS mGps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkpermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkBT();
        checkLoaction();
    }

    @Override
    protected void onDestroy() {
        if (mWatch != null) {
            mWatch.disconnect();
        }
        if (mGps != null) {
            mGps.disconnect();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case START_CAMERA:
                if (resultCode == RESULT_OK) {
                    imageView.setImageBitmap(getBitmapByUri(imageurl));
//                    this.getContentResolver().delete(imageurl, null, null);
                }else{
                    finish();
                }
            case ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    loadBTDevice();
                }
        }
    }

    @Override
    protected void onStop() {
        if (mWatch != null) {
            mWatch.disconnect();
        }
        if (mGps != null) {
            mGps.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MAKE_FEELING_REQUEST:
                boolean flag = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "please accept " + permissions[i], Toast.LENGTH_LONG).show();
                        flag = false;
                    }
                }
                if (flag) {
                    Toast.makeText(this, "加載成功", Toast.LENGTH_LONG).show();
                    takePicture();
                } else {
                    Toast.makeText(this, "加載失敗", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void takePicture() {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_"
                + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance().getTime())
                + ".jpg";
        imageurl = FileProvider.getUriForFile(this
                , getPackageName() + ".fileprovider"
                , new File((filePath + fileName)));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageurl);
        startActivityForResult(intent, START_CAMERA);
    }

    @Override
    public byte[] getPicture() {
        try {
            InputStream in = getContentResolver().openInputStream(imageurl);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) == -1) {
                baos.write(buffer);
            }
            byte[] result = baos.toByteArray();
            in.close();
            baos.close();
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, CreateFeeling.class);
        activity.startActivity(intent);
    }

    private Bitmap getBitmapByUri(Uri picuUi) {
        Bitmap bitmap = null;
        try {
            InputStream in = getContentResolver().openInputStream(picuUi);
            bitmap = BitmapFactory.decodeStream(in);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, displayMetrics.widthPixels, displayMetrics.heightPixels);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void checkpermission() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.BLUETOOTH
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, MAKE_FEELING_REQUEST);
    }

    private void checkBT() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
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
        if (((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER) || ((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            loadLocation();

        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));    //開啟設定頁面
        }
    }

    private void loadBTDevice() {
        mWatch = new Watch(this);
        mWatch.connect();
    }

    private void loadLocation() {
        mGps = new GPS(this);
    }

    class GetAllDataAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            while(mGps.getAddress()== null || mWatch.getHeartRate()==0 || mWatch.getHumidity()==0 || mWatch.getTemperature()==0){

            }

            return null;
        }
    }
}
