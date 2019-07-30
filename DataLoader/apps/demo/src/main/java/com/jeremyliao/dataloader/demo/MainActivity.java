package com.jeremyliao.dataloader.demo;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.dataloader.core.DataLoader;
import com.jeremyliao.dataloader.core.source.DataSource;
import com.jeremyliao.dataloader.core.utils.GenericsUtils;
import com.jeremyliao.dataloader.demo.bean.CommonParam;
import com.jeremyliao.dataloader.demo.bean.CommonResult;
import com.jeremyliao.dataloader.demo.bean.DemoParam;
import com.jeremyliao.dataloader.demo.bean.DemoResult;
import com.jeremyliao.dataloader.demo.dataload.DemoDataSource;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testLoadData1(View view) {
        CommonParam<String> param = new CommonParam<>();
        Class superClassGenericType = GenericsUtils.getGenericType(param.getClass());
        DataSource<String> dataSource = DataLoader.get(DemoDataSource.class).getData1("hello world");
        dataSource.result().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
        dataSource.error().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void testLoadData2(View view) {
        DemoParam param = new DemoParam();
        param.name = "aaaaa";
        param.no = 100;
        DataSource<DemoResult> dataSource = DataLoader.get(DemoDataSource.class).getData2(param);
        dataSource.result().observe(this, new Observer<DemoResult>() {
            @Override
            public void onChanged(@Nullable DemoResult s) {
                Toast.makeText(MainActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        dataSource.error().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void testLoadData3(View view) {
        CommonParam<String> param = new CommonParam<>();
        param.param = "aaaaa";
        DataSource<CommonResult<String>> dataSource = DataLoader.get(DemoDataSource.class).getData3(param);
        dataSource.result().observe(this, new Observer<CommonResult<String>>() {
            @Override
            public void onChanged(@Nullable CommonResult<String> result) {
                Toast.makeText(MainActivity.this, result.data, Toast.LENGTH_SHORT).show();
            }
        });
        dataSource.error().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                Toast.makeText(MainActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
