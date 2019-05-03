package com.sidstory.video.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParserData {
    private static StringBuffer buffer;
    static Document document=null;
    public  static int PARSER_PAGE=1;
    public static  int PARSER_URL=0;
    private static VideoData data=null;
    private static String urllink;
    public static List<VideoData> paserHome(String url,int parserType,Context context){
        ArrayList<VideoData> list=new ArrayList<>();
        if (parserType==PARSER_URL){
            String connect = connect(url, context);
            if (connect==null){
                return null;
            }
            document=Jsoup.parse(connect);
        }
        else {
            if (url!=null){
                document = Jsoup.parse(url);
                Log.d("page",url);
            }else {
                return null;
            }

        }

        Elements elementsByClass = document.getElementsByClass("mb-wrap");
        for(Element urlelement:elementsByClass){
            data=new VideoData();
            String href =getUrl(context)+ urlelement.attr("href");
            data.setVideourl(href);
            Elements imgrowurl=urlelement.getElementsByClass("mb-img");
            String imgrow = imgrowurl.attr("style");
            String imgurl = imgurl(imgrow,context);
            data.setImgurl(imgurl);
            Elements title = urlelement.getElementsByClass("title");
            String text = title.text();
            data.setTitle(text);
            Elements td = urlelement.getElementsByAttribute("align");
            for (Element t:td){//时间和观看人数
                String align = t.attr("align");
                if (align.equals("left")){//时间
                    String[] split = t.text().split(" ");
                    data.setTime(split[0]);
                }else if (align.equals("right")){
                    data.setWatch(t.text());//人数
                }
            }


            if (!href.contains("VIP")){//过滤vip
                list.add(data);
            }

        }
            return list;
    }
    static String imgurl(String url,Context context){
        String regrex="\\/d\\/file\\/.+\\.[a-z|A-Z]{3,4}";
        String regrex1="https{0,1}.+\\.?[a-z]{3,4}";
        Pattern pattern=Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(url);
        String finalurl=null;
        while (matcher.find())
        {
            finalurl=matcher.group();

        }
        if (finalurl==null){
    pattern=Pattern.compile(regrex1);
    matcher=pattern.matcher(url);
        while (matcher.find()){
            finalurl=matcher.group();
            return finalurl;
        }
        }
        return getUrl(context)+finalurl;
    }
    public static String realVideoUrl(String rowurl,Context context){
        String connect = connect(rowurl,context);
        String regrex1="\\/e\\/DownSys/play/.+pathid=\\d?";
        String regrex="https?.+.m3u8";
        Pattern pattern = Pattern.compile(regrex1);
        Matcher matcher =null;
        matcher=pattern.matcher(connect);
        String videoUrl="";
        while (matcher.find()){
            videoUrl=getUrl(context)+matcher.group();
        }
        String connect1 = connect(videoUrl,context);
        if (connect1==null){
            return null;
        }
        Pattern pattern1=Pattern.compile(regrex);
        Matcher matcher1 = pattern1.matcher(connect1);
        String real="";
        while (matcher1.find()){
           real= matcher1.group();
        }
        return real;
    }
    public static String connect(String url,Context context) {
        if (!isNetworkConnected(context)){
            return null;
        }
        String result=null;
        if (url==null||!url.contains("http")){
            return null;
        }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        OkHttpClient client=new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build();
        Request request= new Request.Builder().url(url).get().removeHeader("User-Agent").addHeader("User-Agent",getUserAgent(context)).build();

        Call call = client.newCall(request);
        try {
            Response execute = call.execute();
            result=execute.body().string();
            execute.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;

    }
    public static PageCodeData getpagecode(String page){//搜索界面
        if (page==null)return null;
        Document parse = Jsoup.parse(page);
        Elements pages = parse.getElementsByClass("pages");
        if (pages==null)
            return null;
        PageCodeData data=new PageCodeData();
        for (Element e:pages){
            Elements href = e.getElementsByAttribute("href");
            String href1 = href.attr("href");
            if (href1!=null){
                String[] split = href1.split("&searchid=");
                if (split.length<2){
                    return null;
                }
                data.setSearchid(split[1]);
            }
            Elements title = e.getElementsByAttribute("title");

                Elements b = title.select("b");
                String text = b.text();
                data.setTotal(Integer.parseInt(text));


        }
        return data;
    }

    public static boolean isNetworkConnected(Context context) {// get natworkstute
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    public static String getUrl(Context context){
        SharedPreferences preferences=context.getSharedPreferences("settings",Context.MODE_PRIVATE);
        String url = preferences.getString("url", "https://m.kpd491.com");
        return url;
    }
}
