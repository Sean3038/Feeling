package com.example.ffes.feeling;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xiaopo.flying.sticker.StickerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.browser)
    Button browser;
    @BindView(R.id.button2)
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.browser, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.browser:
                StickerTest.start(this);
                break;
            case R.id.button2:
                CreateFeeling.start(this);
                break;
        }
    }
}
