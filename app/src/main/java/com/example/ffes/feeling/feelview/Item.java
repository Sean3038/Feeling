package com.example.ffes.feeling.feelview;

import android.net.Uri;

/**
 * Created by Ffes on 2017/12/24.
 */

public class Item{
    String id;
    Uri image;

    public Item(){}

    public Item(String id, Uri image){
        this.id=id;
        this.image=image;
    }

    public String getId() {
        return id;
    }

    public Uri getImage() {
        return image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}