package com.example.ffes.feeling.stickermange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ffes on 2017/11/2.
 */

public class HeartRateStickerFragment extends PageFragment implements StickerAdapter.OnStickerSelected {

    StickerAdapter adapter;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        try {
//            adapter=new StickerAdapter(getContext(),Arrays.asList(getStickname()),this);
//            imagelist.setLayoutManager(new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false));
//            imagelist.setAdapter(adapter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        setHeartRate(80);
    }

    public List<String> getStickname(String path) {
        String [] stickers=null;
        List<String> files=new ArrayList<>();
        try {
//            JSONObject result=new JSONObject(loadJSONFromAsset());
//            JSONObject sticker= null;
//            sticker = result.getJSONObject("sticker");
//            JSONArray jsonArray=sticker.getJSONArray("Animal");
//            stickers= new String[jsonArray.length()];
//            for(int x=0;x<jsonArray.length();x++) {
//                stickers[x] = jsonArray.getString(x);
//            }
            String[] filenames=getContext().getAssets().list(path);
            for(int i=0;i<filenames.length;i++){
                files.add(path+"/"+filenames[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is =getActivity().getAssets().open("sticker.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void setHeartRate(float heartRate){
        if(heartRate>100){
            loadSticker(getStickname("heartrate/more100"));
        }else if(heartRate>80){
            loadSticker(getStickname("heartrate/80~100"));
        }else if(heartRate>60){
            loadSticker(getStickname("heartrate/60~80"));
        }else if(heartRate<60){
            loadSticker(getStickname("heartrate/less60"));
        }
    }

    public void loadSticker(List<String> stickers){
        adapter=new StickerAdapter(getContext(),stickers,this);
        imagelist.setLayoutManager(new GridLayoutManager(getContext(),4,GridLayoutManager.VERTICAL,false));
        imagelist.setAdapter(adapter);
    }

    @Override
    public void onSelect(String name) {
        ((OnReturnSticker)getActivity()).onClickStick(OnReturnSticker.HEART,name);
    }
}
