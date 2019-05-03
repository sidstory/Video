package com.sidstory.video;

import androidx.multidex.MultiDexApplication;

import com.coder.zzq.smartshow.core.SmartShow;

import cn.bmob.v3.Bmob;

public class Applications extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "b4dbc115080904b663c28e3675d65db3");
        SmartShow.init(this);

    }
}
