package com.example.ffes.feeling.feelingsticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;


import com.example.ffes.feeling.R;
import com.xiaopo.flying.sticker.Sticker;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ffes on 2017/12/22.
 */

public class PersonSticker extends Sticker {

    Drawable drawable;
    Drawable emoji;
    Drawable body;
    Drawable cloth;

    Rect realBounds;
    Rect emojiBounds;
    Rect clothBounds;
    Rect bodyBounds;
    Context context;

    public PersonSticker(Context context){
        this.context=context;
        this.drawable= ContextCompat.getDrawable(context, R.drawable.custom_transparent_background);
        body=loadDrawable("humidity/default/0.png");
        realBounds=new Rect(0,0,getWidth(),getHeight());
        emojiBounds=new Rect(180,180,300,280);
        clothBounds=new Rect(125,315,355,435);
        bodyBounds=new Rect(0,0,getWidth(),getHeight());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Matrix matrix=getMatrix();
        canvas.save();
        canvas.concat(matrix);
        if(drawable!=null){
            drawable.setBounds(realBounds);
            drawable.draw(canvas);
        }
        canvas.restore();

        canvas.save();
        canvas.concat(matrix);
        if(body!=null){
            body.setBounds(bodyBounds);
            body.draw(canvas);
        }
        canvas.restore();

        canvas.save();
        canvas.concat(matrix);
        if(emoji!=null){
            emoji.setBounds(emojiBounds);
            emoji.draw(canvas);
        }
        canvas.restore();

        canvas.save();
        canvas.concat(matrix);
        if(cloth!=null){
            cloth.setBounds(clothBounds);
            cloth.draw(canvas);
        }
        canvas.restore();

    }

    @Override
    public int getWidth() {
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }

    @Override
    public Sticker setDrawable(@NonNull Drawable drawable) {
        return null;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @NonNull
    @Override
    public Sticker setAlpha(@IntRange(from = 0L, to = 255L) int alpha) {
        return null;
    }

    public PersonSticker setEmoji(String path){
        emoji=loadDrawable(path);

        return this;
    }

    public PersonSticker setBody(String path){
        body=loadDrawable(path);

        return this;
    }

    public PersonSticker setCloth(String path){
        cloth=loadDrawable(path);

        return this;
    }

    private Drawable loadDrawable(String name){
        InputStream input= null;
        try {
            input = context.getResources().getAssets().open(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapDrawable bitmapDrawable=new BitmapDrawable(context.getResources(),input);
        return bitmapDrawable;
    }
}
