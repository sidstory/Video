package com.sidstory.video;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.coder.zzq.smartshow.toast.SmartToast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jaeger.library.StatusBarUtil;
import com.sidstory.video.Utils.ParserData;
import com.sidstory.video.Utils.SortDisplayAdapter;
import com.sidstory.video.Utils.SortPageData;
import com.sidstory.video.Utils.VideoData;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.rmiri.skeleton.Master.IsCanSetAdapterListener;

public class SortDisplayActivity extends AppCompatActivity {
    String sort="";
    ArrayList<SortPageData> pageData=new ArrayList<>();
    ArrayList<VideoData> predata=new ArrayList<>();
    private TextView sorttitle;
    int position=0;
    int currentpage=2;
    private AdView adView;
    private Context context=SortDisplayActivity.this;
    SortDisplayAdapter adapter=null;
    List<VideoData> datas=new ArrayList<>();
    private PullLoadMoreRecyclerView recyclerView;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    ArrayList<VideoData> list= (ArrayList<VideoData>) msg.obj;
                    adapter.addMoreDataAndSkeletonFinish(list);
                    predata=list;
                    recyclerView.setPushRefreshEnable(true);
                    break;
                }
                case 1:{
                    if (!recyclerView.getPushRefreshEnable())
                        recyclerView.setPushRefreshEnable(true);
                        pageData= (ArrayList<SortPageData>) msg.obj;

                    break;
                }
                case 2:{
                    position=recyclerView.getVerticalScrollbarPosition();
                    ArrayList<VideoData> data= (ArrayList<VideoData>) msg.obj;
                    predata.addAll(data);
                    recyclerView.invalidate();
                    adapter.addMoreDataAndSkeletonFinish(predata);
                    adapter.notifyDataSetChanged();
                    recyclerView.setPullLoadMoreCompleted();
                    recyclerView.scrollTo(0,position);
                    break;
                }

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_display);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColorNoTranslucent(this, Color.WHITE);
        recyclerView=findViewById(R.id.sort_display_recycle_view);
        MobileAds.initialize(this, "ca-app-pub-2368191189662624~5140029622");
        adView=findViewById(R.id.sort_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        sorttitle=findViewById(R.id.sort_display_title);
        sorttitle.setText(getIntent().getStringExtra("title"));
        sort = getIntent().getStringExtra("url");
        recyclerView.setStaggeredGridLayout(2);
        adapter=new SortDisplayAdapter(this, new ArrayList<VideoData>(), recyclerView,new IsCanSetAdapterListener() {
            @Override
            public void isCanSet() {
                recyclerView.setAdapter(adapter);
            }
        });
        if (ParserData.isNetworkConnected(this)){
            getData();
        }else{
            SmartToast.error("网络连接失败！");
        }

        recyclerView.setPullRefreshEnable(false);
        recyclerView.setPushRefreshEnable(false);
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<VideoData> list = ParserData.paserHome(sort+"/index_"+currentpage+".html",ParserData.PARSER_URL,context);
                        Message message=Message.obtain();
                            message.what=2;
                            message.obj=list;
                            handler.sendMessage(message);
                            currentpage++;

                    }
                }).start();


            }
        });
    }

    private void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<VideoData> list = ParserData.paserHome(sort,ParserData.PARSER_URL,context);
                if (list!=null){
                    Message msg=Message.obtain();
                    msg.what=0;
                    msg.obj=list;
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }
    @Override
    protected void onResume() {
        adView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        adView.pause();
        super.onPause();
    }
}
