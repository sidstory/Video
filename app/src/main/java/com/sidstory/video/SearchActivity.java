package com.sidstory.video;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.coder.zzq.smartshow.toast.SmartToast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jaeger.library.StatusBarUtil;
import com.sidstory.video.Utils.PageCodeData;
import com.sidstory.video.Utils.ParserData;
import com.sidstory.video.Utils.SearchAdapter;
import com.sidstory.video.Utils.SearchUtil;
import com.sidstory.video.Utils.VideoData;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.rmiri.skeleton.Master.IsCanSetAdapterListener;

public class SearchActivity extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private TextView result;
    private SweetAlertDialog pDialog;
    private int onclick=0;
    private int totalpage=0;
    private AdView adView;
    private int currentpage=2;
    SearchAdapter adapter=null;
    private PullLoadMoreRecyclerView recyclerView;
    ArrayList<VideoData> data=new ArrayList<>();
    PageCodeData pageCodeData=new PageCodeData();
    ArrayList<VideoData> predata=new ArrayList<>();
    int position=0;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    pDialog.dismiss();
                    textView.setClickable(true);
                    ArrayList<VideoData> list= (ArrayList<VideoData>) msg.obj;
                    if (list.size()==0){
                        SmartToast.fail("找不到与"+editText.getText().toString()+"相关的数据");
                    }
                    predata=list;
                    adapter.addMoreDataAndSkeletonFinish(list);
                    if (!recyclerView.getPushRefreshEnable())
                    result.setVisibility(View.VISIBLE);
                    break;
                }
                case 1:{
                    if (!recyclerView.getPushRefreshEnable())
                        recyclerView.setPushRefreshEnable(true);
                   PageCodeData data= (PageCodeData) msg.obj;
                   if (data.getTotal()>0){
                       result.setText("找到"+data.getTotal()+"条与"+editText.getText().toString()+"相关数据");
                   }
                    else {
                       result.setText("找到"+adapter.getItemCount()+"条与"+editText.getText().toString()+"相关数据");
                   }

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
                case 3:{
                    recyclerView.setPullLoadMoreCompleted();
                    break;
                }
                }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColorNoTranslucent(this, Color.WHITE);
        MobileAds.initialize(this, "ca-app-pub-2368191189662624~5140029622");
        adView=findViewById(R.id.search_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        textView=findViewById(R.id.start_search);
        result=findViewById(R.id.search_result_text);
        editText=findViewById(R.id.edit_search);
        recyclerView=findViewById(R.id.search_recycle_view);
        pDialog = new SweetAlertDialog(SearchActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在搜索");
        pDialog.setCancelable(false);
        recyclerView.setStaggeredGridLayout(2);
        recyclerView.setPullRefreshEnable(false);
        recyclerView.setPushRefreshEnable(false);
        adapter=new SearchAdapter(getApplicationContext(), new ArrayList<VideoData>(), recyclerView,new IsCanSetAdapterListener() {
            @Override
            public void isCanSet() {
                recyclerView.setAdapter(adapter);
            }
        });
        recyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
            }
            @Override
            public void onLoadMore() {
                Log.d("pagedata",pageCodeData.getTotal()+"");
               if (pageCodeData.getTotal()==0){
                   recyclerView.setPushRefreshEnable(false);
                   return;
               }
                int a=pageCodeData.getTotal()/20;
                if (((pageCodeData.getTotal()%20)!=0)){
                    a++;
                }
                if (currentpage>a){
                    recyclerView.setPullLoadMoreCompleted();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url=ParserData.getUrl(getApplicationContext())+"/e/search/result/index.php?page="+currentpage+pageCodeData.PAGEMIDDLE+pageCodeData.getSearchid();
                        Log.d("pageurl",url);
                        List<VideoData> list = ParserData.paserHome(url, ParserData.PARSER_URL,getApplicationContext());
                        if (list.size()>0){
                            Message message=Message.obtain();
                            message.what=2;
                            message.obj=list;
                            handler.sendMessage(message);
                            currentpage++;
                        }else {
                            Message message=Message.obtain();
                            message.what=3;
                            handler.sendMessage(message);

                        }

                    }
                }).start();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String keyword=editText.getText().toString();
                if (!keyword.isEmpty()){
                    textView.setClickable(false);
                    if (recyclerView.getVisibility()==View.INVISIBLE){
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    if (onclick>0){

                        pDialog.show();
                    }
                    onclick++;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String request = SearchUtil.request(keyword,getApplicationContext());
                                if (request!=null){
                                    getpaggecode(request);
                                    List<VideoData> list = ParserData.paserHome(request,ParserData.PARSER_PAGE,getApplicationContext());
                                    Message message=Message.obtain();
                                    message.what=0;
                                    message.obj=list;
                                    handler.sendMessage(message);

                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        });
    }
    private void getpaggecode(final String page){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PageCodeData getpagecode = ParserData.getpagecode(page);
                if (getpagecode!=null){
                    pageCodeData=getpagecode;
                    Message message=Message.obtain();
                    message.what=1;
                    message.obj=getpagecode;
                    handler.sendMessage(message);
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
