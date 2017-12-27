package com.example.ffes.feeling.feelview;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ffes.feeling.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ffes on 2017/12/24.
 */

public class FeelPictureAdapter extends RecyclerView.Adapter<FeelPictureAdapter.ViewHolder> {

    List<Item>  items;
    Context context;
    OnItemClick listener;

    FeelPictureAdapter(Context context, List<Item> items, OnItemClick listener){
        this.items=items;
        this.context=context;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Item item=items.get(holder.getAdapterPosition());
        holder.loadImage(item.getImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(holder,item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.image);
        }

        public void loadImage(Uri image){
            Picasso.with(context).load(image).centerCrop().resize(400,400).into(imageView);
        }
    }


    public interface OnItemClick{
        void onClick(FeelPictureAdapter.ViewHolder vh,Item item);
    }
}
