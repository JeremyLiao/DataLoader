package com.jeremyliao.dataloader.demo;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.dataloader.core.DataLoader;
import com.jeremyliao.dataloader.core.source.DataSource;
import com.jeremyliao.dataloader.demo.dataload.DemoDataSource;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testLoadData(View view) {
        DataSource<String> dataSource = DataLoader.get(DemoDataSource.class).getDemoData("aa");
        dataSource.result().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
        dataSource.error().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {

            }
        });
    }
}
