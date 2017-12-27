package com.example.ffes.feeling.stickermange;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ffes.feeling.R;

/**
 * Created by Ffes on 2017/11/2.
 */

public class PageFragment extends Fragment {


    protected RecyclerView imagelist;

    public interface OnReturnSticker{
        int HUM=0;
        int TEMP=1;
        int HEART=2;

        void onClickStick(int TYPE,String stick);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stickerlayout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagelist=(RecyclerView) view.findViewById(R.id.imagelist);
    }
}
