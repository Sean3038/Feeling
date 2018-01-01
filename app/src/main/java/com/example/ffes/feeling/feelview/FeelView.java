package com.example.ffes.feeling.feelview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.example.ffes.feeling.ImageZoomDialog;
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
    @BindView(R.id.progressBar2)
    FrameLayout progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel_view);
        ButterKnife.bind(this);

        repository = new FirebaseRepository(FirebaseDatabase.getInstance(), FirebaseStorage.getInstance(), FirebaseAuth.getInstance());
        initPhoto();
    }

    private void initPhoto() {
        progressBar2.setVisibility(View.VISIBLE);
        repository.getAlbum(new GetCallBack<List<Item>>() {
            @Override
            public void onSuccess(List<Item> data) {
                adapter = new FeelPictureAdapter(FeelView.this, data, FeelView.this);
                imageList.setAdapter(adapter);
                imageList.setLayoutManager(new GridLayoutManager(FeelView.this, 4, GridLayoutManager.VERTICAL, false));
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    @Override
    public void onClick(FeelPictureAdapter.ViewHolder vh, Item item) {
        ImageZoomDialog.newInstance(item.getImage().toString()).show(getSupportFragmentManager(), "Image");
    }

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, FeelView.class);
        activity.startActivity(intent);
    }
}
