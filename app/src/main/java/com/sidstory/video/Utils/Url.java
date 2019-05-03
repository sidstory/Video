package com.sidstory.video.Utils;

import cn.bmob.v3.BmobObject;

public class Url extends BmobObject {
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;
}