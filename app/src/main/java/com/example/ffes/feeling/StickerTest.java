package com.example.ffes.feeling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.ffes.feeling.api.Feel;
import com.example.ffes.feeling.api.FirebaseRepository;
import com.example.ffes.feeling.api.UploadCallBack;
import com.example.ffes.feeling.feelingsticker.FeelingSticker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class StickerTest extends AppCompatActivity implements Animation.AnimationListener {

    @BindView(R.id.stickerview)
    StickerView stickerview;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.fabHum)
    FloatingActionButton fabHum;
    @BindView(R.id.fabTemp)
    FloatingActionButton fabTemp;
    @BindView(R.id.fabHeart)
    FloatingActionButton fabHeart;
    @BindView(R.id.fabAuto)
    FloatingActionButton fabAuto;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.fabSave)
    FloatingActionButton fabSave;


    FeelingSticker temperature;
    FeelingSticker heartRate;
    FeelingSticker humidity;

    FirebaseRepository repository;

    int width;
    int height;
    boolean isOpened = false;
    boolean isProgress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_test);
        ButterKnife.bind(this);

        repository=new FirebaseRepository(FirebaseDatabase.getInstance(), FirebaseStorage.getInstance(), FirebaseAuth.getInstance());
        stickerview.configDefaultIcons();
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(this, R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(this, R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());
        deleteIcon.setIconEvent(new DeleteIconEvent());
        stickerview.setIcons(Arrays.asList(deleteIcon,zoomIcon));
        Timber.d(Calendar.getInstance().getTime().toString());
    }

    private void autoLayout() {
        DisplayMetrics monitorsize = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(monitorsize);
        width = monitorsize.widthPixels;
        height = monitorsize.heightPixels;
        layoutHeartRate();
        layoutHumidity();
        layoutTemperature();

        stickerview.invalidate();
        stickerview.setConstrained(true);
    }

    private void layoutHeartRate() {
        if (heartRate == null) {
            return;
        }
        int offsetx = -width / 4;
        int offsety = height / 2 / 5;
        heartRate.getMatrix().setTranslate(0, offsety);
        heartRate.getMatrix().postScale(0.7f,0.7f);
    }

    private void layoutTemperature() {
        if (temperature == null) {
            return;
        }
        int offsetx = -width / 4;
        int offsety = height / 5;
        temperature.getMatrix().setTranslate(0, offsety);
        temperature.getMatrix().postScale(0.7f,0.7f);
    }

    private void layoutHumidity() {
        if (humidity == null) {
            return;
        }
        int offsetx = -width / 4;
        int offsety = height * 3 / 2 / 5;
        humidity.getMatrix().setTranslate(0, offsety);
        humidity.getMatrix().postScale(0.7f,0.7f);
    }

    private void openMenu() {
//        Animator animation= ViewAnimationUtils
//                .createCircularReveal(fabTemp
//                        ,fabTemp.getMeasuredWidth()/2
//                        ,fabTemp.getMeasuredHeight()/2
//                        ,0
//                        ,Math.max(fabTemp.getWidth(), fabTemp.getHeight()) / 2);
//        fabTemp.setVisibility(View.VISIBLE);
//        animation.start();
        Animation aheart = AnimationUtils.loadAnimation(this, R.anim.subbuttom_open);
        Animation aauto = AnimationUtils.loadAnimation(this, R.anim.subbuttom_open);
        Animation atemp = AnimationUtils.loadAnimation(this, R.anim.subbuttom_open);
        Animation ahum = AnimationUtils.loadAnimation(this, R.anim.subbuttom_open);
        Animation asave=AnimationUtils.loadAnimation(this,R.anim.subbuttom_open);
        asave.setStartOffset(100);
        aheart.setStartOffset(200);
        atemp.setStartOffset(300);
        ahum.setStartOffset(400);
        fabSave.startAnimation(asave);
        fabTemp.startAnimation(atemp);
        fabAuto.startAnimation(aauto);
        fabHeart.startAnimation(aheart);
        fabHum.startAnimation(ahum);

    }

    private void closeMenu() {
//        Animator animation= ViewAnimationUtils
//                .createCircularReveal(fabTemp
//                        ,fabTemp.getMeasuredWidth()/2
//                        ,fabTemp.getMeasuredHeight()/2
//                        ,Math.max(fabTemp.getWidth(), fabTemp.getHeight()) / 2
//                        ,0);
//        animation.start();
//        animation.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                fabTemp.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
        Animation aheart = AnimationUtils.loadAnimation(this, R.anim.subbutton_close);
        Animation aauto = AnimationUtils.loadAnimation(this, R.anim.subbutton_close);
        Animation atemp = AnimationUtils.loadAnimation(this, R.anim.subbutton_close);
        Animation ahum = AnimationUtils.loadAnimation(this, R.anim.subbutton_close);
        Animation asave=AnimationUtils.loadAnimation(this,R.anim.subbutton_close);
        aauto.setStartOffset(400);
        asave.setStartOffset(300);
        aheart.setStartOffset(200);
        atemp.setStartOffset(100);
        aauto.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabHeart.setVisibility(View.INVISIBLE);
                fabHum.setVisibility(View.INVISIBLE);
                fabTemp.setVisibility(View.INVISIBLE);
                fabAuto.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fabSave.startAnimation(asave);
        fabTemp.startAnimation(atemp);
        fabAuto.startAnimation(aauto);
        fabHeart.startAnimation(aheart);
        fabHum.startAnimation(ahum);
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, StickerTest.class);
        activity.startActivity(intent);
    }

    @OnClick(R.id.floatingActionButton)
    public void onViewClicked() {
        Animation animation;
        if (isOpened) {
            animation = AnimationUtils.loadAnimation(this, R.anim.floatbuttom_close);
        } else {
            animation = AnimationUtils.loadAnimation(this, R.anim.floatbuttom_open);
        }
        if (!isProgress) {
            animation.setAnimationListener(this);
            floatingActionButton.startAnimation(animation);
            isProgress = true;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (isOpened) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (isOpened) {
            isOpened = false;
        } else {
            isOpened = true;
        }
        isProgress = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @OnClick({R.id.fabHum, R.id.fabTemp, R.id.fabHeart, R.id.fabAuto,R.id.fabSave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabHum:
                stickerview.remove(humidity);
                humidity = new FeelingSticker(this).setText("24").setType(FeelingSticker.HUMIDITY).resizeText();
                humidity.getMatrix().postScale(0.7f,0.7f);
                stickerview.addSticker(humidity);
                break;
            case R.id.fabTemp:
                stickerview.remove(temperature);
                temperature = new FeelingSticker(this).setText("24").setType(FeelingSticker.TEMPERATURE).resizeText();
                temperature.getMatrix().postScale(0.7f,0.7f);
                stickerview.addSticker(temperature);
                break;
            case R.id.fabHeart:
                stickerview.remove(heartRate);
                heartRate = new FeelingSticker(this).setText("24").setType(FeelingSticker.HEARTRATE).resizeText();
                heartRate.getMatrix().postScale(0.7f,0.7f);
                stickerview.addSticker(heartRate);
                break;
            case R.id.fabAuto:
                autoLayout();
                break;
            case R.id.fabSave:
                Bitmap bitmap=stickerview.createBitmap();
                final ProgressDialog progressDialog=ProgressDialog.show(this,"Upload","please waiting.....",false,false);
                progressDialog.show();
                Feel feel=createFeel();

                repository.uploadFeeling(feel, Bitmap2Bytes(bitmap), new UploadCallBack() {
                    int count=0;
                    @Override
                    public void onSuccess(String message) {
                        count++;
                        if(count==2){
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        count++;
                        if(count==2){
                            progressDialog.dismiss();
                        }
                    }
                });
                break;
        }
    }

    private byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private Feel createFeel(){
        Calendar c=Calendar.getInstance();
        String time=c.get(Calendar.YEAR)+""+c.get(Calendar.MONTH)+""+c.get(Calendar.DATE);
        Feel feel=new Feel();
        feel.setHumidity(24);
        feel.setHeartRate(120);
        feel.setTemperature(30);
        feel.setDate(time);
        return feel;
    }
}
