package com.jeremyliao.dataloader.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jeremyliao.dataloader.DataLoader;
import com.jeremyliao.dataloader.interfaces.LoadTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void load(View v) {
        int preLoaderId = DataLoader.load(new LoadTask<String>() {
            @Override
            public String loadData() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                return "data from network server";
            }
        });
        Intent intent = new Intent(this, PreLoadBeforeLaunchActivity.class);
        intent.putExtra("preLoaderId", preLoaderId);
        startActivity(intent);
    }
}
