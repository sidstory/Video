package com.sidstory.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coder.zzq.smartshow.toast.SmartToast;
import com.google.android.gms.ads.InterstitialAd;
import com.sidstory.video.Utils.Url;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SplashActivity extends AppCompatActivity {
    private LinearLayout splash;
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private long timerMilliseconds;
    private SharedPreferences preferences;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView=findViewById(R.id.splash_img);
        preferences= this.getSharedPreferences("settings",Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();
        Glide.with(this).load(getResources().getDrawable(R.drawable.splash)).apply(RequestOptions.centerCropTransform()).into(imageView);
        BmobQuery<Url> query=new BmobQuery<>();
        query.order("-createdAt");
        query.findObjects(new FindListener<Url>() {
            @Override
            public void done(List<Url> list, BmobException e) {
                if (e==null){
                    Url url = list.get(0);
                    edit.putString("url",url.getUrl());
                    edit.apply();

                }
                else{
                    SmartToast.fail("获取数据失败");
                    return;
                }
            }
        });

        boolean closesplash = preferences.getBoolean("splashclose", false);
        if (closesplash){
            goMain();
        }
        waittime();

    }



    private void waittime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                goMain();

            }
        }).start();
    }
private void goMain(){
    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
}



}