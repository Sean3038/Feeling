package com.example.ffes.feeling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ffes.feeling.feelingsticker.FeelingSticker;
import com.xiaopo.flying.sticker.StickerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StickerTest extends AppCompatActivity {

    @BindView(R.id.stickerview)
    StickerView stickerview;

    FeelingSticker one;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_test);
        ButterKnife.bind(this);
        stickerview.configDefaultIcons();
        one=new FeelingSticker(this).setText("24").setType(FeelingSticker.HEARTRATE).resizeText();
        stickerview.addSticker(one);
        stickerview.addSticker(new FeelingSticker(this).setText("56").setType(FeelingSticker.HUMIDITY).resizeText());
        stickerview.addSticker(new FeelingSticker(this).setText("40").setType(FeelingSticker.TEMPERATURE).resizeText());

        float[] points=stickerview.getStickerPoints(one);
        Timber.d("Points",points);
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, StickerTest.class);
        activity.startActivity(intent);
    }
}
