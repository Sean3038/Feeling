package com.example.ffes.feeling.stickermange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ffes on 2017/12/27.
 */

public class TemperatureStickerFramgment extends PageFragment implements StickerAdapter.OnStickerSelected{

    StickerAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTemperature(18);
    }


    public List<String> getStickname(String path) {
        String [] stickers=null;
        List<String> files=new ArrayList<>();
        try {
            String[] filenames=getContext().getAssets().list(path);
            for(int i=0;i<filenames.length;i++){
                files.add(path+"/"+filenames[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public void setTemperature(float temperature){
        if(temperature>28){
            loadSticker(getStickname("temperature/more28"));
        }else if(temperature>25){
            loadSticker(getStickname("temperature/25~28"));
        }else if(temperature>15){
            loadSticker(getStickname("temperature/15~25"));
        }else if(temperature>8){
            loadSticker(getStickname("temperature/8~15"));
        }else if(temperature<8){
            loadSticker(getStickname("temperature/less8"));
        }
    }

    public void loadSticker(List<String> stickers){
        adapter=new StickerAdapter(getContext(),stickers,this);
        imagelist.setLayoutManager(new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false));
        imagelist.setAdapter(adapter);
    }

    @Override
    public void onSelect(String name) {
        ((OnReturnSticker)getActivity()).onClickStick(OnReturnSticker.TEMP,name);
    }
}
