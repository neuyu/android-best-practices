package com.neu.androidbestpractices;

import android.app.Application;

/**
 * Created by neu on 16/2/2.
 */
public class MyApplication extends Application{
    /**
     * 全局实例
     */
    private static MyApplication sApp;

    /**
     * 获取实例
     * @return
     */
    public static MyApplication getInstance(){
        return sApp;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        sApp = this;

    }
}
