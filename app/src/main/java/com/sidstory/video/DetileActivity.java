package com.sidstory.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jaeger.library.StatusBarUtil;
import com.sidstory.video.Utils.DetailAdapter;
import com.sidstory.video.Utils.ParserData;
import com.sidstory.video.Utils.VideoData;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JzvdStd;
import io.rmiri.skeleton.Master.IsCanSetAdapterListener;
import io.rmiri.skeleton.SkeletonGroup;


public class DetileActivity extends AppCompatActivity {
    private SkeletonGroup skeletonGroup;
    private TextView textView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    skeletonGroup.setShowSkeleton(false);
                    skeletonGroup.finishAnimation();
                    jzvdStd.setUp((String) msg.obj,title,JzvdStd.SCREEN_WINDOW_NORMAL);

                    textView.setVisibility(View.VISIBLE);
                    jzvdStd.setVisibility(View.VISIBLE);
                    break;
                }
                case 1:{
                    ArrayList<VideoData> list= (ArrayList<VideoData>) msg.obj;
                    adapter.addMoreDataAndSkeletonFinish(list);
                }
            }

        }
    };
    DetailAdapter adapter=null;
    List<VideoData> data=new ArrayList<>();
    private JzvdStd jzvdStd;
    private RecyclerView recyclerView;
    private AdView adView;
    String realVideoUrl ="";
    String title="";
    String img="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detile);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColorNoTranslucent(this, Color.WHITE);
        MobileAds.initialize(this, "ca-app-pub-2368191189662624~5140029622");
        adView=findViewById(R.id.detail_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        FirebaseAnalytics.getInstance(this);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"adview load success");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"adview load success"+i);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,bundle);
            }
        });
        StatusBarUtil.setTranslucent(this);

        SharedPreferences preferences=this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean wifidialog = preferences.getBoolean("wifidialog", false);
        jzvdStd=findViewById(R.id.video_player);

        if (wifidialog){
            jzvdStd.WIFI_TIP_DIALOG_SHOWED=true;
        }
        textView=findViewById(R.id.guess_like);
        recyclerView=findViewById(R.id.detail_recycle);
        skeletonGroup=findViewById(R.id.detail_activity_group);
        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        adapter= new DetailAdapter(getApplicationContext(), new ArrayList<VideoData>(), recyclerView, new IsCanSetAdapterListener() {
            @Override
            public void isCanSet() {
                recyclerView.setAdapter(adapter);
            }
        });
        String url = getIntent().getStringExtra("url");
        getUrl(url);
        getGuess(url);
        String img = getIntent().getStringExtra("img");
        Glide.with(DetileActivity.this).load(img).into(jzvdStd.thumbImageView);
        title=getIntent().getStringExtra("title");
        jzvdStd.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        JzvdStd.FULLSCREEN_ORIENTATION=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JzvdStd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;





    }

    private void getGuess(final String url) {//猜你喜欢

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VideoData> data = ParserData.paserHome(url,ParserData.PARSER_URL,getApplicationContext());
                if (data.size()>0){
                    Message message=Message.obtain();
                    message.what=1;
                    message.obj=data;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }


    @Override
    public void onBackPressed() {
        if (jzvdStd.backPress()){
            return;
        }
        finish();

    }

    @Override
    protected void onResume() {
        if(adView!=null){
            adView.resume();
        }
        super.onResume();
        JzvdStd.goOnPlayOnResume();
    }

    @Override
    protected void onDestroy() {
        if(adView!=null){
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(adView!=null){
        adView.pause();
    }
        super.onPause();
        JzvdStd.releaseAllVideos();

    }
    private void getUrl(final String url){//获取视频实际地址
        new Thread(new Runnable() {
            @Override
            public void run() {
                String realVideoUrl = ParserData.realVideoUrl(url,getApplicationContext());
                if (realVideoUrl!=null){
                    Message message=Message.obtain();
                    message.what=0;
                    message.obj=realVideoUrl;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}



