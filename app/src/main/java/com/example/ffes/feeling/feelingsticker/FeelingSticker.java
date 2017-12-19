package com.example.ffes.feeling.feelingsticker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.example.ffes.feeling.R;
import com.xiaopo.flying.sticker.Sticker;

/**
 * Created by Ffes on 2017/12/15.
 */

public class FeelingSticker extends Sticker{
    public static final int TEMPERATURE=1;
    public static final int HEARTRATE=2;
    public static final int HUMIDITY=3;

    private static final String mEllipsis = "\u2026";

    private float maxTextSizePixels;
    private float minTextSizePixels;

    private float lineSpacingMultiplier = 1.0f;
    private float lineSpacingExtra = 0.0f;

    Context context;
    Rect realBounds;
    Rect textRect;
    Rect iconRect;
    Rect unitRect;
    TextPaint textPaint;
    StaticLayout staticLayout;
    Layout.Alignment alignment;
    Drawable drawable;
    Drawable icon;
    Drawable unit;
    String text;

    public FeelingSticker(Context context){
        this.context=context;
        drawable= ContextCompat.getDrawable(context, R.drawable.sticker_transparent_background);
        icon= ContextCompat.getDrawable(context,R.drawable.ic_temp);
        unit=ContextCompat.getDrawable(context,R.drawable.ic_degree);
        textPaint=new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        realBounds=new Rect(0,0,getWidth(),getHeight());
        textRect=new Rect(getWidth()*2/7,0,getWidth()*5/7,getHeight());

        init();

        minTextSizePixels=convertSpToPx(6);
        maxTextSizePixels=convertSpToPx(32);
        alignment= Layout.Alignment.ALIGN_CENTER;
        textPaint.setTextSize(maxTextSizePixels);
        textPaint.setColor(Color.WHITE);
    }

    private void init(){
        float scaleh=icon.getIntrinsicHeight()/textRect.height();
        float scalew=icon.getIntrinsicWidth()/textRect.left;
        float scale= (scaleh > scalew) ? scaleh+1 : scalew+1;
        iconRect=new Rect(0,0,(int)(icon.getIntrinsicWidth()/scale),(int)(icon.getIntrinsicHeight()/scale));

        scaleh=unit.getIntrinsicHeight()/textRect.height();
        scalew=unit.getIntrinsicWidth()/textRect.left;
        scale= (scaleh > scalew) ? scaleh+1 : scalew+1;
        unitRect=new Rect(0,0,(int)(unit.getIntrinsicWidth()/scale),(int)(unit.getIntrinsicHeight()/scale));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Matrix matrix=getMatrix();
        canvas.save();
        canvas.concat(getMatrix());
        if(drawable!=null){
            drawable.setBounds(realBounds);
            drawable.draw(canvas);
        }
        canvas.restore();

        canvas.save();
        canvas.concat(getMatrix());
        icon.setBounds(iconRect);
        unit.setBounds(unitRect);
        if (textRect.width() == getWidth()) {
            int dy = getHeight() / 2 - staticLayout.getHeight() / 2;
            // center vertical
            canvas.translate(0, dy);
        } else {
            int dx = textRect.left;
            int dy = textRect.top + textRect.height() / 2 - staticLayout.getHeight() / 2;
            canvas.translate(dx, dy);
        }
        staticLayout.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.concat(matrix);
        int offsetx=realBounds.left+textRect.left/2-iconRect.width()/2;
        int offsety=realBounds.top+realBounds.height()/2-iconRect.height()/2;
        canvas.translate(offsetx,offsety);
        icon.draw(canvas);
        canvas.restore();


        canvas.save();
        canvas.concat(matrix);
        offsetx=realBounds.left+(realBounds.right-textRect.right)/2-unitRect.width()/2;
        offsety=realBounds.top+realBounds.height()/2-unitRect.height()/2;
        canvas.translate(textRect.right,0);
        canvas.translate(offsetx,offsety);
        unit.draw(canvas);
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
        this.drawable=drawable;
        realBounds=new Rect(0,0,getWidth(),getHeight());
        textRect=new Rect(getWidth()*2/7,0,getWidth()*5/7,getHeight());
        return this;
    }

    @NonNull
    public FeelingSticker setType(@NonNull int type){
        switch (type){
            case TEMPERATURE:
                icon= ContextCompat.getDrawable(context,R.drawable.ic_temp);
                unit=ContextCompat.getDrawable(context,R.drawable.ic_degree);
                break;
            case HEARTRATE:
                icon= ContextCompat.getDrawable(context,R.drawable.ic_heart);
                unit= ContextCompat.getDrawable(context,R.drawable.ic_dpm);
                break;
            case HUMIDITY:
                icon= ContextCompat.getDrawable(context,R.drawable.ic_water);
                unit=ContextCompat.getDrawable(context,R.drawable.ic_percent);
                break;
        }
        init();
        return this;
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

    @NonNull public FeelingSticker setLineSpacing(float add, float multiplier) {
        lineSpacingMultiplier = multiplier;
        lineSpacingExtra = add;
        return this;
    }

    @NonNull public FeelingSticker setText(@Nullable String text) {
        this.text = text;
        return this;
    }

    @Nullable public String getText() {
        return text;
    }

    /**
     * Resize this view's text size with respect to its width and height
     * (minus padding). You should always call this method after the initialization.
     */
    @NonNull public FeelingSticker resizeText() {
        final int availableHeightPixels = textRect.height();

        final int availableWidthPixels = textRect.width();

        final CharSequence text = getText();

        // Safety check
        // (Do not resize if the view does not have dimensions or if there is no text)
        if (text == null
                || text.length() <= 0
                || availableHeightPixels <= 0
                || availableWidthPixels <= 0
                || maxTextSizePixels <= 0) {
            return this;
        }

        float targetTextSizePixels = maxTextSizePixels;
        int targetTextHeightPixels =
                getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);

        // Until we either fit within our TextView
        // or we have reached our minimum text size,
        // incrementally try smaller sizes
        while (targetTextHeightPixels > availableHeightPixels
                && targetTextSizePixels > minTextSizePixels) {
            targetTextSizePixels = Math.max(targetTextSizePixels - 2, minTextSizePixels);

            targetTextHeightPixels =
                    getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);
        }

        // If we have reached our minimum text size and the text still doesn't fit,
        // append an ellipsis
        // (NOTE: Auto-ellipsize doesn't work hence why we have to do it here)
        if (targetTextSizePixels == minTextSizePixels
                && targetTextHeightPixels > availableHeightPixels) {
            // Make a copy of the original TextPaint object for measuring
            TextPaint textPaintCopy = new TextPaint(textPaint);
            textPaintCopy.setTextSize(targetTextSizePixels);

            // Measure using a StaticLayout instance
            StaticLayout staticLayout =
                    new StaticLayout(text, textPaintCopy, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
                            lineSpacingMultiplier, lineSpacingExtra, false);

            // Check that we have a least one line of rendered text
            if (staticLayout.getLineCount() > 0) {
                // Since the line at the specific vertical position would be cut off,
                // we must trim up to the previous line and add an ellipsis
                int lastLine = staticLayout.getLineForVertical(availableHeightPixels) - 1;

                if (lastLine >= 0) {
                    int startOffset = staticLayout.getLineStart(lastLine);
                    int endOffset = staticLayout.getLineEnd(lastLine);
                    float lineWidthPixels = staticLayout.getLineWidth(lastLine);
                    float ellipseWidth = textPaintCopy.measureText(mEllipsis);

                    // Trim characters off until we have enough room to draw the ellipsis
                    while (availableWidthPixels < lineWidthPixels + ellipseWidth) {
                        endOffset--;
                        lineWidthPixels =
                                textPaintCopy.measureText(text.subSequence(startOffset, endOffset + 1).toString());
                    }

                    setText(text.subSequence(0, endOffset) + mEllipsis);
                }
            }
        }
        textPaint.setTextSize(targetTextSizePixels);
        staticLayout =
                new StaticLayout(this.text, textPaint, textRect.width(), alignment, lineSpacingMultiplier,
                        lineSpacingExtra, true);
        return this;
    }

    public float getMinTextSizePixels() {
        return minTextSizePixels;
    }

    protected int getTextHeightPixels(@NonNull CharSequence source, int availableWidthPixels,
                                      float textSizePixels) {
        textPaint.setTextSize(textSizePixels);
        // It's not efficient to create a StaticLayout instance
        // every time when measuring, we can use StaticLayout.Builder
        // since api 23.
        StaticLayout staticLayout =
                new StaticLayout(source, textPaint, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
                        lineSpacingMultiplier, lineSpacingExtra, true);
        return staticLayout.getHeight();
    }

    private float convertSpToPx(float scaledPixels) {
        return scaledPixels * context.getResources().getDisplayMetrics().scaledDensity;
    }
}
