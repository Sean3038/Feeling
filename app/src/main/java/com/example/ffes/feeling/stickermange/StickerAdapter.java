package com.example.ffes.feeling.stickermange;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ffes.feeling.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Ffes on 2017/11/2.
 */

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder>{

    Context context;
    List<String> filenames;
    OnStickerSelected listen;

    StickerAdapter(Context context, List<String> filenames, OnStickerSelected listen){
        this.filenames=filenames;
        this.context=context;
        this.listen=listen;
    }

    interface OnStickerSelected{
        void onSelect(String name);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        InputStream image=null;
        try {
            image=context.getAssets().open(filenames.get(holder.getAdapterPosition()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.sticker.setImageBitmap(BitmapFactory.decodeStream(image));
        holder.sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen.onSelect(filenames.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filenames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView sticker;
        public ViewHolder(View itemView) {
            super(itemView);
            sticker=(ImageView)itemView.findViewById(R.id.sticker);
        }
    }
}
