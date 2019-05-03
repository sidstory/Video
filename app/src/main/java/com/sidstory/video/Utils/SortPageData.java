package com.sidstory.video.Utils;

import android.content.Context;

public class SortPageData {
    private String page="";

    public String getPage(Context context) {
        return ParserData.getUrl(context) +page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
