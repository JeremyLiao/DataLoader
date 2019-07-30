package com.jeremyliao.dataloader.demo.bean;

/**
 * Created by liaohailiang on 2019-07-29.
 */
public class DemoResult {

    public String name;
    public int no;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name: ").append(name).append(";");
        sb.append("no: ").append(no).append(";");
        return sb.toString();
    }
}
