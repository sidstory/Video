package com.sidstory.video;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.coder.zzq.smartshow.toast.SmartToast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaeger.library.StatusBarUtil;

import cn.bmob.v3.update.BmobUpdateAgent;

public class MainActivity extends AppCompatActivity {
    FragmentManager manager=getSupportFragmentManager();
    BottomNavigationView navigation;
    private Long firstpress = new Long(0);
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){

            }
        }
    };
    private TextView search;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:{
                    manager.beginTransaction().replace(R.id.main_fragment,new HomeFragment()).commit();
                    return true;
                }


                case R.id.navigation_new: {
                    manager.beginTransaction().replace(R.id.main_fragment,new SortFragment()).commit();
                    return true;
                }

                case R.id.navigation_account:{
                    manager.beginTransaction().replace(R.id.main_fragment,new SettingFragment()).commit();
                    return true;
                }

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColorNoTranslucent(this,Color.WHITE);
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);
        search=findViewById(R.id.text_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        manager.beginTransaction().replace(R.id.main_fragment,new HomeFragment()).commit();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.getChildAt(0).setSelected(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    public void onBackPressed() {
        Long presentpress = System.currentTimeMillis();
        if ((presentpress - firstpress) < 2000) {
            finish();
        } else {
            SmartToast.show("再次按下退出");
            firstpress = presentpress;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
