package com.example.ffes.feeling.feelview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ffes.feeling.R;
import com.example.ffes.feeling.api.FirebaseRepository;
import com.example.ffes.feeling.api.GetCallBack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeelView extends AppCompatActivity implements FeelPictureAdapter.OnItemClick {


    @BindView(R.id.image_list)
    RecyclerView imageList;

    FeelPictureAdapter adapter;
    FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel_view);
        ButterKnife.bind(this);

        repository=new FirebaseRepository(FirebaseDatabase.getInstance(), FirebaseStorage.getInstance(), FirebaseAuth.getInstance());
        initPhoto();
    }

    private void initPhoto(){
        repository.getAlbum(new GetCallBack<List<Item>>() {
            @Override
            public void onSuccess(List<Item> data) {
                adapter=new FeelPictureAdapter(FeelView.this,data,FeelView.this);
                imageList.setAdapter(adapter);
                imageList.setLayoutManager(new GridLayoutManager(FeelView.this,4,GridLayoutManager.VERTICAL,false));
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    @Override
    public void onClick(FeelPictureAdapter.ViewHolder vh, Item item) {

    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, FeelView.class);
        activity.startActivity(intent);
    }
}
