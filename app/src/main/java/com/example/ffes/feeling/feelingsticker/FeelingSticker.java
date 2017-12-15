package com.example.ffes.feeling.feelingsticker;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.xiaopo.flying.sticker.Sticker;

/**
 * Created by Ffes on 2017/12/15.
 */

public class FeelingSticker extends Sticker{
    @Override
    public void draw(@NonNull Canvas canvas) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public Sticker setDrawable(@NonNull Drawable drawable) {
        return null;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return null;
    }

    @NonNull
    @Override
    public Sticker setAlpha(@IntRange(from = 0L, to = 255L) int alpha) {
        return null;
    }
}
