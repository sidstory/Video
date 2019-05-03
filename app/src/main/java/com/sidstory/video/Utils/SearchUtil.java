package com.sidstory.video.Utils;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchUtil {
    public static String request(String content, Context context) throws UnsupportedEncodingException {
        String result="";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OkHttpClient client=new OkHttpClient.Builder().readTimeout(120, TimeUnit.SECONDS).build();
        RequestBody requestBody = null;
        FormBody formBody = new FormBody
                .Builder()
                .add("show","title,newstext")//设置参数名称和参数值
                .add("keyboard",content)
                .build();
        Request request = new Request.Builder()
                .url(ParserData.getUrl(context)+"/e/search/index.php")
                .post(formBody)
                .build();
        Call call =client.newCall(request);
// 同步请求
        try {
            Response response= call.execute();
            result=response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.d("search",result);
        return result;

    }
}
