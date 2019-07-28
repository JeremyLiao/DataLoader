package com.jeremyliao.dataloader.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jeremyliao.dataloader.DataLoader;
import com.jeremyliao.dataloader.interfaces.LoadListener;

public class PreLoadBeforeLaunchActivity extends AppCompatActivity {

    TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preload);
        tvShow = findViewById(R.id.tv_show);
        int id = getIntent().getIntExtra("preLoaderId", -1);
        DataLoader.listen(id, this, new LoadListener<String>() {
            @Override
            public void onDataArrived(String data) {
                tvShow.setText(data);
            }
        });
    }
}
