# DataLoader
![license](https://img.shields.io/github/license/JeremyLiao/DataLoader.svg) [![version](https://img.shields.io/badge/JCenter-v1.0.0-blue.svg)](https://mvnrepository.com/artifact/com.jeremyliao/)

DataLoader是一个Android异步数据加载框架。最常用的场景用于Activity打开之前预加载数据，在Activity的UI布局初始化完成后显示预加载的数据，大大缩短启动时间。

## DataLoader的特点
1. 用于异步数据加载。
2. 基于LiveData，生命周期感知，在Activity中使用的时候不用关注何时remove listener。

## 典型应用场景
1. 在Application.onCreate中预加载数据，在需要用到的页面中获取预加载的数据
2. 在启动页中预加载主页所需的数据，减少用户等待时间
3. startActivity之前就开始预加载，UI初始化完成后显示预加载的数据
4. 复杂页面(UI初始化耗时较多的页面)内部在UI初始化开始之前预加载数据，UI初始化完成后显示预加载的数据
5. ListView/RecyclerView在上拉加载更多之前预加载下一页的数据

## 添加依赖
Via Gradle:

```
implementation 'com.jeremyliao:data-loader:1.0.0'
```

## 使用
#### 1. 预加载数据
```
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
```

#### 2. 使用预加载的数据
```
int id = getIntent().getIntExtra("preLoaderId", -1);
DataLoader.listen(id, this, new LoadListener<String>() {
    @Override
    public void onDataArrived(String data) {
        tvShow.setText(data);
    }
});
```

#### 3. 刷新数据
```
DataLoader.refresh(id);
```

## 混淆规则

```
-dontwarn com.jeremyliao.dataloader.**
-keep class com.jeremyliao.dataloader.** { *; }
```

## Demo
简单的Demo可参考：[MainActivity.java](DataLoader/app/src/main/java/com/jeremyliao/dataloader/app/MainActivity.java)

## Reference
本项目参考了开源项目[luckybilly/PreLoader](https://github.com/luckybilly/PreLoader)，借鉴了PreLoader的设计思想和使用场景。只是利用LiveData重新实现，构架更为简单，使用起来也不用关注何时remove listener，使用更方便。在此对PreLoader的作者[@luckybilly](https://github.com/luckybilly)表示感谢。
